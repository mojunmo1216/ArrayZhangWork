/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : FileHelper.java
 * @ProjectName : uniCarPlatform
 * @PakageName : com.unisound.unicar.framework.util
 * @Author : Alieen
 * @CreateDate : 2015-07-06
 */
package com.unisound.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class FileHelper {
	public static final String TAG = "FileHelper";
	
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
	private static final SimpleDateFormat mWakeupDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH", Locale.getDefault());

	
	/**
	 * @Description : 拷贝Assets制定文件夹下所有文件到目标路径
	 * @Author : Alieen
	 * @CreateDate : 2015-07-06
	 * @param Context
	 * @param fromPath
	 * @param savePath
	 * @return boolean
	 */
	public static boolean copyFileFromAssets(Context context, String fromPath, String savePath) {
		FileOutputStream out = null;
		try {
			isPathValid(savePath);
			String[] files = context.getAssets().list(fromPath);
			byte[] buffer = new byte[1024];
			for (int i = 0; i < files.length; i++) {
				File file = new File(savePath + File.separator + files[i]);
				if (file.exists()) {
					file.delete();
				} 
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = new FileOutputStream(file);
				InputStream in = context.getAssets().open(fromPath + File.separator + files[i]);
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0) {
						break;
					}
					out.write(buffer, 0, nRead);
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
			return true;
		} catch (Exception e) {
			LogUtils.d(TAG, "init files error");
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
	
	public static void isPathValid(String path){
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public static void createFile(File file, StringBuffer buffer) {  
        try {  
        	
        	isPathValid(file.getAbsolutePath());
        	
        	if (file.exists()) {
				file.delete();
			} 
			try {
				file.createNewFile();
				 PrintWriter p = new PrintWriter(new FileOutputStream(file.getAbsolutePath()));  
	             p.write(buffer.toString());  
	             p.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	
	/**
	 * 拷贝文件
	 * 
	 * @param src
	 * @param tar
	 * @throws Exception
	 */
	public static void copyFile(File src, File tar) throws Exception {
		if (src.isFile()) {
			InputStream is = new FileInputStream(src);
			OutputStream op = new FileOutputStream(tar);
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(op);
			byte[] bt = new byte[8192];
			int len = bis.read(bt);
			while (len != -1) {
				bos.write(bt, 0, len);
				len = bis.read(bt);
			}
			bis.close();
			bos.close();
		} else if (src.isDirectory()) {
			File[] f = src.listFiles();
			tar.mkdirs();
			for (int i = 0; i < f.length; i++) {
				copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator + f[i].getName()));
			}
		}
	}
	
	public static byte[] InputStreamToByte(InputStream is) throws IOException {  
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
        int ch;  
        while ((ch = is.read()) != -1) {  
            bytestream.write(ch);  
        }  
        byte imgdata[] = bytestream.toByteArray();  
        bytestream.close();  
        return imgdata;  
    } 
	
    public static ArrayList<String> readConfigFromFile(String filePath) {
		
    	LogUtils.d(TAG, "-readConfigFromFile-" + filePath);
		File file = new File(filePath);
		ArrayList<String> protocalList = new ArrayList<String>();
		
		if(file.exists()) {
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
				BufferedReader bReader = new BufferedReader(reader);
				String lineTxt = null;
				
				while ((lineTxt = bReader.readLine()) != null) {
					if(!lineTxt.startsWith("#")){
						protocalList.add(lineTxt);
					}
				}

				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return protocalList;
	}
    
    public static String getFileDataPath(Context context){
    	
    	return context.getFilesDir().getAbsolutePath();
    }
    
    public static String generateFileName(String tag, String ex) {
		return tag + "_" + mDateFormat.format(new Date(System.currentTimeMillis())) + "." + ex;
	}
	
    public static String generateFileNameHour(String ex) {
		return mWakeupDateFormat.format(new Date(System.currentTimeMillis())) + "." + ex;
	}
    
    public static boolean isSDCardExists() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	@SuppressWarnings("deprecation")
	public static long getAvaiableSDCard() {
        if (isSDCardExists()) {
            File pathFile = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(pathFile.getPath());

            long nAvailable = statfs.getAvailableBlocks();
            long nBlockSize = statfs.getBlockSize();

            long availableMB = nAvailable * nBlockSize / 1024 / 1024;

            LogUtils.d(TAG, "getAvaiableSDCard :" + availableMB);
            return availableMB;
        }
        LogUtils.d(TAG, "getAvaiableSDCard unmount");
        return 0;
    }

    public static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo me = new ActivityManager.MemoryInfo();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(me);

        long unused = me.availMem / 1024 / 1024;

        LogUtils.d(TAG, "getAvailableMemory :" + unused);
        return unused;
    }
}
