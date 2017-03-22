package com.haloai.hud.hudupgraderlib;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.haloai.hud.hudupgraderlib.mina.HudUpgradeCodecFactory;
import com.haloai.hud.hudupgraderlib.mina.HudUpgradeServerHander;

public class HudUpgraderServer {
	private int offset = 0;
	private Context mContext;
	private IoAcceptor mAcceptor;
	private boolean clientSide = false;
	private boolean apkUpgrade = true;

	public HudUpgraderServer(Context context) {
		this.mContext = context;
	}

	public void startServer() {

		mAcceptor = new NioSocketAcceptor();
		// 设置Filter链
		mAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		mAcceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new HudUpgradeCodecFactory(apkUpgrade,
						clientSide)));

		// 设置消息处理类（创建、关闭Session，可读可写等等，继承自接口IoHandler）
		mAcceptor.setHandler(new HudUpgradeServerHander(mContext) {
			@Override
			public void sessionClosed(IoSession session) throws Exception {
				super.sessionClosed(session);
				if ((Integer)session.getAttribute(HudUpgradeConstants.DECODER_TYPE_KEY) == HudUpgradeConstants.APP_DATA_CODE) {
					Intent intent=new Intent(HudUpgradeConstants.ACTION_INS_APK);
					mContext.sendBroadcast(intent);
				}
			}
		});
		// 设置接收缓存区大小
		mAcceptor.getSessionConfig().setReadBufferSize(
				HudUpgradeConstants.DATA_SIZE);
		mAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		try {
			// 服务器开始监听
			mAcceptor.bind(new InetSocketAddress(
					HudUpgradeConstants.SERVER_LISTEN_PORT + offset));
			Log.i("HudUpgraderServer", "startserver :"
					+ (HudUpgradeConstants.SERVER_LISTEN_PORT + offset));
			Handler handler=new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {				
				@Override
				public void run() {
					Toast.makeText(mContext.getApplicationContext(), "服务端绑定成功："+(HudUpgradeConstants.SERVER_LISTEN_PORT + offset), Toast.LENGTH_SHORT).show();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			offset++;
			if (HudUpgradeConstants.SERVER_LISTEN_PORT + offset > HudUpgradeConstants.SERVER_LISTEN_PORT_MAX) {
				offset = 0;
			}
			HudUpgraderServer.this.startServer();
		}
	}

	public void stopServer() {
		if (mAcceptor != null && mAcceptor.isActive()) {
			mAcceptor.unbind();
			mAcceptor.dispose();
		}
	}

	public void updraderCompleted() {
		HudUpgradeServerHander.setResponse(HudUpgradeConstants.APP_DATA_CODE,
				HudUpgradeConstants.INS_SUCCESS, "UpdraderCompleted");
	}

}
