package com.unisound.data.source.bean;

import java.util.Arrays;

public class Contact {
	private int rawContactId;
	private String name;
	private String pyName;
	private String[] phone;

	public Contact() {
		super();
	}

	public Contact(String name, String pyName, String[] phone) {
		super();
		this.name = name;
		this.pyName = pyName;
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPyName() {
		return pyName;
	}

	public void setPyName(String pyName) {
		this.pyName = pyName;
	}

	public String[] getPhone() {
		return phone;
	}

	public void setPhone(String[] phone) {
		this.phone = phone;
	}

	public int getRawContactId() {
		return rawContactId;
	}

	public void setRawContactId(int rawContactId) {
		this.rawContactId = rawContactId;
	}

	@Override
	public String toString() {
		return "Contact [rawContactId=" + rawContactId + ", name=" + name
				+ ", pyName=" + pyName + ", phone=" + Arrays.toString(phone)
				+ "]";
	}
}
