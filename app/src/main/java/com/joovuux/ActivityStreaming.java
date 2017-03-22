package com.joovuux;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.joovuux.connection.Camera;
import com.joovuux.connection.NewVideoActivity;
import com.joovuux.settings.ActivityModeSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;

import com.joovuux.connection.VideoActivity;
import com.joovuux.settings.MainSettings;
import com.joovuux.settings.ModeSettings;


public class ActivityStreaming extends Activity {

	private SharedPreferences settingsPref;
	public static final String MODE_STREAMING_PREF = "pickedMode streaming";
	public static final String PICKED_MODE_PREF = "picked pickedMode";
	private String pickedMode;
	private String[] modes = {ActivityModeSettings.MODE1, ActivityModeSettings.MODE2};

	private boolean hide = false;
    protected NewVideoActivity videoActivity;
	private boolean isRecordNow;
	private View progressBar;
	private View frameStartRec;
	private boolean btnInfoChecked;
	private Spinner spinnerModeSelect;
	private ImageView ivInfoPortrait;
	private View btnInfoPortrait;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_streaming_activity);


		btnInfoPortrait = findViewById(R.id.btnInfoPortrait);
		ivInfoPortrait = (ImageView) findViewById(R.id.ivInfoPortrait);
		btnInfoPortrait.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (btnInfoChecked) {
					btnInfoPortrait.setBackgroundColor(Color.parseColor("#f7f7f7"));
					ivInfoPortrait.setImageResource(R.drawable.stream_info);
					btnInfoChecked = false;
					findViewById(R.id.infoFrame).setVisibility(View.GONE);
				} else{
					btnInfoPortrait.setBackgroundColor(Color.parseColor("#48d8b7"));
					ivInfoPortrait.setImageResource(R.drawable.stream_info_press);
					btnInfoChecked = true;
					findViewById(R.id.infoFrame).setVisibility(View.VISIBLE);

					findViewById(R.id.scrollView5).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							btnInfoPortrait.setBackgroundColor(Color.parseColor("#f7f7f7"));
							ivInfoPortrait.setImageResource(R.drawable.stream_info);
							btnInfoChecked = false;
							findViewById(R.id.infoFrame).setVisibility(View.GONE);
						}
					});
				}


				return false;
			}
		});

		progressBar = findViewById(R.id.progressBar2);

		findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final View infoFrame = findViewById(R.id.infoFrame);

//		findViewById(R.id.btnInfo).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				infoFrame.setVisibility(View.VISIBLE);
//			}
//		});

		infoFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				infoFrame.setVisibility(View.GONE);
			}
		});

