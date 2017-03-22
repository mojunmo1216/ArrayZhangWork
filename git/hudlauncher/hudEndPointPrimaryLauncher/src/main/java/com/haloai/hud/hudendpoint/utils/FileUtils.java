package com.haloai.hud.hudendpoint.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.haloai.hud.hudendpoint.upgrade.UpgradeConstant;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 项目名称：hudlauncher
 * 类描述：使用反射的方式获取sdcard2的挂载状态。
 * 创建人：zhangyong
 * 创建时间：2016/4/25 20:18
 * 修改人：zhang
 * 修改时间：2016/4/25 20:18
 * 修改备注：
 */
public class FileUtils {

    // 被废弃
    /*public static String[] getPrimaryStoragePath(Context context) {
        try {
            Class<?>[] paramClasses = {};
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths",  paramClasses);
            Object[] params = {};
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, params);
            // first element in paths[] is primary storage path
            return paths;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    *//**
     * 获取外部存储的存储状态。
     * @return boolean
     *//*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean getSdcard1StorageState(Context context) {
        String[] primaryStoragePathList = getPrimaryStoragePath(context);

        if (primaryStoragePathList == null) {
            Log.e("CarRecorderFileUtils", "获取不到外部存储路径集合");
            return false;
        }
        // primaryStoragePathList[1]：角标1代表第1个外部存储。
        String storageState = Environment.getExternalStorageState(new File(primaryStoragePathList[1]));
        if ("mounted".equals(storageState)){
            return true;
        }else if("removed".equals(storageState)){
            return false;
        }else if ("bad_removal".equals(storageState)){
            return false;
        }else {
            return false;
        }
    }*/

    /**
     *  处理图片
     * @param bm 所要转换的bitmap
     * @param newWidth 新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 获取本地音乐播放列表
     * @return
     */
    public static ArrayList<String> getLocalMusicList(){
        String musicPath = EndpointsConstants.BasePath+"/Music";
        File file = new File(musicPath);
        try{
            if(file == null || !file.exists()){
                // TODO: 2016/8/22 创建music文件夹
                return null;
            }

            String[] list = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".mp3"); // TODO: 2016/8/22 更多音乐格式的扩展。
                }
            });

            if (list == null || list.length <= 0) {
                return null;
            }

            ArrayList<String> musicPathList = new ArrayList<String>();

            for (String musicName: list) {
                musicPathList.add(musicPath + "/" + musicName);
            }

            return musicPathList;
        } catch (Exception e) {
            Log.e("FileUtils", "读取磁盘文件出错");
            return null;
        }

    }

    public static String getUpgradeFilePath(Context context) {
        String path = "";
        int sdk_int = Build.VERSION.SDK_INT;
        if (sdk_int < 21) {
            // the android version before 5.0
            path = build_external_path() + "/adupsfota/update.zip";
        } else if (sdk_int < 23) {
            // the android version between 5.0 to 6.0
            path = context.getFilesDir() + "/adupsfota/update.zip";
        } else {
            // the android version after 6.0
            path = context.getFilesDir() + "/adupsfota/update.zip";
        }
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs())
                path = "";
        }
        HaloLogger.logE("StorageUtils" ,"get_update_file_path() path = "+path);
        return path;
    }


    public static String build_external_path() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
