package com.haloai.hud.hudendpoint.fragments.amap;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.arwaylib.draw.view.CompassManager;
import com.haloai.hud.hudendpoint.arwaylib.draw.view.SpeedPanelView;
import com.haloai.hud.hudendpoint.fragments.view.ShakeView;
import com.haloai.hud.hudendpoint.primarylauncher.HaloAMapNaviStartActivity;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.utils.HaloLogger;

import org.apache.http.util.EntityUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HudAMapFragmentMain extends Fragment {

	private Context mContext;

	private ViewGroup      mNaviPanelViewgroup;
	private ViewGroup      mCompassViewgroup;
	private TextView       mDirectionTextview;
	private TextView       mSpeedTextview;
	private SpeedPanelView mSpeedView;

	private ImageView mDirectionImageview;

	private ImageView mSystemTimeHourTenImageview;
	private ImageView mSystemTimeHourOneImageview;
	private ImageView mSystemTimeMinuteTenImageview;
	private ImageView mSystemTimeMinuteOneImageview;

	private int[]        mSpeedNumImg       = {R.drawable.smooth_number_0, R.drawable.smooth_number_1, R.drawable.smooth_number_2,
			R.drawable.smooth_number_3, R.drawable.smooth_number_4, R.drawable.smooth_number_5, R.drawable.smooth_number_6,
			R.drawable.smooth_number_7, R.drawable.smooth_number_8, R.drawable.smooth_number_9};
	private boolean      mTimeDisplayEnbale = false;
	private Time mSystemTimer       = null;
	private TimeReceiver mTimeReceiver      = null;

	private CompassManager mCompassManager;
	private IntentFilter mTimeFilter      = null;
	private float        mTargetDirection = 0;
	private List<ObjectAnimator> mAnimators = new ArrayList<>();
	public boolean IS_SHOW = false;
	public boolean IS_UPDATE = false;
	private String mOrientaionText[] = new String[]{"北","西北","西","西南","南","东南","东","东北"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mContext = getActivity();
		View mMainLayout = inflater.inflate(R.layout.fragment_amap_main, container, false);

		mSystemTimeHourTenImageview = (ImageView) mMainLayout.findViewById(R.id.hour_ten_imageview);
		mSystemTimeHourOneImageview = (ImageView) mMainLayout.findViewById(R.id.hour_one_imageview);
		mSystemTimeMinuteTenImageview = (ImageView) mMainLayout.findViewById(R.id.minute_ten_imageview);
		mSystemTimeMinuteOneImageview = (ImageView) mMainLayout.findViewById(R.id.minute_one_imageview);

		mNaviPanelViewgroup = (ViewGroup) mMainLayout.findViewById(R.id.navi_panel_viewgroup);
		mCompassViewgroup = (ViewGroup) mMainLayout.findViewById(R.id.compass_viewgroup);

		mDirectionImageview = (ImageView) mMainLayout.findViewById(R.id.compass_direction_imageview);
		mDirectionImageview.setAlpha(0.5f);
		mSpeedView = (SpeedPanelView) mMainLayout.findViewById(R.id.navi_panel_view);
		mDirectionTextview = (TextView) mMainLayout.findViewById(R.id.compass_textview);
		mSpeedTextview = (TextView) mMainLayout.findViewById(R.id.speed_panel_textview);

		initTimer(mContext);
		initSensor(mContext);

		updateSpeed(0);

		return mMainLayout;
	}

	class TimeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				refreshDisplayTime();
			}
		}
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){
			IS_SHOW = false;
		}else {
			if(IS_SHOW) showSpeedPanelAnim();
		}
	}

	public void prepareSpeedPanelAnim() {
		mAnimators.clear();

		ObjectAnimator anim;
		anim = ObjectAnimator.ofFloat(mNaviPanelViewgroup, "ScaleX",1,0.1f);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);

		anim = ObjectAnimator.ofFloat(mNaviPanelViewgroup, "ScaleY",1,0.1f);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);

		anim = ObjectAnimator.ofFloat(mCompassViewgroup, "alpha",1,0.1f);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);
	}

	public void showSpeedPanelAnim(){
		mAnimators.clear();

		ObjectAnimator anim;
		anim = ObjectAnimator.ofFloat(mNaviPanelViewgroup, "ScaleX",0.1f,1);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);

		anim = ObjectAnimator.ofFloat(mNaviPanelViewgroup, "ScaleY",0.1f,1);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);

		anim = ObjectAnimator.ofFloat(mCompassViewgroup, "alpha",0.1f,1);
		anim.setInterpolator(new LinearInterpolator());
		mAnimators.add(anim);
		startAnim();
	}

	public void startAnim() {
		int duration = 1000;
		for (ObjectAnimator a: mAnimators){
			if(a.isStarted()){
				a.cancel();
			}
			a.setDuration(duration);
			a.start();
		}
	}



	public void showNaviPanel(){
		mNaviPanelViewgroup.setScaleX(1);
		mNaviPanelViewgroup.setScaleY(1);
		mCompassViewgroup.setAlpha(1);
	}


	public void initTimer(Context context) {
		mContext = context;
		initTimeTick();
		refreshDisplayTime();
	}

