package com.haloai.hud.lib.transportlayer.bluetooth_ble;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.haloai.hud.utils.HaloLogger;

public class HudBTBleHandler {

	private static final int HEADER_SIZE = 6;
	private static final boolean BLE_STATUS_LOG = true;
	private static final boolean DEBUG_MODE = true;
	private static final boolean NOTIFY_MODE = true;

	public static final int BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE = 1;//
    public static final int BT_BLE_CONNECTION_MESSAGE_WRITE = 2;
    public static final int BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT = 3;
    public static final int BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST = 4;//
    public static final int BT_BLE_CONNECTION_MESSAGE_CONNECTED = 5;//
    public static final int BT_BLE_CONNECTION_MESSAGE_CREATE_ADVERTISE = 6;//
    public static final int BT_CONNECTION_MESSAGE_CREATE_LISTEN_ADVERTISE = 7;//
    public static final int BT_CONNECTION_MESSAGE_CREATE_FAILED_ADVERTISE = 8;//


	public static final String CHARACTERISTIC_UUID = "496a72bd-d145-4475-b0a2-0150ed160d1f";
	public static final String SERVICE_UUID = "0ec9a2fb-9f2c-414e-8e00-2751927db699";
	public static final String HEART_RATE_SERVICE_UUID = "7355d7cc-791b-4e1f-beb9-d62ecba8c20c";
	private static final int PACKAGE_SIZE = 256;
	private static final byte HEADER_MSB=0x14;
	private static final byte HEADER_LSB=0x43;
	private Context mContext;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
	private BluetoothGattServer mServer;
	private boolean mConnected;

	private BluetoothGattCharacteristic mCharacter;
	//send data member
	private byte[] mSendData;
	private int mSendDataLength;
	private boolean mIsSendingData;
	private int mSendCurrentIndex;
	
	//receive data member
	private ByteBuffer mReceiveData;
	private int mReceiveDataLength;
	private boolean mIsReceivingData;
	private boolean mIsAdverting = true;
	private int mReceiveCurrentIndex;
	
	private Handler mConnectionHandler;
	
	private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
		@Override
		public void onStartSuccess(AdvertiseSettings settingsInEffect) {
			super.onStartSuccess(settingsInEffect);
			mConnectionHandler.obtainMessage(BT_CONNECTION_MESSAGE_CREATE_LISTEN_ADVERTISE).sendToTarget();
			if (BLE_STATUS_LOG){
				LOGI("ADV onStartSuccess");
			}
			mIsAdverting = false;
		}

