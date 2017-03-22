package com.haloai.hud.lib.transportlayer.bluetooth;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;
import com.haloai.hud.lib.transportlayer.interfaces.IPhoneEndpointTransportInterface;
import com.haloai.hud.lib.transportlayer.interfaces.IPhoneEndpointTransportNotification;
import com.haloai.hud.lib.transportlayer.interfaces.IPhoneEndpointTransportNotification.HardwareStatus;
import com.haloai.hud.lib.transportlayer.interfaces.IPhoneEndpointTransportNotification.HardwareStatusReason;
import com.haloai.hud.utils.IHaloLogger;
/**
 * Phone的蓝牙SPP实现类
 *
 * @author Created by Mo Bing(mobing@haloai.com) on 21/11/14.
 */
public class PhoneEndpointTransportBTSpp implements IPhoneEndpointTransportInterface {

    //
    private Context mContext;
    private ConnectionState mConnectionState;
    private EndpointDeviceInfo mHudDeviceInfo;
    private IPhoneEndpointTransportNotification mHudHardwareNotification;

    private IHaloLogger log = null;

    //Bluetooth related
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private BluetoothSPPConnectedThread mConnectedThread;
    
    //The UUID of ISSC bluetooth module spp
    private static final UUID ISSC_BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private List<EndpointDeviceInfo> mAlreadyPairedHudDevices;
    private List<String> mAlreadyReportedHudDeviceAddresses;

    // Connection retrying methods
    private int connectionRetryCount = 0;
    private long connectionRetryDuration = 0;
    private ConnectionRetryHandler mRetryHandler;
    private boolean isRetryingConnect = false;

    //Preference constants
    private static String FMEBT_PREF_NAME = "HALOHUD";
    private static String BT_ADDRESS_KEY = "HALOHUD_ADDRESS";
    private static String BT_NAME = "HALOHUD_NAME";
    private boolean retryBecauseConnectFailed = true;	//用于表示retry是由主动发起连接造成的，还是因为连接被动断开造成的。前者只需要重试2次，后者会不停的重试。

//    private List<IHudProtocolPacket> mReceivedHudProtocolPacket;

    public PhoneEndpointTransportBTSpp(Context context) {
        this.mContext = context;
        this.mConnectionState = ConnectionState.STATE_NONE;
        mRetryHandler = new ConnectionRetryHandler(this);
    }
    
