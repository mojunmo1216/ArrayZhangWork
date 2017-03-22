package com.haloai.hud.upgraderserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.HudUpgraderServer;

public class UpgraderService extends Service {
	private static final String TAG = "UpdateAppService";
	private ServerBroadcastReceiver receiver;
	private HudUpgraderServer server;

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "UpdateAppService onBind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		unregisterReceiver(receiver);
		server.stopServer();
		return super.onUnbind(intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "UpgraderService onStart");
		super.onStart(intent, startId);
		Intent startServer = new Intent(HudUpgradeConstants.ACTION_START_SERVER);
		this.sendBroadcast(startServer);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "UpgraderService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		server = new HudUpgraderServer(this);
		regiserServerReceiver();
		Log.i(TAG, "UpdateAppService register");
	}

	public class ServerBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(HudUpgradeConstants.ACTION_START_SERVER)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						server.startServer();
						Log.i(TAG, "UpgraderService  startServer");
					}
				}).start();

			} else if (action.equals(HudUpgradeConstants.ACTION_INS_APK)) {

				UpgraderActivity activity = ((UpgraderApplication) getApplication())
						.getInstance();
				if (activity != null)
					activity.finish();
				if (intent
						.getStringExtra(HudUpgradeConstants.INTENT_INS_SUCCESS_KEY) != null) {
					server.updraderCompleted();
				}
				Log.i(TAG, "UpgraderService  installAPK");
			} else if (action.equals(HudUpgradeConstants.ACTION_START_ACTIVITY)) {
				
				Intent intents = new Intent(context, UpgraderActivity.class);
				intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intents);
				Log.i(TAG, "UpgraderService  statrActivity");
			}

		}

	}

	private void regiserServerReceiver() {
		receiver = new ServerBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(HudUpgradeConstants.ACTION_INS_APK);
		filter.addAction(HudUpgradeConstants.ACTION_START_SERVER);
		filter.addAction(HudUpgradeConstants.ACTION_START_ACTIVITY);
		this.registerReceiver(receiver, filter);
		Log.i("HudUpgraderServer", "regiserServerReceiver");
	}

}
