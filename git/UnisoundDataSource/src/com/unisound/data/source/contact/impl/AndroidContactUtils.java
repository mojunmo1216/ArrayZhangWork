package com.unisound.data.source.contact.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.unisound.data.source.bean.Contact;
import com.unisound.data.source.utils.PinyinUtils;


public class AndroidContactUtils {
	
	private Context mContext;
	private ContentResolver contentResolver;
	private Uri rawContactsUri;
	private Uri dataUri;
	private List<Contact> contactList;
	private AndroidContactListener contactListener;
	
	private final static int PHONE_V2 = 5;

	private static final String RAW_CONTACTS_URI = "content://com.android.contacts/raw_contacts";
	private static final String DATA_URI = "content://com.android.contacts/data";

	public AndroidContactUtils(Context mContext) {
		this.mContext = mContext;
		init();
	}
	
	public void setListener(AndroidContactListener contactListener){
		this.contactListener = contactListener;
	}

	private void init() {
		contactList = new ArrayList<Contact>();
		contentResolver = mContext.getContentResolver();
		rawContactsUri = Uri.parse(RAW_CONTACTS_URI);
		dataUri = Uri.parse(DATA_URI);
	}

	//use thread
	public void getContacts(){
		ExecutorService pool = Executors.newFixedThreadPool(1);
		pool.execute(new Runnable() {
			@Override
			public void run() {
				startSearch();
			}
		});
	}
	
	private List<Contact> startSearch() {
		if(contactListener != null){
			contactListener.onSearchBegin();
		}
		
		Cursor rawContactCursor = contentResolver.query(rawContactsUri, new String[]{"_id", "display_name"}, "deleted=0", null, null);
		while(rawContactCursor != null && rawContactCursor.moveToNext()){
			int rawContactId = rawContactCursor.getInt(0);
			String contactName = rawContactCursor.getString(1);
			
			if(contactName != null){
				Contact contact = new Contact();
				contact.setRawContactId(rawContactId);
				contact.setName(contactName);
				
				String pyName = PinyinUtils.getPinYin(contactName);
				contact.setPyName(pyName);
				
				Cursor dataCursor = contentResolver.query(dataUri, new String[]{"data1"}, "raw_contact_id=" + rawContactId + " AND mimetype_id=" + PHONE_V2, null, null);
				String[] phones = new String[dataCursor.getCount()];
				int phoneIndex = 0;
				while(dataCursor.moveToNext()){
					String phone = dataCursor.getString(0);
					phones[phoneIndex] = phone;
					phoneIndex++;
				}
				dataCursor.close();
				contact.setPhone(phones);
				contactList.add(contact);
			}
		}
		rawContactCursor.close();
		
		if(contactListener != null){
			contactListener.onSearchEnd(contactList);
		}
		return contactList;
	}
}
