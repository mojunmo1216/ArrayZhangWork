package com.haloai.hud.hudendpoint.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.haloai.hud.hudendpoint.primarylauncher.HUDApplication;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;

public class HudAMapAnimation {

	public static Animation getCMDListAnmation(){
		//todo hanyu move the value to HudEndPointConstants which convert by dip2px function
		//Animation animation=new TranslateAnimation(200, 0, 0, 0);
		Animation animation=new TranslateAnimation(DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), 133), 0, 0, 0);

		animation.setDuration(800);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}

	public static Animation getStrategyViewAnimation(Context context,boolean flag,int translateY){
		Animation animation;
		if(flag){
			animation=new TranslateAnimation(0, 0, translateY, 0);
		}else{
			animation=new TranslateAnimation(0, 0, 0, -translateY);
		}
		animation.setDuration(1500);
		animation.setInterpolator(context,android.R.anim.accelerate_decelerate_interpolator);
		return animation;
	}

	public static ObjectAnimator startMusicIconRolling(View view){
		ObjectAnimator animator=ObjectAnimator.ofFloat(view, "Rotation",0f,360f);
		animator.setRepeatCount(-1);
		animator.setDuration(2000);
		animator.setRepeatMode(Animation.RESTART);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
		return animator;
	}

	public static ObjectAnimator startRecording(View view){
		ObjectAnimator animator=ObjectAnimator.ofFloat(view, "Alpha",1f,0.2f,1f);
		animator.setRepeatCount(-1);
		animator.setDuration(1000);
		animator.setRepeatMode(Animation.RESTART);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
		return animator;
	}

	public static ObjectAnimator startLoading(View view){
		ObjectAnimator animator=ObjectAnimator.ofFloat(view, "Rotation",0f,360f);
		animator.setRepeatCount(-1);
		animator.setDuration(800);
		animator.setRepeatMode(Animation.RESTART);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
		return animator;
	}

	public static ObjectAnimator startCallIconRolling(View view){
		ObjectAnimator animator=ObjectAnimator.ofFloat(view, "Rotation",0f,-360f);
		animator.setRepeatCount(-1);
		animator.setDuration(2000);
		animator.setRepeatMode(Animation.RESTART);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
		return animator;
	}

	public static void startPoiViewAnimation(View view,int translateX,int duration){
		ObjectAnimator animation=ObjectAnimator.ofFloat(view,"TranslationX",view.getTranslationX(),view.getTranslationX()+translateX);
		animation.setDuration(duration);
		animation.start();

	}

	public static Animation getRouteViewAnimation(int off){

		//Animation animation=new TranslateAnimation(0,0,-100,0);
		Animation animation=new TranslateAnimation(0,0,DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), -67),0);
		animation.setDuration(1300+off*100);
		animation.setInterpolator(new AccelerateInterpolator(2));
		return animation;
	}

	public static void startCancelNaviShowAnimation(RelativeLayout cancel_navi){
		ObjectAnimator animator = ObjectAnimator.ofFloat(cancel_navi, "TranslationX", 0,DisplayUtil.dip2px(HUDApplication.getInstance(), 213));
		animator.setDuration(500);
		animator.start();
	}

	public static void startCancelNaviHideAnimationDelay500(RelativeLayout cancel_navi){
		ObjectAnimator animator = ObjectAnimator.ofFloat(cancel_navi, "TranslationX", DisplayUtil.dip2px(HUDApplication.getInstance(), 213),0);
		animator.setDuration(500);
		animator.start();
	}

	public static void startCancelNaviHideAnimationDelay0(RelativeLayout cancel_navi){
		ObjectAnimator animator = ObjectAnimator.ofFloat(cancel_navi, "TranslationX", DisplayUtil.dip2px(HUDApplication.getInstance(), 213),0);
		animator.setDuration(1);
		animator.start();
	}

	public static void startPropertyAnimation(View v,String propertyName,float value,int duration){
		ObjectAnimator oa = ObjectAnimator.ofFloat(v, propertyName, value);
		oa.setDuration(duration);
		oa.start();
	}

	public static ObjectAnimator startMusicChangeAnimation(View view,int translateX){
		ObjectAnimator animation=ObjectAnimator.ofFloat(view,"TranslationX",view.getTranslationX(),view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX()+translateX,view.getTranslationX());
		animation.setDuration(4000);
		animation.start();
		return animation;
	}

	public static ObjectAnimator startCallingAnimation(View view,int translateX){
		ObjectAnimator animation=ObjectAnimator.ofFloat(view,"TranslationX",view.getTranslationX(),view.getTranslationX()+translateX);
		animation.setDuration(500);
		animation.start();
		return animation;
	}

	public static Animation getLoadingAnimation() {
		//Animation animation = new TranslateAnimation(30,840-300-30,0,0);
		Animation animation = new TranslateAnimation(DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), 20),DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), 340),0,0);

		animation.setDuration(1000);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.REVERSE);
		return animation;
	}

	public static Animation getStartMusicAnimation(){
		Animation scale=new ScaleAnimation(1f,1.32f,1f,1.32f);
		scale.setDuration(1000);
		scale.setRepeatCount(1);
		scale.setRepeatMode(Animation.REVERSE);
		return scale;
	}

	public static Animation getMusicStatusAnimation(){
		Animation scale=new ScaleAnimation(1.32f,1f,1.32f,1f);
		scale.setDuration(1000);
		return scale;
	}


	public static void startCallIconShakeAnimation(View callIcon){
		ObjectAnimator animator = ObjectAnimator.ofFloat(callIcon, "Rotation", 0,0,-50,50,-50,50,-50,50,-50,50,-50,50,-50,0,0,0,0,0,0);
		animator.setDuration(1600);
		animator.setRepeatCount(-1);
		animator.setRepeatMode(Animation.RESTART);
		animator.start();
	}

	public static void startOutgoingNextPageAnimation(RelativeLayout rlContent) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(rlContent, "TranslationX", rlContent.getTranslationX(),rlContent.getTranslationX()-
				DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), 470/1.5f));
		animator.setDuration(350);
		animator.start();
	}

	public static void startOutgoingPrePageAnimation(RelativeLayout rlContent) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(rlContent, "TranslationX", rlContent.getTranslationX(),rlContent.getTranslationX()+
				DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), 470/1.5f));
		animator.setDuration(350);
		animator.start();
	}

	public static Animation getWakeWordInvisibleAnimation(){
		AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(1500);
		animation.setFillAfter(true);
		return animation;
	}

	public static Animation getWakeWordvisibleAnimation(){
		AlphaAnimation animation = new AlphaAnimation(0.0f,1.0f);
		animation.setDuration(1500);
		animation.setFillAfter(true);
		return animation;
	}

	
	
	
	// 行车记录仪动画
	public static ObjectAnimator getNextVideoAnimation(ImageView iv, Context context){
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "TranslationY", iv.getTranslationY(), iv.getTranslationY() - DisplayUtil.dip2px(context, 186f));
		animator.setDuration(1000);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		return animator;
	}

	public static ObjectAnimator getCarRecorderPreVideoAnimation(ImageView iv, Context context){
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "TranslationY", iv.getTranslationY(), DisplayUtil.dip2px(context, 186f) + iv.getTranslationY());
		animator.setDuration(1000);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		return animator;
	}

	public static void startGreenLineAnimation(View mIvLine){
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLine, "scaleX", 1f, 0f);
		animator.setDuration(300);

		ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvLine, "translationX", 0f, DisplayUtil.dip2px(HUDApplication.getInstance().getApplicationContext(), -200f));
		animator2.setDuration(300);
		set.playTogether(animator,animator2);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.start();
	}
}
