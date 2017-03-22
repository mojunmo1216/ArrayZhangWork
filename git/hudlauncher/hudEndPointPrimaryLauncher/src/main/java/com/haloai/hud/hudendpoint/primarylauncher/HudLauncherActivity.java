package com.haloai.hud.hudendpoint.primarylauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.services.PhoneProxyService;
import com.haloai.hud.hudendpoint.utils.WifiHostBiz;
import com.haloai.hud.navigation.NavigationSDKAdapter;
import com.haloai.hud.utils.HaloLogger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 起始Activity，初始化数据、各Service、启动指定的Content Activity. Content
 * Activity可以是第三方App定制，由HudLauncherActivity根据配置和指令启动对应的Content Activity
 * 缺省加载HudDefaultContentActivity
 * 
 * @author harrymoo
 * 
 */

public class HudLauncherActivity extends Activity {
	private final static String TAG = "HudLauncherActivity";

	private Context appContext;
	private String SSID;
	private String wifiPWD;
	private int wifiSetFailedId = 0;// 用于标识wifi设置失败
	private int wifiHandSetId = 0;// 用来标识wifi是否为手动设置，避免wifi开机自动登录时弹出toast

	private int imageNumber;
	private byte[] imageByteArray;
	private boolean stopOpenNewActivity = false;

	private NetworkConnectChangedReceiver networkConnectChangedReceiver;
	private Timer titleTextViewAnimatorTimer;
//	private final BroadcastReceiver wifiConfigurationListener = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action
//					.equals(PhoneNotificationDispatcher.MSG_WIFI_CONFIGURATION_CHANGED_MESSAGE)) {
//				String wifiApName = intent
//						.getStringExtra(PhoneNotificationDispatcher.MSG_WIFI_CONFIGURATION_CHANGED_WIFIAPNAME);
//				String wifiPassword = intent
//						.getStringExtra(PhoneNotificationDispatcher.MSG_WIFI_CONFIGURATION_CHANGED_WIFIPASSWORD);
//				System.out.println("收到广播" + wifiApName + "\n" + wifiPassword);
//
//				SSID = wifiApName;
//				wifiPWD = wifiPassword;
//				WifiUtils localWifiUtils = new WifiUtils(
//						HudLauncherActivity.this);
//				wifiSetFailedId = 0;
//				wifiHandSetId = 1;
//
//				if (localWifiUtils.WifiCheckState() != WifiManager.WIFI_STATE_ENABLED) {
//					localWifiUtils.WifiOpen();
//					// Toast.makeText(HudLauncherActivity.this,
//					// "WIFI没有打开，正在打开WIFI", Toast.LENGTH_LONG).show();
//
//					try {
//						Timer timer = new Timer();
//						timer.schedule(new MyTimerTask(), 200);
//					} catch (Exception e) {
//						// Toast.makeText(HudLauncherActivity.this, "问题1",
//						// Toast.LENGTH_LONG).show();
//						e.printStackTrace();
//					}
//
//				} else {
//					try {
//						localWifiUtils.getConfiguration();
//						localWifiUtils.connectAP(SSID, wifiPWD);
//						Timer timer002 = new Timer();
//						timer002.schedule(new MyTimerTask002(), 4000);
//
//					} catch (Exception e) {
//						// Toast.makeText(HudLauncherActivity.this, "问题2",
//						// Toast.LENGTH_LONG).show();
//						e.printStackTrace();
//
//					}
//				}
//			}
//		}
//	};

//	private Handler handler = new Handler() {
//		int i = 0;
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1:
//				WifiUtils localWifiUtils = new WifiUtils(
//						HudLauncherActivity.this);
//				if (localWifiUtils.WifiCheckState() != WifiManager.WIFI_STATE_ENABLED) {
//
//					try {
//						if (i < 100) {
//							i++;
//							Timer timer = new Timer();
//							timer.schedule(new MyTimerTask(), 200);
//						}
//					} catch (Exception e) {
//						Toast.makeText(HudLauncherActivity.this,
//								"问题3,i=" + i + "次", Toast.LENGTH_LONG).show();
//						e.printStackTrace();
//					}
//
//				} else {
//					try {
//						Toast.makeText(HudLauncherActivity.this,
//								"WIFI已经打开，正在配置WIFI", Toast.LENGTH_LONG).show();
//						localWifiUtils.getConfiguration();
//						localWifiUtils.connectAP(SSID, wifiPWD);
//						Log.i("haliwifi ", "WIFI已经打开，正在配置WIFI");
//
//						Timer timer002 = new Timer();
//						timer002.schedule(new MyTimerTask002(), 4000);
//
//					} catch (Exception e) {
//						Toast.makeText(HudLauncherActivity.this, "问题4",
//								Toast.LENGTH_LONG).show();
//						e.printStackTrace();
//					}
//
//				}
//				break;
//			}
//			super.handleMessage(msg);
//		}
//
//	};

//	private class MyTimerTask extends TimerTask {
//		@Override
//		public void run() {
//			Message message = new Message();
//			message.what = 1;
//			handler.sendMessage(message);
//		}
//	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					initAll();
					break;

