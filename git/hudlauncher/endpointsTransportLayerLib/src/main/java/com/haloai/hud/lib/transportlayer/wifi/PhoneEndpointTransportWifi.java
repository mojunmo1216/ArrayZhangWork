package com.haloai.hud.lib.transportlayer.wifi;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.List;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
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
 * Phone端的wifi实现类
 * 
 * @author NemoHo 11/12/15
 */
public class PhoneEndpointTransportWifi implements
		IPhoneEndpointTransportInterface {
	private static final String SERVER_NAME = "ap_server";
	private static String TAG = "PhoneEndpointTransportWifi";
	private Context mContext;
	private ConnectionState mConnectionState;
	private PhoneWifiHandler mPhoneWifiHandler;
	private IPhoneEndpointTransportNotification mHudNotification;
	private IHaloLogger mLogger;
	private EndpointDeviceInfo mHudDeviceInfo;
	private boolean mRetryBecauseConnectFailed;
	private ConnectionRetryHandler mRetryHandler;
	private int mConnectionRetryCount;
	private long mConnectionRetryDuration;
	private boolean mIsRetryingConnect;

	public PhoneEndpointTransportWifi(Context context) {
		this.mContext = context;
		this.mConnectionState = ConnectionState.STATE_NONE;
		this.mPhoneWifiHandler = new PhoneWifiHandler(mWifiConnectionHandler);
		this.mRetryHandler = new ConnectionRetryHandler(this);
	}

	@Override
	public EndpointDeviceInfo getRecentDevice() {
		// 返回当前连入的AP信息
		EndpointDeviceInfo hudDeviceInfo = new EndpointDeviceInfo();
		String serverIp = getServerIp();
		if (serverIp == null || serverIp.isEmpty()) {
			return null;
		}
		hudDeviceInfo.setUniqueAddress(serverIp);
		hudDeviceInfo.setName(SERVER_NAME);
		return hudDeviceInfo;
	}

	// 获取AP端地址（其实就是网关地址）
	private String getServerIp() {
		try {
			WifiManager manager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcp = manager.getDhcpInfo();
			int ipInt = dhcp.gateway;
			String serverip = String.valueOf(new StringBuilder()
					.append((ipInt & 0xff)).append('.')
					.append((ipInt >> 8) & 0xff).append('.')
					.append((ipInt >> 16) & 0xff).append('.')
					.append(((ipInt >> 24) & 0xff)).toString());
			return serverip;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public void clearRecentDevice() {
		// 针对WIFI，该方法无意义
	}

	@Override
	public void connect(
			final IPhoneEndpointTransportNotification hudHardwareNotification,
			final EndpointDeviceInfo hudDeviceInfo)
			throws HudTransportException {
		// TODO 首先连入HUD AP
//		if (!connect2AP()) {
//			new Thread() {
//				public void run() {
//					SystemClock.sleep(300);
//					connect(hudHardwareNotification, hudDeviceInfo);
//				};
//			}.start();
//			return;
//		}
		LOGI("calling connect.");

		if (hudHardwareNotification == null)
			throw new HudTransportException(
					"calling connect - IHudHardwareNotification parameter cannot be null.");

		if (hudDeviceInfo == null)
			throw new HudTransportException(
					"calling connect - hudDeviceInfo parameter cannot be null.");

		if (isConnectionState()) {
			disconnect();
		}

		if (mConnectionState != ConnectionState.STATE_NONE
				&& mConnectionState != ConnectionState.STATE_END_DISCOVERING) {
			throw new HudTransportException(
					"Expected bluetooth connection state is STATE_NONE, current is "
							+ mConnectionState);
		}

		mHudNotification = hudHardwareNotification;
		mHudDeviceInfo = hudDeviceInfo;

		setState(ConnectionState.STATE_CONNECTING);

		mRetryBecauseConnectFailed = true; // 如果主动发起连接失败，只会重试有限次数；与之相反的是，如果连接成功后连接再丢失则无限重试。
		deviceConnect(hudDeviceInfo);
	}

	private void deviceConnect(final EndpointDeviceInfo hudDeviceInfo) {
		LOGI("deviceConnect()");
		String ip = getServerIp();
		if (ip != null) {
			LOGI("Connecting to: Address: " + ip);
			setState(ConnectionState.STATE_CONNECTING);
			mPhoneWifiHandler.connect(ip);
		}
	}

	/**
	 * 
	 * @param socket
	 * @param device
	 */
	public synchronized void connected() {
		LOGI("calling connected.");

		// Cancel the thread that completed the connection
		// Cancel any thread currently running a connection
		// if (mPhoneWifiHandler != null) {
		// mPhoneWifiHandler.disconnect();
		// mPhoneWifiHandler.cancel();
		// mPhoneWifiHandler = null;
		// }
		//
		// // Start the thread to manage the connection and perform
		// transmissions
		// mPhoneWifiHandler = new PhoneWifiHandler(mWifiConnectionHandler);
		// mPhoneWifiHandler.setLogger(mLogger);
		// mPhoneWifiHandler.start();

		setState(ConnectionState.STATE_CONNECTED);

		// Message msg =
		// mWifiConnectionHandler.obtainMessage(PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECTED);
		// mWifiConnectionHandler.sendMessage(msg);
	}

	private void setState(ConnectionState state) {
		LOGI("State change from " + mConnectionState + " to " + state);
		mConnectionState = state;
	}

	private boolean isConnectionState() {
		return (mConnectionState == ConnectionState.STATE_START_CONNECTING)
				|| (mConnectionState == ConnectionState.STATE_WAITING_BLUETOOTH_STATE)
				|| (mConnectionState == ConnectionState.STATE_CONNECTING)
				|| (mConnectionState == ConnectionState.STATE_CONNECTED)
				|| (mConnectionState == ConnectionState.STATE_DISCONNECTING);
	}

	@Override
	public void searchForHudDevices(
			IPhoneEndpointTransportNotification hudHardwareNotification)
			throws HudTransportException {
		// 对于WIFI，此方法无意义
	}

	@Override
	public List<EndpointDeviceInfo> retrievePairedDevices() {
		// 对于WIFI，此方法无意义
		return null;
	}

	@Override
	public void stopDeviceSearch() {
		// 对于WIFI，此方法无意义
	}

	@Override
	public boolean isConnected() {
		if (mConnectionState == null) {
			return false;
		} else {
			return mPhoneWifiHandler.isConnected();
		}
	}

	@Override
	public boolean isSearching() {
		// TODO 对于WIFI，该方法无意义
		return false;
	}

	@Override
	public void disconnect() {
		LOGI("calling disconnect.");
		// cancelRetryCycle();
		if (isConnectionState()) {
			deviceDisconnect(DisconnectReason.USER_REQUEST);
		}
		setState(ConnectionState.STATE_NONE);
	}

	private void deviceDisconnect(DisconnectReason reason) {
		LOGI("deviceDisconnect. reason is " + reason);
		ConnectionState oldState = mConnectionState;
		if (mPhoneWifiHandler != null) {
			setState(ConnectionState.STATE_DISCONNECTING);
			mPhoneWifiHandler.disconnect();
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
				&& (mHudNotification != null)) {
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

			mHudNotification.hardwareStatus(hardwareStatus, disconnectReason);
		}
	}

	@Override
	public void send(ByteBuffer byteBuffer) {
		LOGI("calling send. data length is " + byteBuffer.array().length);
		if (mConnectionState == ConnectionState.STATE_CONNECTED
				&& mPhoneWifiHandler != null) {
			synchronized (this) {
				try {
					mPhoneWifiHandler.write(byteBuffer.array());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			LOGW("Trying to send without being connected.");
		}
	}

	@Override
	public void setLogger(IHaloLogger engineLogger) {
		this.mLogger = engineLogger;
	}

	private static class ConnectionRetryHandler extends android.os.Handler {

		private final WeakReference<PhoneEndpointTransportWifi> mService;

		ConnectionRetryHandler(PhoneEndpointTransportWifi activity) {
			this.mService = new WeakReference<PhoneEndpointTransportWifi>(
					activity);
		}

		public void handleMessage(Message msg) {
			synchronized ((PhoneEndpointTransportWifi) this.mService.get()) {
				PhoneEndpointTransportWifi service = (PhoneEndpointTransportWifi) this.mService
						.get();
				if (service != null) {
					service.handleConnectionRetry();
				}
			}
		}
	}

	private void handleConnectionRetry() {
		synchronized (this) {
			if (this.mRetryBecauseConnectFailed
					&& this.mConnectionRetryCount >= 5) {
				// 针对WIFI做特殊处理，重复10次
				// Retry 6 times at most
				cancelRetryCycle();

				// Send a message back to the client
				Message msg = mWifiConnectionHandler
						.obtainMessage(PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CANNOT_CONNECT);
				mWifiConnectionHandler.sendMessage(msg);
			} else {
				this.mConnectionRetryCount += 1;
				deviceConnect(mHudDeviceInfo);
			}
		}
	}

	private void cancelRetryCycle() {
		LOGI("cancelRetryCycle(): Stop \"Connection Retry Message\"! Connection retry duration: ["
				+ (System.currentTimeMillis() - this.mConnectionRetryDuration)
				/ 1000.0D + "] seconds");
		this.mConnectionRetryCount = 0;
		this.mConnectionRetryDuration = 0;
		mRetryHandler.removeMessages(0);
		this.mIsRetryingConnect = false;
	}

	// Decoupling the bluetooth connected thread and main ui thread.
	private final Handler mWifiConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CANNOT_CONNECT:
				if (!mIsRetryingConnect) // Doesn't notify upper app when it's
											// in retrying status.
					mHudNotification.hardwareStatus(
							HardwareStatus.DEVICE_DISCONNECTED,
							HardwareStatusReason.FAILED);
				break;
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECTION_LOST:
				connectionLost();
				mHudNotification.hardwareStatus(
						HardwareStatus.DEVICE_DISCONNECTED,
						HardwareStatusReason.LOST_CONNECTION);
				break;
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE:
				byte[] data = (byte[]) msg.obj;
				LOGI(String.format("Data arrived. data length is %d",
						data.length));
				mHudNotification.dataArrival(data);
				break;
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_WRITE:
				break;
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECTED:
				connected();
				mHudNotification.hardwareStatus(
						HardwareStatus.DEVICE_CONNECTED,
						HardwareStatusReason.ENDED);
				break;
			case PhoneWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECT_FAILD:
				mHudNotification.hardwareStatus(
						HardwareStatus.DEVICE_DISCONNECTED,
						HardwareStatusReason.FAILED);
				// if (mConnectionState != ConnectionState.STATE_DISCONNECTING)
				// {
				// if (mConnectionState == ConnectionState.STATE_CONNECTED ||
				// mConnectionState == ConnectionState.STATE_CONNECTING) {
				// mConnectionState = ConnectionState.STATE_CONNECTING;
				// enableRetryCycle();
				// }
				// }
				// break;
			}
		}
	};

	private void connectionLost() {
		if (mConnectionState != ConnectionState.STATE_NONE
				&& mConnectionState != ConnectionState.STATE_DISCONNECTING) {
			if (mConnectionState == ConnectionState.STATE_CONNECTING
					|| mConnectionState == ConnectionState.STATE_CONNECTED) {
				mConnectionState = ConnectionState.STATE_CONNECTING;
				this.mRetryBecauseConnectFailed = false;
				enableRetryCycle();
			}
		}
	}

	private void enableRetryCycle() {

		mIsRetryingConnect = true;
		if (this.mConnectionRetryCount <= 0) {
			LOGI("enableRetryCycle(): Enable connection retry cycle!");
			this.mConnectionRetryDuration = System.currentTimeMillis();
			this.mConnectionRetryCount = 1;
		}
		int retryTimeout = getNextRetryTimeout();
		LOGI("enableRetryCycle(): Enqueue \"Connection Retry Message\" after: "
				+ retryTimeout + " ms; ["
				+ (System.currentTimeMillis() - this.mConnectionRetryDuration)
				/ 1000.0D + "] seconds");
		mRetryHandler.sendEmptyMessageDelayed(0, retryTimeout);

	}

	private int getNextRetryTimeout() {
		// int retVal = (int) Math.pow(this.mConnectionRetryCount <= 0 ? 1
		// : this.mConnectionRetryCount, 2.0D) * 1000;
		//
		// if (retVal > 10 * 60 * 1000) {
		// retVal = 10 * 60 * 1000;
		// }
		// return retVal;
		// 针对WIFI做特殊处理
		return 1000;
	}

	// -------------------------connection info enum start-----------------//
	private static enum ConnectionState {
		STATE_NONE, STATE_START_DISCOVERING, STATE_DISCOVERING, STATE_END_DISCOVERING, STATE_START_CONNECTING, STATE_WAITING_BLUETOOTH_STATE, STATE_WAITING_BLUETOOTH_STATE_FOR_SEARCH, STATE_CONNECTING, STATE_CONNECTED, STATE_DISCONNECTING
	}

	private static enum DisconnectReason {
		USER_REQUEST, CANNOT_CONNECT, CONNECTION_LOST, INTERNAL_USE
	}

	// -------------------------connect info enum end-------------------//

	// -------------------------------Log 辅助类 start---------------------//
	private void LOGI(String msg) {
		if (mLogger != null)
			mLogger.logI(this.getClass().getName(), msg);
	}

	private void LOGE(String msg) {
		if (mLogger != null)
			mLogger.logE(this.getClass().getName(), msg);
	}

	private void LOGW(String msg) {
		if (mLogger != null)
			mLogger.logW(this.getClass().getName(), msg);
	}
	// -------------------------------Log 辅助类 end----------------------//
}
