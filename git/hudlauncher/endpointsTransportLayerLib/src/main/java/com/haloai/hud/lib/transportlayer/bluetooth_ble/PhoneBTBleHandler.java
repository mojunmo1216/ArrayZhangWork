package com.haloai.hud.lib.transportlayer.bluetooth_ble;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;

/**
 * Phone端BLE处理类
 * 
 * @author 龙
 */
public class PhoneBTBleHandler {

	public static final int BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE = 1;
	public static final int BT_BLE_CONNECTION_MESSAGE_WRITE = 2;
	public static final int BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT = 3;
	public static final int BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST = 4;
	public static final int BT_BLE_CONNECTION_MESSAGE_CONNECTED = 5;
	public static final int BT_BLE_CONNECTION_MESSAGE_START_SCANBLE = 6;
	public static final int BT_BLE_CONNECTION_MESSAGE_STOP_SCANBLE = 7;
	public static final int BT_BLE_CONNECTION_MESSAGE_FOUND_DEVICE = 8;

	private static final byte HEADER_MSB = 0x14;
	private static final byte HEADER_LSB = 0x43;

	private static final String TAG = "PhoneBTBleHandler";

	private BluetoothAdapter mBluetoothAdapter;
	private Context mContext;
	private Handler mConnectionHandler;

	// UUID
	public static final String CHARACTERISTIC_UUID = "496a72bd-d145-4475-b0a2-0150ed160d1f";
	public static final String SERVICE_UUID = "0ec9a2fb-9f2c-414e-8e00-2751927db699";
	public static final String HEART_RATE_SERVICE_UUID = "7355d7cc-791b-4e1f-beb9-d62ecba8c20c";

	// status
	private boolean isSearching;
	private boolean isConnected;
	private byte[] mSendData;
	private int mSendCurrentIndex;
	private int mSendDataLength;
	private boolean mIsSendingData;
	private int PACKAGE_SIZE = 20;
	private boolean mIsReceivingData;
	private int mReceiveDataLength;
	private ByteBuffer mReceiveData;
	private int mReceiveCurrentIndex;
	private BluetoothGatt mGatt;
	private BluetoothDevice mDevice;
	private BluetoothGattCharacteristic mCharacteristic;

