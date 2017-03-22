package com.haloai.huduniversalio.gesture;

/**
 * author       : wangshengxing;
 * date         : 20/12/2016;
 * email        : wangshengxing@haloai.com;
 * package_name : com.haloai.huduniversalio.utils;
 * project_name : MyApplication;
 */
public class GestureCode {
    public int   type  = 0;
    public float score = 0;

    public GestureCode(int type) {
        this.type = type;
    }

    public GestureCode(int type, float score) {
        this.type = type;
        this.score = score;
    }
}
