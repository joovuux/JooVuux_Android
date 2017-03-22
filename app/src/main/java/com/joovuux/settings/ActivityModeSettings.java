package com.joovuux.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.joovuux.ActiveActivitiesTracker;
import ua.net.lsoft.joovuux.R;

import com.joovuux.MyApp;
import com.joovuux.connection.Camera;

public class ActivityModeSettings extends Activity {


    public static final String MODE1 = "Mode 1";
    public static final String MODE2 = "Mode 2";
    public static final String PHOTO_MODE = "Photo Mode";

    public static String[] modes = {MODE1, MODE2/*, PHOTO_MODE*/};


    private Fragment mode1Fragment = FragmentModeSettings.newInstance(MODE1);
    private Fragment mode2Fragment = FragmentModeSettings.newInstance(MODE2);
    private Fragment modePhotoFragment = FragmentPhotoModeSettings.newInstance(PHOTO_MODE);
    private SharedPreferences settingsPref;
    private String mode = MODE1;
    private String request;
    public String selectedMode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_mode_settings);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Camera.send260();
                return null;
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView tvModeTitle = (TextView) findViewById(R.id.tvModeTitle);

        View btnLeftModePick = findViewById(R.id.btnLeftModePick);
        btnLeftModePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionCurrentMode = 0;
                for(int i = 0; i < modes.length; i++){
                    if(mode.equalsIgnoreCase(modes[i])){
                        positionCurrentMode = i;
                        break;
                    }
                }

                switch (positionCurrentMode){
                    case 0:
                        tvModeTitle.setText("MODE 2");
                        mode = modes[1];
                        settingsPref = getSharedPreferences(mode, MODE_PRIVATE);
                        getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode2Fragment).commit();
                        break;
                    case 1:
                        tvModeTitle.setText("MODE 1");
                        mode = modes[0];
                        settingsPref = getSharedPreferences(mode, MODE_PRIVATE);
                        getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode1Fragment).commit();
                }

            }
        });
        View btnRightModePick = findViewById(R.id.btnRightModePick);
        btnRightModePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionCurrentMode = 0;
                for(int i = 0; i < modes.length; i++){
                    if(mode.equalsIgnoreCase(modes[i])){
                        positionCurrentMode = i;
                        break;
                    }
                }

                switch (positionCurrentMode){
                    case 0:
                        tvModeTitle.setText("MODE 2");
                        mode = modes[1];
                        settingsPref = getSharedPreferences(mode, MODE_PRIVATE);
                        getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode2Fragment).commit();
                        break;
                    case 1:
                        tvModeTitle.setText("MODE 1");
                        mode = modes[0];
                        settingsPref = getSharedPreferences(mode, MODE_PRIVATE);
                        getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode1Fragment).commit();

                }
            }
        });



        settingsPref = getSharedPreferences(mode, MODE_PRIVATE);

        if(getIntent().getStringExtra("mode") != null){
            mode = getIntent().getStringExtra("mode");
        }



        if(mode == null || mode.equalsIgnoreCase(MODE1)){
            tvModeTitle.setText("MODE 1");
            getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode1Fragment).commit();
        } else {
            tvModeTitle.setText("MODE 2");
            getFragmentManager().beginTransaction().replace(R.id.mainFrame, mode2Fragment).commit();
        }


//        final ToggleButton btnMode1 = (ToggleButton) findViewById(R.id.btnMode1);
//        final ToggleButton btnMode2 = (ToggleButton) findViewById(R.id.btnMode2);
//        final ToggleButton btnPhotoMode = (ToggleButton) findViewById(R.id.btnPhotoMode);
//
//
//        currentModeCheck(btnMode1, btnMode2, btnPhotoMode);
//
//
//        btnMode1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                modePicked(btnMode1, btnMode2, btnPhotoMode, mode1Fragment);
//                mode = MODE1;
//                selectedMode = "";
//            }
//        });
//
//        btnMode2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                modePicked(btnMode2, btnMode1, btnPhotoMode, mode2Fragment);
//                mode = MODE2;
//                selectedMode = "_mode2";
//            }
//        });
//
//
//        btnPhotoMode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                modePicked(btnPhotoMode, btnMode2, btnMode1, modePhotoFragment);
//                mode = PHOTO_MODE;
//                selectedMode = "";
//            }
//        });


    }

    @Override
    protected void onResume() {
        super.onResume();

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


    private void currentModeCheck(ToggleButton btnMode1, ToggleButton btnMode2, ToggleButton btnPhotoMode) {
        if (mode == null || mode.equals(MODE1)){
            btnMode1.setChecked(true);
            modePicked(btnMode1, btnMode2, btnPhotoMode, mode1Fragment);
        } else if (mode.equals(MODE2)) {
            btnMode2.setChecked(true);
            modePicked(btnMode2, btnMode1, btnPhotoMode, mode2Fragment);
        } else if (mode.equals(PHOTO_MODE)) {
            btnPhotoMode.setChecked(true);
            modePicked(btnPhotoMode, btnMode2, btnMode1, modePhotoFragment);
        }
    }

    private void modePicked(ToggleButton pickedModeBtn, ToggleButton btn2, ToggleButton btn3, Fragment modeFragment) {
        btn2.setChecked(false);
        btn3.setChecked(false);
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.mainFrame, modeFragment).commit();
    }




}