	private LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (device == null) {
				return;
			}
			EndpointDeviceInfo scan = new EndpointDeviceInfo();
			scan.setUniqueAddress(device.getAddress());
			scan.setName(device.getName());
			mConnectionHandler.obtainMessage(
					BT_BLE_CONNECTION_MESSAGE_FOUND_DEVICE, -1, -1, scan)
					.sendToTarget();
			cancelSearch();
		}
	};

	private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if (newState == BluetoothGattServer.STATE_CONNECTED) {
				isConnected = true;
				mGatt.discoverServices();
			} else if (newState == BluetoothGattServer.STATE_DISCONNECTED) {
				isConnected = false;
				if (status == BluetoothGattServer.STATE_CONNECTING) {
					mConnectionHandler.obtainMessage(
							BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT)
							.sendToTarget();
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			BluetoothGattService service = mGatt.getService(UUID
					.fromString(SERVICE_UUID));
			
			mCharacteristic = service.getCharacteristic(UUID
					.fromString(CHARACTERISTIC_UUID));
			mGatt.setCharacteristicNotification(mCharacteristic, true);
			// 到此算是连接BLE成功
			if(mCharacteristic!=null){
				mConnectionHandler.obtainMessage(
						BT_BLE_CONNECTION_MESSAGE_CONNECTED).sendToTarget();
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			if (mIsSendingData) {
				sendData2HUD();
			}
		}
		
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			// 第一次获取的是head信息
			if (!mIsReceivingData) {
				handleHeader(characteristic.getValue());
			} else {
				handleData(characteristic.getValue());
			}
		}

	};

	public PhoneBTBleHandler(Context context, Handler bluetoothConnectionHandler) {
		this.mContext = context;
		this.mConnectionHandler = bluetoothConnectionHandler;
		final BluetoothManager mBluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		mBluetoothAdapter.enable();
	}

	/**
	 * 停止扫描，断开与HUD端的连接
	 */
	public void cancel() {
		cancelSearch();
		disconnect();
	}

	/**
	 * 判断当前Phone是否处于扫描状态
	 * 
	 * @return true表示处于扫描状态
	 */
	public boolean isSearch() {
		return isSearching;
	}

	/**
	 * 停止扫描BLE设置
	 */
	public void cancelSearch() {
		if (mBluetoothAdapter != null)
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		mConnectionHandler.obtainMessage(
				BT_BLE_CONNECTION_MESSAGE_STOP_SCANBLE).sendToTarget();
	}

	/**
	 * 判断当前Phone是否与HUD相连
	 * 
	 * @return true表示Phone与HUD处于连接状态
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * 向HUD端写数据
	 * 
	 * @param array
	 *            要写入的byte[]
	 */
	public void write(byte[] array) {
		this.mSendData = array;
		this.mSendCurrentIndex = 0;
		this.mSendDataLength = array.length;
		this.mIsSendingData = true;
		BluetoothGattService service = mGatt.getService(UUID
				.fromString(SERVICE_UUID));
		mCharacteristic = service.getCharacteristic(UUID
				.fromString(CHARACTERISTIC_UUID));
		mGatt.setCharacteristicNotification(mCharacteristic, true);
		sendHeader2Hud();
	}

	private void sendHeader2Hud() {
		if (mCharacteristic == null) {
			return;
		}
		byte[] header = new byte[6];
		header[0] = HEADER_MSB;
		header[1] = HEADER_LSB;
		byte[] bytes = int2Bytes(mSendDataLength);
		header[2] = bytes[0];
		header[3] = bytes[1];
		header[4] = bytes[2];
		header[5] = bytes[3];
		mCharacteristic.setValue(header);
		boolean bool = mGatt.writeCharacteristic(mCharacteristic);
		int a=2;
	}

	private void sendData2HUD() {
		if (mCharacteristic == null) {
			return;
		}
		if (mSendCurrentIndex + PACKAGE_SIZE < mSendData.length) {
			mCharacteristic.setValue(ByteBuffer.wrap(mSendData,
					mSendCurrentIndex, PACKAGE_SIZE).array());
			mSendCurrentIndex += PACKAGE_SIZE;
		} else {
			mCharacteristic.setValue(ByteBuffer.wrap(mSendData,
					mSendCurrentIndex, mSendData.length - mSendCurrentIndex)
					.array());
			mSendCurrentIndex = 0;
			mSendData = null;
			mSendDataLength = 0;
			mIsSendingData = false;
		}
		mGatt.writeCharacteristic(mCharacteristic);
	}

	private void handleHeader(byte[] value) {
		byte tag1 = value[0];
		byte tag2 = value[1];
		if (tag1 == HEADER_MSB && tag2 == HEADER_LSB) {
			mIsReceivingData = true;
			mReceiveDataLength = bytes2Int(Arrays.copyOfRange(value, 2, 6));
		}
	}

	private void handleData(byte[] data) {
		if (mReceiveData == null) {
			mReceiveData = ByteBuffer.allocate(mReceiveDataLength);
		}
		if(mReceiveCurrentIndex+data.length>=mReceiveDataLength){
			int length = mReceiveDataLength-mReceiveCurrentIndex;
			mReceiveData.put(Arrays.copyOf(data,length));
		}else {
			mReceiveData.put(data);
		}
		mReceiveCurrentIndex += data.length;
		if (mReceiveCurrentIndex >= mReceiveDataLength) {
			mConnectionHandler.obtainMessage(
					BT_BLE_CONNECTION_MESSAGE_DATA_AVAILABLE,
					mReceiveDataLength, -1, mReceiveData.array())
					.sendToTarget();
			mIsReceivingData = false;
			mReceiveDataLength = 0;
			mReceiveCurrentIndex = 0;
			mReceiveData = null;
		}
	}

	/**
	 * 开始扫描BLE设备
	 */
	public void startSearch() {
		if (mBluetoothAdapter != null)
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		mConnectionHandler.obtainMessage(
				BT_BLE_CONNECTION_MESSAGE_START_SCANBLE).sendToTarget();
	}

	/**
	 * 断开到HUD的连接
	 */
	public void disconnect() {
		if (mGatt != null) {
			mGatt.disconnect();
			mConnectionHandler.obtainMessage(
					BT_BLE_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();
		}
	}

	public void connect(String address) {
		mDevice = mBluetoothAdapter.getRemoteDevice(address);
		if (mDevice == null) {
			Log.e(TAG, "device is null");
			mConnectionHandler.obtainMessage(
					BT_BLE_CONNECTION_MESSAGE_CANNOT_CONNECT).sendToTarget();
			return;
		}
		mGatt = mDevice.connectGatt(mContext, false, mBluetoothGattCallback);
	}

	// ---------------------------------------------------------工具 方法 start-----------------------------------------//

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

	// ---------------------------------------------------------工具 方法
	// end-----------------------------------------//

}
