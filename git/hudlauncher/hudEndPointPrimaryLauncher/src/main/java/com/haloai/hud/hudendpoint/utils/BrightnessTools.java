package com.haloai.hud.hudendpoint.utils;
import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
public class BrightnessTools {

	
	    /* Android的屏幕亮度在2.1+的时候提供了自动调节的功能，  
	     * 所以，如果当开启自动调节功能的时候， 进行调节好像是没有一点作用的，  
	     * 所以需要先进行判断是否开启了屏幕亮度的自动调节功能。  
	     */  
	  
	      
	  
	    /** * 判断是否开启了自动亮度调节 */  
	  
	    public static boolean isAutoBrightness(Activity activity) {      
	  
	    boolean automicBrightness = false;      
	  
	    try{          
	  
	    automicBrightness = Settings.System.getInt( activity.getContentResolver(),                  
	  
	    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;     
	  
	     }   
	  
	    catch(SettingNotFoundException e)   
	  
	    {         
	  
	     e.printStackTrace();    
	  
	      }      
	  
	    return automicBrightness;  
	    }  
	    //然后就是要获得当前的亮度了：  
	  
	    /** * 获取屏幕的亮度 */  
	  
	    public static int getScreenBrightness(Activity activity) {     
	  
	    int nowBrightnessValue = 0;      
	  
	    ContentResolver resolver = activity.getContentResolver();      
	  
	    try{          
	  
	    nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);    
	  
	      }  
	  
	     catch(Exception e) {         
	  
	     e.printStackTrace();    
	  
	      }      
	  
	    return nowBrightnessValue;  
	    }  
	      
	    //那如何修改屏幕的亮度呢？  
	  
	          
	  
	    /** * 设置亮度 */  
	  
	    public static void setBrightness(Activity activity, int brightness) {     
	  
	     // Settings.System.putInt(activity.getContentResolver(),      
	  
	    // Settings.System.SCREEN_BRIGHTNESS_MODE,      
	  
	    // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);      
	  
	    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();     
	  
	     lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);    
	  
	  
	     activity.getWindow().setAttributes(lp);   
	     saveBrightness(activity,brightness );
	     }  
	      
	    //那么，能设置了，但是为什么还是会出现，设置了，没反映呢？  
	  
	    //嘿嘿，那是因为，开启了自动调节功能了，那如何关闭呢？这才是最重要的：  
	  
	    /** * 停止自动亮度调节 */  
	  
	    public static void stopAutoBrightness(Activity activity) {     
	  
	     Settings.System.putInt(activity.getContentResolver(),            
	  
	      Settings.System.SCREEN_BRIGHTNESS_MODE,             
	  
	     Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
	     }  
	    //能开启，那自然应该能关闭了哟哟，那怎么关闭呢？很简单的：  
	  
	    /** * 开启亮度自动调节 *    
	  
	    * @param activity */  
	  
	       
	  
	    public static void startAutoBrightness(Activity activity) {     
	  
	     Settings.System.putInt(activity.getContentResolver(),             
	  
	     Settings.System.SCREEN_BRIGHTNESS_MODE,              
	  
	    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  
	  
	    }  
	      
	    //像刚才那样设置的话，只能在当前的activity中有作用，一旦退出的时候，会发现毫无作用，需要强制保存！  
	  
	    /** * 保存亮度设置状态 */  
	  
	    public static void saveBrightness(Activity activity, int brightness) {  
	    	  ContentResolver resolver =	activity.getContentResolver();
	  
	    Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");     
	  
	     android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);      
	  
	   
	  
	     resolver.notifyChange(uri, null);}  
	}  

