package com.haloai.hud.hudupgraderlib;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class HudUpgraderInstallAPK {

	public static void installAndStartApk(final Context context,
			final String apkPath, final String packageName) {
		if ((apkPath == null) || (context == null)) {
			return;
		}

		File file = new File(apkPath);
		if (file.exists() == false) {
			return;
		}
		new Thread() {
			public void run() {
				Intent intent = null;
				if (silentUnInstall(context, packageName)) {
					Log.i("silentUnInstall", "SUCCESS!");
					if (silentInstall(apkPath)) {
						intent = new Intent(HudUpgradeConstants.ACTION_INS_APK);
						intent.putExtra(
								HudUpgradeConstants.INTENT_INS_SUCCESS_KEY,
								HudUpgradeConstants.INTENT_INS_SUCCESS_KEY);
						HudUpgradeConstants.fileValOrDel(apkPath, false);// 删除安装完成的apk
						startApp(context, packageName);// 启动，activity
					}
				} else {
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context.getApplicationContext(),
									"卸载失败！", Toast.LENGTH_SHORT).show();
						}
					});
				}
				if (intent == null) {
					intent = new Intent(HudUpgradeConstants.ACTION_INS_APK);
				}
				intent.putExtra(HudUpgradeConstants.INTENT_INS_SUCCESS_KEY,
						HudUpgradeConstants.INTENT_INS_SUCCESS_KEY);
				context.sendBroadcast(intent);
			};
		}.start();

	}

	public static List<ResolveInfo> findActivitiesForPackage(Context context,
			String packageName) {
		final PackageManager pm = context.getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);

		final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}

	/**
	 * 静默安装
	 */
	public static boolean silentInstall(String apkPath) {
		String cmd1 = "chmod 777 " + apkPath + " \n";
		String cmd2 = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
				+ apkPath + " \n";
		return execWithSID(cmd1, cmd2);
	}

	/**
	 * 静默卸载
	 */
	public static boolean silentUnInstall(Context context, String packageName) {
		boolean flag = true;

		String cmd = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall "
				+ packageName + " \n";
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info != null) {
				flag = execWithSID(cmd);
			}
		} catch (NameNotFoundException e) {

		}

		return flag;
	}

	/*
	 * 执行命令
	 */
	private static boolean execWithSID(String... args) {
		boolean isSuccess = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);

			for (String tmp : args) {
				dataOutputStream.writeBytes(tmp);
			}

			dataOutputStream.flush(); // 提交命令
			dataOutputStream.close(); // 关闭流操作
			out.close();

			isSuccess = waitForProcess(process);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

	private static boolean waitForProcess(Process p) {
		boolean isSuccess = false;
		int returnCode;
		try {
			returnCode = p.waitFor();
			switch (returnCode) {
			case 0:
				isSuccess = true;
				break;

			case 1:
				break;

			default:
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}

	private static void startApp(Context context, String packagename) {
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packagename);
		if (intent == null) {
			Log.i("", "APP not found!");
			return;
		}
		context.startActivity(intent);
	}
}
