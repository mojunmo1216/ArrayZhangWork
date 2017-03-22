package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;

/**
 * Created by zhangrui on 16/4/18.
 */

public class TransportKeyDispatcherImp implements IDataDispatcher {

    private Context mContext;
    public TransportKeyDispatcherImp(Context context){
        this.mContext=context;
    }
    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {

       /* HudQueryRequest hudQueryRequest=phone2HudMessages.getHudQueryRequest();
        if(hudQueryRequest!=null){
            String code=hudQueryRequest.getPhoneRequestCode();
            if(code.equalsIgnoreCase(PhoneConnectionConstants.CONNECTION_TRANSPORT_KEY)){
                String key=hudQueryRequest.getPhoneBindHudKey();
                HudStatusQueryResp hudStatusQueryResp=new HudStatusQueryResp();
                hudStatusQueryResp.setHudResponCode(code);
                if(key!=null&&!key.equals("")){
                    SharedPreferences sharedPreferences=mContext.getSharedPreferences(HudEndPointConstants.SP_BIND_SECRET_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(HudEndPointConstants.SP_BIND_SECRET_KEY,key);
                    editor.commit();
                    hudStatusQueryResp.setHudResponResult("YES");
                }else{
                    hudStatusQueryResp.setHudResponResult("NO");
                }
                Hud2PhoneMessages hud2PhoneMessages=new Hud2PhoneMessages();
                hud2PhoneMessages.setHudStatusQueryResp(hudStatusQueryResp);
                PhoneConnectionManager.getDataSender().sendData(hud2PhoneMessages.encapsulateHudH2PData());
            }
        }*/
        return null;
    }
}
