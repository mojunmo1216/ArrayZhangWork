package com.unisound.framework.data.contact;

import java.util.List;

import com.unisound.data.source.bean.Contact;

public interface ContactsListener {
	void onSearchBegin();
	void onSearchEnd(List<Contact> contacts);
}
