package com.haloai.hud.upgraderserver;

import android.app.Activity;
import android.os.Bundle;

public class UpgraderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((UpgraderApplication)getApplication()).setInstance(this);

	}

}