//	public void requestByGet() throws Exception {
//		String path = "http://www.114time.com/api/clock.php";
//		// 新建一个URL对象
//		URL url = new URL(path);
//		// 打开一个HttpURLConnection连接
//		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//		// 设置连接超时时间
//		urlConn.setConnectTimeout(5 * 1000);
//		//设置为get请求
//		urlConn.setRequestMethod("GET");
//		// 开始连接
//		urlConn.connect();
//		// 判断请求是否成功
//		if (urlConn.getResponseCode() == 200) {
//			// 获取返回的数据
//			byte[] data = StreamUtils.getBytes(urlConn.getInputStream());
//            // 生成 JSON 对象
//			JSONObject result = new JSONObject(new String(data,"UTF-8"));
//			String time = (String)result.get("times");
//			Log.e("YLQTIME","time:"+time);
//		}else {
//			Log.e("YLQTIME","requestfailture");
//		}
//		urlConn.disconnect();
//	}

	private void initTimeTick() {
		mSystemTimer = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		mTimeReceiver = new TimeReceiver();
		mTimeFilter = new IntentFilter();
		mTimeFilter.addAction(Intent.ACTION_TIME_TICK);
		mContext.registerReceiver(mTimeReceiver, mTimeFilter);
		mTimeDisplayEnbale = true;
	}

	public void updateSpeed(int speed) {
		if (mSpeedView != null) {
			mSpeedView.setSpeed(speed);
		}
		if (mSpeedTextview != null) {
			mSpeedTextview.setText("" + speed);
		}

	}

	private void refreshDisplayTime() {
		if (!mTimeDisplayEnbale) {
			return;
		}
		mSystemTimer.setToNow(); // 取得系统时间。
		int hour = mSystemTimer.hour;
		int minute = mSystemTimer.minute;
		mSystemTimeHourTenImageview.setImageResource(mSpeedNumImg[(hour / 10) % 10]);
		mSystemTimeHourOneImageview.setImageResource(mSpeedNumImg[(hour) % 10]);
		mSystemTimeMinuteTenImageview.setImageResource(mSpeedNumImg[(minute / 10) % 10]);
		mSystemTimeMinuteOneImageview.setImageResource(mSpeedNumImg[(minute) % 10]);
	}

	private CompassManager.CompassLister mCompassLister = new CompassManager.CompassLister() {
		@Override
		public void onOrientationChange(float orientation) {
			if (mDirectionTextview != null) {
				if(!IS_UPDATE){
					IS_UPDATE = true;
					mHandler.sendEmptyMessageDelayed(0,500);
					mDirectionTextview.setText(mOrientaionText[((int) (orientation+22.5f)%360)/45]);
				}
			}

		}
	};

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			IS_UPDATE = false;
		}
	};

	public void updatePanelDirection() {
		float direction = normalizeDegree(mTargetDirection * -1.0f);
		String text = "";
		if (direction > 22.5f && direction < 157.5f) {
			// east
			text = "东";

		} else if (direction > 202.5f && direction < 337.5f) {
			// west
			text = "西";
		}

		if (direction > 112.5f && direction < 247.5f) {
			// south
			text = "南";
		} else if (direction < 67.5 || direction > 292.5f) {
			// north
			text = "北";
		}
		// TODO: 16/11/2016 判断方向
		text = "北";
		if (mDirectionTextview != null) {
			mDirectionTextview.setText(text);
		}
	}

	private void initSensor(Context context) {
		mCompassManager = CompassManager.getInstance();
		mCompassManager.init(context);
		//样机的
		mCompassManager.setRotation(180);
		mCompassManager.addCompassLister(mCompassLister);
	}


	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}


}

