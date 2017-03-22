package com.haloai.hud.lib.transportlayer.wifi;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.os.Handler;

import com.haloai.hud.utils.IHaloLogger;

/**
 * Phone端WIFI传输的实现类(client)
 * 
 * @author NemoHo 11/12/15
 */
public class PhoneWifiHandler {
	private static String TAG = "PhoneWifiHandler";

	// connect message constant
	public static final int WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE = 1;
	public static final int WIFI_CONNECTION_MESSAGE_WRITE = 2;
	public static final int WIFI_CONNECTION_MESSAGE_CANNOT_CONNECT = 3;
	public static final int WIFI_CONNECTION_MESSAGE_CONNECTION_LOST = 4;
	public static final int WIFI_CONNECTION_MESSAGE_CONNECTED = 5;
	public static final int WIFI_CONNECTION_MESSAGE_CREATE_SESSION = 6;
	public static final int WIFI_CONNECTION_MESSAGE_CREATE_LISTEN_SESSION = 7;
	public static final int WIFI_CONNECTION_MESSAGE_CONNECT_FAILD = 8;

	//can use port area
	private static  int SERVER_LISTEN_PORT = 1234;

	private static final int LENGTH_START = 0;
	private static final int LENGTH_END = 4;
	private static final int DIGEST_START = 4;
	private static final int DIGEST_END = 20;
	private static final int REAL_DATA_START = 20;
	

	private Handler mNotificationHandler;
	private NioSocketConnector mConnector;

	private IHaloLogger mLogger;

	private IoSession mSession;
	private boolean mIsConnected;
	private IoHandlerAdapter mIoHandler = new IoHandlerAdapter() {
		@Override
		public void sessionClosed(IoSession session) throws Exception {
			super.sessionClosed(session);
			mIsConnected = false;
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("one Client Connection"
					+ session.getRemoteAddress());
			mSession = session;
			mIsConnected = true;
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_CONNECTED).sendToTarget();
			// 在连接建立后，发送标识码到服务端，否则连接会被服务端断开
			// TODO 测试字节数组，先不管验证
			// session.write("haloai");
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			super.exceptionCaught(session, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			System.out.println("receive data from server");
			IoBuffer buf = (IoBuffer) message;
			byte[] array = buf.array();
			int dataLength = bytes2Int(Arrays.copyOfRange(array, LENGTH_START, LENGTH_END));
			byte[] digest = Arrays.copyOfRange(array, DIGEST_START, DIGEST_END);
			byte[] data = Arrays.copyOfRange(array, REAL_DATA_START, array.length);
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE, dataLength, -1,
					data).sendToTarget();
			if (DigestMatch(array, digest)) {
				LOGI("Digest match.  ok transfer ");
			} else {
				LOGI("Digest did not match.  Corrupt transfer?");
			}
		}

	};

	public PhoneWifiHandler(Handler wifiConnectionHandler) {
		this.mNotificationHandler = wifiConnectionHandler;
	}

	/**
	 * 初始化客户端并连接到服务端HUD
	 * 
	 * @param ip
	 *            AP 的地址（其实就是网关）
	 */
	public void connect(final String ip) {
		new Thread() {
			public void run() {
				mConnector = new NioSocketConnector();
				mConnector.getFilterChain().addLast("myChin_byte",
						new ProtocolCodecFilter(new ByteArrayCodecFactory()));

				mConnector.setHandler(mIoHandler);

				mConnector.setConnectTimeout(30);

				try {
					ConnectFuture cf = mConnector
							.connect(new InetSocketAddress(ip,
									SERVER_LISTEN_PORT));
					cf.awaitUninterruptibly();
					cf.getSession().getCloseFuture().awaitUninterruptibly();
				} catch (Exception e) {
					e.printStackTrace();
					mNotificationHandler.obtainMessage(WIFI_CONNECTION_MESSAGE_CONNECT_FAILD).sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 获取当前的客户端的连接状态
	 * 
	 * @return true表示该客户端连接上了服务端
	 */
	public boolean isConnected() {
		return mIsConnected;
	}

	/**
	 * 断开与服务端的连接
	 */
	public void disconnect() {
		if (mSession != null) {
			mSession.close(true);
			mSession = null;
		}
		if (mConnector != null) {
			mConnector.dispose();
			mConnector = null;
		}
	}

	/**
	 * 向服务端HUD写数据
	 * 要写到服务端的数据的byte[]表示形式
	 */
	public void write(byte[] buffer) {
		if (mSession != null) {
			byte[] digest = getDigest(buffer);
			//IoBuffer在allocate之后就不能再修改大小了，因此大小设置必须正确
			IoBuffer io = IoBuffer.allocate((int) buffer.length + 4 + digest.length);
			io.put(int2Bytes((int) buffer.length+digest.length));
			io.put(digest);
			io.put(buffer);
			io.flip();
			mSession.write(io);
		}
	}
	
	/**
	 * 取消客户端到服务端的连接
	 */
	public void cancel() {
		if (mSession != null) {
			disconnect();
		}
		if (mConnector != null) {
			mConnector.dispose();
			mConnector = null;
		}
	}

	public void setLogger(IHaloLogger logger) {
		this.mLogger = logger;
	}

	/**
	 * 该方法在蓝牙中用于在单独线程中进行数据的读写操作，貌似次数不需要
	 */
	public void start() {

	}

	// -------------------------辅助 工具 方法---------------------------//
	private byte[] int2Bytes(int num) {
		byte[] b = new byte[4];
		// int mask=0xff;
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	private static int bytes2Int(byte[] b) {
		byte[] a = new byte[4];
		int i = a.length - 1, j = b.length - 1;
		for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
			if (j >= 0)
				a[i] = b[j];
			else
				a[i] = 0;// 如果b.length不足4,则将高位补0
		}
		int v0 = (a[0] & 0xff) << 24;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		int v1 = (a[1] & 0xff) << 16;
		int v2 = (a[2] & 0xff) << 8;
		int v3 = (a[3] & 0xff);
		return v0 + v1 + v2 + v3;
	}

	private byte[] getDigest(byte[] array) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			return messageDigest.digest(array);
		} catch (Exception ex) {
			// Log.e(TAG, ex.toString());
			throw new UnsupportedOperationException(
					"MD5 algorithm not available on this device.");
		}
	}

	public boolean DigestMatch(byte[] bufferData, byte[] digestData) {
		return Arrays.equals(getDigest(bufferData), digestData);
	}

	private void LOGI(String msg) {
		if (mLogger != null)
			mLogger.logI(this.getClass().getName(), msg);
	}
}
