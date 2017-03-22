package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileData;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkTicket;

public class HudUpgradeCodecFactory extends DemuxingProtocolCodecFactory {

    public HudUpgradeCodecFactory(boolean apkUpgrade, boolean clientSide) {
    	if (apkUpgrade) {
    		if (clientSide) { // Client
    			super.addMessageEncoder(HudUpgradeApkFileInfo.class, ApkInfoClientEncoder.class);
    			super.addMessageEncoder(HudUpgradeApkFileData.class, ApkDataClientEncoder.class);
    			super.addMessageDecoder(ApkTicketClientDecoder.class);
    		} else { //Server
    			super.addMessageDecoder(ApkDataServerDecoder.class);
    			super.addMessageDecoder(ApkInfoServerDecoder.class);  			
    			super.addMessageEncoder(HudUpgradeApkTicket.class, ApkTicketServerEncoder.class);
    		
    		}
    	} else {//For OTA upgrade
    		if (clientSide) {
    			
    		} else {
    			
    		}
    	}
    }
    
	

}
