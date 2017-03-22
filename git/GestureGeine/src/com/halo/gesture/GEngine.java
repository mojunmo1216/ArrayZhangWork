package com.halo.gesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import mobile.chgper.ChgperActivity;
import android.os.FileObserver;
import android.util.Log;
import com.halo.gesture.IGestureConnectionListener.IGestureNotifier;

/**
 * Created by zhangrui on 16/7/13.
 */
public class GEngine {
	//initialize
	public GEngine(){
		
	}
	//fixed variable
	public static final boolean isHudApk = false;//true for Halo Hud device
	private static String base= "/system/etc/Halo";
	private static String fileUpdateStatusPath = base+"/Gesture/update_status";//升级进度，当前字节数／总共字节数
	private static String fileUpdatePath = base+"/Gesture/update";//升级需求，true为需要升级
	private static FileObserver fileUpdateObserver;//监测升级文件的改动
	private static FileObserver fileUpdateStatusObserver;//监测升级进度的改动
	//gesture variable
	public static String gestureType = "";//手势类型
	public static String gestureWave = "";//动作类型
	public static int WaveStartX = 0;//动作开始坐标 x
	public static int WaveStartY = 0;//动作开始坐标 y
	public static int WaveStartZ = 0;//动作开始坐标 x
	public static int WaveEndX = 0;//动作结束坐标 x
	public static int WaveEndY = 0;//动作结束坐标 y
	public static int WaveEndZ = 0;//动作结束坐标 z
	public static float GestureScore;
	//class variable
	private static Process procDirPermission, procEpPermission, onCltAdb, offCtlAdb;
	public static boolean boolDrawFrame = false;
	private static boolean boolHaloUsbPort = false;
	private static IGestureConnectionListener mConnectStaListener;
	private static IGestureNotifier mGestureNotifier;
	//
	//native interface  JNI入口
	//
	private static native int ConnectDevice();
	private static native int DisconnectDevice();
	public static native int[] GetCurrentImage(int[] image, int w, int h);
	private static native int StartGesture();
	private static native int StopGesture();
	private static native int OpenUsbPort();
	private static native int CloseUsbPort();
	private static native String GetRemoteVersion();
	private static native String GetNativeVersion();
    /*************************************************************************/
    private static native int TestFunction();
    public static void init(IGestureNotifier gestureNotifier){
        mGestureNotifier=gestureNotifier;
    }
    /*************************************************************************/
    //invoked by Java Native Interface
    public static void onGestureUpdateJni(String Type, String Wave, int StartX, int StartY, int StartZ, int EndX, int EndY, int EndZ ,float Score) {
        gestureType = Type;
        gestureWave = Wave;
        WaveStartX = StartX;
        WaveStartY = StartY;
        WaveStartZ = StartZ;
        WaveEndX = EndX;
        WaveEndY = EndY;
        WaveEndZ = EndZ;
        GestureScore = Score;
        if(mGestureNotifier!=null&&Type!=null&&Wave!=null) {
            parserGestureType(Type, Wave,StartX,StartY,StartZ,Score);
        }
    }

