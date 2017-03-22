package com.unisound.data.source.contact.impl;

import android.content.Context;

import com.unisound.data.source.contact.IContactOperator;

public class ContactOperator implements IContactOperator{
	private Context mContext;

	private AndroidContactUtils androidContactUtils;
	private AndroidContactListener contactListener;
	
	public ContactOperator(Context mContext, AndroidContactListener contactListener){
		this.mContext = mContext;
		this.contactListener = contactListener;
	}

	@Override
	public void getContacts() {
		androidContactUtils = new AndroidContactUtils(mContext);
		androidContactUtils.setListener(contactListener);
		androidContactUtils.getContacts();
	}
}
