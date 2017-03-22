package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;

public class ProtocolDispatcherImp implements IDataDispatcher {
	private Context mContext;

	public ProtocolDispatcherImp(Context context){
			this.mContext=context;
	}
	@Override
	public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
		/*if(phone2HudMessages.getHudQueryRequest()!=null){
			String code=phone2HudMessages.getHudQueryRequest().getPhoneRequestCode();
			if(code.equalsIgnoreCase(PhoneConnectionConstants.CONNECTION_PHONE_BIND_HUD)){
				HudStatusQueryResp hudStatusQueryResp=new HudStatusQueryResp();
				hudStatusQueryResp.setHudResponCode(code);
				SharedPreferences sharedPreferences=mContext.getSharedPreferences(HudEndPointConstants.SP_BIND_SECRET_KEY,Context.MODE_PRIVATE);
				if(sharedPreferences.getString(HudEndPointConstants.SP_BIND_SECRET_KEY,null)!=null){
					hudStatusQueryResp.setHudResponResult("NO");
				}else{
					hudStatusQueryResp.setHudResponResult("YES");
				}
				Hud2PhoneMessages hud2PhoneMessages=new Hud2PhoneMessages();
				hud2PhoneMessages.setHudStatusQueryResp(hudStatusQueryResp);
				PhoneConnectionManager.getDataSender().sendData(hud2PhoneMessages.encapsulateHudH2PData());
			}
		}*/
		return null;
	}
}
