package com.haloai.huduniversalio.gesture;

import com.haloai.huduniversalio.GesturePoint;

/**
 * author       : wangshengxing;
 * date         : 15/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.gesture;
 * project_name : MyApplication;
 */
public class GestureLocaterPara {
    public int x;
    public int y;
    public int z;
    public float score;
    //点选区域划分个数
    public int blockNumber = 3;

    public GestureLocaterPara(GesturePoint point, int blockCount) {
        this.x = GesturePoint.GESTRUEAREAWEIDTH-point.x;
        this.y = GesturePoint.GESTRUE_AREA_HEIGHT-point.y;
        this.z = GesturePoint.GESTRUE_AREA_ALTITUDE-point.z;
        this.score = point.score;
        this.blockNumber = blockCount;
        rebound();
    }

    /**
     * x轴的数组范围为0到240,但是只有20~220才是有效的
     */
    private void rebound(){
        if(x<20){
            x=0;
        }if(x>220){
            x=220;
        }
    }
}