				case 1:
					break;
			}
		}
	};

//
//	private class MyTimerTask002 extends TimerTask {
//		@Override
//		public void run() {
//			Message message = new Message();
//			message.what = 1;
//			handler002.sendMessage(message);
//		}
//	}

	private void openWifiHostBiz(){
		String androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
		WifiHostBiz wifiHostBiz =new WifiHostBiz(this);
		wifiHostBiz.setWifiApEnabled(true,androidId);
	}

	public class NetworkConnectChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
				wifiSetFailedId = 1;
				System.out.println("WIFI配置启动");
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (parcelableExtra != null) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;
					Log.e("H3c", "isConnected" + isConnected + " info :"
							+ networkInfo.toString());
					if (isConnected) {
						if (wifiHandSetId == 1) {
							// Toast.makeText(HudLauncherActivity.this,
							// "WIFI配置成功", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
		}
	}

	private final BroadcastReceiver msgListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			if (action
//					.equals(PhoneNotificationDispatcher.MSG_WECHAT_START_MESSAGE)) {
//				Log.i("HudActivity", "收到启动微信命令");
//				WeChatController.getInstance(appContext).startWeChatService();
//			} else if (action
//					.equals(PhoneNotificationDispatcher.MSG_SET_SCREEN_THEME_MESSAGE)) {
//				Log.i("hanyu", "收到切换屏幕主题命令");
//				String themeName = intent
//						.getStringExtra(PhoneNotificationDispatcher.MSG_SCREEN_THEME_NAME);
//				if (themeName.equalsIgnoreCase("CareLand")) {
//					Intent themeIntent = new Intent(HudLauncherActivity.this,
//							HaloCareLandActivity.class);
//
//					HudLauncherActivity.this.startActivity(themeIntent);
//				} else 
//				if (themeName.equalsIgnoreCase("Default")) {
//					Intent themeIntent = new Intent(HudLauncherActivity.this,
//							HaloHudDefaultActivity.class);
//
//					HudLauncherActivity.this.startActivity(themeIntent);
//				} else if (themeName.equalsIgnoreCase("AMap")) {
//					// Intent themeIntent = new Intent(HudLauncherActivity.this,
//					// HaloHudDefaultActivity.class);
//
//					// HudLauncherActivity.this.startActivity(themeIntent);
//
//				}
//			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HaloLogger.logE("speech_info","Luncher Activity is starting...");
		setContentView(R.layout.activity_hud_launcher);
		appContext = this.getApplicationContext();
		mHandler.sendEmptyMessageDelayed(0,3000);//延时3秒启动BLE,WIFI,FM服务
		// this.startInnerBaiduNaviContent();
		// this.startCareLandContent();
		// this.startBaiduNaviContent();
		this.startAMapNaviContent();

//		this.getApplicationContext()
//				.registerReceiver(
//						wifiConfigurationListener,
//						new IntentFilter(
//								PhoneNotificationDispatcher.MSG_WIFI_CONFIGURATION_CHANGED_MESSAGE));
//		IntentFilter msgFilter = new IntentFilter();
//		msgFilter.addAction(PhoneNotificationDispatcher.MSG_WECHAT_START_MESSAGE);
//		msgFilter.addAction(PhoneNotificationDispatcher.MSG_SET_SCREEN_THEME_MESSAGE);

//		this.getApplicationContext().registerReceiver(msgListener, msgFilter);

//		IntentFilter filter = new IntentFilter();
//		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
//		registerReceiver(networkConnectChangedReceiver, filter);

		// TODO start gps
		//if(!isGpsOpen()){
			//TODO this method do not word in android 4.0/5.0
			//openGps();
		//}

		// TODO start wifi ap
		//openWifi4Ap();

		/**
		 * 启动视频传输的服务
		 */
	//	Toast.makeText(this, "开始启动视频传输服务", Toast.LENGTH_SHORT).show();
		Intent testActivityIntent = new Intent();
		testActivityIntent.setAction("android.intent.action.START_VIDOE_DOWNLOAD_SERVICE");
		testActivityIntent.setPackage("com.hud.haloai.videodownload");
		startService(testActivityIntent);
	}


	private void openWifi4Ap() {
	//	new (this).startWifiAp("haloai", "12345678");
	}

