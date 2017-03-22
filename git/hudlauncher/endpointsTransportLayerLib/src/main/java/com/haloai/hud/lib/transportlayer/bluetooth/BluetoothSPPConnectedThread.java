package com.haloai.hud.lib.transportlayer.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.haloai.hud.utils.IHaloLogger;

/**
 * @author hanyu
 *
 */
public class BluetoothSPPConnectedThread extends Thread {
	
	private static String TAG = "BluetoothSPPConnectedThread";
	 private final BluetoothSocket mmSocket;
     private InputStream mmInStream;
     private OutputStream mmOutStream;
     protected Handler notificationHandler;
     private IHaloLogger mLogger;
    
     public static final int BT_CONNECTION_MESSAGE_DATA_AVAILABLE = 1;
     public static final int BT_CONNECTION_MESSAGE_WRITE = 2;
     public static final int BT_CONNECTION_MESSAGE_CANNOT_CONNECT = 3;
     public static final int BT_CONNECTION_MESSAGE_CONNECTION_LOST = 4;
     public static final int BT_CONNECTION_MESSAGE_CONNECTED = 5;
     public static final int BT_CONNECTION_MESSAGE_CREATE_SOCKET = 6;
     public static final int BT_CONNECTION_MESSAGE_CREATE_LISTEN_SOCKET = 7;
     
     protected static final int CHUNK_SIZE = 4192;
     protected static final int HEADER_MSB = 0x10;
     protected static final int HEADER_LSB = 0x55;
     private int MSG_HEADER_COUNT = 22;
     private int MSG_DIGEST_LENGTH = 16;
     private int MSG_DIGEST_START_INDEX = 6;
     private int MSG_SIZE_START_INDEX = 2;


     public BluetoothSPPConnectedThread(BluetoothSocket socket,Handler parentHandler) {
        // LOGI("create ConnectedThread");
         mmSocket = socket;
         notificationHandler = parentHandler;

         // Get the BluetoothSocket input and output streams
         try {
             mmInStream = socket.getInputStream();
             mmOutStream = socket.getOutputStream();
         } catch (IOException e) {
        	 e.printStackTrace();
         }
     }
     
     public void setLogger(IHaloLogger logger) {
    	 mLogger = logger;
     }