//        videoActivity = new VideoActivity();


		settingsPref = getSharedPreferences(MODE_STREAMING_PREF, MODE_PRIVATE);

		pickedMode = modes[settingsPref.getInt(PICKED_MODE_PREF, 0)];

		spinnerModeSelect = (Spinner) findViewById(R.id.spinnerModeSelect);
		initSpinner(modes, spinnerModeSelect, PICKED_MODE_PREF);

		View frameSettingsBtn = findViewById(R.id.frameSettingsBtn);
		frameSettingsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isRecordNow) {
					Toast.makeText(ActivityStreaming.this, "You now recording video, please stop record, if you wont to change some settings.", Toast.LENGTH_LONG).show();
					return;
				}

				Intent intent = new Intent(getApplicationContext(), ActivityModeSettings.class);
				intent.putExtra("mode", pickedMode);
				startActivity(intent);
			}
		});

		View frameWhiteBalanceBtn =  findViewById(R.id.frameWhiteBalanceBtn);
		frameWhiteBalanceBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isRecordNow){
					Toast.makeText(ActivityStreaming.this, "You now recording video, please stop record, if you wont to change some settings.", Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(getApplicationContext(), ActivityWhiteBalance.class);
				startActivity(intent);
			}
		});
		frameStartRec = findViewById(R.id.frameStartRec);
		frameStartRec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRecordNow) {
					Camera.stopRecord(ActivityStreaming.this);
					isRecordNow = false;
				} else {
					Camera.startRecord(ActivityStreaming.this);
					isRecordNow = true;
				}
			}
		});


		final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		animation.setDuration(300); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		frameStartRec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!isRecordNow) {
					Camera.startRecord(ActivityStreaming.this);
					frameStartRec.setBackgroundResource(R.drawable.animation_color);
					frameStartRec.startAnimation(animation);
					isRecordNow = true;
				} else {
					Camera.stopRecord(ActivityStreaming.this);
					frameStartRec.setBackgroundResource(R.drawable.selector_design_btn);
					frameStartRec.clearAnimation();
					isRecordNow = false;
				}
			}
		});



		findViewById(R.id.btnMakePhoto).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isRecordNow) {
					Toast.makeText(ActivityStreaming.this, "You are currently recording video. Please stop recording, if you want to take a photo", Toast.LENGTH_LONG).show();
					return;
				}
				final ProgressDialog progDailog = ProgressDialog.show(ActivityStreaming.this, null, "take photo", true);


				new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground(Void... params) {
						return Camera.makePhoto();
					}

					@Override
					protected void onPostExecute(String aVoid) {
						super.onPostExecute(aVoid);
						progDailog.dismiss();
						Toast.makeText(ActivityStreaming.this, aVoid, Toast.LENGTH_SHORT).show();
					}
				}.executeOnExecutor(Camera.getExecutorCameraCommands());
			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
		ActiveActivitiesTracker.activityStarted();
	}

	@Override
	protected void onStop() {
		super.onStop();
		ActiveActivitiesTracker.activityStopped(this);
	}

	@Override
	protected void onPause() {
		progressBar.setVisibility(View.VISIBLE);
		super.onPause();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
                Camera.send260();
				return null;
			}

		}.executeOnExecutor(Camera.getExecutorCameraCommands());

		hide = true;


	}

	@Override
	protected void onResume() {
		super.onResume();
		final MyApp myApp = ((MyApp)getApplication());
		myApp.connect(this);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				while (myApp.isConnecting()){
					try {
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);

				if(myApp.isConnected()){
					videoActivity = new NewVideoActivity();
					hide = false;

					new AsyncTask<Void, Void, String>() {
						@Override
						protected String doInBackground(Void... params) {
							return Camera.getCurrentOption(MainSettings.SPINNER_DEFAULT_MODE_CAMERA_STATS);
						}

						@Override
						protected void onPostExecute(String s) {
							if(s.equalsIgnoreCase("mode2")){
								pickedMode = ActivityModeSettings.MODE2;
								spinnerModeSelect.setSelection(1);
							} else {
								pickedMode = ActivityModeSettings.MODE1;
								spinnerModeSelect.setSelection(0);
							}
							loadInformation((pickedMode.equalsIgnoreCase(ActivityModeSettings.MODE2)) ? "_mode2" : "");
							super.onPostExecute(s);
						}
					}.executeOnExecutor(Camera.getExecutorCameraCommands());


				}
			}
		}.execute();


	}

	public static void showNoConnectionDialog(final Context ctx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setCancelable(true);
		builder.setMessage("No Connection, check WiFi settings");
		builder.setTitle("Connect to camera");
		builder.setPositiveButton("Check settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(DialogInterface dialog) {
//                return;
//            }
//        });

		builder.show();
	}

	private void checkIfRecord(final Animation animation) {
		new AsyncTask<Void, Boolean, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
//				while(true){
//
//					if(hide){
//						break;
//					}
//					if (Camera.getCameraStatus().equalsIgnoreCase("record")){
//						publishProgress(true);
//					} else {
//						publishProgress(false);
//					}
//
//					try {
//						TimeUnit.MILLISECONDS.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

				// re-schedule timer here
				// otherwise, IllegalStateException of
				// "TimerTask is scheduled already"
				// will be thrown
				final Timer mTimer = new Timer();


				// delay 1000ms, repeat in 5000ms
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (Camera.getCameraStatus().equalsIgnoreCase("record")){
							publishProgress(true);
						} else {
							publishProgress(false);
						}

						if(hide){
							mTimer.cancel();
						}
					}



				}, 0, 1000);



				return false;
			}

			@Override
			protected void onProgressUpdate(Boolean... values) {

				if(hide){
					return;
				}

				if (values[0]){
					isRecordNow = true;
					frameStartRec.setBackgroundResource(R.drawable.animation_color);
					frameStartRec.startAnimation(animation);
				} else {
					frameStartRec.setBackgroundResource(R.drawable.selector_design_btn);
					frameStartRec.clearAnimation();
					isRecordNow = false;
				}

			}

		}.executeOnExecutor(Camera.getExecutorCameraStatus());
	}


	private void initSpinner(final String[] data, Spinner spinner, final String key) {
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<>(this, R.layout.item_simple_spinner, data);
		adapter.setDropDownViewResource(R.layout.item_spinner);

		spinner.setAdapter(adapter);
		spinner.setSelection(settingsPref.getInt(key, 0));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
				pickedMode = data[position];
				if(videoActivity == null || !videoActivity.isAdded()){
					return;
				}

				if (isRecordNow) {
					Toast.makeText(ActivityStreaming.this, "You now recording video, please stop record, if you wont to change some settings.", Toast.LENGTH_LONG).show();
					return;
				}

				final ProgressDialog modeChangeDialog = ProgressDialog.show(ActivityStreaming.this, null, "Change mode", true);

				new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						Camera.send260();
						Camera.sendSettings((data[position].equalsIgnoreCase(ActivityModeSettings.MODE1)) ? "mode1" : "mode2", "default_mode");
						Camera.send259();
						return "";
					}

					@Override
					protected void onPostExecute(String result) {
						videoActivity = new NewVideoActivity();
						loadInformation((pickedMode.equalsIgnoreCase(ActivityModeSettings.MODE2)) ? "_mode2" : "");
						modeChangeDialog.dismiss();
					}
				}.executeOnExecutor(Camera.getExecutorCameraCommands());



			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}


	private void loadInformation(String mode) {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				((MyApp)getApplication()).setSettingsMap(Camera.getCurrentSettings());
				return Camera.getCurrentOption(MainSettings.CAMERA_TIME);
			}

			@Override
			protected void onPostExecute(String cameraTime) {
				super.onPostExecute(cameraTime);
				HashMap<String, String> settings = ((MyApp) getApplication()).getSettingsMap();

				TextView tvDateformat = (TextView) findViewById(R.id.tvDateformat);
				tvDateformat.setText(settings.get(MainSettings.SPINNER_DATE_FORMAT));

				TextView tvCameraTime = (TextView) findViewById(R.id.tvCameraTime);


				SimpleDateFormat cameraDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
				String dateFromat = "yyyy/MM/dd";
				if(tvDateformat.getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[0])){
					dateFromat = "yyyy/MM/dd";
				} else if(tvDateformat.getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[1])){
					dateFromat = "dd/MM/yyyy";
				} else if(tvDateformat.getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[2])){
					dateFromat = "MM/dd/yyyy";
				}

				SimpleDateFormat userDateFormat = new SimpleDateFormat(dateFromat +" HH:mm:ss");
				Date date = new Date();
				try {
					date = cameraDateFormat.parse(cameraTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				tvCameraTime.setText(userDateFormat.format(date));

				TextView tvBeepNoise = (TextView) findViewById(R.id.tvBeepNoise);
				tvBeepNoise.setText(settings.get(MainSettings.TOGGLE_BEEP_NOISES));

				TextView tvRecordingLedIndicator = (TextView) findViewById(R.id.tvRecordingLedIndicator);
				tvRecordingLedIndicator.setText(settings.get(MainSettings.TOGGLE_RECORDING_LED_INDICATOR));

				TextView tvDefaultCameraMode = (TextView) findViewById(R.id.tvDefaultCameraMode);
				tvDefaultCameraMode.setText(settings.get(MainSettings.SPINNER_DEFAULT_MODE_CAMERA_STATS));

				TextView tvStandByTime = (TextView) findViewById(R.id.tvStandByTime);
				tvStandByTime.setText(settings.get(MainSettings.SPINNER_STANDBY_TIME));

				TextView tvMotionDetection = (TextView) findViewById(R.id.tvMotionDetection);
				tvMotionDetection.setText(settings.get(MainSettings.TOGGLE_MOTION_DETECTION));

				TextView tvMotionTurnOff = (TextView) findViewById(R.id.tvMotionTurnOff);
				tvMotionTurnOff.setText(settings.get(MainSettings.SPINNER_MOTION_TURN_OFF));
//
//				TextView tvTvOut = (TextView) findViewById(R.id.tvTvOut);
//				tvTvOut.setText(settings.get(MainSettings.SPINNER_TV_OUT));
//
//				TextView tvGsensor = (TextView) findViewById(R.id.tvGsensor);
//				tvGsensor.setText(settings.get(MainSettings.TOGGLE_G_SENSOR));

				TextView tvCarPlateStamp = (TextView) findViewById(R.id.tvCarPlateStamp);
				tvCarPlateStamp.setText(settings.get(MainSettings.TOGGLE_CAR_PLATE_STAMP));

				TextView tvCarNummber = (TextView) findViewById(R.id.tvCarNummber);
				tvCarNummber.setText(settings.get(MainSettings.CAR_NUMBER));

				TextView tvWiFiPassword = (TextView) findViewById(R.id.tvWiFiPassword);
				tvWiFiPassword.setText(settings.get(MainSettings.WIFI_PASSWORD));

				TextView tvSpeedStamp = (TextView) findViewById(R.id.tvSpeedStamp);
				tvSpeedStamp.setText(settings.get(MainSettings.TOGGLE_SPEED_STAMP));

				TextView tvSpeedUnit = (TextView) findViewById(R.id.tvSpeedUnit);
				tvSpeedUnit.setText(settings.get(MainSettings.SPINNER_SPEED_UNIT));

				TextView tvTimedMode = (TextView) findViewById(R.id.tvTimedMode);
				tvTimedMode.setText(settings.get(ModeSettings.TOGGLE_TIMED_MODE));

				TextView tvFromTime = (TextView) findViewById(R.id.tvFromTime);
				tvFromTime.setText(settings.get(MainSettings.TIME_MODE_START_TIME));

				TextView tvToTime = (TextView) findViewById(R.id.tvToTime);
				tvToTime.setText(settings.get(MainSettings.TIME_MODE_FINISH_TIME));

				TextView tvVideoTimeStamp = (TextView) findViewById(R.id.tvVideoTimeStamp);
				tvVideoTimeStamp.setText(settings.get(ModeSettings.VIDEO_TIME_STAMP));

				TextView tvLoopRecording = (TextView) findViewById(R.id.tvLoopRecording);
				tvLoopRecording.setText(settings.get(ModeSettings.LOOP_RECORDING));

				TextView tvTimeLapseVideo = (TextView) findViewById(R.id.tvTimeLapseVideo);
				tvTimeLapseVideo.setText(settings.get(ModeSettings.TIME_LAPSE_VIDEO));

				TextView tvPowerOnDelay = (TextView) findViewById(R.id.tvPowerOnDelay);
				tvPowerOnDelay.setText(settings.get(MainSettings.SPINNER_POWER_ON_DELAY));

				TextView tvLowBatteryWarning = (TextView) findViewById(R.id.tvLowBatteryWarning);
				tvLowBatteryWarning.setText(settings.get(ModeSettings.LOW_BATTERY_WARNING));

				TextView tvWifiAutoStart = (TextView) findViewById(R.id.tvWifiAutoStart);
				tvWifiAutoStart.setText(settings.get(ModeSettings.WIFI_AUTO_START));
			}
		}.executeOnExecutor(Camera.getExecutorCameraCommands());

		TextView tvVideoResolution = (TextView) findViewById(R.id.tvVideoResolution);
		setCurrentOption(tvVideoResolution, ModeSettings.VIDEO_RESOLUTION + mode);

		TextView tvAudio = (TextView) findViewById(R.id.tvAudio);
		setCurrentOption(tvAudio, ModeSettings.AUDIO);

		TextView tvRotateVideo = (TextView) findViewById(R.id.tvRotate180Degrees);
		setCurrentOption(tvRotateVideo, ModeSettings.AUTO_ROTATE);

		TextView tvVideoBirRates = (TextView) findViewById(R.id.tvVideoBirRates);
		setCurrentOption(tvVideoBirRates, ModeSettings.VIDEO_BITRATES + mode);


		TextView tvVideoClipLength = (TextView) findViewById(R.id.tvVideoClipLength);
		setCurrentOption(tvVideoClipLength, ModeSettings.VIDEO_CLIP_LENGHT + mode);


		TextView tvWDR = (TextView) findViewById(R.id.tvWDR);
		setCurrentOption(tvWDR, ModeSettings.WDR + mode);


		TextView tvFieldOfView = (TextView) findViewById(R.id.tvFieldOfView);
		setCurrentOption(tvFieldOfView, ModeSettings.FIELD_OF_VIEW + mode);

		startStream();

	}

	private void setCurrentOption(final TextView textView, final String key) {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return ((MyApp)getApplication()).getSettingsMap().get(key);
//				return Camera.getCurrentOption(key);
			}

			@Override
			protected void onPostExecute(String option) {
				textView.setText(option.replace("P", "FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));
			}
		}.executeOnExecutor(Camera.getExecutorCameraCommands());

	}

	private void startStream() {
		new AsyncTask<Void, Void, String>() {
            String res = "";
			@Override
			protected String doInBackground(Void... voids) {
//				if(!Camera.checkConnection(ActivityStreaming.this)) {
//					return "Camera Disconnected";
//				} else if (!Camera.pingCamera() || !Camera.getToken(ActivityStreaming.this)){
//					return "Camera is connected but does not respond";
//				}


                StringBuilder s = new StringBuilder();
				if(Camera.send260()){
					s.append("260 succes ");
				} else {
					return "Camera is connected but does not respond";
				}

				s.append(Camera.saveLowResolutionClip());
				s.append(Camera.setOutTypeRTSP());
				s.append(Camera.send259());
				return s.toString();
			}

			@Override
			protected void onPostExecute(String result) {

				progressBar.setVisibility(View.GONE);
//				if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
//					Toast.makeText(ActivityStreaming.this, result, Toast.LENGTH_LONG).show();
//				}

				if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
					((MyApp)ActivityStreaming.this.getApplication()).showLogDialog(ActivityStreaming.this);
				}


//				boolean showlog = getSharedPreferences("MainSettings", MODE_PRIVATE).getBoolean(MainSettings.CONNECTION_LOG, true);
				if (!hide ) {
//					videoActivity = new VideoActivity();
//					if(!videoActivity.isAdded()){
						getFragmentManager().beginTransaction().replace(R.id.frame, videoActivity).commit();
//					}
				}


				final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
				animation.setDuration(300); // duration - half a second
				animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
				animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
				animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

				checkIfRecord(animation);

			}
		}.executeOnExecutor(Camera.getExecutorCameraCommands());
	}

}