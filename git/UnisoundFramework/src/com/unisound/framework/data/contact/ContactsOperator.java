package com.unisound.framework.data.contact;

import java.util.List;

import com.unisound.data.source.bean.Contact;
import com.unisound.data.source.contact.IContactOperator;
import com.unisound.data.source.contact.impl.AndroidContactListener;
import com.unisound.data.source.contact.impl.ContactOperator;
import com.unisound.framework.utils.LogUtils;

import android.content.Context;

public class ContactsOperator {
	private static final String TAG = ContactsOperator.class.getSimpleName();
	
	private List<Contact> contactsList;
	private static ContactsOperator mContactsUtils;
	private static ContactsListener contactsListener;

	
	public static ContactsOperator getInstance(Context mContext, ContactsListener contactsListener){
		ContactsOperator.contactsListener = contactsListener;
		if(mContactsUtils == null){
			synchronized (ContactsOperator.class) {
				if(mContactsUtils == null){
					mContactsUtils = new ContactsOperator(mContext);
				}
				
			}
		}
		return mContactsUtils;
	}
	
	public static ContactsOperator newInstance(Context mContext, ContactsListener contactsListener){
		ContactsOperator.contactsListener = contactsListener;
		mContactsUtils = new ContactsOperator(mContext);
		return mContactsUtils;
	}
	
	private ContactsOperator(Context mContext){
		IContactOperator contactOperator = new ContactOperator(mContext, new AndroidContactListener() {
			@Override
			public void onSearchEnd(List<Contact> contacts) {
				contactsList = contacts;
				if(contactsListener != null){
					contactsListener.onSearchEnd(contacts);
				}
			}
			
			@Override
			public void onSearchBegin() {
				if(contactsListener != null){
					contactsListener.onSearchBegin();
				}
			}
		});
		contactOperator.getContacts();
	}
	
	public void startSearchContacts(){
		if(contactsList != null){
			contactsListener.onSearchBegin();
			contactsListener.onSearchEnd(contactsList);
		} else{
			LogUtils.e(TAG, "getContacts report : contactsList is null.");
		}
	}
}