    private static void parserGestureType(String type, String wave,int x, int y, int z,float score){
    	 if (type.contains("V_SIGN")){
    	        mGestureNotifier.onGestureCallBack(IGestureNotifier.INPUTER_GESTURE_V_TYPE, score);
    	    }else if (type.contains("POINT")){
    	    	 if ((wave.contains("LEFT_TO_RIGHT")) || (wave.contains("RIGHT_TO_LEFT")))
    	             mGestureNotifier.onGestureCallBack(IGestureNotifier.INPUTER_GESTURE_SLIDE, score);
    	           else if (wave.contains("CLOCKWISE"))
    	             mGestureNotifier.onGestureCallBack(IGestureNotifier.INPUTER_GESTURE_CLOCKWISE, score);
    	           else if (wave.contains("EASTERN"))
    	             mGestureNotifier.onGestureCallBack(IGestureNotifier.INPUTER_GESTURE_ANTICLOCKWISE, score);
    	           else
    	             mGestureNotifier.onGesturePoint(x, y, z, score);
    	    }else if(type.contains("PALM")){
    	    	if (wave.contains("LEFT_TO_RIGHT")){ 
    	    		mGestureNotifier.onGestureCallBack(IGestureNotifier.INPUTER_GESTURE_PALM, score);
    	    	}
    	    }
    }
    //invoked by Java Native Interface
    public static void onConnectStatusChangeJni(int status)
    {
    	boolDrawFrame = false;
    	if(status == 1){
			boolDrawFrame = true;
		}
	   mConnectStaListener.onGestureDevState(status);
	}
	//
	//Test code here,
	//
	public static int FunctionForTest(){
		int ret = 0;
		TestFunction();
		return ret;
	}
	//
	//Version
	//
	public static String getVersionFromRemote(){
		String verStr = null;
		verStr = GetRemoteVersion();
		return verStr;
	}
	public static String getVersionFromNative(){
		String verStr = null;
		verStr = GetNativeVersion();
		return verStr;
	}
	//connect gesture device
	//
	public static int connectGestureDevice(){
		int ret = -1;
		ret = ConnectDevice();
		return ret;
	}
	//
	//disconnect gesture device
	//
	public static int disconnectGestureDevice(){
		int ret = -1;
		boolDrawFrame = false;
		ret = DisconnectDevice();
		return ret;
	}
	//
	//opening usb port
	//返回值：－1:修改权限失败，－2:开启手势端口失败，－3:升级进行中，手势不可使用
	public static int openGestureUsbPort(){
		int ret_permission = -1;//Initial set to -1
		int native_ret = -1;//Initial set to -1
		if(checkUpdateFlag() == true){
			return -3;//Gesture service is Updating
		}
		if(boolHaloUsbPort == false){
			boolHaloUsbPort = true;
			try {
				offCtlAdb = Runtime.getRuntime().exec("stop adbd");//su -c stop adbd
				offCtlAdb.waitFor();//success return 0
				if(isHudApk){
					//first time to change the directory access
					ChgperActivity.startPerService();
					ret_permission = 0;
					Thread.sleep(1000);//wait for permission operation to finished
				}else{
					procDirPermission = Runtime.getRuntime().exec("su -c chmod 777 /dev /dev/usb-ffs /dev/usb-ffs/adb /dev/usb-ffs/adb/ep0");
					ret_permission = procDirPermission.waitFor();
				}
				native_ret = OpenUsbPort();
				if (isHudApk){
					//second time to change ep1 and ep2 access
					//use this if the target device is custom HUD devices;
					ChgperActivity.startPerService();
					ret_permission = 0;
					Thread.sleep(1000);
				}else{
					procEpPermission = Runtime.getRuntime().exec("su -c chmod 666 /dev/usb-ffs/adb/ep0 /dev/usb-ffs/adb/ep1 /dev/usb-ffs/adb/ep2");//su -c 
					ret_permission = procEpPermission.waitFor();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(ret_permission == 0){
			if(native_ret == 0){
				return 0;
			}else{
				return -2;//Open Gesture port Error! AKA:Generating ep1 & ep2 failed.
			}
		}else{
			return -1;//Stopping adbd Error: changing permission error
		}
	}
	//
	//closing usb port
	//返回值：－1:开启adbd失败，－2:关闭手势端口失败。－3，手势升级进行中，不可用。-4:手势端口未开启，不可关闭
	public static int closeGestureUsbPort(){
		int ret_permission = -1;//Initial set to -1
		int native_ret = -1;//Initial set to -1
		if(checkUpdateFlag() == true){
			return -3;//Gesture service is Updating
		}
		if(boolHaloUsbPort == true){
			boolHaloUsbPort = false;
			native_ret = CloseUsbPort();
			//Thread.sleep(1000);//sleep for 1 seconds
			//start adbd service again
			try {
				onCltAdb = Runtime.getRuntime().exec("start adbd");
				ret_permission = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			return -4;
		}
		if(native_ret == 0){
			if(ret_permission == 0){
				return 0;
			}else{
				return -1;//starting adbd failed
			}
		}else{
			return -2;//closing gesture port failed
		}
	}
	//
	//Check update request true of false
	//
	public static boolean checkUpdateFlag(){
		FileInputStream fis = null;
		String StatusStr = null;
		boolean boolUpdateRequest = true;
		try {
			fis = new FileInputStream(fileUpdatePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(fis == null) return false;
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		try {
			StatusStr = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolUpdateRequest = StatusStr.contains("true");
		return boolUpdateRequest;
	}
	//
	//check update status
	//
	public static int checkUpdateProgress(){
		int updatePercentage = 0;
		String bytesStored = null;
		String bytesTotal = null;
		String progressString = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileUpdateStatusPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("GEngine", "IOException FileInputStream");
			e.printStackTrace();
		}
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		try {
			progressString = br.readLine();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("GEngine", "IOException readLine");
			e.printStackTrace();
		}
		if(progressString.contains("%")){
			String parts[] = progressString.split("%");
			bytesStored = parts[1];
			bytesTotal = parts[3];
		}
		updatePercentage = 100 * Integer.parseInt(bytesStored) / Integer.parseInt(bytesTotal);
		return updatePercentage;
	}
	//
	//Set Listener
	//
	public static void setConnectionListener(IGestureConnectionListener Listener){
		mConnectStaListener = Listener;
		File file = new File(GEngine.fileUpdatePath);
		File file1 = new File(GEngine.fileUpdateStatusPath);
		if(file == null || file1== null){
			return;
		}
		fileUpdateObserver=new FileObserver(GEngine.fileUpdatePath)
		{
			@Override
			public void onEvent(int event, String path) {
				int result_event = event & FileObserver.ALL_EVENTS;
				switch (result_event){
				case android.os.FileObserver.CREATE:
					Log.i("GEngine", "Update Flag = " + GEngine.checkUpdateFlag());
					mConnectStaListener.onUpdateFlagChange(GEngine.checkUpdateFlag());
					break;
					
				case android.os.FileObserver.MODIFY:
					Log.i("GEngine", "Update Flag = " + GEngine.checkUpdateFlag());
					mConnectStaListener.onUpdateFlagChange(GEngine.checkUpdateFlag());
					break;
				}
			}
		};
		
		fileUpdateStatusObserver=new FileObserver(GEngine.fileUpdateStatusPath)
		{
			@Override
			public void onEvent(int event, String path) {
				int result_event = event & FileObserver.ALL_EVENTS;
				switch (result_event){
				case android.os.FileObserver.CREATE:
					Log.i("GEngine", "Update progress = " + GEngine.checkUpdateProgress());
					mConnectStaListener.onUpdateProgressChange(GEngine.checkUpdateProgress());
					break;
					
				case android.os.FileObserver.MODIFY:
					Log.i("GEngine", "Update progress = " + GEngine.checkUpdateProgress());
					mConnectStaListener.onUpdateProgressChange(GEngine.checkUpdateProgress());
					break;
				}
			}
		};
		startUpdateObserver();
	}
	//
	//FileObserver
	//
	private static int startUpdateObserver(){
		int ret = 0;
		Log.i("GEngine", "startUpdateObserver");
		Log.i("GEngine", "fileUpdateObserver: startWatching");
		fileUpdateObserver.startWatching();
		Log.i("GEngine", "fileUpdateStatusObserver: startWatching");
		fileUpdateStatusObserver.startWatching();
		return ret;
	}
	//
	//stop observer
	//
	private static int stopUpdateObserver(){
		int ret = 0;
		Log.i("GEngine", "stopUpdateObserver");
		Log.i("GEngine", "fileUpdateObserver: stopWatching");
		fileUpdateObserver.stopWatching();
		Log.i("GEngine", "fileUpdateStatusObserver: stopWatching");
		fileUpdateStatusObserver.stopWatching();
		return ret;
	}
	//end of class
}
