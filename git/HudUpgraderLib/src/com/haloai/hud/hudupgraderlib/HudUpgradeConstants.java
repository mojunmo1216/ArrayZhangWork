package com.haloai.hud.hudupgraderlib;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class HudUpgradeConstants {
	public static  CharsetEncoder encoder = Charset.defaultCharset().newEncoder();	
	public static  CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
	
	public static final int SERVER_LISTEN_PORT = 5566;//初始端口号
	public static final int SERVER_LISTEN_PORT_MAX = 5570;//最大端口号

	public static final int PROTOCOL = 1;//协议版本

	public static final int APP_INFO_CODE=100;
	public static final int APP_DATA_CODE=101;
	public static final int DATA_STATUS_NEED=110;
	public static final int DATA_STATUS_OK=112;

	public static final int VAL_SUCCESS=200;
	public static final int VAL_FAILED=202;
	public static final int INS_SUCCESS=300;
	public static final int INS_FAILED=303;
	
	public static final int HEADER_LENGTH=4;
	
	public static final String HUD_UP_PNAME="com.haloai.hud.hudendpoint.primarylauncher";
	
	public static final String ACTION_SEND_APK="com.haloai.hud.sendapk";
	
	public static final String ACTION_VAL_APK="com.haloai.hud.valapk";
	
	public static final String ACTION_START_ACTIVITY="com.haloai.hud.startactivity";
	
	public static final String ACTION_INS_APK="com.haloai.hud.insapk";
	
	public final static String ACTION_START_SERVER = "com.haloai.hud.startserver";
	
	public static final String INTENT_INS_SUCCESS_KEY="InstallAPKSuccess";
	
	public static final String DECODER_TYPE_KEY=HudUpgradeConstants.class.getName()+".TYPE";
	
	public static final int DATA_SIZE=1024*1024;
	
	//得到文件的MD5校验值
    public static String getApkMD5(String path) 
    {				
				File file=new File(path);		
			    if (!file.isFile())
			    {
			      return null;
			    }
			    MessageDigest digest = null;
			    FileInputStream in=null;
			    byte buffer[] = new byte[1024];
			    int len;
			    try {
			      digest = MessageDigest.getInstance("MD5");
			      in = new FileInputStream(file);
			      while ((len = in.read(buffer, 0, 1024)) != -1) {
			        digest.update(buffer, 0, len);
			      }
			      in.close();
			    } catch (Exception e) {
			      e.printStackTrace();
			      return null;
			    }
			    BigInteger bigInt = new BigInteger(1, digest.digest());
			    return bigInt.toString(16);
	}
    //byte数组转成int
    public static int byteArrayToInt(byte[] b)
    { 
    	byte[] a = new byte[4]; 
    	int i = a.length - 1,j = b.length - 1; 
    	for (; i >= 0 ; i--,j--) {//从b的尾部(即int值的低位)开始copy数据 
    	if(j >= 0) 
    	a[i] = b[j]; 
    	else 
    	a[i] = 0;//如果b.length不足4,则将高位补0 
    	} 
    	int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位 
    	int v1 = (a[1] & 0xff) << 16; 
    	int v2 = (a[2] & 0xff) << 8; 
    	int v3 = (a[3] & 0xff) ; 
    	return v0 + v1 + v2 + v3; 
    }
    //int转成byte数组
    public static byte[] intToByteArray(int num)
	{
	       byte[] b=new byte[4];
	       //int mask=0xff;
	       for(int i=0;i<4;i++){
	            b[i]=(byte)(num>>>(24-i*8));
	       }
	      return b;
	} 
    
	 // 得到ip
	public  static String getServerIp(Context context) 
	{
		String serviceip=null;
		try {
				WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				DhcpInfo dhcp = manager.getDhcpInfo();
				int ipInt = dhcp.gateway;
				serviceip = String.valueOf(new StringBuilder()
						.append((ipInt & 0xff)).append('.')
						.append((ipInt >> 8) & 0xff).append('.')
						.append((ipInt >> 16) & 0xff).append('.')
						.append(((ipInt >> 24) & 0xff)).toString());

			} catch (Throwable t) {
				t.printStackTrace();
			}
		return serviceip;
	}
 	
	//得到数据包数量
	public static int getPackageNum(String path){
		File file=new File(path);
		double length=file.length();
		double size=DATA_SIZE;
		int num=(int)Math.ceil(length/size);
		return num;
	}
	
	//验证或者删除apk
	public static boolean fileValOrDel(String path,boolean flag){
		File file=new File(path);
		if(file.exists()){
			if(flag){
				return true;//验证apk
			}else{
				file.delete();//删除apk
			}
		}
		return false;
	}
		
	
}