//	private void openGps() {
//		Intent GPSIntent = new Intent();
//        GPSIntent.setClassName("com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider");
//        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
//        GPSIntent.setData(Uri.parse("custom:3"));
//        try {
//            PendingIntent.getBroadcast(this, 0, GPSIntent, 0).send();
//        } catch (CanceledException e) {
//            e.printStackTrace();
//        }
//	}

	private boolean isGpsOpen() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LOGI("onDestroy called");
		// Unbind from the service
		this.unbindService(mConnection);
		//this.unregisterReceiver(msgListener);
//		this.unregisterReceiver(networkConnectChangedReceiver);
//		this.getApplicationContext().unregisterReceiver(wifiConfigurationListener);
	}

	private void initAll() {

		// Initialize HUD device Information
//		handler002.sendEmptyMessageDelayed(1,1000);
//		GlobalVariants.initAllVariants(appContext);

		// Initialize all High Frequency Data Services include OBD, GPS, Sensor.
		// HFDataControllerFactory.initHFDataSources(this);

		// Initialize Phone Proxy Service
		Intent intent = new Intent(this, PhoneProxyService.class);
		this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		// initialize wifi update service
		/*
		 * Intent service = new Intent();
		 * service.setClassName("com.halo.silent.AppInstall",
		 * "com.halo.silent.AppInstall.AutoUpgradeHaloHudService");
		 * this.startService(service);
		 */

	}


