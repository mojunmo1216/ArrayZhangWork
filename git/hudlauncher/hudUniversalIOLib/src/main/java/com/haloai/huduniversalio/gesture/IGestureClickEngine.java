package com.haloai.huduniversalio.gesture;

import com.haloai.huduniversalio.GesturePoint;

/**
 * 使用：
 *  1、handleGesturePoint 返回功能区的定位
 *  2、确认本次操作
 *      一定时间没有操作屏幕后，再次回到手势时重新定位
 *      本次输入后，需要重新再移动
 *
 * author       : wangshengxing;
 * date         : 15/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.gesture;
 * project_name : MyApplication;
 */
public interface IGestureClickEngine {
    /**
     *
     * @param point
     * @param blockCount 移动区域的个数
     * @return 返回areaIndex
     */
    int handleGesturePoint(GesturePoint point, int blockCount);

    /**
     * 确认本次输入
     * @return
     */
    int handleSure();

    /**
     * 是否在完成点击，正在定位移动
     * @return
     */
    boolean isMoveLocating();
}
