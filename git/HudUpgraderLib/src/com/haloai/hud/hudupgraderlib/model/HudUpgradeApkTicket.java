package com.haloai.hud.hudupgraderlib.model;

import java.io.Serializable;
import java.nio.charset.CharacterCodingException;

import org.apache.mina.core.buffer.IoBuffer;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;

public class HudUpgradeApkTicket extends AbstractUpgradeData implements Serializable{

	private static final long serialVersionUID = -4183905909939427049L;
	private int responCode;//返回状态码
	private String UUID;//验证的UUID
	
	public HudUpgradeApkTicket(int type,int responCode,String UUID) {
		super(getToal(UUID),type);
		this.responCode=responCode;
		this.UUID=UUID;
	}
    
	public HudUpgradeApkTicket(int toal,int type,IoBuffer iobuffer) throws CharacterCodingException{
		super(toal,type);
		int responCode=iobuffer.getInt();
		int UUIDLength=iobuffer.getInt();
		String UUID=iobuffer.getString(UUIDLength,HudUpgradeConstants.decoder);
		this.responCode=responCode;
		this.UUID=UUID;
		
	}
	public int getResponCode() {
		return responCode;
	}

	public String getUUID() {
		return UUID;
	}
    
	@Override
	protected int getAllFieldsDataLength() {
		return 4+4+UUID.length();
	}
	
	public static int getToal(String UU) {
		return 4+4+4+UU.length();
	}
	public void encode(IoBuffer ioBuffer) {
		super.encode(ioBuffer);		
		try {
			ioBuffer.putInt(responCode);
			ioBuffer.putInt(UUID.length());
			ioBuffer.putString(UUID, HudUpgradeConstants.encoder);			
		} catch (CharacterCodingException e) {
		     
			e.printStackTrace();
		}
	}

}
