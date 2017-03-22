package com.haloai.hud.lib.transportlayer.wifi;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import android.os.Handler;
import android.util.Log;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.hud.utils.IHaloLogger;

/**
 * Hud端WIFI传输的实现类(Server) 暂时不考虑验证问题
 * 
 * @author NemoHo 11/12/15
 */
public class HudWifiHandler {
	private static String TAG = "HudWifiHandler";

	// connect message constant
	public static final int WIFI_CONNECTION_MESSAGE_DATA_AVAILABLE = 1;
	public static final int WIFI_CONNECTION_MESSAGE_WRITE = 2;
	public static final int WIFI_CONNECTION_MESSAGE_CANNOT_CONNECT = 3;
	public static final int WIFI_CONNECTION_MESSAGE_CONNECTION_LOST = 4;
	public static final int WIFI_CONNECTION_MESSAGE_CONNECTED = 5;
	public static final int WIFI_CONNECTION_MESSAGE_CREATE_SESSION = 6;
	public static final int WIFI_CONNECTION_MESSAGE_CREATE_LISTEN_SESSION = 7;
	public static final int WIFI_CONNECTION_MESSAGE_BIND_SUCCESS = 8;
	protected static final int WIFI_CONNECTION_MESSAGE_BIND_START = 9;

	//can use port area
	private static  int SERVER_LISTEN_PORT = 1234;
	
	private static final int LENGTH_START = 0;
	private static final int LENGTH_END = 4;
	private static final int DIGEST_START = 4;
	private static final int DIGEST_END = 20;
	private static final int REAL_DATA_START = 20;


	protected NioSocketAcceptor mAcceptor;

	private boolean mHasClientConnected = false;

	protected IoSession mSession;
	private IoHandlerAdapter mIoHandler = new IoHandlerAdapter() {

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			super.sessionOpened(session);
			if(!mHasClientConnected){
			mHasClientConnected = true;
			HudWifiHandler.this.mSession = session;
			}else{
				session.close(true);
			}
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			System.out.println("receive data from client");
			IoBuffer buf = (IoBuffer) message;
			// 前四位表示长度，后面的length-4才是正式数据
			byte[] array = buf.array();
			int dataLength = bytes2Int(Arrays.copyOfRange(array, LENGTH_START, LENGTH_END));
			// TODO digest的长度确定，也就是下面的两个20不确定
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

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("客户端与服务端断开连接.....");
			mHasClientConnected = false;
			LOGI(" connected end.");
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_CONNECTION_LOST).sendToTarget();
		}
		
		
	};

	private Handler mNotificationHandler;

	private IHaloLogger mLogger;

	public HudWifiHandler(Handler handler) {
		this.mNotificationHandler = handler;
	}

	/**
	 * 该方法初始化Hud端Server，同时绑定端口进行连接监听
	 */
	public void start() {
		stop();
		cancel();
		new Thread() {
			public void run() {
				mNotificationHandler.obtainMessage(
						WIFI_CONNECTION_MESSAGE_BIND_START)
						.sendToTarget();
				mAcceptor = new NioSocketAcceptor();

				// 设置Filter链
				mAcceptor.getFilterChain().addLast("logger",
						new LoggingFilter());
				mAcceptor.getFilterChain().addLast("myChin_byte",
						new ProtocolCodecFilter(new ByteArrayCodecFactory()));
				mAcceptor.setHandler(mIoHandler);
				// 设置服务监听端口可以被重用
				mAcceptor.setReuseAddress(true);
				mAcceptor.getSessionConfig().setReuseAddress(true);
				// 设置接收缓存区大小
				mAcceptor.getSessionConfig().setReadBufferSize(2048 * 10);
				mAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
						10);
				bindPort();
			};
		}.start();
	}
	
	private void bindPort() {
		try {
			// 服务器开始监听
			mAcceptor.bind(new InetSocketAddress(SERVER_LISTEN_PORT));
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_CREATE_LISTEN_SESSION)
					.sendToTarget();
			mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_BIND_SUCCESS)
					.sendToTarget();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 断开与客户端的所有连接
	 */
	public void stop() {
		if (mSession != null) {
			mSession.close(true);
		}
	}

	/**
	 * 取消对端口的绑定
	 */
	public void cancel() {
		if (mAcceptor != null) {
			mAcceptor.unbind(new InetSocketAddress(SERVER_LISTEN_PORT));
			mAcceptor.getFilterChain().clear();
			mAcceptor.dispose();
			mAcceptor = null;
		}
	}

	/**
	 * @return 表示是否有客户端连接到服务端
	 */
	public boolean isConnected() {
		return mHasClientConnected;
	}

	/**
	 * 发送数据到客户端
	 * 
	 * @param array
	 */
	public void write(byte[] array) {
		if (mSession != null) {
			byte[] digest = getDigest(array);
			IoBuffer io = IoBuffer.allocate(array.length + 4 + digest.length);
			io.put(int2bytes(array.length + digest.length));
			io.put(digest);
			io.put(array);
			io.flip();
			mSession.write(io);
			this.mNotificationHandler.obtainMessage(
					WIFI_CONNECTION_MESSAGE_WRITE, -1, -1, array)
					.sendToTarget();
		}
	}

	// -----------------------------辅助 工具 方法 ---------------------------------//
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

	/**
	 * 工具方法将int型数据转成4个字节的字节数组表示
	 * 
	 * @param num
	 *            需要转换的int值
	 * @return 返回的是int值对应的byte[]数据
	 */
	private byte[] int2bytes(int num) {
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

	public void setLogger(IHaloLogger logger) {
		this.mLogger = logger;
	}
}