     public void run() {
        byte[] headerBytes = new byte[MSG_HEADER_COUNT];
        byte[] digest = new byte[MSG_DIGEST_LENGTH];
		int headerIndex = 0;
		boolean waitingForHeader = true;
		int remainingSize = 0;
		int totalSize = 0;
		ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
         // Keep listening to the InputStream while connected
        while (true) {
             try {
             	if (waitingForHeader) {
					byte[] header = new byte[1];
					mmInStream.read(header, 0, 1);
					LOGI("Received Header Byte: " + header[0]);
					headerBytes[headerIndex++] = header[0];

					if (headerIndex == MSG_HEADER_COUNT) {
						LOGI(String.format("headerBytes=%s", bytes2HexString(headerBytes)));
						if ((headerBytes[0] == HEADER_MSB) && (headerBytes[1] == HEADER_LSB)) {
							byte[] dataSizeBuffer = Arrays.copyOfRange(headerBytes, MSG_SIZE_START_INDEX, MSG_DIGEST_START_INDEX);
							totalSize = ByteArrayToInt(dataSizeBuffer);
							remainingSize = totalSize;
							
							LOGI("Data size: " + totalSize);
							//System.arraycopy(headerBytes, MSG_DIGEST_START_INDEX, digest, 0, MSG_DIGEST_LENGTH)
							digest = Arrays.copyOfRange(headerBytes, MSG_DIGEST_START_INDEX, MSG_HEADER_COUNT);
							LOGI("Header Received.  Now obtaining length:"+ totalSize + " digest:"+ digest.length+ " | " + bytes2HexString(digest));
							waitingForHeader = false;
							
						} else {
							LOGI("Did not receive correct header");
							mmInStream.read();
							//reset all for next command
							headerIndex = 0;
						 	waitingForHeader = true;
						 	dataOutputStream.reset();
							// handler.sendEmptyMessage(MessageType.INVALID_HEADER);
						}
					}

				} else {
					// Read the data from the stream in chunks
					LOGI("Waiting for data.  Expecting " +remainingSize + " bytes.");
					byte[] bodyDataBuffer = new byte[remainingSize];
//					byte[] bodyDataBuffer = new byte[2000];
//					int bytesRead;
//					while((bytesRead = mmInStream.read(bodyDataBuffer)) != -1){
//						LOGI("Read " + bytesRead + " bytes into bodyDataBuffer");
//						dataOutputStream.write(bodyDataBuffer, 0, bytesRead);
//						remainingSize -= bytesRead;
//					}
					
					int bytesRead = mmInStream.read(bodyDataBuffer);
					LOGI("Read " + bytesRead + " bytes into bodyDataBuffer");
					dataOutputStream.write(bodyDataBuffer, 0, bytesRead);
//					dataOutputStream.write(bodyDataBuffer);
					remainingSize -= bytesRead;

					if (remainingSize == 0) {
						LOGI("Expected data has been received.");
						// check the integrity of the data
						final byte[] data = dataOutputStream.toByteArray();
						LOGI(String.format("Expected data has been received.Data.length=%d", data.length));
						notificationHandler.obtainMessage(BT_CONNECTION_MESSAGE_DATA_AVAILABLE, totalSize, -1, data).sendToTarget();

						if (DigestMatch(data, digest)) {
							LOGI("Digest match.  ok transfer ");
							// Send the obtained bytes to the main thread
							
						} else {
							LOGI("Digest did not match.  Corrupt transfer?");
							// handler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
						}
						//reset all for next command
						headerIndex = 0;
					 	waitingForHeader = true;
					 	dataOutputStream.reset();
					}
				}
             } catch (IOException e) {
            	 LOGI("Connection Lost in connected thread.");
            	 e.printStackTrace();
                 // notificationHandler.obtainMessage(BT_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();
                 break;
             } catch (Exception ex) {
            	 ex.printStackTrace();
            	 break;
             }
         }
         LOGI(" connected thread run end.");
         notificationHandler.obtainMessage(BT_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();    
     }

	

	/**
      * Write to the connected OutStream.
      * @param buffer  The bytes to write
      */
     public void write(byte[] buffer) {
      
         try {
     		// Send the header control first
			mmOutStream.write(HEADER_MSB);
			mmOutStream.write(HEADER_LSB);
			// write size
			mmOutStream.write(IntToByteArray(buffer.length));

			// write digest
			byte[] digest = GetDigest(buffer);
//			LOGI(String.format("digest.length=%d; digest.bytes=%s", digest.length, bytes2HexString(digest)));
//			LOGI("Sending data: " + bytes2HexString(buffer));
			mmOutStream.write(digest);
			mmOutStream.write(buffer);
			//LOGI(String.format("Sending:1055%s%s%s", bytes2HexString(IntToByteArray(buffer.length)), bytes2HexString(digest), bytes2HexString(buffer)));
			mmOutStream.flush(); // end the package to send
			notificationHandler.obtainMessage(BT_CONNECTION_MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			System.out.println("蓝牙第一步：\n");
         } catch (IOException e) {
             //LOGE("Exception during write " + e);
         }
     }

     private static String bytes2HexString(byte[] b) {
         String ret = "";
         for (int i = 0; i < b.length; i++) {
             String hex = Integer.toHexString(b[ i ] & 0xFF);
             if (hex.length() == 1) {
                 hex = '0' + hex;
             }
             ret += hex.toUpperCase();
         }
         return ret;
     }
     
     public void cancel() {
         try {
             mmSocket.close();
         } catch (IOException e) {
             //LOGE("close() of connect socket failed " + e);
         }
     }
     public byte[] IntToByteArray(int a) {
 		byte[] ret = new byte[4];
 		ret[3] = (byte) (a & 0xFF);
 		ret[2] = (byte) ((a >> 8) & 0xFF);
 		ret[1] = (byte) ((a >> 16) & 0xFF);
 		ret[0] = (byte) ((a >> 24) & 0xFF);
 		return ret;
 	}

 	public boolean DigestMatch(byte[] bufferData, byte[] digestData) {
 		return Arrays.equals(GetDigest(bufferData), digestData);
 	}

 	public int ByteArrayToInt(byte[] b) {
 		return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16)
 				+ ((b[0] & 0xFF) << 24);
 	}

 	public byte[] GetDigest(byte[] Data) {
 		try {
 			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
 			return messageDigest.digest(Data);
 		} catch (Exception ex) {
 			//Log.e(TAG, ex.toString());
 			throw new UnsupportedOperationException(
 					"MD5 algorithm not available on this device.");
 		}
 	}

	private void LOGI(String msg) {
        if (mLogger != null) mLogger.logI(this.getClass().getName(), msg);
    }

 }
