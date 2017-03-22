package com.haloai.hud.hudendpoint.fragments.view;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haloai.hud.hudendpoint.primarylauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshengxing on 16/5/12.
 */
public class HudCardView{
    private ListView mListview;
    private View mStartLine;
    private ImageView mImageView;

    private View mMainView;

    private List<String> mListName;
    private BaseAdapter mAdapter;

    private HudCardView(Context context,int layout){
    }
    private HudCardView(ListView mListview, View mStartLine, ImageView mImageView) {
        this.mListview = mListview;
        this.mStartLine = mStartLine;
        this.mImageView = mImageView;
    }

    private HudCardView(Context context,ViewGroup viewGroup) {
//        LayoutInflater inflater = LayoutInflater.from(context);

//        mMainView = inflater.inflate(R.layout.speech_panel_cardview,viewGroup);
        mMainView = View.inflate(context, R.layout.speech_panel_cardview, null);
        mListview = (ListView)mMainView.findViewById(R.id.speech_item_command_listview);
        mStartLine = mMainView.findViewById(R.id.speech_item_start_line);
        mImageView = (ImageView)mMainView.findViewById(R.id.speech_item_image);

//        ViewGroup.LayoutParams listLayoutParams = mListview.getLayoutParams();
//        listLayoutParams.width = 100;
//        mListview.setLayoutParams(listLayoutParams);

        initResource(context);
        initView();
    }

    public View getLayout(){
        return mMainView;
    }
    private void initView(){
        mListview.setAdapter(mAdapter);
    }

    private void initResource(Context context){
        mListName = new ArrayList<>();
        mAdapter = new ModuleArrayAdapter(context,mListName);
    }

    public void setNaviIcon(int id){
        mImageView.setBackgroundResource(id);
    }

    public void updateContent(List<String> stringList){
        mListName.clear();
        mListName.addAll(stringList);
        mAdapter.notifyDataSetChanged();
    }

    public class ModuleArrayAdapter extends BaseAdapter {
        private Context ctx;
        private List<String> list;

        public ModuleArrayAdapter(Context context, List<String> poiList) {
            this.ctx = context;
            this.list = poiList;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ctx, R.layout.hud_cardview_list_item, null);

                holder.tittle = (TextView) convertView
                        .findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String item = list.get(position);
            holder.tittle.setText(item);
            return convertView;
        }

        private class ViewHolder {
            TextView tittle;
        }

    }

    public static class Builer{
        public static HudCardView inflate(Context context,ViewGroup viewGroup){
            return new HudCardView(context,viewGroup);
        }
    }
}
