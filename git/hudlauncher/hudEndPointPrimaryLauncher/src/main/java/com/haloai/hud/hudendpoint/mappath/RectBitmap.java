package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.haloai.hud.utils.HaloLogger;


/**
 * Created by wangshengxing on 16/5/27.
 */
public class RectBitmap {
    private boolean[][] mBitmap = null;
    private int mWidth;
    private int mHeight;
    private Canvas mCanvas;

    private Rect mWindowRect;

    private Paint truePaint = new Paint();
    private Paint falsePaint = new Paint();

    public RectBitmap(Canvas canvas, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mCanvas = canvas;
        this.mWindowRect = new Rect(0,0,width,height);
        mBitmap = new boolean[height][width];
        initPaint();
    }

    public void initPaint(){
        truePaint.setColor(Color.BLACK);
        truePaint.setStrokeWidth(1);
        truePaint.setStyle(Paint.Style.STROKE);
        falsePaint.setColor(Color.WHITE);
        falsePaint.setStrokeWidth(1);
        falsePaint.setStyle(Paint.Style.STROKE);

    }

    /**
     * 加一个一个矩形数据
     */
    public void refreshView(){
        for (int i = 0; i < mHeight ; i++) {
            for (int j = 0; j < mWidth; j++) {
                if(mBitmap[i][j]){
                    mCanvas.drawPoint(i,j,truePaint);
                }else {
                    mCanvas.drawPoint(i,j,falsePaint);
                }
            }
        }
    }
    /**
     * 加一个一个矩形数据
     */
    public Rect popRect(Point point, Rect size) {
        Rect result = null;

        return result;
    }
    private static final String WSX = "wangshengxing";
    private static final boolean DEBUG_MODE = true;
    /**
     * 加一个一个矩形数据
     */
    public boolean pushRect(Rect pushRect) {
        Rect fitRect = RectUtils.intersect(mWindowRect,pushRect);
        if(DEBUG_MODE){
            HaloLogger.logI(WSX,"pushRect ,fitRect is "+fitRect);
        }
        if (fitRect.height()<0 || fitRect.width()<0){
            return false;
        }
        rPushRect(fitRect);
        return true;
    }

    /**
     * 加一个已经经过检测的矩形
     */
    private boolean rPushRect(Rect fitRect){
        for (int i = 0; i < fitRect.height() ; i++) {
            for (int j = 0; j < fitRect.width(); j++) {
                mBitmap[i][j] = true;
            }
        }
        return true;
    }

}
