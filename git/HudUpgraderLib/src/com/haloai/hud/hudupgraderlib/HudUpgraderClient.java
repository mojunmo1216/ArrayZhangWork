package com.haloai.hud.hudupgraderlib;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.haloai.hud.hudupgraderlib.mina.HudUpgradeClientHander;
import com.haloai.hud.hudupgraderlib.mina.HudUpgradeCodecFactory;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;

public class HudUpgraderClient {

	private boolean currentUpdating;
	private Context mContext;
	private boolean clientSide = true;
	private boolean apkUpgrade = true;
	private NioSocketConnector mConnector;
	private HudUpgradeClientHander mClientHandler;
	private int offset = 0;
	private HudUpgradingStatusNotifier statusNotifier;
	private String TAG = "HudUpgraderClient";
	private String path;
	private ClientBroadcastReceiver receiver;

	public HudUpgraderClient(Context context) {
		this.mContext = context;
		registerClientReceiver();
	}

	private void connectServer() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				mConnector = new NioSocketConnector();
				// 创建接受数据的过滤器
				DefaultIoFilterChainBuilder chain = mConnector.getFilterChain();

				chain.addLast("codec", new ProtocolCodecFilter(
						new HudUpgradeCodecFactory(apkUpgrade, clientSide)));
				mClientHandler = new HudUpgradeClientHander(mContext) {
					// 连接状态的回调
					@Override
					public void sessionClosed(IoSession session)
							throws Exception {
						super.sessionClosed(session);
						if (session.getAttribute(HudUpgradeConstants.DECODER_TYPE_KEY) == null) {
							statusNotifier.onUpgradeResult(HudUpgradeResultType.SERVER_CONNECTION_BROKEN);
						}
					}

					@Override
					public void sessionOpened(IoSession session)
							throws Exception {
						super.sessionOpened(session);
						Intent intent = new Intent(
								HudUpgradeConstants.ACTION_VAL_APK);
						mContext.sendBroadcast(intent);
					}
				};
				mConnector.setHandler(mClientHandler);
				mConnector.setConnectTimeout(30);
				// 连接到服务器：
				ConnectFuture cf;
				String IP = HudUpgradeConstants.getServerIp(mContext);
				if (IP.equals("")) {
					statusNotifier
							.onUpgradeResult(HudUpgradeResultType.SERVER_CONNECT_FAILED);
				} else {
					try {
						cf = mConnector
								.connect(new InetSocketAddress(IP,
										HudUpgradeConstants.SERVER_LISTEN_PORT
												+ offset));
						cf.awaitUninterruptibly();
						cf.getSession().getCloseFuture().awaitUninterruptibly();
					} catch (Exception e) {
						e.printStackTrace();
						offset++;
						if (HudUpgradeConstants.SERVER_LISTEN_PORT + offset > HudUpgradeConstants.SERVER_LISTEN_PORT_MAX) {
							statusNotifier.onUpgradeResult(HudUpgradeResultType.SERVER_CONNECT_FAILED);						
							offset = 0;
						}
						HudUpgraderClient.this.connectServer();
					}
				}
			}
		}).start();
	}

	public void startAPKUpgrade(String apkPath,
			HudUpgradingStatusNotifier statusNotifier) {
		if (HudUpgradeConstants.fileValOrDel(apkPath, true)) {
			this.statusNotifier = statusNotifier;
			this.path = apkPath;
			try {
				connectServer();//
			} catch (Exception e) {
				e.printStackTrace();
				statusNotifier
				.onUpgradeResult(HudUpgradeResultType.SERVER_CONNECT_FAILED);
			}
			
		} else {
			statusNotifier
					.onUpgradeResult(HudUpgradeResultType.UPGRADING_FILE_MISSING);
		}

	}

	// 解析apk得到应用信息
	public HudUpgradeApkFileInfo getAppInfo() {
		PackageManager pm = mContext.getPackageManager();// pm
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);// 解析应用信息
		if (info != null) {
			ApplicationInfo appinfo = info.applicationInfo;
			appinfo.publicSourceDir = path;// 要使用appinfo，需要设置publicSourceDir
			int protocol = HudUpgradeConstants.PROTOCOL;// 协议版本
			int pnum = HudUpgradeConstants.getPackageNum(path);
			String version = info.versionName;// 应用版本信息
			String packagename = appinfo.packageName;// 包名
			String MD5 = HudUpgradeConstants.getApkMD5(path);
			HudUpgradeApkFileInfo app = new HudUpgradeApkFileInfo(protocol,
					pnum, version, packagename, path, MD5);
			return app;
		}
		return null;
	}

	public void stopAPKUpgrade() {
		if (mConnector != null) {
			mConnector.dispose();
		}
		mContext.unregisterReceiver(receiver);
	}

	public void startOTAUpgrade(String otaFilePath,
			HudUpgradingStatusNotifier statusNotifier) {

	}

	public void stopOTAUpgrade() {

	}

	public boolean isUpgrading() {
		return currentUpdating;
	}

	public enum HudUpgradeResultType {
		SERVER_CONNECT_FAILED, 
		SERVER_CONNECTION_BROKEN, 
		UPGRADING_FILE_MISSING,
		UPGRADING_FILE_ILLEGAL,
		USER_CANCELLED,
		UPGRADE_COMPLETED
	}

	public interface HudUpgradingStatusNotifier {
		public void onUpgradeResult(HudUpgradeResultType result);

		public void onUpgradingProgress(int progress);
	}

	private void registerClientReceiver() {
	    receiver = new ClientBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(HudUpgradeConstants.ACTION_VAL_APK);
		filter.addAction(HudUpgradeConstants.ACTION_SEND_APK);
		mContext.registerReceiver(receiver, filter);
		Log.i(TAG, "registerClientReceiver");
	}


	public class ClientBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 验证apk
			if (action.equals(HudUpgradeConstants.ACTION_VAL_APK)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						HudUpgradeApkFileInfo info = getAppInfo();
						if (info != null) {
							mClientHandler.validateAPP(info);
							Log.i(TAG, "validateAPP.....");
						} else {
							statusNotifier
									.onUpgradeResult(HudUpgradeResultType.UPGRADING_FILE_ILLEGAL);
						}
					}
				}).start();
				// 发送apk
			} else if (action.equals(HudUpgradeConstants.ACTION_SEND_APK)) {
				final String UUID = intent.getStringExtra("UUID");
				new Thread(new Runnable() {
					@Override
					public void run() {
						mClientHandler.sendAPK(UUID, statusNotifier);
						Log.i(TAG, "sendAPP....");
					}
				}).start();
			}

		}

	}

}
