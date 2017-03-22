package com.joovuux;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import ua.net.lsoft.joovuux.R;

public class ActivityMode extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mode);
	}

	@Override
	protected void onResume() {
		ActiveActivitiesTracker.activityStarted();
		super.onResume();


	}

	@Override
	protected void onPause() {
		super.onPause();
		ActiveActivitiesTracker.activityStopped(this);
	}

	public void onClick(View view){

		Intent intent = new Intent(getApplicationContext(), ActivityStreaming.class);
		startActivity(intent);
	}
	
}
