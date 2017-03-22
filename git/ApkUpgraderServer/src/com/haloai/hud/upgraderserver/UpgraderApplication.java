package com.haloai.hud.upgraderserver;

import android.app.Application;

public class UpgraderApplication extends Application {
	private UpgraderActivity mActivity;

	public void setInstance(UpgraderActivity activity) {
		this.mActivity = activity;
	}

	public UpgraderActivity getInstance() {
		return mActivity;
	}
}
