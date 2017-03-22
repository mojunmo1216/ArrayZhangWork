package com.haloai.huduniversalio;

import com.haloai.huduniversalio.gesture.GestureClickEngine;
import com.haloai.huduniversalio.gesture.GestureClickLocater;
import com.haloai.huduniversalio.gesture.GestureLocaterPara;
import com.haloai.huduniversalio.gesture.IGestureClickLocater;

/**
 * Created by ylq on 2016/11/12.
 */
public class GesturePoint {
    public int x;//坐标方向与安卓标准方向相反
    public int y;
    public int z;
    public float score;
    public static int GESTRUEAREAWEIDTH = 240;
    public static int GESTRUE_AREA_ALTITUDE = 180;
    public static int GESTRUE_AREA_HEIGHT = 180;//待定

    public GesturePoint(){};

    public GesturePoint(int x,int y,int z,float score){
        this.x = x;
        this.y = y;
        this.z = z;
        this.score = score;
    }

    static public int testParseGesturePoint(GesturePoint point, int areaCount){
        IGestureClickLocater locater = new GestureClickLocater();
        return locater.locateClickArea(new GestureLocaterPara(point,4));
    }

    static public int parseGesturePoint(GesturePoint point, int areaCount) {
       return GestureClickEngine.getInstance().handleGesturePoint(point, areaCount);
    }

    static public int parseGesturePoint2(GesturePoint point, int areaCount){
        if (areaCount == 3){
            if (point.x < 95){
                return 2;
            }else if ( point.x > 145){
                return 0;
            }else {
                return 1;
            }
        }
        if (areaCount < 1 || areaCount >6){
            return -1;
        }
        int size = GESTRUEAREAWEIDTH/areaCount;
        if (point.x == 0){
            return areaCount-1;
        }
        if (point.x == GESTRUEAREAWEIDTH){
            return 0;
        }
        return areaCount - 1 - point.x/size;

    }
}
