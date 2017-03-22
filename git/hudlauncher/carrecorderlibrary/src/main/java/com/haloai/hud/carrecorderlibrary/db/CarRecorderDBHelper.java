package com.haloai.hud.carrecorderlibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.haloai.hud.carrecorderlibrary.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：SQLiteDataBaseDemo
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/9/5 14:04
 * 修改人：zhang
 * 修改时间：2016/9/5 14:04
 * 修改备注：
 */
public class CarRecorderDBHelper extends SQLiteOpenHelper {

    // shiyiyuefen shizuihouqixian , renshizhongdatiaozheng, 手头事情
    // 测试手机端控制播放，找出上下图片切换不顺畅的原因。不到两个月的时间。
    // 添加一个状态提示

    public static final String DATABASE_NAME = "carrecorder.db";
    public static final int DATABASE_VERSION = 1;

    /*表名*/
    public static final String TABLE_NAME = "carrecorder";
    /*字段*/
    public static final String VIDEO_NAME = "video_name";
    public static final String VIDEO_SIZE = "video_size";
    public static final String VIDEO_DURATION = "video_duration";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String THUMB_BYTE_ARRAY = "thumb_byte_array";
    public static final String VIDEO_PATH = "video_path";
    public static final String VIDEO_TYPE = "video_type";

    /*视频类型*/
    public static final String LOOP_VIDEO = "front";
    public static final String LOCK_VIDEO = "protect";

    private static CarRecorderDBHelper helper;
    private SQLiteDatabase mDatabase;


    public static CarRecorderDBHelper getSingleCarRecorderDBHelper(Context context) {
        if (helper == null) {
            synchronized (CarRecorderDBHelper.class) {
                if (helper == null) {
                    helper = new CarRecorderDBHelper(context);
                }
            }
        }
        return helper;
    }

