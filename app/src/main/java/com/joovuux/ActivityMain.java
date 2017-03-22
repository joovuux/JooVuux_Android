package com.joovuux;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.joovuux.connection.ActivityConnection;
import com.joovuux.connection.Camera;
import com.joovuux.gallery.ActivityGallery;
import com.joovuux.settings.ActivityMainSettings;
import com.joovuux.settings.ActivityModeSettings;
import com.joovuux.settings.ModeSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;


public class ActivityMain extends Activity {

    public boolean exit = true;
    private View frameStartRec;
    private boolean isRecordNow;
    private boolean paused;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;



    private void registerToken(final String token){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, /*"http://lali.com.ua/registetoken.php"*//*"https://www.joovuu-x.com/ios/push.php"*/"http://joovuu.com/push/index.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TOKEN", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }){

            int i = 123123 / 33;

            @Override
            protected Map<String,String> getParams(){

                String did = Settings.Secure.getString(ActivityMain.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                Map<String,String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("did", did);
                params.put("action", "register-device");
                params.put("platform", "android");
                Log.d("TOKEN", token);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.new_main_activity);


//        new AsyncTask<Void, Void, Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                InstanceID instanceID = InstanceID.getInstance(ActivityMain.this);
//                try {
//                    String token = instanceID.getToken("376066628724",
//                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                    registerToken(token);
//                    Log.d("TOKEN", token);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();



        findViewById(R.id.btnMakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecordNow) {
                    Toast.makeText(ActivityMain.this, "You are currently recording video. Please stop recording, if you want to take a photo", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progDailog = ProgressDialog.show(ActivityMain.this, null, "take photo", true);


                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.makePhoto();
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        progDailog.dismiss();
                        Toast.makeText(ActivityMain.this, aVoid, Toast.LENGTH_SHORT).show();
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());

            }
        });

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(300); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back i

        frameStartRec =  findViewById(R.id.frameStartRec);
        frameStartRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecordNow) {
                    Camera.startRecord(ActivityMain.this);
                    frameStartRec.setBackgroundResource(R.drawable.animation_color);
                    frameStartRec.startAnimation(animation);
                    isRecordNow = true;
                } else {
                    Camera.stopRecord(ActivityMain.this);
                    frameStartRec.setBackgroundResource(R.drawable.selector_design_btn);
                    frameStartRec.clearAnimation();
                    isRecordNow = false;
                }
            }
        });

        SharedPreferences settingsPref = getPreferences(MODE_PRIVATE);
        if(settingsPref.getInt("clearedd", 0) == 0){
            getSharedPreferences("MainSettings", MODE_PRIVATE).edit().clear().apply();
            settingsPref.edit().clear().putInt("clearedd", 1).apply();
        }


        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if(prefs.getInt("demosss", 0) == 0){
            ModeSettings.defaultSettings(this);
            prefs.edit().putInt("demosss", 1).apply();
        }

        View btnGallery =  findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityGallery.class);
                exit = false;

                startActivityForResult(intent, 5);
            }
        });

        View frameMainSettings =  findViewById(R.id.frameMainSettings);
        frameMainSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordNow) {
                    Toast.makeText(ActivityMain.this, "You are currently recording video. Please stop recording", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ActivityMainSettings.class);
                exit = false;

                startActivityForResult(intent, 6);
            }
        });

        View frameModeSettings =  findViewById(R.id.frameModeSettings);
        frameModeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordNow) {
                    Toast.makeText(ActivityMain.this, "You are currently recording video. Please stop recording", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ActivityModeSettings.class);
                exit = false;

                startActivityForResult(intent, 7);
            }
        });

        View frameStreamingBtn =  findViewById(R.id.frameStreamingBtn);
        frameStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityStreaming.class);
                exit = false;

                startActivityForResult(intent, 8);
            }
        });

        View frameLogo =  findViewById(R.id.frameLogo);
        frameLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogo.class);
                exit = false;

                startActivityForResult(intent, 9);
            }
        });

        View frameConnectToCamera =  findViewById(R.id.frameConnectToCamera);
        frameConnectToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityConnection.class);
                exit = false;

                startActivityForResult(intent, 10);
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        exit = true;
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
    protected void onResume() {

        super.onResume();

//        new AsyncTask<Void, Void, Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                InstanceID instanceID = InstanceID.getInstance(ActivityMain.this);
//                try {
//                    String token = instanceID.getToken("376066628724",
//                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                    registerToken(token);
//                    Log.d("TOKEN", token);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                Camera.sendBroadcast("amba discovery", ActivityMain.this);
//                return null;
//            }
//        }.execute();

        Intent intent = getIntent();
        intent.getStringExtra("message");
//
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));


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

                if(((MyApp)getApplication()).getSettingsMap() == null){
                    ((MyApp)getApplication()).setSettingsMap(Camera.getCurrentSettings());
                    Log.d("SETTINGS MAP",((MyApp)getApplication()).getSettingsMap().toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                paused = false;

                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                animation.setDuration(300); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back i

                checkIfRecord(animation);

                new AsyncTask<Void, Void, String>(){


                    @Override
                    protected String doInBackground(Void... params) {

                        if (Camera.pingCamera() && Camera.getToken(ActivityMain.this)){
                            return "Camera Connected";
                        } else if(!Camera.pingCamera() && Camera.checkWiFiConnectedName(ActivityMain.this) ) {
                            return "Camera is connected but not respond";
                        } else {
                            return "Camera Disconnected";
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(!result.equalsIgnoreCase("")){
                            Toast.makeText(ActivityMain.this, result, Toast.LENGTH_LONG).show();

                        }

                        super.onPostExecute(result);
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());

            }
        }.execute();



    }

    private void checkIfRecord(final Animation animation) {
        new AsyncTask<Void, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
//                while(true){
//
//                    if(paused){
//                        break;
//                    }
//                    if (Camera.getCameraStatus().equalsIgnoreCase("record")){
//                        publishProgress(true);
//                    } else {
//                        publishProgress(false);
//                    }
//
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
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

                        if(paused) {
                            mTimer.cancel();
                        }


                    }
                }, 0, 1000);

                return false;
            }

            @Override
            protected void onProgressUpdate(Boolean... values) {

                if(paused){
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

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
        Log.e("LIFECYLCE", "MAIN ACTITIVY onPause");
        if (exit) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    if (!Camera.checkConnection(ActivityMain.this)) {
                        return null;
                    }
                    return null;
                }
            }.executeOnExecutor(Camera.getExecutorCameraCommands());
        }

    }


    private boolean checkFacebookApp(){
        try{
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.facebook.katana", 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }


}
