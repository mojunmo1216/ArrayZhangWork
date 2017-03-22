package com.unisound.data.source;

import java.util.List;

import com.unisound.data.source.bean.Contact;
import com.unisound.data.source.contact.IContactOperator;
import com.unisound.data.source.contact.impl.AndroidContactListener;
import com.unisound.data.source.contact.impl.ContactOperator;
import com.unisound.data.source.utils.LogUtils;
import com.unisound.data.source.utils.MultiTaskTest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class MainActivity extends Activity {
	private static final String TAG = "ContactUtils";
	private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this.getApplicationContext();
        
        startMutiTask();
    }

    private void startMutiTask() {
    	task = new MultiTaskTest();
    	task.setTask(runnable);
    	task.startTask();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			searchOperator();
		}
	};
	private MultiTaskTest task;
	private void searchOperator() {
		IContactOperator contactOperator = new ContactOperator(mContext, new AndroidContactListener() {
			@Override
			public void onSearchEnd(List<Contact> contactList) {
				LogUtils.d(TAG, "onSearchEnd contactList.size is " + contactList.size());
				if(contactList.size() == 0){
					task.stopTask();
					LogUtils.e(TAG, "task failure");
					return;
				}
				showContacts(contactList);
				task.done4Once();
			}
			@Override
			public void onSearchBegin() {
				LogUtils.d(TAG, "onSearchBegin");
			}
		});
		contactOperator.getContacts();
	}

	private synchronized void showContacts(List<Contact> contacts) {
		LogUtils.d(TAG, task.getCurrentTime() + " time : ");
		for(Contact contactItem : contacts){
			LogUtils.d(TAG, contactItem.toString());
		}
	}

}
