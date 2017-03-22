package com.haloai.huduniversalio.gesture;

/**
 * author       : wangshengxing;
 * date         : 15/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.gesture;
 * project_name : MyApplication;
 */
public interface IGestureClickLocater {
    /**
     * 计算手势点选的功能块定位
     * @return  -1 返回非法
     */
    int locateClickArea(GestureLocaterPara para);

    /**
     * 定位识别区的下标
     * @param para
     * @return -1 返回非法
     */
    int locateMovingArea(GestureLocaterPara para);

    /**
     * 获取对应的深度值对应的选区个数
     * @return
     */
    int getAreaCount(int depthPixel);
}