    @Override
    public EndpointDeviceInfo getRecentDevice() {
        SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME, 0);
        if (pref.contains(BT_ADDRESS_KEY)) {
            String hudAddress = pref.getString(BT_ADDRESS_KEY, "");
            EndpointDeviceInfo hudDeviceInfo = null;
            if (!hudAddress.isEmpty()) {
                hudDeviceInfo = new EndpointDeviceInfo();
                hudDeviceInfo.setUniqueAddress(hudAddress);

                String hudName = pref.getString(BT_NAME, "");
                hudDeviceInfo.setName(hudName);
            }
            return hudDeviceInfo;
        }
        return null;
    }
    
    @Override
    public void clearRecentDevice() {
        SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME, 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        if (pref.contains(BT_ADDRESS_KEY)) {
            prefEditor.remove(BT_ADDRESS_KEY);
        }
        if (pref.contains(BT_NAME))
            prefEditor.remove(BT_NAME);
        prefEditor.commit();
    }
    
    private void setRecentDevice(String hudAddress, String hudName) {
        SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME, 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        if (hudAddress != null) {
            prefEditor.putString(BT_ADDRESS_KEY, hudAddress);
        }
        if (hudName != null)
            prefEditor.putString(BT_NAME, hudName);
        prefEditor.commit();
    }
    
    @Override
    public void connect(IPhoneEndpointTransportNotification hudHardwareNotification, EndpointDeviceInfo hudDeviceInfo) throws HudTransportException {
        LOGI("calling connect.");

        if (hudHardwareNotification == null)
            throw new HudTransportException("calling connect - IHudHardwareNotification parameter cannot be null.");

        if (hudDeviceInfo == null)
            throw new HudTransportException("calling connect - hudDeviceInfo parameter cannot be null.");

        if (isConnectionState())
            disconnect();

        if (isSearchingState())
            stopDeviceSearch();

        if (mConnectionState != ConnectionState.STATE_NONE && mConnectionState != ConnectionState.STATE_END_DISCOVERING) {
            throw new HudTransportException("Expected bluetooth connection state is STATE_NONE, current is " + mConnectionState);
        }

        mHudHardwareNotification = hudHardwareNotification;
        mHudDeviceInfo = hudDeviceInfo;

        if (!setupBluetoothEnv(ConnectionState.STATE_CONNECTING)) {
            return;
        }

        setState(ConnectionState.STATE_CONNECTING);

        this.retryBecauseConnectFailed = true; //如果主动发起连接失败，只会重试有限次数；与之相反的是，如果连接成功后连接再丢失则无限重试。
        deviceConnect(hudDeviceInfo);
    }

    @Override
    public void searchForHudDevices(IPhoneEndpointTransportNotification hudHardwareNotification) throws HudTransportException {
        LOGI("Calling searchForClientDevices.");

        if (hudHardwareNotification == null)
            throw new HudTransportException("calling connect - IHudHardwareNotification parameter cannot be null.");

        if (isConnectionState())
            disconnect();

        if (isSearchingState())
            stopDeviceSearch();

        if (mConnectionState != ConnectionState.STATE_NONE && mConnectionState != ConnectionState.STATE_END_DISCOVERING)
            throw new HudTransportException("Expected bluetooth connection state is STATE_NONE, current is " + mConnectionState);

        mHudHardwareNotification = hudHardwareNotification;

        if (!setupBluetoothEnv(ConnectionState.STATE_DISCOVERING)) {
            return;
        }

        mAlreadyPairedHudDevices = retrievePairedDevices();
        mAlreadyReportedHudDeviceAddresses = new ArrayList<String>();

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        setState(ConnectionState.STATE_DISCOVERING);
        mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.STARTED);

        mBluetoothAdapter.startDiscovery();
        
    }
    
    @Override
    public List<EndpointDeviceInfo> retrievePairedDevices() {
        LOGI("Calling retrievePairedDevices.");

        List<EndpointDeviceInfo> pairedHudDevices = new ArrayList<EndpointDeviceInfo>();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedBtDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice btDevice : pairedBtDevices) {
//            if (log != null) {
//                ParcelUuid[] uuids = btDevice.getUuids();
//                LOGI("All supported UUID in this device - " + btDevice.getName());
//                for (ParcelUuid puuid : uuids) {
//                    LOGI(puuid.getUuid().toString());
//                }
//            }

            EndpointDeviceInfo hudDeviceInfo = new EndpointDeviceInfo();
            hudDeviceInfo.setUniqueAddress(btDevice.getAddress());
            hudDeviceInfo.setName(btDevice.getName());

            pairedHudDevices.add(hudDeviceInfo);
        }

        return pairedHudDevices;
    }

    @Override
    public void stopDeviceSearch() {
        LOGI("calling stopDeviceSearch.");

        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        if (mAlreadyPairedHudDevices != null)
            mAlreadyPairedHudDevices.clear();
        mAlreadyPairedHudDevices = null;

        if (mAlreadyReportedHudDeviceAddresses != null)
            mAlreadyReportedHudDeviceAddresses.clear();
        mAlreadyReportedHudDeviceAddresses = null;

        clearBluetoothEvn();

        setState(ConnectionState.STATE_NONE);
    }
    
    @Override
    public boolean isConnected() {
        return mConnectionState == ConnectionState.STATE_CONNECTED;
    }

    @Override
    public boolean isSearching() {
        return isSearchingState();
    }

    @Override
    public void disconnect() {
        LOGI("calling disconnect.");
        cancelRetryCycle();
        if (isConnectionState()) {
            deviceDisconnect(DisconnectReason.USER_REQUEST);
        } else if (isSearchingState())
            stopDeviceSearch();

        clearBluetoothEvn();
        setState(ConnectionState.STATE_NONE);
    }

    @Override
    public void send(ByteBuffer byteBuffer) {
        LOGI("calling send. data length is " + byteBuffer.array().length);
        if (mConnectionState == ConnectionState.STATE_CONNECTED && mConnectedThread != null) {
            synchronized (this) {
                mConnectedThread.write(byteBuffer.array());
            }
        } else {
            LOGW("Trying to send without being connected.");
        }
    }
    
