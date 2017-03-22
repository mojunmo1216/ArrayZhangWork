package com.haloai.hud.hudendpoint.fragments.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.utils.HaloLogger;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/7/15 14:58
 * 修改人：zhang
 * 修改时间：2016/7/15 14:58
 * 修改备注：
 */
public class VolumeRingView extends View {
    private double mOutsideCircleRadius = 0;
    private int WIDTH = 0;
    private int HEIGHT = 0;
    private float VOLUME_MAX = 300f;
    private float VOLUME_MIN = 0f;
    private Bitmap mVolumeBackgroundBitmap = null;
    private Bitmap mFullVolumePreBitmap = null;
    private Bitmap mVolumeBackClipBitmap = null;
    private Bitmap mFullVolumeClipBitmap = null;
    private Canvas mTempCanvas = null;
    private float mVolume = 0f;
    private float mCurrentVolume = 0f;
    private Path mPath = new Path();
    private RectF mVolumeRectF = null;
    private boolean mClockWise = true;

    public VolumeRingView(Context context) {
        super(context);
    }

    public VolumeRingView(Context context, int width, int height) {
        super(context);

        WIDTH = width;
        HEIGHT = height;

        //生成最大速度的底图
        mVolumeBackgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.soundview_voice_ring_bg);
        Bitmap target = Bitmap.createBitmap(width, height, mVolumeBackgroundBitmap.getConfig());
        Canvas temp_canvas = new Canvas(target);
        temp_canvas.drawBitmap(mVolumeBackgroundBitmap, null, new Rect(0, 0, target.getWidth(), target.getHeight()), null);
        mVolumeBackgroundBitmap = target;

        //创建一个对最大速度进行裁剪之后的底图,最终绘制到画布上的是该图
        mVolumeBackClipBitmap = Bitmap.createBitmap(mVolumeBackgroundBitmap.getWidth(), mVolumeBackgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mVolumeBackClipBitmap);

