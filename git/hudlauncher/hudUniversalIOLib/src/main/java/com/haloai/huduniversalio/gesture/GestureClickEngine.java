package com.haloai.huduniversalio.gesture;

import android.util.Log;

import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 * 实现：
 * 1、判断点击模块的位置
 * 2、移动点选位置
 *
 * author       : wangshengxing;
 * date         : 15/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.gesture;
 * project_name : MyApplication;
 */
public class GestureClickEngine implements IGestureClickEngine{

//    public static final String TAG = GestureClickEngine.class.getSimpleName();
    public static final String TAG = HudIOConstants.GESTURE_TAG;
    public static final boolean IS_DEBUG = true;


    public static final long INPUTER_FILTER_TIME = 10;
    public static final long INPUTER_RESET_TIME  = 5000;
    public static final int  CLICK_FILTER_NUMBER = 2;

//    public static final long INPUTER_FILTER_TIME = 200;

    private static GestureClickEngine mGestureClickEngine = null;

    private GestureStatus mStatus = GestureStatus.STATUS_LOCATING;

    private IGestureClickLocater mLocater = null;

    private Timer mTimer = new Timer();
    private TimerTask mResetTask = null;

    private boolean            mNeedReset          = false;
    private int                mClickCnt           = 0;
    private int                mLastClickAreaIndex = 0;
    private int                mLastMoveAreaIndex  = 0;
    private int                mLastAreaCount      = 0;
    private GestureLocaterPara mLastPara           = null;
    public  int                mCoordXMoveFilter   = 0;
    public  long               mLastHandleTime     = 0;

    private enum GestureStatus{
        STATUS_LOCATING,//初次输入手势，定位在哪个区域
        STATUS_MOVING,//在定位区域后，识别移动行为
    }

    public static GestureClickEngine getInstance(){
        if (mGestureClickEngine == null) {
            mGestureClickEngine = new GestureClickEngine();
        }
        return mGestureClickEngine;
    }

    public GestureClickEngine() {
        reset();
        mLocater = new GestureClickLocater();

    }

    @Override
    public int handleSure() {
        reset();
        return 0;
    }
    public int handleGesturePoint(GesturePoint point, final int blockCount){
        return GesturePoint.parseGesturePoint2(point,blockCount);
    }


