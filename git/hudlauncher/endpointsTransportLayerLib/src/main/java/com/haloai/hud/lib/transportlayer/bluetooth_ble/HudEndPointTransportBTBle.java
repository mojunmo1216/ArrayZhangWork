package com.haloai.hud.lib.transportlayer.bluetooth_ble;

import java.nio.ByteBuffer;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportInterface;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportNotification;
import com.haloai.hud.utils.IHaloLogger;
import static android.bluetooth.le.AdvertiseCallback.*;
/**
 * HUD的蓝牙BLE实现类
 * @author 龙
 *
 */
public class HudEndPointTransportBTBle implements IHudEndpointTransportInterface {

	private static final boolean DEBUG = true;
	protected static final String NAME = "HUD-BLE-SERVER";
	private final String TAG = "HudBTBleHandler";
	private HudBTBleHandler mHudBTBleHandler;
	private IHudEndpointTransportNotification mNotification;
	private EndpointDeviceInfo phoneDeviceInfo;
	private IHaloLogger mLogger = null;
	private Context mContext;
	

	public HudEndPointTransportBTBle(Context context) {
		mContext = context;
		phoneDeviceInfo = new EndpointDeviceInfo();
		mHudBTBleHandler = new HudBTBleHandler(context,mBluetoothConnectionHandler);
	}

	@Override
	public void start(IHudEndpointTransportNotification noticiationHandler) throws HudTransportException {
		mNotification = noticiationHandler;
		LOGI("start");
		beginAdrvertiser(50);
	}

	@Override
	public EndpointDeviceInfo currentConnectedPhone() {
		return phoneDeviceInfo;
	}

	@Override
	public void stop() throws HudTransportException {
		try {
			LOGI("Try to cancel the hud.");
			if (mHudBTBleHandler != null) {
				mHudBTBleHandler.cancel();
				mHudBTBleHandler = null;
			}
			phoneDeviceInfo = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public boolean isConnected() {
		if (mHudBTBleHandler != null)
			return mHudBTBleHandler.isConnected();
		else
			return false;
	}

	@Override
	public void setLogger(IHaloLogger logger) {
		this.mLogger = logger;
	}

	@Override
	public void send(ByteBuffer byteBuffer) {
		if (mHudBTBleHandler != null) {
			synchronized (this) {
				mHudBTBleHandler.write(byteBuffer.array());
			}
		}
	}

	private void beginAdrvertiser(int delayedTime) {	
		mBluetoothConnectionHandler.sendEmptyMessageDelayed(HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CREATE_ADVERTISE,delayedTime);
	}

	private final Handler mBluetoothConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST:
				LOGI("BT ble Connection Lost. Begin to listen again.");
				mNotification.phoneDisconnected();
				break;
			case HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE:
				mNotification.dataArrival((byte[]) msg.obj);
				LOGI(" BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE ");
				break;
			case HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_WRITE:
				break;
			case HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CONNECTED:
				if (phoneDeviceInfo == null){
					phoneDeviceInfo = new EndpointDeviceInfo();
				}
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				phoneDeviceInfo.setUniqueAddress(device.getAddress());
				phoneDeviceInfo.setName(device.getName());
				mNotification.phoneConnected(phoneDeviceInfo);
				LOGI(" BT_BLE_CONNECTION_MESSAGE_CONNECTED :"+ phoneDeviceInfo.toString());
				break;
			case HudBTBleHandler.BT_BLE_CONNECTION_MESSAGE_CREATE_ADVERTISE:
				LOGI("BeginAdvertise");
				mHudBTBleHandler.start();
				break;
			case HudBTBleHandler.BT_CONNECTION_MESSAGE_CREATE_LISTEN_ADVERTISE:
				if (DEBUG) {
//					Toast.makeText(mContext,"广播成功",Toast.LENGTH_LONG).show();
				}
				break;
			case HudBTBleHandler.BT_CONNECTION_MESSAGE_CREATE_FAILED_ADVERTISE:
				int errorCode = (Integer) msg.obj;
				if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE ){
					if (DEBUG) {
						Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
					}
				} else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
					if (DEBUG) {
						Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
					}
				} else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
					if (DEBUG) {
						Log.e(TAG, "advertise_failed_already_started");
					}
				} else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
					if (DEBUG) {
						Log.e(TAG, "advertise_failed_internal_error");
					}
				} else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
					if (DEBUG) {
						Log.e(TAG, "advertise_failed_feature_unsupported");

					}
				}
				break;

			}
		}
	};
	
	private void LOGI(String msg) {
        if (mLogger != null) mLogger.logI(this.getClass().getName(), msg);
    }

    private void LOGE(String msg) {
        if (mLogger != null) mLogger.logE(this.getClass().getName(), msg);
    }

    private void LOGW(String msg) {
        if (mLogger != null) mLogger.logW(this.getClass().getName(), msg);
    }
}
