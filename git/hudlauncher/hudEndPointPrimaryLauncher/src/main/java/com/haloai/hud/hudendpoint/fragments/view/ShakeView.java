package com.haloai.hud.hudendpoint.fragments.view;


import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.haloai.hud.hudendpoint.fragments.amap.HudAMapFragmentMain;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;

public class ShakeView extends View {

	private Context mContext;
	private int[] mIndex = new int[11];
	private int[] mRands = new int[11];
	private int mCount;
	Random rand = new Random(System.currentTimeMillis());
	private int mY ; // 220;
	private int mLeftColor=Color.rgb(33,180,193);
	private int mRightColor=Color.rgb(20,218,121);
	private int HALF_WAVE_LENGTH ;

	
	public ShakeView(Context context) {
		super(context);
		this.mContext = context;
		
		mY = DisplayUtil.dip2px(mContext,147);//220px
		HALF_WAVE_LENGTH = DisplayUtil.dip2px(mContext,13);//20px
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	// Paint p = new Paint();
	// p.setStrokeWidth(5);
	// p.setStyle(Paint.Style.STROKE);
	// p.setColor(Color.BLACK);
	// p.setAntiAlias(true);
	//
	// /**
	// * 要求：画出波形图 1.一个高一个低 --> 根据循环变量i%2是否等于0来控制 2.两边低中间高 -->
	// * 根据循环变量i是否大于等于MAX/2来控制 3.随机高度 --> 使用Random控制在原有标准基础上有一定的偏移量
	// * 4.能够通过int型控制整个的整体变化效果--int为0-100,0为直线，100为最高峰的位置
	// * 5.每次int不为0时，效果显示就是最中间为int+偏移量，两边为int/2+偏移量 6.随机的只是当前波的最高处位置而已
	// */
	//
	// // 屏幕大小：width280 * height840
	// // 波总宽度：500
	// // 单个波宽： 20
	// // 单向波高：100
	// // first point left： 140 120
	// // first point up： 140+(isUp +index else -index，isAdd) 120+20/2
	// // first point right：140 120+20
	// // 最后一个波的位置：140
	// // isAdd 0 1 2 3 4 5 6 7 8
	// // index/5 index/4 index/3 index/2 index index/2 index/3 index/4 index/5
	// // step 1
	// // cycle 20ms
	// int left_x = 140;
	// int left_y = 120;
	// int up_x = 140;
	// int up_y = 120 + 20 / 2;
	// int right_x = 140;
	// int right_y = 120 + 20;
	//
	// Random rand = new Random(System.currentTimeMillis());
	// rand.setSeed(System.currentTimeMillis() + rand.nextLong());
	// for (int i = 0; i < 9; i++) {
	// Path path = new Path();
	//
	// boolean isUp = true;
	// if (i % 2 != 0) {
	// isUp = false;
	// }
	//
	// int current = (mCount + 1) / (Math.abs(4 - i) + 1);
	//
	// int offset = 0;
	// if (current / 4 != 0)
	// offset = rand.nextInt(current / 4) - (current) / 8;//
	// -current/2~current/2
	// path.moveTo(left_x, left_y + i * 20);
	// int x = isUp ? current : -current;
	// x += offset;
	// path.quadTo(up_x + x, up_y + i * 20, right_x, right_y + i * 20);
	//
	// canvas.drawPath(path, p);
	//
	// }
	// Paint paint = new Paint();
	// paint.setColor(Color.RED);
	// paint.setTextSize(100);
	// canvas.rotate(90, 140, 700);
	// canvas.drawText("" + mIndex, 140, 700, paint);
	// }

	int mCur = 0;				
	private int mStep;
	private Paint mPaint;

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint = new Paint();
		mPaint.setStrokeWidth(1.5f);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);

		int left_x = DisplayUtil.dip2px(mContext,199);//60+28+210=298 ;
		int left_y = mY;
		int up_x = DisplayUtil.dip2px(mContext,205);//60+28+210+ 20 / 2 = 308;
		int up_y = mY ;
		int right_x =  DisplayUtil.dip2px(mContext,212);// 60+28+210+ 20=318;
		int right_y = mY ;

		Path path = new Path();

		Shader mShader = null;
		mShader = new LinearGradient(0, left_y , right_x + mIndex.length * HALF_WAVE_LENGTH + left_x ,
				right_y, new int[] { Color.TRANSPARENT,
				mLeftColor,mRightColor, Color.TRANSPARENT },
				new float[] { 0, 0.15f, 0.85f, 1 }, Shader.TileMode.REPEAT);
		mPaint.setShader(mShader);

		//if voice power level is not zero
		if (mIndex[5] != 0) {
			canvas.drawLine(0, left_y , left_x, left_y, mPaint);
			for (int i = 0; i < mIndex.length; i++) {
				float offset = (mCur++ * (mStep - Math.abs(5 - i + 0.3f)));
				if (i % 2 != 0) {
					offset = -offset;
				}

				path.moveTo(left_x+ i * HALF_WAVE_LENGTH, left_y );
				path.quadTo(up_x + i * HALF_WAVE_LENGTH, up_y+ offset + mRands[i] , right_x + i * HALF_WAVE_LENGTH,
						right_y);
			}
			canvas.drawPath(path, mPaint);

			canvas.drawLine(right_x+ (mIndex.length-1) * HALF_WAVE_LENGTH, right_y , right_x+ (mIndex.length) * HALF_WAVE_LENGTH + left_x,
					right_y , mPaint);
		}else{
			//canvas.drawLine(0, left_y, right_x + mIndex.length * HALF_WAVE_LENGTH + 60+28+210,right_y, mPaint);		
			canvas.drawLine(0, left_y, right_x + mIndex.length * HALF_WAVE_LENGTH + left_x,right_y, mPaint);		
		}

/*		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(100);
		canvas.rotate(90, 140, 700);
		canvas.drawText("" + mIndex[4], 140, 700, paint);*/
	}

	/**
	 * i <=100 && i>=0
	 * 
	 * @param
	 */
	public void draw(int h, final Handler handler) {
		int up=DisplayUtil.dip2px(mContext,7);//10px
		int i=DisplayUtil.dip2px(mContext,(h*2)/3);
		if (i < 0)
			i = 0;
		if (i > up)
			i = up;
		mIndex = new int[11];
		mRands = new int[11];
		if (i != 0) {
			for (int t = 0; t < mIndex.length; t++) {
				mIndex[t] = i - Math.abs(5 - t) * 10 >= 0 ? i - Math.abs(5 - t)
						* 10 : 0;
				if (mIndex[t] / 2 > 0) {
					mRands[t] = rand.nextInt(mIndex[t] / 2) - mIndex[t] / 4;
				} else {
					mRands[t] = 0;
				}
			}
		}

		mCur = 0;
		mStep = 4;
		int total = 100;// ms
		final long timeStep = total / (mIndex[5] / mStep + 1) >= 1 ? total
				/ (mIndex[5] / mStep + 1) : 1;
		new Thread() {
			public void run() {
				for (int i = 0; i * mStep <= mIndex[5]; i++) {
					handler.sendEmptyMessage(0);
					SystemClock.sleep(timeStep);
				}
			}
		}.start();
	}
	
	public void setLocationY(int y,Handler handler){
		this.mY = y;
		handler.sendEmptyMessage(0);
	}
	public void setLineColor(int left,int right,Handler handler){
		this.mLeftColor=left;
		this.mRightColor=right;
		handler.sendEmptyMessage(0);
	}
}
