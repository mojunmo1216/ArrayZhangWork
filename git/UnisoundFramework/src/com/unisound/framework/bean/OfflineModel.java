package com.unisound.framework.bean;

public class OfflineModel {

	private String domain;
	private String text;
	private String name;
	private String intent;

	public OfflineModel() {
		super();
	}

	public OfflineModel(String domain, String text, String name, String intent) {
		super();
		this.domain = domain;
		this.text = text;
		this.name = name;
		this.intent = intent;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	@Override
	public String toString() {
		return "OfflineModel [domain=" + domain + ", text=" + text + ", name="
				+ name + ", intent=" + intent + "]";
	}

}
