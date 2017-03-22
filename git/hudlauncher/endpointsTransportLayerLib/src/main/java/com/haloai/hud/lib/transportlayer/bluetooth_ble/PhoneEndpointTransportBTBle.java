package com.haloai.hud.lib.transportlayer.bluetooth_ble;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
 * Phone的蓝牙BLE实现类
 * 
 * @author 龙
 */
public class PhoneEndpointTransportBTBle implements
		IPhoneEndpointTransportInterface {

	//
	private Context mContext;
	private ConnectionState mConnectionState;
	private EndpointDeviceInfo mHudDeviceInfo;
	private IPhoneEndpointTransportNotification mHudHardwareNotification;

	private IHaloLogger log = null;

	private List<EndpointDeviceInfo> mAlreadyPairedHudDevices;
	private List<String> mAlreadyReportedHudDeviceAddresses;

	// Connection retrying methods
	private int connectionRetryCount = 0;
	private long connectionRetryDuration = 0;
	private ConnectionRetryHandler mRetryHandler;
	private boolean isRetryingConnect = false;

	// Preference constants
	private static String FMEBT_PREF_NAME = "HALOHUD";
	private static String BT_ADDRESS_KEY = "HALOHUD_ADDRESS";
	private static String BT_NAME = "HALOHUD_NAME";
	private boolean retryBecauseConnectFailed = true; // 用于表示retry是由主动发起连接造成的，还是因为连接被动断开造成的。前者只需要重试2次，后者会不停的重试。

	// ble connection handlr class
	private PhoneBTBleHandler mPhoneBTBleHandler;

	public PhoneEndpointTransportBTBle(Context context) {
		this.mContext = context;
		this.mConnectionState = ConnectionState.STATE_NONE;
		this.mPhoneBTBleHandler = new PhoneBTBleHandler(context,
				mBluetoothConnectionHandler);
		mRetryHandler = new ConnectionRetryHandler(this);
	}

	@Override
	public EndpointDeviceInfo getRecentDevice() {
		// TODO 获取曾经连过的设备
		SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME,
				0);
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
		// TODO 清除曾经连过的设备
		SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME,
				0);
		SharedPreferences.Editor prefEditor = pref.edit();
		if (pref.contains(BT_ADDRESS_KEY)) {
			prefEditor.remove(BT_ADDRESS_KEY);
		}
		if (pref.contains(BT_NAME))
			prefEditor.remove(BT_NAME);
		prefEditor.commit();
	}

	private void setRecentDevice(String hudAddress, String hudName) {
		// TODO 设置/添加曾经连过的设备
		SharedPreferences pref = mContext.getSharedPreferences(FMEBT_PREF_NAME,
				0);
		SharedPreferences.Editor prefEditor = pref.edit();
		if (hudAddress != null) {
			prefEditor.putString(BT_ADDRESS_KEY, hudAddress);
		}
		if (hudName != null)
			prefEditor.putString(BT_NAME, hudName);
		prefEditor.commit();
	}

	@Override
	public void connect(
			IPhoneEndpointTransportNotification hudHardwareNotification,
			EndpointDeviceInfo hudDeviceInfo) throws HudTransportException {
		LOGI("calling connect.");

		if (hudHardwareNotification == null)
			throw new HudTransportException(
					"calling connect - IHudHardwareNotification parameter cannot be null.");

		if (hudDeviceInfo == null)
			throw new HudTransportException(
					"calling connect - hudDeviceInfo parameter cannot be null.");

		if (isConnectionState())
			disconnect();

		if (mPhoneBTBleHandler.isSearch())
			stopDeviceSearch();

		if (mConnectionState != ConnectionState.STATE_NONE
				&& mConnectionState != ConnectionState.STATE_END_DISCOVERING) {
			throw new HudTransportException(
					"Expected bluetooth connection state is STATE_NONE, current is "
							+ mConnectionState);
		}

		mHudHardwareNotification = hudHardwareNotification;
		mHudDeviceInfo = hudDeviceInfo;

		setState(ConnectionState.STATE_CONNECTING);

		this.retryBecauseConnectFailed = true; // 如果主动发起连接失败，只会重试有限次数；与之相反的是，如果连接成功后连接再丢失则无限重试。
		deviceConnect(hudDeviceInfo);
	}

	@Override
	public void searchForHudDevices(
			IPhoneEndpointTransportNotification hudHardwareNotification)
			throws HudTransportException {
		LOGI("Calling searchForClientDevices.");

		if (hudHardwareNotification == null)
			throw new HudTransportException(
					"calling connect - IHudHardwareNotification parameter cannot be null.");

		if (isConnectionState())
			disconnect();

		if (mPhoneBTBleHandler.isSearch())
			stopDeviceSearch();

		if (mConnectionState != ConnectionState.STATE_NONE
				&& mConnectionState != ConnectionState.STATE_END_DISCOVERING)
			throw new HudTransportException(
					"Expected bluetooth connection state is STATE_NONE, current is "
							+ mConnectionState);

		mHudHardwareNotification = hudHardwareNotification;

		mAlreadyPairedHudDevices = retrievePairedDevices();
		mAlreadyReportedHudDeviceAddresses = new ArrayList<String>();

		setState(ConnectionState.STATE_DISCOVERING);
		mHudHardwareNotification
				.hardwareStatus(
						IPhoneEndpointTransportNotification.HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED,
						IPhoneEndpointTransportNotification.HardwareStatusReason.STARTED);

		mPhoneBTBleHandler.startSearch();
	}

	@Override
	public List<EndpointDeviceInfo> retrievePairedDevices() {
		LOGI("Calling retrievePairedDevices.");

		List<EndpointDeviceInfo> pairedHudDevices = new ArrayList<EndpointDeviceInfo>();
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedBtDevices = btAdapter.getBondedDevices();
		for (BluetoothDevice btDevice : pairedBtDevices) {
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

		if (mPhoneBTBleHandler != null && mPhoneBTBleHandler.isSearch())
			mPhoneBTBleHandler.cancelSearch();

		if (mAlreadyPairedHudDevices != null)
			mAlreadyPairedHudDevices.clear();
		mAlreadyPairedHudDevices = null;

		if (mAlreadyReportedHudDeviceAddresses != null)
			mAlreadyReportedHudDeviceAddresses.clear();
		mAlreadyReportedHudDeviceAddresses = null;

		setState(ConnectionState.STATE_NONE);
	}

	@Override
	public boolean isConnected() {
		if(mPhoneBTBleHandler==null){
			return false;
		}
		return mPhoneBTBleHandler.isConnected();
	}

	@Override
	public boolean isSearching() {
		if(mPhoneBTBleHandler==null){
			return false;
		}
		return mPhoneBTBleHandler.isSearch();
	}

	@Override
	public void disconnect() {
		LOGI("calling disconnect.");
		cancelRetryCycle();
		if (isConnectionState()) {
			deviceDisconnect(DisconnectReason.USER_REQUEST);
		} else if (mPhoneBTBleHandler.isSearch())
			stopDeviceSearch();

		setState(ConnectionState.STATE_NONE);
	}

	@Override
	public void send(ByteBuffer byteBuffer) {
		LOGI("calling send. data length is " + byteBuffer.array().length);
		if (mConnectionState == ConnectionState.STATE_CONNECTED
				&& mPhoneBTBleHandler != null) {
			synchronized (this) {
				mPhoneBTBleHandler.write(byteBuffer.array());
			}
		} else {
			LOGW("Trying to send without being connected.");
		}
	}

	@Override
	public void setLogger(IHaloLogger engineLogger) {
		log = engineLogger;
	}

	private void deviceConnect(EndpointDeviceInfo deviceInfo) {
		LOGI("deviceConnect()");
		if (mPhoneBTBleHandler != null) {
			if (mPhoneBTBleHandler.isSearch()) {
				mPhoneBTBleHandler.cancelSearch();
			}
			if (mPhoneBTBleHandler.isConnected()) {
				mPhoneBTBleHandler.disconnect();
			}
			mPhoneBTBleHandler.connect(deviceInfo.getUniqueAddress());
			setState(ConnectionState.STATE_CONNECTING);
		} else {
			LOGI("mPhoneBTBleHandler is null'");
		}
	}

	private void setState(ConnectionState state) {
		LOGI("State change from " + mConnectionState + " to " + state);
		mConnectionState = state;
	}

	private static enum ConnectionState {
		STATE_NONE, STATE_START_DISCOVERING, STATE_DISCOVERING, STATE_END_DISCOVERING, STATE_START_CONNECTING, STATE_WAITING_BLUETOOTH_STATE, STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH, STATE_CONNECTING, STATE_CONNECTED, STATE_DISCONNECTING
	}

	private static enum DisconnectReason {
		USER_REQUEST, CANNOT_CONNECT, CONNECTION_LOST, INTERNAL_USE
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected() {
		LOGI("calling connected.");

		// stop this searching
		if (mPhoneBTBleHandler != null) {
			mPhoneBTBleHandler.cancelSearch();
		}

		// Store the connected device address in order to connect it directly
		// next time.
		// TODO 是否需要存储以前连接过的设备
		if(mHudDeviceInfo!=null)
		setRecentDevice(mHudDeviceInfo.getUniqueAddress(),
				mHudDeviceInfo.getName());

		setState(ConnectionState.STATE_CONNECTED);

		Message msg = mBluetoothConnectionHandler
				.obtainMessage(PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CONNECTED);
		mBluetoothConnectionHandler.sendMessage(msg);
	}

	private void connectionFailed() {
		if (mConnectionState != ConnectionState.STATE_DISCONNECTING) {
			if (mConnectionState == ConnectionState.STATE_CONNECTED
					|| mConnectionState == ConnectionState.STATE_CONNECTING) {
				mConnectionState = ConnectionState.STATE_CONNECTING;
				enableRetryCycle();
			}
		}
	}

	private void connectionLost() {

		if (mConnectionState != ConnectionState.STATE_NONE
				&& mConnectionState != ConnectionState.STATE_DISCONNECTING) {
			if (mConnectionState == ConnectionState.STATE_CONNECTING
					|| mConnectionState == ConnectionState.STATE_CONNECTED) {
				mConnectionState = ConnectionState.STATE_CONNECTING;
				this.retryBecauseConnectFailed = false;
				enableRetryCycle();
			}
		}
	}

	private boolean isConnectionState() {
		return (mConnectionState == ConnectionState.STATE_START_CONNECTING)
				|| (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE)
				|| (mConnectionState == ConnectionState.STATE_CONNECTING)
				|| (mConnectionState == ConnectionState.STATE_CONNECTED)
				|| (mConnectionState == ConnectionState.STATE_DISCONNECTING);
	}

	private void deviceDisconnect(DisconnectReason reason) {
		LOGI("deviceDisconnect. reason is " + reason);
		ConnectionState oldState = mConnectionState;
		if (mPhoneBTBleHandler != null) {
			setState(ConnectionState.STATE_DISCONNECTING);
			mPhoneBTBleHandler.cancel();
			mPhoneBTBleHandler = null;
		}

		if (reason != DisconnectReason.INTERNAL_USE) {
			setState(ConnectionState.STATE_NONE);

			if (oldState != ConnectionState.STATE_NONE)
				notifyClientDisconnect(reason);
		}
	}

	private void notifyClientDisconnect(DisconnectReason reason) {
		LOGI("notifyClient(" + reason.toString() + ")");
		if ((reason != DisconnectReason.INTERNAL_USE)
				&& (mHudHardwareNotification != null)) {
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

			mHudHardwareNotification.hardwareStatus(hardwareStatus,
					disconnectReason);
		}
	}

	private int getNextRetryTimeout() {
		int retVal = (int) Math.pow(this.connectionRetryCount <= 0 ? 1
				: this.connectionRetryCount, 2.0D) * 1000;

		if (retVal > 10 * 60 * 1000) {
			retVal = 10 * 60 * 1000;
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
		LOGI("enableRetryCycle(): Enqueue \"Connection Retry Message\" after: "
				+ retryTimeout + " ms; ["
				+ (System.currentTimeMillis() - this.connectionRetryDuration)
				/ 1000.0D + "] seconds");
		mRetryHandler.sendEmptyMessageDelayed(0, retryTimeout);
	}

	private void cancelRetryCycle() {
		LOGI("cancelRetryCycle(): Stop \"Connection Retry Message\"! Connection retry duration: ["
				+ (System.currentTimeMillis() - this.connectionRetryDuration)
				/ 1000.0D + "] seconds");
		this.connectionRetryCount = 0;
		this.connectionRetryDuration = 0;
		mRetryHandler.removeMessages(0);
		isRetryingConnect = false;
	}

	public void handleConnectionRetry() {
		synchronized (this) {
			if (this.retryBecauseConnectFailed
					&& this.connectionRetryCount >= 2) {
				// Retry 3 times at most
				cancelRetryCycle();

				// Send a message back to the client
				Message msg = mBluetoothConnectionHandler
						.obtainMessage(PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT);
				mBluetoothConnectionHandler.sendMessage(msg);
			} else {
				this.connectionRetryCount += 1;
				deviceConnect(mHudDeviceInfo);
			}
		}
	}

	private static class ConnectionRetryHandler extends android.os.Handler {
		private final WeakReference<PhoneEndpointTransportBTBle> mService;

		ConnectionRetryHandler(PhoneEndpointTransportBTBle activity) {
			this.mService = new WeakReference<PhoneEndpointTransportBTBle>(
					activity);
		}

		public void handleMessage(Message msg) {
			synchronized ((PhoneEndpointTransportBTBle) this.mService.get()) {
				PhoneEndpointTransportBTBle service = (PhoneEndpointTransportBTBle) this.mService
						.get();
				if (service != null) {
					service.handleConnectionRetry();
				}
			}
		}
	}

	private final Handler mBluetoothConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT:
				if (!isRetryingConnect) // Doesn't notify upper app when it's in
										// retrying status.
					mHudHardwareNotification.hardwareStatus(
							HardwareStatus.DEVICE_DISCONNECTED,
							HardwareStatusReason.FAILED);
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST:
				connectionLost();
				mHudHardwareNotification.hardwareStatus(
						HardwareStatus.DEVICE_DISCONNECTED,
						HardwareStatusReason.LOST_CONNECTION);
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE:
				byte[] data = (byte[]) msg.obj;
				LOGI(String.format("Data arrived. data length is %d",
						data.length));
				mHudHardwareNotification.dataArrival(data);
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_WRITE:
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CONNECTED:
				mHudHardwareNotification.hardwareStatus(
						HardwareStatus.DEVICE_CONNECTED,
						HardwareStatusReason.ENDED);
				connected();
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_START_SCANBLE:
				mHudHardwareNotification.hardwareStatus(
						HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED,
						HardwareStatusReason.STARTED);
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_STOP_SCANBLE:
				break;
			case PhoneBTBleHandler.BT_BLE_CONNECTION_MESSAGE_FOUND_DEVICE:
				EndpointDeviceInfo deviceInfo = (EndpointDeviceInfo) msg.obj;
				if (!mAlreadyReportedHudDeviceAddresses.contains(deviceInfo
						.getUniqueAddress())) {
					mAlreadyReportedHudDeviceAddresses.add(deviceInfo
							.getUniqueAddress());
					mHudHardwareNotification.newDeviceAvailable(deviceInfo);
				}
				mHudHardwareNotification.hardwareStatus(
						HardwareStatus.DEVICE_SEARCH_STATUS_CHANGED,
						HardwareStatusReason.ENDED);
				break;
				

			}
		}
	};

	private void LOGI(String msg) {
		if (log != null)
			log.logI(PhoneEndpointTransportBTBle.class.getName(), msg);
	}

	private void LOGE(String msg) {
		if (log != null)
			log.logE(PhoneEndpointTransportBTBle.class.getName(), msg);
	}

	private void LOGW(String msg) {
		if (log != null)
			log.logW(PhoneEndpointTransportBTBle.class.getName(), msg);
	}
}