//    @Override
//    public List<IHudProtocolPacket> takeReceivedHudProtocolPacket() {
//    	List<IHudProtocolPacket> returnedList = new ArrayList<IHudProtocolPacket>();
//    	if (mReceivedHudProtocolPacket != null) {
//	    	returnedList.addAll(mReceivedHudProtocolPacket);
//	    	mReceivedHudProtocolPacket.clear();
//    	}
//        return returnedList;
//    }

    @Override
    public void setLogger(IHaloLogger engineLogger) {
        log = engineLogger;
    }

    private void deviceConnect(EndpointDeviceInfo deviceInfo)
    {
        LOGI("deviceConnect()");

        String hudAddress = deviceInfo.getUniqueAddress();
        if (hudAddress != null) {
            deviceDisconnect(DisconnectReason.INTERNAL_USE);
            BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(hudAddress);
            LOGI("Connecting to: " + bluetoothDevice + "; Address: " + hudAddress);

            mConnectThread = new ConnectThread(bluetoothDevice);

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            setState(ConnectionState.STATE_CONNECTING);
            mConnectThread.start();
        }
    }

    private void setState(ConnectionState state) {
        LOGI("State change from " + mConnectionState + " to " + state);
        mConnectionState = state;
    }

    private boolean setupBluetoothEnv(ConnectionState connectionState) throws HudTransportException {
        if (mBluetoothAdapter != null)
            return true;

        LOGI("Calling setupBluetoothEnv.");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            setState(ConnectionState.STATE_NONE);
            mHudHardwareNotification.hardwareStatus(HardwareStatus.DEVICE_DISCONNECTED, HardwareStatusReason.NOT_AVAILABLE);
            throw new HudTransportException("Bluetooth is not supported. Please be sure that you put the appropriated bluetooth permissions in the AndroidManifest.xml");
        }

        mContext.registerReceiver(mBluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        mContext.registerReceiver(mBluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        mContext.registerReceiver(mBluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        mContext.registerReceiver(mBluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        mContext.registerReceiver(mBluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        if (!mBluetoothAdapter.isEnabled()) {
            LOGI("Bluetooth isn't enabled. To notify user.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBTIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (connectionState == ConnectionState.STATE_CONNECTING)
            	setState(ConnectionState.STATE_WAITING_BLUETOOTH_STATE);
            else if (connectionState == ConnectionState.STATE_DISCOVERING)
            	setState(ConnectionState.STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH);
            try {
                mContext.startActivity(enableBTIntent);
            } catch (ActivityNotFoundException e) {
                setState(ConnectionState.STATE_NONE);
                throw new HudTransportException("Activate bluetooth intent exception:" + e);
            }
//            mHudHardwareNotification.hardwareStatus(HardwareStatus.DEVICE_DISCONNECTED, HardwareStatusReason.NOT_ENABLED);
            return false;
        }

        return true;
    }

    private void clearBluetoothEvn() {
        try {
            mContext.unregisterReceiver(mBluetoothBroadcastReceiver);
        } catch (Exception e) {  }
        mBluetoothAdapter = null;
    }

    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	if (mAlreadyPairedHudDevices != null) { //系统自己的“搜索设备”也会回调到这里，所以需要确认是不是我们发起的设备搜索动作
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                if (device != null && isSearchingState()) {
	                    LOGI("Found new bluetooth device - [" + device.getName() + "]");
	                    EndpointDeviceInfo hudDeviceInfo = new EndpointDeviceInfo();
	                    hudDeviceInfo.setName(device.getName());
	                    hudDeviceInfo.setUniqueAddress(device.getAddress());
	                    boolean hasReported = mAlreadyReportedHudDeviceAddresses.contains(device.getAddress());
	                    if (hasReported) {
	                        mHudHardwareNotification.updateDeviceInfo(hudDeviceInfo);
	                    } else {
	                        mAlreadyReportedHudDeviceAddresses.add(device.getAddress());
	                        mHudHardwareNotification.newDeviceAvailable(hudDeviceInfo);
	                    }
	                    //To handle the paired bluetooth devices.
	                    if (mAlreadyPairedHudDevices != null && mAlreadyPairedHudDevices.size() > 0) {
	                        for (int i=0; i<mAlreadyPairedHudDevices.size(); i++) {
	                            EndpointDeviceInfo pariedHudDeviceInfo = mAlreadyPairedHudDevices.get(i);
	                            if (pariedHudDeviceInfo.getUniqueAddress().compareToIgnoreCase(hudDeviceInfo.getUniqueAddress()) == 0) {
	                                mAlreadyPairedHudDevices.remove(i);
	                            }
	                        }
	                    }
	                }
            	}
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            	if (mAlreadyPairedHudDevices != null) {//系统自己的“搜索设备”也会回调到这里，所以需要确认是不是我们发起的设备搜索动作
	                setState(ConnectionState.STATE_DISCOVERING);
	                mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.STARTED);
            	}
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mAlreadyPairedHudDevices != null) {//系统自己的“搜索设备”也会回调到这里，所以需要确认是不是我们发起的设备搜索动作
                    for (int i=0; i<mAlreadyPairedHudDevices.size(); i++) {
                        mHudHardwareNotification.newDeviceAvailable(mAlreadyPairedHudDevices.get(i));
                    }
                    mAlreadyPairedHudDevices.clear();
                    mAlreadyPairedHudDevices = null;
	                setState(ConnectionState.STATE_END_DISCOVERING);
	                mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.ENDED);
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.WAS_TURNED_OFF);
                        if (mConnectionState == ConnectionState.STATE_CONNECTING || mConnectionState == ConnectionState.STATE_CONNECTED || mConnectionState == ConnectionState.STATE_START_CONNECTING) {
                            if (mConnectionState == ConnectionState.STATE_START_CONNECTING) {
                                cancelRetryCycle();
                            }
                            deviceDisconnect(DisconnectReason.CONNECTION_LOST);
                            setState(ConnectionState.STATE_WAITING_BLUETOOTH_STATE);
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.WAS_TURNED_ON);
                        if (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH) {
                            searchForHudDevices(mHudHardwareNotification);
                        } else if (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE) {
                            handleConnectionRetry();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.IS_TURNING_OFF);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON: //11
                        mHudHardwareNotification.hardwareStatus(IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_STATUS_CHANGED, IPhoneEndpointTransportNotification.HardwareStatusReason.IS_TURNING_ON);
                        break;
                }
            }
