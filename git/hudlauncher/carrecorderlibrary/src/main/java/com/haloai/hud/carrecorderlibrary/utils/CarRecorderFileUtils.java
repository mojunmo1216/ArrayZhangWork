package com.haloai.hud.carrecorderlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CarRecorderFileUtils {

    /**
     * 返回指定目录中以.3gp结尾且是Recfront开头的文件列表
     *
     * @param path 目录路径
     * @return 符合条件的文件名list
     */
    public static List<String> getPathList(String path) {
        if (path == null) {
            Log.e("CarRecorderFileUtils", "没有该类型的视频");
            return null;
        }
        String[] subFiles = new File(path).list();
        if (subFiles == null) {
            Log.e("CarRecorderFileUtils", "没有该类型的视频");
            return null;
        }
        List<String> list = new ArrayList<>(subFiles.length);
        for (String string : subFiles) {
            if ((string.endsWith(".3gp")||string.endsWith(".mp4")) && string.startsWith(HudCarcorderConstants.FILEPREFIX)) {
                list.add(path + "/" + string);
            }
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.trim().compareTo(rhs);
            }
        });
        return list;
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals("mounted");
    }

    /**
     * 获取格林尼治时间中的时分
     *
     * @param millis 格林尼治时间毫秒数
     * @return  "HH:mm"格式的时间
     */
    public static String timeTransform(long millis) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        java.util.Date dt = new Date(millis);
        return sdf.format(dt);
    }

    /**
     * 字节转为兆，保留两位有效小数
     *
     * @param length 字节数
     * @return 以兆为单位的文件大小
     */
    public static String byte2M(long length) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return df.format(length / (1024d * 1024d)) + "M";
    }

    /**
     * 获取特定视频的开始录制时间
     *
     * @param videoName 视频名字
     * @return 如果不是特定文件则返回-1
     */
    public static long getVideoStartTime(String videoName) {
        if (videoName.length() >= 24 && HudCarcorderConstants.FILEPREFIX.equalsIgnoreCase(videoName.substring(0, 8))) {
            // 根据名字获取创建时间
            String date = videoName.substring(9, 24);
            String formatType = "yyyyMMdd_HHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(formatType);
            Date dt;
            try {
                dt = sdf.parse(date);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                Log.e("record_info","error:"+e.getMessage());
                return -1;
            }
            Log.e("record_info","time:"+dt.getTime());
            return dt.getTime();
        } else {
            Log.e("record_info","error:"+"string error");
            return -1;
        }
    }

    /**
     * 获取特定视频的开始录制时间
     * 2016-09-12 16:43:59 时间转成标准毫秒数
     *
     * @param startTime 格林尼治时间 单位：毫秒
     * @return 如果不是特定文件则返回-1
     */
    public static long getVideoStartTime2(String startTime) {
        if (startTime == null || startTime.length() <= 0) {
            // 根据名字获取创建时间   2016-09-12 16:43:59
            String formatType = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(formatType);
            Date dt;
            try {
                dt = sdf.parse(startTime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                return -1;
            }
            return dt.getTime();
        } else {
            return -1;
        }
    }


    /**
     * 根据视频生成缩略图字符数组
     *
     * @param videoPath return absolute videoPath of the image
     */
    public static byte[] getImageByteArray(String videoPath) {
        Bitmap bitmap = null;
        File file = new File(videoPath);
        if (!file.exists()) {
            Log.e("CarRecorderFileUtils", "视频文件不存在");
            return null;
        }
        if (TextUtils.isEmpty(videoPath)) {
            Log.e("CarRecorderFileUtils", "视频路径为空");
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null) {
            Log.e("CarRecorderFileUtils", "从视频获取的Bitmap为null");
            return null;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, HudCarcorderConstants.CARERECORDER_PLAYING_IMAGE_WIDTH, HudCarcorderConstants.CARERECORDER_PLAYING_IMAGE_HEIGHT, true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        bitmap.recycle();
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 从本地文件读取压缩过的图片。
     *
     * @param imagePath 图片路径
     * @param pixelW 图片长边的像素
     * @param pixelH 图片短边像素
     * @return  bitmap
     */
    public static Bitmap readImageFromLocal(String imagePath, float pixelW, float pixelH) {
        if (imagePath == null) {
            return null;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, newOpts);// 高能报警，请勿删除

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int be = 1;
        if (w > h && w > pixelW) {
            be = (int) (newOpts.outWidth / pixelW);
        } else if (w < h && h > pixelH) {
            be = (int) (newOpts.outHeight / pixelH);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, newOpts);
        return bitmap;
    }

    /**
     * 将毫秒转化为特定的格式 如 136215毫秒——2:16
     *
     * @param duration time of mill...
     * @return 2:16
     */
    public static String milliseconds2NormalTime(long duration) {
        int totalSec = (int) (duration / 1000f + 0.5);
        String minTime = "" + totalSec / 60;
        String secTime = totalSec % 60 < 10 ? "0" + totalSec % 60 : "" + totalSec % 60;
        return minTime + ":" + secTime;
    }

    /**
     * 根据视频路径获取图片数组
     *
     * @param videoPath 视频路径
     * @return  图片二进制数据
     */
    public static String getImageBytes(String videoPath) {
        if (videoPath == null || !videoPath.endsWith(".3gp")) {
            return null;
        }

        File file = new File(videoPath);
        String name = file.getName();

        if (name.length() < 25 || !name.startsWith(HudCarcorderConstants.FILEPREFIX)) {
            return null;
        }
        final String substring = name.substring(0, 24);
        String parent = file.getParent();
        File f = new File(parent);
        String[] files = f.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(substring) && name.length() > 28;
            }
        });
        if (files == null || files.length < 1) {
            return null;
        }
        return file.getParent() + "/" + files[0];
    }

    /**
     * 判断当前摄像头是否可用
     *
     * @return 摄像头是否可用
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            if (mCamera == null) {
                return false;
            }
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile() && file.delete();
    }


}
