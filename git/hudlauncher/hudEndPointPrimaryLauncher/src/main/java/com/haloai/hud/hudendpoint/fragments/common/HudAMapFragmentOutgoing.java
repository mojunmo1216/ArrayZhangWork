package com.haloai.hud.hudendpoint.fragments.common;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.animation.HudAMapAnimation;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.HudIOConstants;

public class HudAMapFragmentOutgoing extends Fragment {

	private FrameLayout mOutgoingView;
	private TextView mTvTitle;
	private TextView mTvCallInfo;
	private LinearLayout mLlCalling;
	private RelativeLayout mRlContent;
	private Context mContext ;
	private RelativeLayout mRlFirstPage;
	private RelativeLayout mRlSecondPage;
	private RelativeLayout mRlThirdPage;
	private LinearLayout mLlPoints;
	private LinearLayout mLlListHead;
//	private ShakeView mShakeView;
	
	private int mCurrentPositionPoint=0;
	
	private float DP_235;
	private float DP_145;
	private float DP_30;
	private float DP_23;
	private float DP_62;
	private float DP_46;
	private float DP_7;
	private float DP_5;
	private float DP_2;
	private float DP_1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		
		DP_235 = DisplayUtil.dip2px(mContext, 235/1.5f);
		DP_145 = DisplayUtil.dip2px(mContext, 145/1.5f);
		DP_62 = DisplayUtil.dip2px(mContext, 62/1.5f);
		DP_46 = DisplayUtil.dip2px(mContext, 46/1.5f);
		DP_30 = DisplayUtil.dip2px(mContext, 30/1.5f);
		DP_23 = DisplayUtil.dip2px(mContext, 23/1.5f);
		DP_7 = DisplayUtil.dip2px(mContext, 35/1.5f);
		DP_5 = DisplayUtil.dip2px(mContext, 5/1.5f);
		DP_2 = DisplayUtil.dip2px(mContext, 2);
		DP_1 = DisplayUtil.dip2px(mContext, 1);

		
		mOutgoingView = (FrameLayout) View.inflate(getActivity(), R.layout.fragment_amap_outgoing, null);
		mTvTitle = (TextView) mOutgoingView.findViewById(R.id.amap_outgoing_title);
		mTvCallInfo = (TextView) mOutgoingView.findViewById(R.id.amap_outgoing_call_info);
		mRlContent = (RelativeLayout) mOutgoingView.findViewById(R.id.amap_outgoing_content);
		mRlFirstPage = (RelativeLayout) mOutgoingView.findViewById(R.id.amap_outgoing_first_page);
		mRlSecondPage = (RelativeLayout) mOutgoingView.findViewById(R.id.amap_outgoing_second_page);
		mRlThirdPage = (RelativeLayout) mOutgoingView.findViewById(R.id.amap_outgoing_third_page);
		mLlPoints = (LinearLayout) mOutgoingView.findViewById(R.id.amap_outgoing_points);
		mLlCalling = (LinearLayout) mOutgoingView.findViewById(R.id.amap_outgoing_ll_calling);
		mLlListHead = (LinearLayout) mOutgoingView.findViewById(R.id.amap_outgoing_list_head);