		@Override
		public void onStartFailure(int errorCode) {
			if (BLE_STATUS_LOG){
				LOGI("ADV onStartFailure");
			}
			Message message = Message.obtain();
			message.what = BT_CONNECTION_MESSAGE_CREATE_FAILED_ADVERTISE;
			message.obj = errorCode;
			mConnectionHandler.sendMessage(message);
			mIsAdverting = true;
			super.onStartFailure(errorCode);


		}
	};
	protected BluetoothDevice mPhoneDevice;

	public HudBTBleHandler(Context context, Handler bluetoothConnectionHandler) {
		this.mContext = context;
		this.mConnectionHandler = bluetoothConnectionHandler;
		init();
	}

	private void init() {
		mBluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		// 开启蓝牙
		mBluetoothAdapter.enable();
		// 获取（周边）蓝牙广告者对象
		mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
	}

	/**
	 * 开始发送广告等待Phone
	 */
	public void start() {
		//1.new一个特征
		mCharacter = new BluetoothGattCharacteristic(
				// 改为写模式
				UUID.fromString(CHARACTERISTIC_UUID),
				BluetoothGattCharacteristic.PROPERTY_WRITE|BluetoothGattCharacteristic.PROPERTY_NOTIFY,
				BluetoothGattCharacteristic.PERMISSION_WRITE|BluetoothGattCharacteristic.PERMISSION_READ);
		// 设置特征对象的值
//		mCharacter.setValue("haloai".getBytes());
		// UUID的设置
		String serviceUUID = SERVICE_UUID;
		// 2.new一个服务：
		BluetoothGattService service = new BluetoothGattService(
				UUID.fromString(serviceUUID),
				BluetoothGattService.SERVICE_TYPE_PRIMARY);
		// 3.把特征添加到服务：
		service.addCharacteristic(mCharacter);
		// 4.获取BluetoothManager：
		mServer = mBluetoothManager.openGattServer(mContext,
				new BluetoothGattServerCallback() {

					@Override
					public void onCharacteristicReadRequest(
							BluetoothDevice device, int requestId, int offset,
							BluetoothGattCharacteristic characteristic) {
						if(mIsSendingData){
							sendData2Phone();
						}
					}

					@Override
					public void onCharacteristicWriteRequest(
							BluetoothDevice device, int requestId,
							BluetoothGattCharacteristic characteristic,
							boolean preparedWrite, boolean responseNeeded,
							int offset, byte[] value) {
						//第一次获取的是head信息
						if(!mIsReceivingData){
							handleHeader(value);//characteristic.getValue()
						}else{
							handleData(value);
						}
						if(BLE_STATUS_LOG){
							LOGI("onCharacteristicWriteRequest called");
						}
						mServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);

					}

					@Override
					public void onConnectionStateChange(BluetoothDevice device,
							int status, int newState) {
						super.onConnectionStateChange(device, status, newState);
						if (newState == BluetoothGattServer.STATE_CONNECTED) {
							Log.i("HudBTBleHandler", "connected");
							HudBTBleHandler.this.mConnected = true;
							mConnectionHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									//连接成功后停止广告
									stopAdvertise();
								}
							},500);
							HudBTBleHandler.this.mPhoneDevice = device;
							mConnectionHandler.obtainMessage(BT_BLE_CONNECTION_MESSAGE_CONNECTED,-1,-1,device).sendToTarget();
						} else if (newState == BluetoothGattServer.STATE_DISCONNECTED) {
							Log.i("HudBTBleHandler", "disconnected");
							HudBTBleHandler.this.mConnected = false;
							if(status == BluetoothGattServer.STATE_CONNECTING){
								mConnectionHandler.obtainMessage(BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT).sendToTarget();
							}else{
								mConnectionHandler.obtainMessage(BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();
							}
							clearReceiveBuffer();
							startAdvertise();
						}else {
							Log.i("HudBTBleHandler", "onConnectionStateChange,newState is "+newState);
						}
					}
				});
		// 6.把service添加到周边
		mServer.addService(service);
		// 7.开始发广播
		startAdvertise();
	}

	private void startAdvertise() {
		if (mBluetoothLeAdvertiser == null) {
			init();
		}
		mBluetoothLeAdvertiser.startAdvertising(
				createAdvSettings(true, 100000), 
				createAdvertiseData(),
				mAdvertiseCallback);
	}

	/**
	 * 创建广告的数据对象（包括UUID，服务数据）
	 * 
	 * @return 返回AdvertiseData对象
	 */
	private AdvertiseData createAdvertiseData() {
		AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
		mDataBuilder.setIncludeDeviceName(true);
		mDataBuilder.addServiceUuid(ParcelUuid.fromString(SERVICE_UUID));
		// TODO: 16/6/12 sen 去年广播心跳UUID服务，可广播较长的设备名，否则只能广播两个字节
		/*ParcelUuid heart_uuid = ParcelUuid.fromString(HEART_RATE_SERVICE_UUID);
		mDataBuilder.addServiceUuid(heart_uuid);
		byte[] serviceData = { (byte) 0x00, (byte) 0x01 };
		mDataBuilder.addServiceData(heart_uuid, serviceData);*/
		AdvertiseData mAdvertiseData = mDataBuilder.build();
		if (mAdvertiseData == null) {

		}
		return mAdvertiseData;
	}

	/**
	 * 创建AdvertiseSettings，作为广告的参数信息
	 * 
	 * @param connectable
	 *            是否可被连接
	 * @param timeoutMillis
	 *            超时时间
	 * @return 返回AdvertiseSettings对象
	 */
	private AdvertiseSettings createAdvSettings(boolean connectable,
			int timeoutMillis) {
		AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
		mSettingsbuilder
				.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
		mSettingsbuilder.setConnectable(connectable);
		mSettingsbuilder.setTimeout(timeoutMillis);
		mSettingsbuilder
				.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
		AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
		if (mAdvertiseSettings == null) {

		}
		return mAdvertiseSettings;
	}

	/**
	 * 取消广告并关闭与Phone端的连接
	 */
	public void cancel() {
		// 停止广播
		stopAdvertise();

		// 关闭连接
		if (mServer != null) {
			mServer.close();
		}
	}

	private void stopAdvertise() {
		if (mBluetoothLeAdvertiser != null) {
			mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
			mBluetoothLeAdvertiser = null;
		}
	}

	/**
	 * 将数据写到Phone端
	 * 
	 * @param array
	 */
	public void write(byte[] array) {
		this.mSendData = array;
		this.mSendCurrentIndex = 0;
		this.mSendDataLength = mSendData.length;
		this.mIsSendingData = true;
		sendHeader2Phone();
		if (DEBUG_MODE){
			LOGI("BLE底层发送的数据为,size = "+array.length+",byte[]="+ Arrays.toString(array));
		}
		if(NOTIFY_MODE){
			while (mIsSendingData){
				sendData2Phone();
			}
		}
	}

	private void sendHeader2Phone() {
		byte[] header = new byte[6];
		header[0]=HEADER_MSB;
		header[1]=HEADER_LSB;
		byte[] bytes = int2Bytes(mSendDataLength);
		header[2]= bytes[0];
		header[3]= bytes[1];
		header[4]= bytes[2];
		header[5]= bytes[3];
		mCharacter.setValue(header);
		//TODO 不确定是否为该方式来通知Phone回调
		mServer.notifyCharacteristicChanged(mPhoneDevice, mCharacter, false);
		if (DEBUG_MODE){
			LOGI("sendHeader2Phone ,size = "+header.length+",data = "+Arrays.toString(header));
		}
	}

	private void sendData2Phone() {
		byte[] sendBytes = null;
		if (mSendCurrentIndex + PACKAGE_SIZE < mSendData.length) {
			sendBytes = Arrays.copyOfRange(mSendData, mSendCurrentIndex,mSendCurrentIndex+PACKAGE_SIZE);
			mSendCurrentIndex += PACKAGE_SIZE;
		} else {
			sendBytes = Arrays.copyOfRange(mSendData, mSendCurrentIndex,mSendData.length);
			mSendCurrentIndex = 0;
			mSendData = null;
			mSendDataLength = 0;
			mIsSendingData = false;
		}
		if (DEBUG_MODE){
			LOGI("sendData2Phone ,size = "+sendBytes.length+",data = "+Arrays.toString(sendBytes));
		}
		mCharacter.setValue(sendBytes);
		//TODO 不确定是否为该方式来通知Phone回调
		// confirm表示通知的方式
		mServer.notifyCharacteristicChanged(mPhoneDevice, mCharacter, false);
	}


	private void handleHeader(byte[] value) {
		if (DEBUG_MODE) {
			LOGI("BLE底层收到包头数据为：size="+value.length+",bytes[] ="+ Arrays.toString(value));
		}
		if (value.length >= HEADER_SIZE) {
			byte tag1 = value[0];
			byte tag2 = value[1];
			if (tag1 == HEADER_MSB && tag2 == HEADER_LSB) {
				mIsReceivingData = true;
				mReceiveDataLength = bytes2Int(Arrays.copyOfRange(value, 2, 6));
				if(value.length > HEADER_SIZE){
					handleData(Arrays.copyOfRange(value, HEADER_SIZE, value.length));
				}
//				LOGI("正在继续接收完整的包数据,length = " +mReceiveDataLength+ ",byte[] is " + Arrays.toString(Arrays.copyOfRange(value, 2, 6)));
			}
		}
	}
	
	private void handleData(byte[] data){
		if (mReceiveData == null) {
			mReceiveData = ByteBuffer.allocate(mReceiveDataLength);
		}
		if(mReceiveCurrentIndex+data.length>=mReceiveDataLength){
			int length = mReceiveDataLength-mReceiveCurrentIndex;
			mReceiveData.put(Arrays.copyOf(data,length));
		}else {
			mReceiveData.put(data);
		}
		if (DEBUG_MODE){
			LOGI("BLE底层收到数据为：size= "+data.length+",byte[] = "+ Arrays.toString(data));
		}
		mReceiveCurrentIndex+=data.length;
		if(mReceiveCurrentIndex >= mReceiveDataLength){
			if (DEBUG_MODE) {
				LOGI("BLE底层收到一个完整的包" + ",byte[] = " + Arrays.toString(mReceiveData.array()));
			}
			mConnectionHandler.obtainMessage(BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE, mReceiveDataLength, -1, mReceiveData.array()).sendToTarget();
			clearReceiveBuffer();
		}


	}
	/**
	 * 清除接收数据
	 * **/
	private void clearReceiveBuffer(){
		mIsReceivingData=false;
		mReceiveDataLength = 0;
		mReceiveCurrentIndex = 0;
//			mReceiveData.clear();
		mReceiveData = null;
	}
	
	/**
	 * 判断是否有Phone端连入
	 * 
	 * @return true表示有Phone连入
	 */
	public boolean isConnected() {
		return mConnected;
	}
	
	//---------------------------------------------------------工具 方法 start-----------------------------------------//
	
	private byte[] int2Bytes(int a) {
 		byte[] ret = new byte[4];
 		ret[3] = (byte) (a & 0xFF);
 		ret[2] = (byte) ((a >> 8) & 0xFF);
 		ret[1] = (byte) ((a >> 16) & 0xFF);
 		ret[0] = (byte) ((a >> 24) & 0xFF);
 		return ret;
 	}

	private int bytes2Int(byte[] b) {
 		return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16)
 				+ ((b[0] & 0xFF) << 24);
 	}
	
	//---------------------------------------------------------工具 方法 end-----------------------------------------//
	private void LOGI(String msg){
		if (DEBUG_MODE) {
			HaloLogger.logI("HudBTBleHandler", msg);
		}
	}

}
