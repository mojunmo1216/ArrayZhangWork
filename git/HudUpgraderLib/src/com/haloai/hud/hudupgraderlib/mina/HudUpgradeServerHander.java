package com.haloai.hud.hudupgraderlib.mina;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.HudUpgraderInstallAPK;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileData;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkTicket;

public class HudUpgradeServerHander extends IoHandlerAdapter {

	private Context mContext;
	private static IoSession mSession;
	private String UUID;
	private HudUpgradeApkFileInfo apkInfo;

	public HudUpgradeServerHander(Context context) {
		this.mContext = context;

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		mSession = session;
		System.out
				.println("one Client Connection" + session.getRemoteAddress());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	/*
	 * 这个方法是目前这个类里最主要的，接收客户端传输的数据
	 */

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		int type = (Integer) session
				.getAttribute(HudUpgradeConstants.DECODER_TYPE_KEY);
		if (type == HudUpgradeConstants.APP_DATA_CODE) {

			HudUpgradeApkFileData data = (HudUpgradeApkFileData) message;
			// 收数据包
			if (data.getUUID().equals(UUID)) {

				receiveApk(data.getApkArray(), data.getArryLength());
				// 收包完成
				if (data.getDataIndex() == apkInfo.getPackageNum()) {

					String MD5 = HudUpgradeConstants.getApkMD5(apkInfo
							.getPath());
					// 校验文件完整性
					if (apkInfo.getMD5().equals(MD5)) {
						Log.i("HudUpgradeServerHander",
								"安装APP:" + apkInfo.getMD5());
						// 静默安装app
						if (apkInfo != null && mContext != null)
						/*	try {*/
								HudUpgraderInstallAPK
								.installAndStartApk(mContext,
										apkInfo.getPath(),
										apkInfo.getPackageName());
							/*} catch (Exception e) {
								e.printStackTrace();
								Intent intent = new Intent(HudUpgradeConstants.ACTION_INS_APK);
								mContext.sendBroadcast(intent);
							}*/

					} else {
						setResponse(HudUpgradeConstants.APP_DATA_CODE,
								HudUpgradeConstants.INS_FAILED, UUID);
					}
				} else if (data.getDataIndex() == 1) {
					Intent intent = new Intent(HudUpgradeConstants.ACTION_START_ACTIVITY);
					mContext.sendBroadcast(intent);
				}

			} else {
				setResponse(HudUpgradeConstants.APP_DATA_CODE,
						HudUpgradeConstants.INS_FAILED, UUID);
			}
			// 验证appInfo
		} else if (type == HudUpgradeConstants.APP_INFO_CODE) {
			Log.i("messageReceived", "验证APP");
			HudUpgradeApkFileInfo info = (HudUpgradeApkFileInfo) message;
			this.apkInfo = info;
			validateAppInfo(info);

		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		System.out.println("客户端与服务端断开连接.....");
	}

	private void receiveApk(byte[] arry, int length) {

		File file = new File(apkInfo.getPath());
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file, true);
			bos = new BufferedOutputStream(fos);
			bos.write(arry, 0, length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	public void validateAppInfo(HudUpgradeApkFileInfo app) {

		if (app.getPackageName().equals(HudUpgradeConstants.HUD_UP_PNAME)
				&& app.getProtocol() == HudUpgradeConstants.PROTOCOL) {
			java.util.UUID uuid = java.util.UUID.randomUUID();
			UUID = uuid.toString();
			setResponse(HudUpgradeConstants.APP_INFO_CODE,
					HudUpgradeConstants.VAL_SUCCESS, UUID);
			HudUpgradeConstants.fileValOrDel(app.getPath(), false);
		} else {
			setResponse(HudUpgradeConstants.APP_INFO_CODE,
					HudUpgradeConstants.VAL_FAILED, "UUID");
		}
	}

	public static void setResponse(int type, int code, String UUID) {
		HudUpgradeApkTicket ticket = new HudUpgradeApkTicket(type, code, UUID);
		mSession.write(ticket);
		Log.i("HudUpgradeServerHander", "setResponse:" + code);
	}

}