//	private class TempCarelandNaviStateUpdater implements
//			CarelandNaviStateUpdater {
//
//		private HudLauncherActivity hudLauncherActivity;
//
//		public TempCarelandNaviStateUpdater(
//				HudLauncherActivity hudLauncherActivity) {
//			this.hudLauncherActivity = hudLauncherActivity;
//		}
//
//		@Override
//		public void onNaviStarted() {
//		}
//
//		@Override
//		public void onNaviStopped() {
//		}
//
//		@Override
//		public void onCarelandAppStartup() {
//			if (titleTextViewAnimatorTimer != null)
//				titleTextViewAnimatorTimer.cancel();
//			titleTextViewAnimatorTimer = null;
//		}
//
//		@Override
//		public void onCarelandAppExit() {
//		}
//
//		@Override
//		public void onCarelandAppForeground() {
//		}
//
//		@Override
//		public void onCarelandAppBackground() {
//		}
//
//	}

	private class CarelandAppStatupTimeOut extends TimerTask {
		@Override
		public void run() {
			carelandAppStartupHandler.sendEmptyMessage(1001);
		}
	}

	Handler carelandAppStartupHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			Intent intent = new Intent(HudLauncherActivity.this,
//					HaloCareLandActivity.class);
//			HudLauncherActivity.this.startActivity(intent);
		}
	};

	private boolean isCarelandRunning() {
		// 获取ActivityManager
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			String name = info.processName;
		}

		return false;
	}

	private void startCareLandContent() {
//		NavigationSDKAdapter
//				.launchNavigationSDK(NavigationSDKAdapter.NAVIGATION_SDK_CARELAND, appContext);
//		NavigationSDKAdapter.getCurrentNavigationAdapter().setNaviStateUpdater(
//				new TempCarelandNaviStateUpdater(this));
//
//		// Bind the careland service for navigation triggering.
//		CarelandNavigationSDKAdapter carelandNaviAdapter = (CarelandNavigationSDKAdapter) NavigationSDKAdapter
//				.getCurrentNavigationAdapter();
//		boolean bres = carelandNaviAdapter.bindCarelandService(this
//				.getApplicationContext());
//		if (bres) {
//			Log.d("hanyu", "bindCarelandService true ");
//			// 凯立德绑定成功，等待其启动完毕后的消息，以启动HaloCareLandActivity
//			titleTextViewAnimatorTimer = new Timer();
//			titleTextViewAnimatorTimer.schedule(new CarelandAppStatupTimeOut(),
//					5000);
//		} else {
//			Log.d("hanyu", "bindCarelandService false ");
//			Toast.makeText(getApplicationContext(),
//					"bind CarelandService return false", Toast.LENGTH_SHORT)
//					.show();
//			// 凯立德启动失败，但是依然显示HaloCareLandActivity
//			// TODO: 凯立德启动失败，需要在HaloCareLandActivity上显示错误信息
////			Intent intent = new Intent(this, HaloCareLandActivity.class);
////			this.startActivity(intent);
//		}
	}

	private void startInnerBaiduNaviContent() {
		NavigationSDKAdapter
				.getNavigationAdapter(NavigationSDKAdapter.NAVIGATION_SDK_BAIDU_INNER, appContext);

		// startBaiduNaviApp();
		// new Thread(){
		// public void run() {
		// while(!isBaiduNaviRunning()){
		// SystemClock.sleep(200);
		// }
		// runOnUiThread(new Runnable(){
		// @Override
		// public void run() {
		// startInnerBaiduActivity();
		// }});
		// };
		// }.start();

		startInnerBaiduActivity();
	}

	private boolean isBaiduNaviRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final List<RunningAppProcessInfo> processes = manager
				.getRunningAppProcesses();
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(10);
		// for (RunningTaskInfo runningTaskInfo : runningTasks) {
		// final RunningTaskInfo temp = runningTaskInfo;
		// runOnUiThread(new Runnable(){
		// @Override
		// public void run() {
		// Toast.makeText(appContext,temp.topActivity.getPackageName(),
		// 0).show();
		// }
		// });
		// }
		for (RunningAppProcessInfo process : processes) {
			// com.baidu.navi
			if (process.processName.contains("com.baidu")) {
				return true;
			}
			// com.baidu.
			//
			// final RunningAppProcessInfo temp = process;
			// runOnUiThread(new Runnable(){
			// @Override
			// public void run() {
			// Toast.makeText(appContext, temp.processName, 0).show();
			// }
			// });
		}
		return false;
	}

	private void startBaiduNaviApp() {
		String uriStr = "bdnavi://launch";
		Uri uri = Uri.parse(uriStr);
		try {
			Intent intent = new Intent("com.baidu.navi.action.START");
			intent.setData(uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startInnerBaiduActivity() {
		// TODO
		// Intent intent = new Intent(this, HaloInnerBaiduActivity.class);
		// this.startActivity(intent);
	}

	private void startBaiduNaviContent() {
//		NavigationSDKAdapter
//				.launchNavigationSDK(NavigationSDKAdapter.NAVIGATION_SDK_BAIDU);
//		Intent intent = new Intent(this, HaloHudDefaultActivity.class);
//		this.startActivity(intent);
	}

	private void startAMapNaviContent() {

		NavigationSDKAdapter.getNavigationAdapter(NavigationSDKAdapter.NAVIGATION_SDK_AMAP, appContext);

		Intent intent = new Intent(this, HaloAMapNaviStartActivity.class);
//		Intent intent = new Intent(this, HaloMockStartActivity.class);
		intent.putExtra("HudLauncherActivity", "It's me!!!!");
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// GlobalVariants.appContext.startActivity(intent);
		this.startActivity(intent);

	}

	/** Defines callback for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LOGI("onServiceConnected");
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			// PhoneProxyServiceBinder binder = (PhoneProxyServiceBinder)
			// service;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			LOGI("onServiceDisconnected");
		}
	};

	// private SpeechRecognitePhoneActionListener srPhoneActionListen = new
	// SpeechRecognitePhoneActionListener() {
	//
	// @Override
	// public void onPhoneAction(String action, String person) {
	// if
	// (action.equalsIgnoreCase(SpeechRecognitionManager.PHONE_CALL_ACTION_TYPE_CALL))
	// {
	// Hud2PhoneCommandHelper.sendCmdOfMakePhoneCall("13533690679");
	// }
	// }
	//
	// };


	@Override
	protected void onStop() {
		super.onStop();
	}

	private void LOGI(String msg) {
		HaloLogger.logI(TAG, msg);
	}

}
