package com.haloai.hud.hudupgraderlib.model;

import java.nio.charset.CharacterCodingException;

import org.apache.mina.core.buffer.IoBuffer;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;



public class HudUpgradeApkFileData extends AbstractUpgradeData {

	private int dataStatus;
	private int dataIndex;
	private String UUID;
	private int arrayLength;
	private static byte[] apkArray = null;
	
	public HudUpgradeApkFileData(int dataStatus,int dataIndex,String UUID,int arryaLength,byte[] apkArray){
		super(getToal(UUID,arryaLength),HudUpgradeConstants.APP_DATA_CODE);		
		this.dataStatus=dataStatus;
		this.dataIndex=dataIndex;
		this.UUID=UUID;
		this.arrayLength=arryaLength;
		HudUpgradeApkFileData.apkArray=apkArray;//new byte[arryaLength];
		//System.arraycopy(apkArray,0,this.apkArray,0,arryaLength);//内存拷贝
		
	}
	public HudUpgradeApkFileData(int toal,int type,IoBuffer iobuffer) throws CharacterCodingException{
		super(toal,type);
		int dataStatus=iobuffer.getInt();
		int dataIndex=iobuffer.getInt();
		int ULength=iobuffer.getInt();
		String UUID=iobuffer.getString(ULength, HudUpgradeConstants.decoder);
		int arrayLength=iobuffer.getInt();
		if (HudUpgradeApkFileData.apkArray == null)
			apkArray=new byte[arrayLength];
		iobuffer.get(apkArray,0,arrayLength);
		
		this.dataStatus=dataStatus;
		this.dataIndex=dataIndex;
		this.UUID=UUID;
		this.arrayLength=arrayLength;
		
	}

	public int getDataStatus() {
		return dataStatus;
	}
    
	
	public int getDataIndex() {
		return dataIndex;
	}
	public String getUUID() {
		return UUID;
	}
    
	public int getArryLength() {
		return arrayLength;
	}
	
	public byte[] getApkArray() {
		return apkArray;
	}
    
	
	public static int getToal(String UU,int arryaLength) {
		return 4+4+4+4+UU.length()+4+arryaLength;
	}
	
	public void encode(IoBuffer ioBuffer) {	
		super.encode(ioBuffer);
		try {
			ioBuffer.putInt(dataStatus);
			ioBuffer.putInt(dataIndex);
			ioBuffer.putInt(UUID.length());
			ioBuffer.putString(UUID, HudUpgradeConstants.encoder);
			ioBuffer.putInt(arrayLength);
		    ioBuffer.put(apkArray,0,arrayLength);
		} catch (CharacterCodingException e) {
			
			e.printStackTrace();
		}
		
        
	}
	@Override
	protected int getAllFieldsDataLength() {
		
		return 4+4+4+UUID.length()+4+arrayLength;
	} 
	
	
}