    /**
     * 默认数据输入频率不20帧
     * @param point
     * @param blockCount
     * @return -1 时代表此次输入无效
     */
    public int handleGesturePoint2(GesturePoint point, final int blockCount) {
        int areaIndex = -1;
        if(blockCount <=1){
            return 0;
        }

        GestureLocaterPara para = new GestureLocaterPara(point, blockCount);

        /**
         * 1、手势数据过滤
         * 2、手势强制状态重置为点击定位模式
         *      功能区数据发生变化
         * 3、定位模式和移动模式工作
         * 4、重置到定位模式
         *      超时自动重置
         *      外界确认此次输入、取消输入
         */

        if (mStatus == GestureStatus.STATUS_LOCATING){
            areaIndex = mLocater.locateClickArea(para);
            if((areaIndex>=0 && areaIndex< blockCount)){
                mClickCnt++;
                mLastClickAreaIndex += areaIndex;
                mLastMoveAreaIndex += mLocater.locateMovingArea(para);
                mLastAreaCount = mLocater.getAreaCount(para.z);
                if(IS_DEBUG){
                    Log.e(TAG, String.format("handleGesturePoint: ,click_action  areaIndex %s ,moveindex %s ",areaIndex/mClickCnt,mLastMoveAreaIndex/mClickCnt));
                }
                if (mClickCnt>=CLICK_FILTER_NUMBER) {
                    mClickCnt=0;
                    mLastClickAreaIndex /= CLICK_FILTER_NUMBER;
                    mLastMoveAreaIndex /= CLICK_FILTER_NUMBER;
                    mLastPara = para;
                    if(blockCount == 1 ){
                        mStatus = GestureStatus.STATUS_LOCATING;
                    }else {
                        mStatus =GestureStatus.STATUS_MOVING;
                    }
                }
                delayResetTask();

            }else {
                Log.e(TAG, String.format("handleGesturePoint: location error , areaIndex %s ",areaIndex));
            }
        }else if(mStatus == GestureStatus.STATUS_MOVING) {
            int movingIndex = mLocater.locateMovingArea(para);//定位分区的下标
            long curenttime = System.currentTimeMillis();
            if(movingIndex<0){//去除边界条件
                return -1;
            }else if(curenttime-mLastHandleTime<INPUTER_FILTER_TIME){
                return -1;
            }
            mLastHandleTime = curenttime;
            int areaCount = mLocater.getAreaCount(para.z);
            int moveStep = movingIndex-mLastMoveAreaIndex;//本次移动步长
            int difX = (mLastPara.x-para.x);
            mCoordXMoveFilter  = (int) (GestureClickLocater.HAND_RECOGNIZE_WIDTH /4*GestureClickLocater.depth2pixel(para.z));
            if(areaCount != mLastAreaCount){//垂直方向移动导致分区变化,将光标移动到当前的位置
                mLastAreaCount = areaCount;
                mLastMoveAreaIndex = movingIndex;
                if(moveStep>0){
                    Log.e(TAG, String.format("handleGesturePoint: , depth step flip,  moveStep = %s",moveStep));
                }else {
                    Log.e(TAG, String.format("handleGesturePoint: , depth flip "));
                }
                moveStep = 0;
            }else if(moveStep>0 && Math.abs(difX)< mCoordXMoveFilter){//水平移动过滤
                Log.e(TAG, String.format("handleGesturePoint: , x flip ,  moveStep = %s ,filter = %s ",moveStep, mCoordXMoveFilter));
                mLastMoveAreaIndex = movingIndex;
                moveStep = 0;
            }else {//正常移动
                mLastPara = para;
                Log.e(TAG, String.format("handleGesturePoint: , step move ,  moveStep = %s ,x move = %s ",moveStep,difX));
            }
            areaIndex = (moveStep)+mLastClickAreaIndex;//确认本次坐标(点击点的坐标+移动方向)
            if(IS_DEBUG){
                Log.e(TAG, String.format("handleGesturePoint: ,move_action  mLastMoveAreaIndex %s , moveStep %s ,areaIndex %s ,moveindex %s ",mLastMoveAreaIndex,moveStep,areaIndex,movingIndex));
            }
            //边界隔离
            if(!isOutOfRange(areaIndex, blockCount)){
                /**
                 * 在区域内,
                 */
                mLastMoveAreaIndex = movingIndex;//保留上一次定位的坐标
                mLastClickAreaIndex += moveStep; //移动点击点的坐标
                areaIndex = rangeIndex(areaIndex, blockCount);
                // TODO: 15/12/2016  延时触发重置到定位模式
                delayResetTask();
            }else {
                /**
                 * 不在区域内,在最左边和最右边的时候走出边界时:
                 *  1、继续走出边界时，进行锁定
                 *  2、如果往回走，则定位移动步数
                 *
                 */
                mLastMoveAreaIndex = movingIndex;//保留上一次分区的坐标
                areaIndex = rangeIndex(areaIndex, blockCount);
                mLastClickAreaIndex = areaIndex;

            }
            areaIndex = rangeIndex(areaIndex, blockCount);
        }
        return areaIndex;
    }

    @Override
    public boolean isMoveLocating() {
        return mStatus == GestureStatus.STATUS_MOVING;
    }

    /**
     * 调试步骤：
     *  1、水平左右移动边界处理
     *  2、竖直路线方向移动，选中位置保持不变
     */


    private void delayResetTask() {
        // FIXME: 18/12/2016 确认是否存在性能问题
        mNeedReset = false;
        if (mResetTask != null) {
            mResetTask.cancel();
        }
        mResetTask = new TimerTask() {
            @Override
            public void run() {
                if (mNeedReset) {
                    reset();
                }
            }
        };
        mNeedReset = true;
        schedule(mResetTask,INPUTER_RESET_TIME);
    }


    /**
     * 去掉超出边界的值
     * @param index
     * @param areaCount
     * @return
     */
    private static int rangeIndex(int index, int areaCount){
        if (index<=0){
            index=0;
        }else if(index+1>areaCount){
            index = areaCount-1;
        }
        return index;
    }

    /**
     * 是否超出边界
     * @param index
     * @param areaCount
     * @return
     */
    private static boolean isOutOfRange(int index, int areaCount){
        return (index<=0 || index+1>areaCount);
    }

    /**
     * 重置资源
     */
    private void reset(){
        HaloLogger.logE(TAG, "reset() called");
        mNeedReset =false;
        mStatus = GestureStatus.STATUS_LOCATING;
        mClickCnt = 0;
        mLastClickAreaIndex = 0;
        mLastMoveAreaIndex = 0;
        mLastAreaCount = 0;
        mLastPara = null;
        mLastHandleTime = 0;
    }
    /**
     * 在本线程执行一个延时任务
     * @param task
     * @param time
     */
    private void schedule(final TimerTask task,long time){
        mTimer= new Timer();
        mTimer.schedule(task,time);
    }

    private void log(String tag ,String msg){

    }
}
