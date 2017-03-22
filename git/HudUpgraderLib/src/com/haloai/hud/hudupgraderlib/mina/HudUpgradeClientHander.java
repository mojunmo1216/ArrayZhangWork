package com.haloai.hud.hudupgraderlib.mina;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.HudUpgraderClient.HudUpgradeResultType;
import com.haloai.hud.hudupgraderlib.HudUpgraderClient.HudUpgradingStatusNotifier;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileData;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkTicket;

public class HudUpgradeClientHander extends IoHandlerAdapter{


	private IoSession mSession;
	private HudUpgradeApkFileInfo apkInfo;
	private Context mContext;
	private HudUpgradingStatusNotifier statusNotifier;
	private boolean isClosedSession=true;
	private static byte[] buffer = new byte[HudUpgradeConstants.DATA_SIZE];

	public HudUpgradeClientHander(Context context) {
		this.mContext = context;

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("one Client Connection" + session.getRemoteAddress());
		this.mSession = session;
		// 在连接建立后，发送标识码到服务端，否则连接会被服务端断开

	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		HudUpgradeApkTicket ticket = (HudUpgradeApkTicket) message;
		int responCode = ticket.getResponCode();
		Log.i("messageReceived", "获得服务端传过来的数据:" + responCode);
		if (responCode == HudUpgradeConstants.VAL_SUCCESS) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, "APP验证成功，上传应用....", Toast.LENGTH_SHORT).show();
				}

			});
			String UUID = ticket.getUUID();
			Intent intent= new Intent(HudUpgradeConstants.ACTION_SEND_APK);
			intent.putExtra("UUID",UUID);
			mContext.sendBroadcast(intent);
			isClosedSession=false;
		} else if (responCode == HudUpgradeConstants.INS_SUCCESS) {
			statusNotifier.onUpgradeResult(HudUpgradeResultType.UPGRADE_COMPLETED);
		} else if (responCode == HudUpgradeConstants.VAL_FAILED) {		
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, "APP验证失败，请重新选择！", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, "APP更新失败,请重新选择!", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		if(isClosedSession)
			sessionClosed(session);
	}

	// 首先验证app,验证成功再上传apk
	public void validateAPP(HudUpgradeApkFileInfo appinfo) {
		this.apkInfo = appinfo;
		if (mSession == null) {
			return;
		}
		try {
			mSession.write(appinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAPK(String UUID,HudUpgradingStatusNotifier statusNotifier) {

		try {
			this.statusNotifier=statusNotifier;
			File file = new File(apkInfo.getPath());
			FileInputStream fileInputStream = new FileInputStream(file);
			
			int len = 0;
			int index = 0;
			while ((len = fileInputStream.read(buffer)) != -1) {
				index++;
				statusNotifier.onUpgradingProgress((index*100)/apkInfo.getPackageNum());				
				HudUpgradeApkFileData data = new HudUpgradeApkFileData(
						HudUpgradeConstants.DATA_STATUS_NEED, index, UUID, len, buffer);
				mSession.write(data);
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
