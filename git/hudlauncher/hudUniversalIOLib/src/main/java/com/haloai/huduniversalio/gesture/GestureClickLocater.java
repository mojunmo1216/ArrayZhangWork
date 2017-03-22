package com.haloai.huduniversalio.gesture;


import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;

/**
 * 手势识别的最佳深度范围为0~120像素内
 * block 屏幕功能模块的下标
 * area 手势识别划分区的下标
 * author       : wangshengxing;
 * date         : 15/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.gesture;
 * project_name : MyApplication;
 */
public class GestureClickLocater implements IGestureClickLocater {
    /*手势垂直区域分两个区，上面是等分区域，下面是等距区域*/

    /**
     * 识别点击手势，需要识别整个手掌，当手大240像素后，识别失效，此时对应手的深度为43
     */
    public static final double ALTITUDE_HIGH_AREA_VALUE = 43;
    public static final double ALTITUDE_FAR_AREA_VALUE  = 150;//远离摄像头的像素阀值

    //m
    public static final double HAND_LENGTH = 0.18;
    public static final double HAND_WIDTH = 0.1;
    public static final double HAND_RECOGNIZE_WIDTH = 0.12;

    private int mAreaCount = -1;
    private int mAreaStep  = -1;
    private int mAreaIndex = -1;

    @Override
    public int locateClickArea(GestureLocaterPara para) {
        int block = 0;
        if(para.blockNumber==1){
            return 0;
        }
        handlePointData(para);
        if (para.z<=ALTITUDE_HIGH_AREA_VALUE){
            return -1;
//            return findClickIndex(mAreaCount,mAreaIndex,para.blockNumber);
        }else {
            return findClickIndex(mAreaCount,mAreaIndex,para.blockNumber);
        }
    }

    private int handlePointData(GestureLocaterPara para){
        HaloLogger.logE(HudIOConstants.GESTURE_TAG,String.format("locateClickArea para x %s , y %s ,z %s ",para.x,para.y,para.z));
        if (para.z<=ALTITUDE_HIGH_AREA_VALUE){//靠近摄像头
            mAreaCount = 2*(para.blockNumber-1);
            mAreaStep = GesturePoint.GESTRUEAREAWEIDTH/mAreaCount;
            mAreaIndex = para.x/mAreaStep;
            HaloLogger.logE(HudIOConstants.GESTURE_TAG,String.format("locateClickArea result near mAreaStep %s , areaCount %s , mAreaIndex %s",mAreaStep,mAreaCount,mAreaIndex));
            mAreaIndex = -1;
        }else if (para.z>=ALTITUDE_FAR_AREA_VALUE){//
            mAreaCount = 2*(para.blockNumber-1);
            mAreaStep = GesturePoint.GESTRUEAREAWEIDTH/mAreaCount;
            mAreaIndex = para.x/mAreaStep;
            HaloLogger.logE(HudIOConstants.GESTURE_TAG,String.format("locateClickArea result far mAreaStep %s , areaCount %s , mAreaIndex %s",mAreaStep,mAreaCount,mAreaIndex));
            mAreaIndex = -1;
        }else {
            double depth2pexel = depth2pixel(para.z)*HAND_RECOGNIZE_WIDTH;
            mAreaCount = (int)(GesturePoint.GESTRUEAREAWEIDTH/depth2pexel);
            mAreaIndex = (int)(para.x/depth2pexel);
            HaloLogger.logE(HudIOConstants.GESTURE_TAG,String.format("locateClickArea result nomor depth2pexel %s , areaCount %s ,mAreaIndex %s",depth2pexel,mAreaCount,mAreaIndex));
        }
        return 0;
    }

    @Override
    public int getAreaCount(int depthPixel) {
        double depth2pexel = depth2pixel(depthPixel)*HAND_RECOGNIZE_WIDTH;
        int arecount = (int)(GesturePoint.GESTRUEAREAWEIDTH/depth2pexel);
        return arecount;
    }

    /**
     * 识别环境：
     *    1、水平移动
     *    2、竖直移动
     *    3、斜面移动
     * @param para
     * @return
     */
    @Override
    public int locateMovingArea(GestureLocaterPara para) {
        handlePointData(para);
        return mAreaIndex;
    }
    /**
     * 均分点击操作屏幕功能块选的下标
     * @param areaCount 手势识别划分区的总数目
     * @param areaIndex 手势识别划分区的下标
     * @param blockNumber 屏幕功能模块数目
     * @return 屏幕功能模块的下标
     */
    public static int findClickIndex(int areaCount , int areaIndex , int blockNumber){
       return centerRemap(areaIndex,areaCount,blockNumber);
    }

    /**
     * 以一个区域中心下标，左右平均映射到另外一个区域
     * @param areaIndex
     * @param areaCount
     * @param blockNumber
     * @return
     */
    private static int  centerRemap(int areaIndex, int areaCount, int blockNumber) {
        return areaIndex*blockNumber/areaCount;
    }


    /**
     * 从一个区域起点下标平均映射到另外一个区域
     * @param areaIndex
     * @param areaCount
     * @param blockNumber
     * @return
     */
    private static int  remap(int areaIndex, int areaCount, int blockNumber) {
        return areaIndex*blockNumber/areaCount;
    }


    /**
     * 靠近摄像头部分区域，点击操作屏幕功能块选的下标
     * @param areaCount 手势识别划分区的总数目
     * @param areaIndex 手势识别划分区的下标
     * @param blockNumber 屏幕功能模块数目
     * @return 屏幕功能模块的下标
     */
    public static int farAreaClickIndex(int areaCount , int areaIndex , int blockNumber){
        if(areaIndex<areaCount*3/4){
            return 0;
        }else {
            return blockNumber-1;
        }
    }
    /**
     * 远离摄像头部分区域，点击操作屏幕功能块选的下标
     * @param areaCount 手势识别划分区的总数目
     * @param areaIndex 手势识别划分区的下标
     * @param blockNumber 屏幕功能模块数目
     * @return 屏幕功能模块的下标
     */

    public static int nearAreaClickIndex(int areaCount , int areaIndex , int blockNumber){
        // TODO: 15/12/2016 修改右侧下标触发 blockNumber
        if(areaIndex<=(areaCount-blockNumber)){
            return 0;
        }
        return blockNumber-1;
    }

    /**
     * x为深度，右侧为手掌长度的所带的像素个数，总宽度为240个像素
     * 即每个深度对应的每单位长度的像素值(默认手掌长0.18)
     * @param x
     * @return
     */
    public static double depth2pixel(double x){
        return (0.0000002768596284*x*x*x*x - 0.0001835757114*x*x*x + 0.04456662603*x*x - 4.958530095*x + 264.9252091)/HAND_LENGTH;
    }



}
