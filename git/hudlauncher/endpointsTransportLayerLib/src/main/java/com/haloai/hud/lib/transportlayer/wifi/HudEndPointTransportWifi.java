package com.haloai.hud.lib.transportlayer.wifi;

import java.nio.ByteBuffer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportInterface;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportNotification;
import com.haloai.hud.utils.IHaloLogger;

/**
 * HUD端的wifi实现类
 * 
 * @author NemoHo 11/12/15
 */
public class HudEndPointTransportWifi implements IHudEndpointTransportInterface {
	private static String TAG = "HudEndPointTransportWifi";
	private EndpointDeviceInfo mPhoneDeviceInfo;
	private IHaloLogger mLogger;
	private IHudEndpointTransportNotification mPhoneNotification;
	private HudWifiHandler mHudWifiHandler;
	private Context mContext;
	
	public HudEndPointTransportWifi(Context context) {
		mPhoneDeviceInfo = new EndpointDeviceInfo();
		mHudWifiHandler = new HudWifiHandler(mWifiConnectionHandler);
		this.mContext = context;
	}

	@Override
	public void start(IHudEndpointTransportNotification noticiationHandler)
			throws HudTransportException {
		mPhoneNotification = noticiationHandler;
		LOGI("start");
		mHudWifiHandler.start();
	}

	@Override
	public void stop() throws HudTransportException {
		try {
			LOGI("Try to close the server socket and disconnect to client");
			if (mHudWifiHandler != null) {
				mHudWifiHandler.stop();
				mHudWifiHandler.cancel();
				mHudWifiHandler = null;
			}
			mPhoneDeviceInfo = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public EndpointDeviceInfo currentConnectedPhone() {
		return mPhoneDeviceInfo;
	}

	@Override
	public boolean isConnected() {
		if(mHudWifiHandler==null){
			return false;
		}
		return mHudWifiHandler.isConnected();
	}

	@Override
	public void send(ByteBuffer byteBuffer) {
		if(mHudWifiHandler!=null){
			synchronized (this) {
				mHudWifiHandler.write(byteBuffer.array());
			}
		}
	}

	@Override
	public void setLogger(IHaloLogger logger) {
		mLogger=logger;
		if(mHudWifiHandler!=null){
			mHudWifiHandler.setLogger(logger);
		}
	}
	
	
		private final Handler mWifiConnectionHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {

				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECTION_LOST:
					LOGI("WIFI Connection Lost. Begin to listen again.");
					//BeginListening(50);
					mPhoneNotification.phoneDisconnected();
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE:
					mPhoneNotification.dataArrival((byte[]) msg.obj);
					LOGI("WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE ");
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_WRITE:
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_CONNECTED:
					if (mPhoneDeviceInfo == null)
						mPhoneDeviceInfo = new EndpointDeviceInfo();
					mPhoneDeviceInfo.setUniqueAddress("gatway"); //ignore this field. Phone(Client) endpoint will retrieve the server ip address via AP property.
					mPhoneDeviceInfo.setName("ap_server");
					mPhoneNotification.phoneConnected(mPhoneDeviceInfo);
					LOGI("WIFI_CONNECTION_MESSAGE_CONNECTED :"+ mPhoneDeviceInfo.toString());
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_CREATE_SESSION:
					LOGI("BeginListening : listenUsingInsecureRfcommWithServiceRecord");

					mHudWifiHandler=new HudWifiHandler(mWifiConnectionHandler);
					if(mHudWifiHandler!=null){
						LOGI("wifi server is ready");
						mHudWifiHandler.start();
					}else
						BeginListening(1000);
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_BIND_SUCCESS:
//					Toast.makeText(mContext, "wifi server bind success", 0).show();
					break;
				case HudWifiHandler.WIFI_CONNECTION_MESSAGE_BIND_START:
//					Toast.makeText(mContext, "wifi server bind start", 0).show();
					break;
				}
			}
		};
		
		private void BeginListening(int delayedTime) {	
			// Send request to get serverSocket;
			mWifiConnectionHandler.sendEmptyMessageDelayed(HudWifiHandler.WIFI_CONNECTION_MESSAGE_CREATE_SESSION,delayedTime);
		}
	
	
	//-------------------------------Log 辅助类    start-------------------------------------//
	private void LOGI(String msg) {
        if (mLogger != null) mLogger.logI(this.getClass().getName(), msg);
    }

    private void LOGE(String msg) {
        if (mLogger != null) mLogger.logE(this.getClass().getName(), msg);
    }

    private void LOGW(String msg) {
        if (mLogger != null) mLogger.logW(this.getClass().getName(), msg);
    }
    //-------------------------------Log 辅助类    end-------------------------------------//
}
