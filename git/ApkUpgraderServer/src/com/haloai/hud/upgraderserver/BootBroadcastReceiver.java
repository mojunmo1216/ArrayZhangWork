package com.haloai.hud.upgraderserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	// 重写onReceive方法
	@Override
	public void onReceive(Context context, Intent intent) {
		// 启动服务端监听客户端操作
		Log.i("BootBroadcastReceiver", "开机升级服务自启动.....");
		Intent service=new Intent(context, UpgraderService.class);
		context.startService(service);
	}

}