		return mOutgoingView;
	}
	
	/**
	 * 设置联系人列表
	 * @param contactNames, countPage
	 */
	public void showContactNames(List<String> contactNames ,int countPage){
		if(contactNames==null || contactNames.size()<=0){
			return;
		}
		mTvTitle.setText("请选择第几个:");
		mRlSecondPage.setVisibility(View.INVISIBLE);
		mRlThirdPage.setVisibility(View.INVISIBLE);
		fullPage(contactNames.subList(
				0, contactNames.size()>=4?4:contactNames.size()),mRlFirstPage);
		mLlPoints.removeAllViews();
		addPoint2Points();
		if(contactNames.size()>4){
			mRlSecondPage.setVisibility(View.VISIBLE);
			fullPage(contactNames.subList(
					4, contactNames.size()>=8?8:contactNames.size()),mRlSecondPage);
			addPoint2Points();
		}
		if(contactNames.size()>8){
			mRlThirdPage.setVisibility(View.VISIBLE);
			fullPage(contactNames.subList(
					8, contactNames.size()>=12?12:contactNames.size()),mRlThirdPage);
			addPoint2Points();
		}
	}

	private void addPoint2Points() {
		View point = new View(mContext);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)DP_7, (int)DP_2);
		if(mLlPoints.getChildCount()>=1){
			params.setMargins((int)DP_1, 0,(int)DP_1,0);
			point.setBackgroundResource(R.drawable.call_uncheck_point);
		}else{
			point.setBackgroundResource(R.drawable.call_check_point);
		}
		point.setLayoutParams(params);
		mLlPoints.addView(point);
	}

	private void fullPage(List<String> subList, RelativeLayout rlPage) {
		Log.e("speech_info", "size"+subList.size());
		Log.e("speech_info", "subList"+subList.toString());
		rlPage.removeAllViews();
		Item firstItem = null;
		Item secondItem = null;
		Item thirdItem = null;
		Item forthItem = null;
		
		RelativeLayout.LayoutParams firstParams = null;
		RelativeLayout.LayoutParams secondParams = null;
		RelativeLayout.LayoutParams thirdParams = null;
		RelativeLayout.LayoutParams forthParams = null;
		switch(subList.size()){
		case 2:
			secondItem = createItemWithResAndStr(R.drawable.call_second,subList.get(1));
		case 1:
			firstItem = createItemWithResAndStr(R.drawable.call_first,subList.get(0));
			firstParams = new LayoutParams((int)DP_235, (int)DP_30);
			firstParams.leftMargin = 0;
			firstParams.topMargin = (int) DP_23;
			firstItem.layout.setLayoutParams(firstParams);
			if(secondItem == null){
				break;
			}
			secondParams = new LayoutParams((int)DP_235, (int)DP_30);
			secondParams.leftMargin = (int)DP_62+(int)DP_235;
			secondParams.topMargin = (int)DP_23;
			secondItem.layout.setLayoutParams(secondParams);
			break;
		case 4:
			forthItem = createItemWithResAndStr(R.drawable.call_forth,subList.get(3));
		case 3:
			thirdItem = createItemWithResAndStr(R.drawable.call_third,subList.get(2));
			secondItem = createItemWithResAndStr(R.drawable.call_second,subList.get(1));
			firstItem = createItemWithResAndStr(R.drawable.call_first,subList.get(0));
			firstParams = new LayoutParams((int)DP_235, (int)DP_30);
			firstParams.leftMargin = 0;
			firstParams.topMargin = 0;
			firstItem.layout.setLayoutParams(firstParams);
			secondParams = new LayoutParams((int)DP_235, (int)DP_30);
			secondParams.leftMargin = (int)DP_62+(int)DP_235;
			secondParams.topMargin = 0;
			secondItem.layout.setLayoutParams(secondParams);			
			thirdParams = new LayoutParams((int)DP_235, (int)DP_30);
			thirdParams.leftMargin = 0;
			thirdParams.topMargin = (int)DP_46;
			thirdItem.layout.setLayoutParams(thirdParams);			
			if(forthItem == null){
				break;
			}
			forthParams = new LayoutParams((int)DP_235, (int)DP_30);
			forthParams.leftMargin = (int)DP_62+(int)DP_235;
			forthParams.topMargin = (int)DP_46;
			forthItem.layout.setLayoutParams(forthParams);			
			break;
		default:
			break;
		}
		List<Item> items = new ArrayList<HudAMapFragmentOutgoing.Item>();
		items.add(firstItem);
		items.add(secondItem);
		items.add(thirdItem);
		items.add(forthItem);
		
		for (Item item : items) {
			if(item!=null && item.layout!=null){
				rlPage.addView(item.layout);
			}
		}
		HaloLogger.logE("HELONG_FIX","WIDTH:"+rlPage.getWidth());
	}
	
	private class Item{
		LinearLayout layout;
		ImageView number;
		TextView name;
	}
	
	private Item createItemWithResAndStr(int res,String name){
		if(name==null || "".equals(name)){
			return null;
		}
		Item item = new Item();
		item.layout = (LinearLayout) View.inflate(mContext, R.layout.activity_amap_outgoing_item, null);
		item.number = (ImageView) item.layout.findViewById(R.id.amap_outgoing_item_icon);
		item.number.setImageResource(res);
		item.name = (TextView) item.layout.findViewById(R.id.amap_outgoing_item_name);
		item.name.setText(name);
		return item;
	}

	/**
	 * 显示上一页
	 */
	public void prePage(){
		HudAMapAnimation.startOutgoingPrePageAnimation(mRlContent);
		mCurrentPositionPoint--;
		changeSelectPoint();
	}
	
	/**
	 * 显示下一页
	 */
	public void nextPage(){
		HudAMapAnimation.startOutgoingNextPageAnimation(mRlContent);
		mCurrentPositionPoint++;
		changeSelectPoint();
	}
	
	private void changeSelectPoint() {
		for(int i=0 ; i<mLlPoints.getChildCount() ; i++){
			View point = mLlPoints.getChildAt(i);
			if(i==mCurrentPositionPoint){
				point.setBackgroundResource(R.drawable.call_check_point);
			}else{
				point.setBackgroundResource(R.drawable.call_uncheck_point);
			}
		}
	}

	/**
	 * 根据传入的姓名和号码集合来填充列表
	 */
	public void showContactNumbers(String name , List<String> numbers){
		mTvTitle.setText("关于"+name+"的号码:");
		mRlContent.setTranslationX((int)DP_145);
		mLlPoints.removeAllViews();
		mRlFirstPage.removeAllViews();
		Item firstItem = createItemWithResAndStr(R.drawable.call_first,numbers.size()>=1?numbers.get(0):"");
		Item secondItem = createItemWithResAndStr(R.drawable.call_second,numbers.size()>=2?numbers.get(1):"");
		Item thirdItem = createItemWithResAndStr(R.drawable.call_third,numbers.size()>=3?numbers.get(2):"");
		Item forthItem = createItemWithResAndStr(R.drawable.call_forth,numbers.size()>=4?numbers.get(3):"");
		
		if(firstItem!=null){
			LayoutParams firstParams = new LayoutParams((int)DP_235, (int)DP_30);
			firstParams.leftMargin = 0;
			firstParams.topMargin = 0;
			firstItem.layout.setLayoutParams(firstParams);			
			mRlFirstPage.addView(firstItem.layout);
		}
		
		if(secondItem!=null){
			LayoutParams secondParams = new LayoutParams((int)DP_235, (int)DP_30);
			secondParams.leftMargin = (int)DP_62+(int)DP_235;
			secondParams.topMargin = 0;
			secondItem.layout.setLayoutParams(secondParams);	
			mRlFirstPage.addView(secondItem.layout);
		}
		
		if(thirdItem!=null){
			LayoutParams thirdParams = new LayoutParams((int)DP_235, (int)DP_30);
			thirdParams.leftMargin = 0;
			thirdParams.topMargin = (int)DP_46;
			thirdItem.layout.setLayoutParams(thirdParams);		
			mRlFirstPage.addView(thirdItem.layout);
		}
		
		if(forthItem!=null){
			LayoutParams forthParams = new LayoutParams((int)DP_235, (int)DP_30);
			forthParams.leftMargin = (int)DP_62+(int)DP_235;
			forthParams.topMargin = (int)DP_46;
			forthItem.layout.setLayoutParams(forthParams);			
			mRlFirstPage.addView(forthItem.layout);
		}
	}
	
	/**
	 *通过传入的号码，通过广播发送出去实现拨号功能
	 */
	/*public void callSomebody(String name,String number){
		//TODO 以广播的方式拨号
		mLlListHead.setVisibility(View.INVISIBLE);
		mRlContent.setVisibility(View.INVISIBLE);
		mLlPoints.setVisibility(View.INVISIBLE);
		mLlCalling.setVisibility(View.VISIBLE);

	}	*/

	@Override
	public void onHiddenChanged(boolean hidden) {
		//在去电Fragment隐藏后调用clearState清空碎片状态
		//包括拨打电话，退出等情况都会去情况状态
		if(hidden){
			clearState();
			HudEndPointConstants.IS_OUTCALL=false;
		}else {
			HudEndPointConstants.IS_OUTCALL=true;
		}
	}

	/**
	 * 清空拨打电话的fragment的状态
	 */
	private void clearState() {
		mTvTitle.setText("");
		mTvCallInfo.setText("");
		mRlFirstPage.removeAllViews();
		mRlSecondPage.removeAllViews();
		mRlThirdPage.removeAllViews();
		mRlContent.setTranslationX((int)DP_145);
		mCurrentPositionPoint=0;
		
		mLlListHead.setVisibility(View.VISIBLE);
		mRlContent.setVisibility(View.VISIBLE);
		mLlPoints.setVisibility(View.VISIBLE);
		mLlCalling.setVisibility(View.INVISIBLE);
	}
}