    private CarRecorderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + "(" + BaseColumns._ID + " integer primary key autoincrement, " + VIDEO_NAME + " varchar(40), " + VIDEO_SIZE + " integer, " + VIDEO_DURATION + " integer, " + START_TIME + " integer, " + END_TIME + " integer, " + THUMB_BYTE_ARRAY + " BLOB, " + VIDEO_PATH + " varchar(100), " + VIDEO_TYPE + " varchar(40))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * 添加视频信息
     *
     * @param videoName  视频名字
     * @param videoSize  视频大小（单位：Byte）
     * @param duration   视频时长（单位：millisecond）
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param thumbBytes 视频对应的缩略图路径
     * @param videoPath  视频路径
     * @param videoType  视频类型
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insert(String videoName, long videoSize, long duration, long startTime, long endTime, byte[] thumbBytes, String videoPath, String videoType) {
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarRecorderDBHelper.VIDEO_NAME, videoName);
        contentValues.put(CarRecorderDBHelper.VIDEO_SIZE, videoSize);
        contentValues.put(CarRecorderDBHelper.VIDEO_DURATION, duration);
        contentValues.put(CarRecorderDBHelper.START_TIME, startTime);
        contentValues.put(CarRecorderDBHelper.END_TIME, endTime);
        contentValues.put(CarRecorderDBHelper.THUMB_BYTE_ARRAY, thumbBytes);
        contentValues.put(CarRecorderDBHelper.VIDEO_PATH, videoPath);
        contentValues.put(CarRecorderDBHelper.VIDEO_TYPE, videoType);
        long insert = mDatabase.insert(TABLE_NAME, null, contentValues);
        dbManager.closeDatabase();
        return insert;

//        String sql = "insert into " + CarRecorderOpenHelper.TABLE_NAME + "(" + CarRecorderOpenHelper.VIDEO_NAME + ", " + CarRecorderOpenHelper.VIDEO_SIZE + ", " + CarRecorderOpenHelper.VIDEO_DURATION + ", " + CarRecorderOpenHelper.START_TIME + ", " + CarRecorderOpenHelper.END_TIME + ", " + CarRecorderOpenHelper.THUMB_BYTE_ARRAY + ", " + CarRecorderOpenHelper.VIDEO_PATH + ", " + CarRecorderOpenHelper.VIDEO_TYPE + ") values(?, ?, ?, ?, ?, ?, ?, ?)";
//        mDatabase.execSQL(sql, new Object[]{videoName, videoSize, duration, startTime, endTime, thumbBytes, videoPath, videoType});
    }


    /**
     * 根据主键删除视频信息。
     *
     * @param id 视频id
     * @return 是否删除
     */
    public boolean delete(long id) {
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getWritableDatabase();
        String[] args = {String.valueOf(id)};
        int delete = mDatabase.delete(TABLE_NAME, "_id = ?", args);
        dbManager.closeDatabase();
        return delete > 0;
//        String sql = "delete from " + CarRecorderOpenHelper.TABLE_NAME + " where " + BaseColumns._ID+ " = ?";
//        mDatabase.execSQL(sql, new Object[]{_id});
    }


    /**
     * 更新
     * @param fieldName 条件字段名
     * @param fieldValue 条件字段值
     * @param updateFieldName   被更新的字段名
     * @param updateFieldValue  被更新的字段值
     */
    public void update(String fieldName, Object fieldValue, String updateFieldName, Object updateFieldValue){
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getWritableDatabase();
        String sql = "update " + TABLE_NAME + " set " + updateFieldName + " = '" + updateFieldValue + "' where " + fieldName + " = " + fieldValue;
        Log.e("zhangyong_debug", "更新信息的sql语句：" + sql);
        mDatabase.execSQL(sql);

//        ContentValues values = new ContentValues();
//        values.put(updateFieldName, (String) updateFieldValue);
//        mDatabase.update(TABLE_NAME, values, fieldName + " = ?", new String[]{(String) fieldValue});
        dbManager.closeDatabase();
    }


    /**
     * 查询某类视频的个数
     *
     * @param type 视频类型
     * @return 视频个数, 如果查询失败则返回-1
     */
    public int getVideoCount(String type) {
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + VIDEO_TYPE + " = ?";
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{type});
        int count = cursor.getCount();
        cursor.close();
        dbManager.closeDatabase();
        return count;
    }

    /**
     * 分页查找某类视频 ,从startindex开始，查找number个
     *
     * @param type       视频类型
     * @param startIndex 开始角标
     * @param number     请求个数
     * @return 视频bean集合
     */
    public List<VideoBean> pagesToFind(String type, int startIndex, int number) {
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getReadableDatabase();
        if (startIndex < 0) {
            Log.e("pagesToFind", "startIndex < 0, 参数异常，已经设为0");
            startIndex = 0;
        }
        if (number <= 0) {
            Log.e("pagesToFind", "number <= 0, 参数异常");
            return null;
        }
        String sql = "select * from " + TABLE_NAME + " where " + VIDEO_TYPE + " = ? order by " + BaseColumns._ID + " desc";
        // TODO: 2016/9/18 尝试
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{type});
        List<VideoBean> videoBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int anInt = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            String videoName = cursor.getString(cursor.getColumnIndex(VIDEO_NAME));
            long videoSize = cursor.getLong(cursor.getColumnIndex(VIDEO_SIZE));
            long videoDuration = cursor.getLong(cursor.getColumnIndex(VIDEO_DURATION));
            long startTime = cursor.getLong(cursor.getColumnIndex(START_TIME));
            long endTime = cursor.getLong(cursor.getColumnIndex(END_TIME));
            String videoPath = cursor.getString(cursor.getColumnIndex(VIDEO_PATH));
            String videoType = cursor.getString(cursor.getColumnIndex(VIDEO_TYPE));

            VideoBean bean = new VideoBean();
            bean.id = anInt;
            bean.videoName = videoName;
            bean.videoSize = videoSize;
            bean.videoDuration = videoDuration;
            bean.startTime = startTime;
            bean.endTime = endTime;
            //  bean.thumbBytes = null; 图片只有在回复给手机和用图片的时候才去拿取
            bean.videoPath = videoPath;
            bean.videoType = videoType;
            videoBeanList.add(bean);
        }
        cursor.close();
        dbManager.closeDatabase();

        if (startIndex >= videoBeanList.size()) {
            return null;
        }
        int endIndex = startIndex + number;
        if (endIndex > videoBeanList.size()) {
            endIndex = videoBeanList.size();
        }
        List<VideoBean> returnList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            returnList.add(videoBeanList.get(i));
        }
        return returnList;
    }

    public Cursor findVideoInfo(String fieldName, Object fieldValue, String destFieldName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        mDatabase = dbManager.getReadableDatabase();
        return mDatabase.query(TABLE_NAME, new String[]{destFieldName}, fieldName + " = ?", new String[]{String.valueOf(fieldValue)}, null, null, null);
    }
}
