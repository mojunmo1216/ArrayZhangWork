package com.haloai.hud.hudendpoint.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.hudendpoint.phoneconnection.PhoneConnectionManager;
import com.haloai.hud.hudendpoint.protocol.IHudNaviController;
import com.haloai.hud.hudendpoint.protocol.IHudPreferenceSetController;
import com.haloai.hud.hudendpoint.protocol.imp.HudQueryRequestDispatcherimp;
import com.haloai.hud.hudendpoint.protocol.imp.CarrecorderVideoInfoDispatcherImp;
import com.haloai.hud.hudendpoint.protocol.imp.ExchangeMessageDispatcherImp;
import com.haloai.hud.hudendpoint.protocol.HudNaviController;
import com.haloai.hud.hudendpoint.protocol.imp.HudPrefQueryRequestDispatcherImp;
import com.haloai.hud.hudendpoint.protocol.HudPreferenceSetController;
import com.haloai.hud.hudendpoint.protocol.imp.HudPrefSetDispatcherImp;
import com.haloai.hud.hudendpoint.protocol.imp.ProtocolDispatcherImp;
import com.haloai.hud.lib.transportlayer.fm.FmManager;
import com.haloai.huduniversalio.inputer.dispatchImpl.PhoneCommandDispatcherImp;
import com.haloai.hud.hudendpoint.protocol.imp.ThumbRequestDispatcherImp;


public class PhoneProxyService extends Service {

    private boolean bInited = false;
    private final IBinder mBinder = new PhoneProxyServiceBinder();


    public class PhoneProxyServiceBinder extends Binder {
        public PhoneProxyService getService() {
            return PhoneProxyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (!bInited)
            init();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        uninit();
        return super.onUnbind(intent);
    }

    private void init() {
        if (bInited) return;


        PhoneConnectionManager phoneConnectionManager = PhoneConnectionManager.getInstance();
        // 绑定fm远程服务
        FmManager.getSingleFmManagerInstance(getApplicationContext()).bindFMService();
        IHudNaviController hudNaviController = new HudNaviController(getApplication());
        HudPreferenceSetController.getInstance().init(getApplicationContext());
        IDataDispatcher protocolDispatcher = new ProtocolDispatcherImp(getApplicationContext());
//		IDataDispatcher remoterCommandDispatcher = new PhoneCommandDispatcherImp(getApplicationContext(),hudNaviController);
//		IDataDispatcher transportKeyDispatcher=new TransportKeyDispatcherImp(getApplicationContext());
        IDataDispatcher exchangeMessageDispatcher = new ExchangeMessageDispatcherImp(getApplicationContext());
        IDataDispatcher carrecorderInfoDispatcherImp = new HudQueryRequestDispatcherimp(getApplicationContext());
        IDataDispatcher carrecorderVideoInfoDispatcherImp = new CarrecorderVideoInfoDispatcherImp(getApplicationContext());
        IDataDispatcher thumbRequestDispatcherImp = new ThumbRequestDispatcherImp(getApplicationContext());
        IDataDispatcher hudPrefQueryRequestDispatcherImp = new HudPrefQueryRequestDispatcherImp(getApplicationContext());
//		phoneConnectionManager.addDataDispatcher(transportKeyDispatcher);
        phoneConnectionManager.addDataDispatcher(protocolDispatcher);
        phoneConnectionManager.addDataDispatcher(exchangeMessageDispatcher);
        phoneConnectionManager.addDataDispatcher(carrecorderInfoDispatcherImp);
        phoneConnectionManager.addDataDispatcher(carrecorderVideoInfoDispatcherImp);
        phoneConnectionManager.addDataDispatcher(thumbRequestDispatcherImp);
        phoneConnectionManager.addDataDispatcher(hudPrefQueryRequestDispatcherImp);
//		phoneConnectionManager.addDataDispatcher(remoterCommandDispatcher);
        phoneConnectionManager.start(getApplicationContext());
        bInited = true;
    }

    private void uninit() {
        bInited = false;
        PhoneConnectionManager.getInstance().stop();
    }

}
