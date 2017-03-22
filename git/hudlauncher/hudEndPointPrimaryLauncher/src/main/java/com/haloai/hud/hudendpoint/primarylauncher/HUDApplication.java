package com.haloai.hud.hudendpoint.primarylauncher;

import android.app.Application;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.fota.iport.MobAgentPolicy;
import com.fota.iport.info.MobileParamInfo;
import com.fota.utils.Trace;
import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.utils.PropertyUtil;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Properties;

public class HUDApplication extends Application {

	private static HUDApplication instance;
	private static String Bugly_Key = "0d503d5329";
	private Properties properties;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//本地异常捕获
		/*CrashUtils crashUtils=CrashUtils.getInstance();
		crashUtils.init(this);*/

		initHudInfo();
		initBugly();
		initFota();
		initConfig();

	}

	public static HUDApplication getInstance() {
		return instance;
	}

/*	public boolean insert(String given_name, String mobile_number) {
		try {
			ContentValues values = new ContentValues();
			Uri rawContactUri = getContentResolver().insert(
					ContactsContract.RawContacts.CONTENT_URI, values);
			long rawContactId = ContentUris.parseId(rawContactUri);
			// 向data表插入姓名数据
			if (given_name != "") {
				values.clear();
				values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
				values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
				values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, given_name);
				getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
						values);
			}

			// 向data表插入电话数据
			if (mobile_number != "") {
				values.clear();
				values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
				values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile_number);
				values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
				getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
						values);
			}
			getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
					values);

		} catch (Exception e) {
			return false;
		}
		return true;

	}*/

	private void initFota(){
		// sdk打印日志功能
		Trace.setLevel(Trace.NONE);//打开日志
//        Trace.setLevel(Trace.NONE);//关闭日志
//        Trace.setLog_path(getExternalCacheDir() + "/iport_log.txt");//设置log存储路径,默认在内置存储根目录下iport_log.txt文件

		//init mid. sure the mid is unique//写文件形式
		/*String mid = SPFTool.getString(this, "mid", null);
		if (mid == null) {
			mid = Mid.getMid(this);
			SPFTool.putString(this, "mid", mid);
		}*/
		//使用imei作为设备mid 需要在manifest.xml文件中要添加 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
		//String mid = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
		//.getDeviceId();

		//init mobile parameter
		try {
			//    手机设备-需要fae在手机中配置build.prop文件
			//TODO 使用烧录进去的设备编号HALO
			MobileParamInfo.initInfo(this, HudEndPointConstants.gHudSerialNumber);
			// 		智能设备
	    	//String version = SPFTool.getString(this, SPFTool.KEY_WEAR_DEVICES_VERSION, Constant.version);
			//MobileParamInfo.initInfo(this, mid, UpgradeConstant.version, UpgradeConstant.oem, UpgradeConstant.models, UpgradeConstant.token, UpgradeConstant.platform, UpgradeConstant.deviceType);
		} catch (Exception e) {
			Log.d("Tag", "MobileParamsInfo = " + MobileParamInfo.getInstance().toString());
			Trace.e("FotaApplication", e.toString());
			//ignore
		}

		// 初始化sdk缓存信息
		MobAgentPolicy.initConfig(this);
	}

	//初始化工程配置 Config.Properties
	private void initConfig(){
		properties= PropertyUtil.loadConfig();
		if(properties==null){ //配置文件不存在的时候创建配置文件 初始化配置信息
			properties=new Properties();
			properties.put(PropertyUtil.KEY_MARGIN_TOP, "90");
			properties.put(PropertyUtil.KEY_MARGIN_RIG, "0");
			properties.put(PropertyUtil.KEY_UPGRADE_CODE,"0");
			//也可以添加基本类型数据 get时就需要强制转换成封装类型
			PropertyUtil.saveConfig(properties);
		}else{
			//properties.put("bool", "no");//put方法可以直接修改配置信息，不会重复添加
			//b=(((String)properties.get("bt_name")).equals("yes"))?true:false;//get出来的都是Object对象 如果是基本类型 需要用到封装类
			EndpointsConstants.MARGIN_TOP=Integer.parseInt((String)properties.get(PropertyUtil.KEY_MARGIN_TOP));
			EndpointsConstants.MARGIN_RIGHT=Integer.parseInt((String)properties.get(PropertyUtil.KEY_MARGIN_RIG));
			if(properties.get(PropertyUtil.KEY_UPGRADE_CODE)!=null){
				EndpointsConstants.UPGRADE_STATUS =Integer.parseInt((String)properties.get(PropertyUtil.KEY_UPGRADE_CODE));
			}else{
				properties.put(PropertyUtil.KEY_UPGRADE_CODE,"0");
				PropertyUtil.saveConfig(properties);
			}

		}

	}

	//初始化Bugly
	private void initBugly(){
		//远程异常捕获
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
		strategy.setAppPackageName("com.haloai.hud.hudendpoint.primarylauncher");//设置包名
		strategy.setDeviceID(HudEndPointConstants.gHudSerialNumber);
		CrashReport.initCrashReport(getApplicationContext(),Bugly_Key,false,strategy);
		BuglyLog.setCache(30 * 1024);//设置日志缓存大小 25K
	}

	private void initHudInfo() {
		PackageInfo pInfo;
		try {
			pInfo = this.getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			HudEndPointConstants.gHudVersionNumber = pInfo.versionName;
			TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			if(!TextUtils.isEmpty(tm.getSimSerialNumber())){
				HudEndPointConstants.gSimICCID = tm.getSimSerialNumber();  //取出ICCID
			}
			if(!TextUtils.isEmpty(tm.getDeviceId())){
				HudEndPointConstants.gHudSerialNumber = tm.getDeviceId();//"HaloHud0001";
			}
			HaloLogger.postE(EndpointsConstants.MAIN_TAG,"DeviceID:"+HudEndPointConstants.gHudSerialNumber);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}


}
