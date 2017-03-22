package com.haloai.hud.lib.transportlayer.bluetooth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportInterface;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportNotification;
import com.haloai.hud.utils.IHaloLogger;

public class HudEndPointTransportBTSpp implements IHudEndpointTransportInterface {

	protected static final String NAME = "HUD-SPP-SERVER";
	protected static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";//TODO 需要改为其他id，防止别人链接？
	private final String TAG = "HudEndPointTransportSpp";
	private ListenningThread mListenningThread;
	private BluetoothSPPConnectedThread mDataTransferThread;
	private IHudEndpointTransportNotification mNotification;
	private BluetoothDevice btDevice;
	private EndpointDeviceInfo phoneDeviceInfo;
	private IHaloLogger mLogger = null;
	private BluetoothServerSocket serverSocket = null;
	

	public HudEndPointTransportBTSpp() {

		phoneDeviceInfo = new EndpointDeviceInfo();
	}

	@Override
	public void start(IHudEndpointTransportNotification noticiationHandler) throws HudTransportException {
		mNotification = noticiationHandler;
		LOGI("start");
		BeginListening(50);
	}

	@Override
	public EndpointDeviceInfo currentConnectedPhone() {
		return phoneDeviceInfo;
	}

	@Override
	public void stop() throws HudTransportException {
		try {
			LOGI("Try to close the server socket");
			if (mListenningThread != null) {
				mListenningThread.cancel();
				mListenningThread = null;
			}
			LOGI("Try to stop the connected thread.");
			if (mDataTransferThread != null) {
				mDataTransferThread.cancel();
				mDataTransferThread = null;
			}
			btDevice = null;
			phoneDeviceInfo = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public boolean isConnected() {
		if (mDataTransferThread != null)
			return true;
		else
			return false;
	}

	@Override
	public void setLogger(IHaloLogger logger) {
		this.mLogger = logger;
	}

	@Override
	public void send(ByteBuffer byteBuffer) {
		// LOGI("calling send. data length is " + byteBuffer.array().length);
		if (mDataTransferThread != null) {
			synchronized (this) {
				mDataTransferThread.write(byteBuffer.array());
			}
		} else {
			// LOGW("Trying to send without being connected.");
		}

	}

	private class ListenningThread extends Thread {
		private BluetoothServerSocket serverSocket;
		
		public ListenningThread(BluetoothServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}
		public void run() {
			BluetoothSocket socket = null;
			while (true) {
				try {
					LOGI(" serverSocket.accept() begin");
					socket = serverSocket.accept();
					btDevice = socket.getRemoteDevice();
					Message msg = mBluetoothConnectionHandler
							.obtainMessage(BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTED);
					mBluetoothConnectionHandler.sendMessage(msg);
					try {
						LOGI("Got connection from client.  Spawning new data transfer thread");
						mDataTransferThread = new BluetoothSPPConnectedThread(
								socket, mBluetoothConnectionHandler);
						mDataTransferThread.setLogger(mLogger);
						mDataTransferThread.start();
//						break;
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}

				} catch (IOException ioe) {
					LOGI("Server socket happen exception. " + ioe.getMessage());
					mDataTransferThread = null;
					ioe.printStackTrace();
//					BeginListening(50);
					break;
				}
			}
			// Reset the ConnectThread because we're done
			synchronized (HudEndPointTransportBTSpp.this) {
				cancel();
			}
		}

		public void cancel() {
			LOGE("cancel()  " );
			try {
				if (serverSocket != null) {
					serverSocket.close();
					serverSocket = null;
					
				}
			} catch (IOException e) {
				LOGE("close() of connect socket failed " + e);
			}
			
		}
	}

	private void BeginListening(int delayedTime) {	
		// Send request to get serverSocket;
		mBluetoothConnectionHandler.sendEmptyMessageDelayed(BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CREATE_SOCKET,delayedTime);
	}

	// Decoupling the bluetooth connected thread and main ui thread.
	private final Handler mBluetoothConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTION_LOST:
				LOGI("BT Connection Lost. Begin to listen again.");
				//BeginListening(50);
				mNotification.phoneDisconnected();
				break;
			case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_DATA_AVAILABLE:
				mNotification.dataArrival((byte[]) msg.obj);
				LOGI(" BT_CONNECTION_MESSAGE_DATA_AVAILABLE ");
				break;
			case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_WRITE:
				break;
			case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CONNECTED:
				if (phoneDeviceInfo == null)
					phoneDeviceInfo = new EndpointDeviceInfo();
				phoneDeviceInfo.setUniqueAddress(btDevice.getAddress());
				phoneDeviceInfo.setName(btDevice.getName());
				mNotification.phoneConnected(phoneDeviceInfo);
				LOGI(" BT_CONNECTION_MESSAGE_CONNECTED :"+ phoneDeviceInfo.toString());
				break;
			case BluetoothSPPConnectedThread.BT_CONNECTION_MESSAGE_CREATE_SOCKET:
				LOGI("BeginListening : listenUsingInsecureRfcommWithServiceRecord");

				try {
					serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(NAME, UUID.fromString(UUID_STRING));
					if(serverSocket!=null){
						LOGI("serverSocket is ready");
						mListenningThread = new ListenningThread(serverSocket);
						mListenningThread.start();
					}else
						BeginListening(1000);
				} catch (IOException ioe) {
					// Send request to get serverSocket after 1s;
					//Log.e("test", "唤醒有异常");
					BeginListening(1000);
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