        // 音量环前景图片准备
        mFullVolumePreBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.soundview_voice_ring);
        Bitmap target11 = Bitmap.createBitmap(width, height, mFullVolumePreBitmap.getConfig());
        Canvas temp_canvas11 = new Canvas(target11);
        temp_canvas11.drawBitmap(mFullVolumePreBitmap, null, new Rect(0, 0, target11.getWidth(), target11.getHeight()), null);
        mFullVolumePreBitmap = target11;

        mFullVolumeClipBitmap = Bitmap.createBitmap(mFullVolumePreBitmap.getWidth(), mFullVolumePreBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mFullVolumeClipBitmap);

        //该矩形对应的外切圆的半径
        mOutsideCircleRadius = Math.sqrt(Math.pow(WIDTH / 2, 2) + Math.pow(HEIGHT / 2, 2));
        //该view对应的边框矩形
        mVolumeRectF = new RectF(0, 0, WIDTH, HEIGHT);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        //对背景图进行裁剪。
        cutSpeedFullBitmap(mVolumeBackClipBitmap, 0.405, mVolume, 0.88f, mVolumeBackgroundBitmap, false);
        canvas.drawBitmap(mVolumeBackClipBitmap, 0, 0, null);

        //根据当前速度对最大音量环背景图进行裁剪
        cutSpeedFullBitmap(mFullVolumeClipBitmap, 0.5, mCurrentVolume, 0.76f, mFullVolumePreBitmap, false);

        //将裁剪后得到的图片绘制到画布上
        canvas.drawBitmap(mFullVolumeClipBitmap, 0, 0, null);

        canvas.restore();
    }

    private Bitmap mCurrentVolumeRingPreBitmap;

    public void createCurrentVolumeRingBitmap(float currentVolume) {
        // 裁剪当前音量环bitmap当成底图使用。
        mCurrentVolume = currentVolume;
        mClockWise = true;
        mCurrentVolumeRingPreBitmap = Bitmap.createBitmap(mFullVolumePreBitmap.getWidth(), mFullVolumePreBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        cutSpeedFullBitmap(mFullVolumeClipBitmap, 0.5, mCurrentVolume, 0.76f, mFullVolumePreBitmap, true);
    }

    private void cutSpeedFullBitmap(Bitmap volumeClipBitmap, double startPercent, float volume, float factor, Bitmap fullBitmap, boolean b) {
        //创建新画布(不能复用该画布,否则无法达到连续裁剪形成的动态效果)
        if (b) {
            mTempCanvas = new Canvas(mCurrentVolumeRingPreBitmap);
        } else {
            mTempCanvas = new Canvas(volumeClipBitmap);
        }
        //清空画布
        mTempCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);

        //创建用于裁剪的path(最终就是将画布中path围成的部分裁剪出来)
        if (mClockWise) {
            createShunPath(startPercent, volume, factor);
        } else {
            createNiPath(startPercent, volume, factor);
        }

        //根据path对画布进行裁剪,此处采用的模式是相交,也就是保留画布上的path围起来的部分,其他部分裁剪掉
        mTempCanvas.clipPath(mPath, Region.Op.INTERSECT);
        //将最大速度底图绘制到被裁剪了的画布上
        if (mClockWise) {
            mTempCanvas.drawBitmap(fullBitmap, 0, 0, null);
        } else {
            // 在此处，用mCurrentVolumeRingPreBitmap或者mVolumeBackgroundBitmap
            if (fullBitmap == mVolumeBackgroundBitmap) {
                mTempCanvas.drawBitmap(fullBitmap, 0, 0, null);
            } else {
                mTempCanvas.drawBitmap(mCurrentVolumeRingPreBitmap, 0, 0, null);
            }
        }
    }

    private void createShunPath(double startPercent, float volume, float factor) {
        mPath = new Path();
        mPath.moveTo(WIDTH / 2, HEIGHT / 2);
        double fromPercent = startPercent;
        float linetoX = (float) (mVolumeRectF.right / 2 + mOutsideCircleRadius * Math.sin(Math.PI * 2 * fromPercent));
        float linetoY = (float) (mVolumeRectF.bottom / 2 - mOutsideCircleRadius * Math.cos(Math.PI * 2 * fromPercent));
        mPath.lineTo(linetoX, linetoY);
        double toPercent1 = fromPercent + (volume / VOLUME_MAX) * factor;
        double toPercent2 = 0;
        if (toPercent1 > 1) {
            toPercent2 = toPercent1 - 1;
            toPercent1 = 1;
        }

        if (toPercent1 > 0.625f) {
            mPath.lineTo(0, mVolumeRectF.bottom);
        }
        if (toPercent1 > 0.875f) {
            mPath.lineTo(0, 0);
        }
        linetoX = (float) (mVolumeRectF.right / 2 + mOutsideCircleRadius * Math.sin(Math.PI * 2 * toPercent1));
        linetoY = (float) (mVolumeRectF.bottom / 2 - mOutsideCircleRadius * Math.cos(Math.PI * 2 * toPercent1));
        mPath.lineTo(linetoX, linetoY);

        if (toPercent2 > 0) {
            if (toPercent2 > 0.125f) {
                mPath.lineTo(mVolumeRectF.right, 0);
            }
            if (toPercent2 > 0.375f) {
                mPath.lineTo(mVolumeRectF.right, mVolumeRectF.bottom);
            }
            if (toPercent2 > 0.625f) {
                mPath.lineTo(0, mVolumeRectF.bottom);
            }
            if (toPercent2 > 0.875f) {
                mPath.lineTo(0, 0);
            }
            linetoX = (float) (mVolumeRectF.right / 2 + mOutsideCircleRadius * Math.sin(Math.PI * 2 * toPercent2));
            linetoY = (float) (mVolumeRectF.bottom / 2 - mOutsideCircleRadius * Math.cos(Math.PI * 2 * toPercent2));
            mPath.lineTo(linetoX, linetoY);
        }

        mPath.close();
    }

    private void createNiPath(double startPercent, float volume, float factor) {
        mPath = new Path();
        mPath.moveTo(WIDTH / 2, HEIGHT / 2);
        double fromPercent = startPercent;
        float linetoX = mVolumeRectF.right;
        float linetoY = mVolumeRectF.bottom;
        mPath.lineTo(linetoX, linetoY);
        double toPercent1 = fromPercent + (volume / VOLUME_MAX) * factor;
        double toPercent2 = 0;
        if (toPercent1 > 1) {
            toPercent2 = toPercent1 - 1;
            toPercent1 = 1;
        }
        //toPercent2 必须小于 0.25
        if (toPercent2 >= 0.25) {
            toPercent2 = 0.25;
        }

        if (toPercent2 <= 0.125 && toPercent2 > 0) {
            mPath.lineTo(mVolumeRectF.right, 0);
        }
        if (toPercent2 <= 0.25 && toPercent2 > 0) {
            linetoX = (float) (mVolumeRectF.right / 2 + mOutsideCircleRadius * Math.sin(Math.PI * 2 * toPercent2));
            linetoY = (float) (mVolumeRectF.bottom / 2 - mOutsideCircleRadius * Math.cos(Math.PI * 2 * toPercent2));
            mPath.lineTo(linetoX, linetoY);
            mPath.close();
            return;
        }

        if (toPercent1 >= 0.375 && toPercent1 <= 1) {
            mPath.lineTo(mVolumeRectF.right, 0);
        }
        if (toPercent1 >= 0.375 && toPercent1 <= 0.875) {
            mPath.lineTo(0, 0);
        }
        if (toPercent1 >= 0.375 && toPercent1 <= 0.625) {
            mPath.lineTo(0, mVolumeRectF.bottom);
        }
        linetoX = (float) (mVolumeRectF.right / 2 + mOutsideCircleRadius * Math.sin(Math.PI * 2 * toPercent1));
        linetoY = (float) (mVolumeRectF.bottom / 2 - mOutsideCircleRadius * Math.cos(Math.PI * 2 * toPercent1));
        mPath.lineTo(linetoX, linetoY);
        mPath.close();
    }


    /**
     * 该方法由于涉及到更新view，必须在主线程中调用。
     *
     * @param volume        背景音量值
     * @param currentVolume 前景音量值
     */
    public void setVolume(float volume, float currentVolume, boolean clockwise) {
        mClockWise = clockwise;
        if (volume < VOLUME_MIN) {
            volume = VOLUME_MIN;
        } else if (volume > VOLUME_MAX) {
            volume = VOLUME_MAX;
    }
        mVolume = volume;

        if (currentVolume < VOLUME_MIN) {
            currentVolume = VOLUME_MIN;
        } else if (currentVolume > VOLUME_MAX) {
            currentVolume = VOLUME_MAX;
        }
        mCurrentVolume = currentVolume;
        invalidate();
    }
}
