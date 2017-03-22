package com.haloai.hud.hudupgraderlib.model;

import java.nio.charset.CharacterCodingException;

import org.apache.mina.core.buffer.IoBuffer;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;



public class HudUpgradeApkFileInfo extends AbstractUpgradeData{

	private int protocol; //协议版本
	private int packageNum;//数据包数量
    private String appVersion; //app版本号
    private String packageName; //app包名
    private String path; //文件路径
    private String MD5; //文件md5 验证
 
	
	public HudUpgradeApkFileInfo(int protocol,int packageNum,String appVersion,String packageName,String path,String MD5)
	{
		super(getToal(appVersion,packageName,path,MD5),HudUpgradeConstants.APP_INFO_CODE);
		this.protocol=protocol;
		this.packageNum=packageNum;
    	this.appVersion=appVersion;
    	this.packageName=packageName;
    	this.path=path;
    	this.MD5=MD5;
	}
    
	public HudUpgradeApkFileInfo(int toal,int type,IoBuffer in) throws CharacterCodingException 
	{
		super(toal,type);
		int protocol=in.getInt();
		int packageNum=in.getInt();
		int appVLength=in.getInt();   		
		String 	appVersion = in.getString(appVLength,HudUpgradeConstants.decoder);		
		int pNameLength=in.getInt();
		String packageName=in.getString(pNameLength, HudUpgradeConstants.decoder);
		int pathLength=in.getInt();
		String path=in.getString(pathLength, HudUpgradeConstants.decoder);
        int MD5Leng=in.getInt();
        String MD5=in.getString(MD5Leng, HudUpgradeConstants.decoder);
        
		this.protocol=protocol;
		this.packageNum=packageNum;
    	this.appVersion=appVersion;
    	this.packageName=packageName;
    	this.path=path;
    	this.MD5=MD5;
		
	}


	public int getProtocol() {
		return protocol;
	}
	public int getPackageNum() {
		return packageNum;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPath() {
		return path;
	}

	public String getMD5() {
		return MD5;
	}
	
	
	public void encode(IoBuffer ioBuffer) {
		super.encode(ioBuffer);
		
		try {
			ioBuffer.putInt(protocol);
			ioBuffer.putInt(packageNum);
			ioBuffer.putInt(appVersion.length());
			ioBuffer.putString(appVersion, HudUpgradeConstants.encoder);
			ioBuffer.putInt(packageName.length());
			ioBuffer.putString(packageName, HudUpgradeConstants.encoder);
			ioBuffer.putInt(path.length());
			ioBuffer.putString(path, HudUpgradeConstants.encoder);
			ioBuffer.putInt(MD5.length());
			ioBuffer.putString(MD5, HudUpgradeConstants.encoder);
		} catch (CharacterCodingException e) {
			
			e.printStackTrace();
		}
		
        
	} 
	public static int getToal(String appVersion,String packageName,String path,String MD5) {
		return 4+4+4+4+appVersion.length()+4+
	    		packageName.length()+4+path.length()+4+MD5.length();
	}
	
	@Override
	protected int getAllFieldsDataLength() {
	
		return 4+4+4+appVersion.length()+4+
	    		packageName.length()+4+path.length()+4+MD5.length();
	}
	
//	protected abstract HudUpgradeApkFileInfo decoder(IoBuffer ioBuffer){
////		hud
//		return null;		
//	}
}
