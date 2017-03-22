package com.unisound.data.source.contact.impl;

import java.util.List;

import com.unisound.data.source.bean.Contact;

public interface AndroidContactListener {
	void onSearchBegin();
	void onSearchEnd(List<Contact> contactList);
}