//            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device != null && mHudDeviceInfo != null && device.getAddress().equals(mHudDeviceInfo.getUniqueAddress())) {
//                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
//                    if (bondState == BluetoothDevice.BOND_BONDING) {
//
//                    }
//                }
//            }
        }
    };

    private static enum ConnectionState
    {
        STATE_NONE,
        STATE_START_DISCOVERING,
        STATE_DISCOVERING,
        STATE_END_DISCOVERING,
        STATE_START_CONNECTING,
        STATE_WAITING_BLUETOOTH_STATE,
        STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_DISCONNECTING
    }

    private static enum DisconnectReason
    {
        USER_REQUEST,
        CANNOT_CONNECT,
        CONNECTION_LOST,
        INTERNAL_USE
    }

	private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

           try {
//                tmp = device.createRfcommSocketToServiceRecord(ISSC);
                tmp = device.createInsecureRfcommSocketToServiceRecord(ISSC_BLUETOOTH_UUID);
            } catch (IOException e) {
                LOGE("Create bluetooth socket failed " + e.getLocalizedMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            LOGI("BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    LOGE("failed to connect " + e);
                    mmSocket.close();
                } catch (IOException e2) {
                    LOGE("unable to close() socket during connection failure");
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (PhoneEndpointTransportBTSpp.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LOGE("close() of connect socket failed " + e);
            }
        }
    }

    

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        LOGI("calling connected.");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
//        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        //Store the connected device address in order to connect it directly next time.
        setRecentDevice(mHudDeviceInfo.getUniqueAddress(), mHudDeviceInfo.getName());

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new BluetoothSPPConnectedThread(socket, mBluetoothConnectionHandler);
        mConnectedThread.setLogger(log);
        mConnectedThread.start();

        setState(ConnectionState.STATE_CONNECTED);

        Message msg = mBluetoothConnectionHandler.obtainMessage(BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTED);
        mBluetoothConnectionHandler.sendMessage(msg);
    }

    private void connectionFailed() {
        if (mConnectionState != ConnectionState.STATE_DISCONNECTING) {
            if (mConnectionState == ConnectionState.STATE_CONNECTED || mConnectionState == ConnectionState.STATE_CONNECTING) {
                mConnectionState = ConnectionState.STATE_CONNECTING;
                enableRetryCycle();
            }
        }
    }

    private void connectionLost() {
    	  
        if (mConnectionState != ConnectionState.STATE_NONE && mConnectionState != ConnectionState.STATE_DISCONNECTING) {
            if (mConnectionState == ConnectionState.STATE_CONNECTING || mConnectionState == ConnectionState.STATE_CONNECTED) {
                mConnectionState = ConnectionState.STATE_CONNECTING;
                this.retryBecauseConnectFailed = false;
                enableRetryCycle();
            }
        }
    }

    private boolean isSearchingState()
    {
        return (mConnectionState == ConnectionState.STATE_START_DISCOVERING) ||
               (mConnectionState == ConnectionState.STATE_DISCOVERING) ||
               (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH);
    }

    private boolean isConnectionState() {
        return (mConnectionState == ConnectionState.STATE_START_CONNECTING) ||
               (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE) ||
               (mConnectionState == ConnectionState.STATE_CONNECTING) ||
               (mConnectionState == ConnectionState.STATE_CONNECTED) ||
               (mConnectionState == ConnectionState.STATE_DISCONNECTING);
    }

    private void deviceDisconnect(DisconnectReason reason)
    {
        LOGI("deviceDisconnect. reason is " + reason);
        ConnectionState oldState = mConnectionState;
        if ((mConnectThread != null) || (mConnectedThread != null)) {
            setState(ConnectionState.STATE_DISCONNECTING);
            if (mConnectThread != null) {
                LOGI("deviceDisconnect(): Stop ConnectThread");
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mConnectedThread != null) {
                LOGI("deviceDisconnect(): Stop ConnectedThread");
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
        }
        if (reason != DisconnectReason.INTERNAL_USE) {
            setState(ConnectionState.STATE_NONE);

            if (oldState != ConnectionState.STATE_NONE)
                notifyClientDisconnect(reason);
        }
    }

    private void notifyClientDisconnect(DisconnectReason reason)
    {
        LOGI("notifyClient(" + reason.toString() + ")");
        if ((reason != DisconnectReason.INTERNAL_USE) && (mHudHardwareNotification != null)) {
            IPhoneEndpointTransportNotification.HardwareStatus hardwareStatus = IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_DISCONNECTED;
            IPhoneEndpointTransportNotification.HardwareStatusReason disconnectReason = IPhoneEndpointTransportNotification.HardwareStatusReason.UNKNOWN;
            switch (reason) {
                case CONNECTION_LOST:
                    disconnectReason = IPhoneEndpointTransportNotification.HardwareStatusReason.LOST_CONNECTION;
                    break;
                case CANNOT_CONNECT:
                    disconnectReason = IPhoneEndpointTransportNotification.HardwareStatusReason.CLIENT_DEVICE_NOT_AVAILABLE;
                    break;
                case USER_REQUEST:
                    disconnectReason = IPhoneEndpointTransportNotification.HardwareStatusReason.USER_REQUEST;
                    break;
                case INTERNAL_USE:
                    return;
                default:
                    LOGI("Unhandled DisconnectReason" + reason.toString());
                    break;
            }

            mHudHardwareNotification.hardwareStatus(hardwareStatus, disconnectReason);
        }
    }

    private int getNextRetryTimeout() {
        int retVal = (int)Math.pow(this.connectionRetryCount <= 0 ? 1 : this.connectionRetryCount, 2.0D) * 1000;

        if (retVal > 10*60*1000) {
            retVal = 10*60*1000;
        }
        return retVal;
    }

    private void enableRetryCycle() {
        isRetryingConnect = true;
        if (this.connectionRetryCount <= 0) {
            LOGI("enableRetryCycle(): Enable connection retry cycle!");
            this.connectionRetryDuration = System.currentTimeMillis();
            this.connectionRetryCount = 1;
        }
        int retryTimeout = getNextRetryTimeout();
        LOGI("enableRetryCycle(): Enqueue \"Connection Retry Message\" after: " + retryTimeout + " ms; [" + (System.currentTimeMillis() - this.connectionRetryDuration) / 1000.0D + "] seconds");
        mRetryHandler.sendEmptyMessageDelayed(0, retryTimeout);
    }

    private void cancelRetryCycle() {
        LOGI("cancelRetryCycle(): Stop \"Connection Retry Message\"! Connection retry duration: [" + (System.currentTimeMillis() - this.connectionRetryDuration) / 1000.0D + "] seconds");
        this.connectionRetryCount = 0;
        this.connectionRetryDuration = 0;
        mRetryHandler.removeMessages(0);
        isRetryingConnect = false;
    }

    private void handleConnectionRetry()
    {
        synchronized (this) {
            if (this.retryBecauseConnectFailed && this.connectionRetryCount >= 2) {
            	//Retry 3 times at most
            	cancelRetryCycle();

	            // Send a message back to the client
	            Message msg = mBluetoothConnectionHandler.obtainMessage(BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CANNOT_CONNECT);
	            mBluetoothConnectionHandler.sendMessage(msg);
            } else {
            	this.connectionRetryCount += 1;
            	deviceConnect(mHudDeviceInfo);
            }
        }
    }

    private static class ConnectionRetryHandler extends android.os.Handler
    {
        private final WeakReference<PhoneEndpointTransportBTSpp> mService;

        ConnectionRetryHandler(PhoneEndpointTransportBTSpp activity)
        {
            this.mService = new WeakReference<PhoneEndpointTransportBTSpp>(activity);
        }

        public void handleMessage(Message msg)
        {
            synchronized ((PhoneEndpointTransportBTSpp)this.mService.get()) {
                PhoneEndpointTransportBTSpp service = (PhoneEndpointTransportBTSpp)this.mService.get();
                if (service != null) {
                    service.handleConnectionRetry();
                }
            }
        }
    }

    //Decoupling the bluetooth connected thread and main ui thread.
    private final Handler mBluetoothConnectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CANNOT_CONNECT:
                	if (!isRetryingConnect) //Doesn't notify upper app when it's in retrying status.
                		mHudHardwareNotification.hardwareStatus(HardwareStatus.DEVICE_DISCONNECTED, HardwareStatusReason.FAILED);
                    break;
                case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTION_LOST:
                	connectionLost();
                    mHudHardwareNotification.hardwareStatus(HardwareStatus.DEVICE_DISCONNECTED, HardwareStatusReason.LOST_CONNECTION);
                    break;
                case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_DATA_AVAILABLE:
                	byte[] data = (byte[]) msg.obj;
                	LOGI(String.format("Data arrived. data length is %d", data.length));
                	mHudHardwareNotification.dataArrival(data);
                    break;
                case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_WRITE:
                    break;
                case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTED:
                    mHudHardwareNotification.hardwareStatus(HardwareStatus.DEVICE_CONNECTED, HardwareStatusReason.ENDED);
                    break;
            }
        }
    };

    private void LOGI(String msg) {
        if (log != null) log.logI(PhoneEndpointTransportBTSpp.class.getName(), msg);
    }

    private void LOGE(String msg) {
        if (log != null) log.logE(PhoneEndpointTransportBTSpp.class.getName(), msg);
    }

    private void LOGW(String msg) {
        if (log != null) log.logW(PhoneEndpointTransportBTSpp.class.getName(), msg);
    }
}
