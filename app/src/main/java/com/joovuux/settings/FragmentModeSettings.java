package com.joovuux.settings;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joovuux.ActiveActivitiesTracker;
import com.joovuux.MyApp;
import com.joovuux.Utils;
import com.joovuux.connection.Camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;

public class FragmentModeSettings extends Fragment {

    private static final String EXTRA_TITLE = "title";

    private boolean isMode2;
    private SharedPreferences settingsPref;
    private String mode;
    private View view;
    private Spinner spinnerVideoResolutions;
    private ToggleButton toggleVideoTimeStamp;
    private ToggleButton toggleAudio;
    private ToggleButton toggleRotate180Degrees;
    private ToggleButton toggleLoopRecording;
    private Spinner spinnerVideoBitRates;
    private Spinner spinnerVideoClipLenght;
    private ToggleButton toggleWDR;
    private Spinner spinnerFieldOfView;
    private Spinner spinnerTimeLapseVideo;


    private ToggleButton toggleDisplaySpeed;



    private ToggleButton toggleLDWS;
    private ToggleButton toggleFDWS;
    private ArrayList<String> changedSettings = new ArrayList<>();

    public String val;
    public String mod;
    public Boolean started = false;
    private int settingsCount;

    private boolean spinerTouched;
    View.OnTouchListener onTouchListener =  new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            spinerTouched = true;
            return false;
        }
    };

    public AsyncTask bgtask = new AsyncTask<Void, Void, Void>() {
        String res;
        @Override
        protected Void doInBackground(Void... voids) {
            started = true;
            res = Camera.sendSettings(val, mod);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            started = false;
            if(getActivity().getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                Toast.makeText(FragmentModeSettings.this.getActivity(), res, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private String request;

    private boolean paused;
    private ProgressDialog progDailog;
    private ScrollView mainScroll;
    private TextView tvVideoResolutions;
    private TextView tvTimeLapseVideo;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.new_fragment_mode_settings, null);
        mode = getArguments().getString(EXTRA_TITLE);

        mainScroll = (ScrollView) view.findViewById(R.id.mainScroll);

        settingsPref = getActivity().getSharedPreferences(mode, Context.MODE_PRIVATE);


        tvVideoResolutions = (TextView) view.findViewById(R.id.tvVideoResolutions);
        tvTimeLapseVideo = (TextView) view.findViewById(R.id.tvTimeLapseVideo);


//        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//        tvTitle.setText(mode);

        spinnerVideoResolutions = (Spinner) view.findViewById(R.id.spinnerVideoResolutions);
        spinnerVideoResolutions.setOnTouchListener(onTouchListener);


        toggleVideoTimeStamp = (ToggleButton) view.findViewById(R.id.toggleVideoTimeStamp);
        toggleAudio = (ToggleButton) view.findViewById(R.id.toggleAudio);
        toggleRotate180Degrees = (ToggleButton) view.findViewById(R.id.toggleRotate180Degrees);
        toggleLoopRecording = (ToggleButton) view.findViewById(R.id.toggleLoopRecording);

        spinnerVideoBitRates = (Spinner) view.findViewById(R.id.spinnerVideoBitRates);
        spinnerVideoBitRates.setOnTouchListener(onTouchListener);

        spinnerVideoClipLenght = (Spinner) view.findViewById(R.id.spinnerVideoClipLenght);
        spinnerVideoClipLenght.setOnTouchListener(onTouchListener);

        toggleWDR = (ToggleButton) view.findViewById(R.id.toggleWDR);
//        spinnerWDR.setOnTouchListener(onTouchListener);

        spinnerFieldOfView = (Spinner) view.findViewById(R.id.spinnerFieldOfView);
        spinnerFieldOfView.setOnTouchListener(onTouchListener);

        spinnerTimeLapseVideo = (Spinner) view.findViewById(R.id.spinnerTimeLapseVideo);

        spinnerTimeLapseVideo.setOnTouchListener(onTouchListener);


        toggleDisplaySpeed = (ToggleButton) view.findViewById(R.id.toggleDisplaySpeed);

        toggleLDWS = (ToggleButton) view.findViewById(R.id.toggleLDWS);
        toggleFDWS = (ToggleButton) view.findViewById(R.id.toggleFDWS);


        view.findViewById(R.id.btnResetAllSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog resetDialog = ProgressDialog.show(FragmentModeSettings.this.getActivity(), null, "reset all settings", true);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        String mode  = (isMode2) ? "_mode2" : "" ;
//                        Camera.resetSetting();
                        Camera.sendSettings(ModeSettings.dataSpinnerVideoResolutions[2], ModeSettings.VIDEO_RESOLUTION + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.VIDEO_RESOLUTION + mode, ModeSettings.dataSpinnerVideoResolutions[2]);


                        Camera.sendSettings("on", ModeSettings.VIDEO_TIME_STAMP + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.VIDEO_TIME_STAMP + mode, "on");

                        Camera.sendSettings("on", ModeSettings.AUDIO + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.AUDIO + mode, "on");

                        Camera.sendSettings("on", ModeSettings.LOOP_RECORDING + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.LOOP_RECORDING + mode, "on");

                        Camera.sendSettings(ModeSettings.dataSpinnerVideoBitRates[0], ModeSettings.VIDEO_BITRATES + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.VIDEO_BITRATES + mode, ModeSettings.dataSpinnerVideoBitRates[0]);

                        Camera.sendSettings(ModeSettings.dataSpinnerVideoClipLenght[2], ModeSettings.VIDEO_CLIP_LENGHT + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.VIDEO_CLIP_LENGHT + mode, ModeSettings.dataSpinnerVideoClipLenght[2]);

                        Camera.sendSettings(ModeSettings.dataSpinnerFieldOfView[0], ModeSettings.FIELD_OF_VIEW + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.FIELD_OF_VIEW + mode, ModeSettings.dataSpinnerFieldOfView[0]);

                        Camera.sendSettings(ModeSettings.dataSpinnerTimeLapseVideo[0], ModeSettings.TIME_LAPSE_VIDEO + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.TIME_LAPSE_VIDEO + mode, ModeSettings.dataSpinnerTimeLapseVideo[0]);

                        Camera.sendSettings("on", ModeSettings.WDR + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.WDR + mode, "on");

//                        Camera.sendSettings("off", ModeSettings.DISPLAY_SPEED);
                        Camera.sendSettings(ModeSettings.dataSpinnerVideoResolutions[0], ModeSettings.VIDEO_RESOLUTION + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.VIDEO_RESOLUTION + mode, ModeSettings.dataSpinnerVideoResolutions[0]);

                        Camera.sendSettings(ModeSettings.dataSpinnerAutoRotate[0], ModeSettings.AUTO_ROTATE + mode);
                        ((MyApp)getActivity().getApplication()).getSettingsMap().put(ModeSettings.AUTO_ROTATE + mode, ModeSettings.dataSpinnerAutoRotate[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        resetDialog.dismiss();
                        initSettings();
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });


        if(((TextView)view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("HDR") ||
                ((TextView)view.findViewById(R.id.tvTimeLapseVideo)).getText().toString().contains("sec") ||
                ((TextView)view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("720 60") ){


            view.findViewById(R.id.WDR).setVisibility(View.GONE);
            view.findViewById(R.id.tvFieldOfView).setVisibility(View.GONE);
            view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.VISIBLE);
            view.findViewById(R.id.TimeLapseVideo).setVisibility(View.GONE);
//            newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, new String[]{"155"}, ModeSettings.FIELD_OF_VIEW);
//            ((TextView)view.findViewById(R.id.tvFieldOfView)).setText("155");
        } else {
//            view.findViewById(R.id.FieldOfView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.WDR).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvFieldOfView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.GONE);
            view.findViewById(R.id.TimeLapseVideo).setVisibility(View.VISIBLE);
            newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, ModeSettings.dataSpinnerFieldOfView, ModeSettings.FIELD_OF_VIEW);
        }


        return view;
    }

    private void initSettings() {

//        progDailog = ProgressDialog.show(getActivity(), null, "loading current settings", true);

        newSpinnerCreate(R.id.tvVideoResolutions, R.id.lvVideoResolutions, ModeSettings.dataSpinnerVideoResolutions, ModeSettings.VIDEO_RESOLUTION);
//        initSpinner(ModeSettings.dataSpinnerVideoResolutions, spinnerVideoResolutions, ModeSettings.VIDEO_RESOLUTION);


        initToggle(toggleVideoTimeStamp, ModeSettings.VIDEO_TIME_STAMP);

        initToggle(toggleAudio, ModeSettings.AUDIO);

        initToggle(toggleLoopRecording, ModeSettings.LOOP_RECORDING);

        newSpinnerCreate(R.id.tvVideoBitRates, R.id.lvVideoBitRates, ModeSettings.dataSpinnerVideoBitRates, ModeSettings.VIDEO_BITRATES);
//        initSpinner(ModeSettings.dataSpinnerVideoBitRates, spinnerVideoBitRates, ModeSettings.VIDEO_BITRATES);

        newSpinnerCreate(R.id.tvVideoClipLenght, R.id.lvVideoClipLenght, ModeSettings.dataSpinnerVideoClipLenght, ModeSettings.VIDEO_CLIP_LENGHT);
//        initSpinner(ModeSettings.dataSpinnerVideoClipLenght, spinnerVideoClipLenght, ModeSettings.VIDEO_CLIP_LENGHT);
//        newSpinnerCreate(R.id.tvWDR, R.id.lvWDR, ModeSettings.dataSpinnerWDR, ModeSettings.WDR);
//        initSpinner(ModeSettings.dataSpinnerWDR, spinnerWDR, ModeSettings.WDR);
//        initSpinner(ModeSettings.dataSpinnerFieldOfView, spinnerFieldOfView, ModeSettings.FIELD_OF_VIEW);

        newSpinnerCreate(R.id.tvTimeLapseVideo, R.id.lvTimeLapseVideo, ModeSettings.dataSpinnerTimeLapseVideo, ModeSettings.TIME_LAPSE_VIDEO);
//        initSpinner(ModeSettings.dataSpinnerTimeLapseVideo, spinnerTimeLapseVideo, ModeSettings.TIME_LAPSE_VIDEO);

        newSpinnerCreate(R.id.tvAutoRotate, R.id.lvAutoRotate, ModeSettings.dataSpinnerAutoRotate, ModeSettings.AUTO_ROTATE);

        initToggle(toggleWDR, ModeSettings.WDR);

        if(((TextView)view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("HDR") ||
                ((TextView)view.findViewById(R.id.tvTimeLapseVideo)).getText().toString().contains("sec") ||
                ((TextView)view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("720 60") ){


            view.findViewById(R.id.WDR).setVisibility(View.GONE);
            view.findViewById(R.id.tvFieldOfView).setVisibility(View.GONE);
            view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.VISIBLE);
            view.findViewById(R.id.TimeLapseVideo).setVisibility(View.GONE);
//            newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, new String[]{"155"}, ModeSettings.FIELD_OF_VIEW);
//            ((TextView)view.findViewById(R.id.tvFieldOfView)).setText("155");
        } else {
//            view.findViewById(R.id.FieldOfView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.WDR).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvFieldOfView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.GONE);
            view.findViewById(R.id.TimeLapseVideo).setVisibility(View.VISIBLE);
            newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, ModeSettings.dataSpinnerFieldOfView, ModeSettings.FIELD_OF_VIEW);
        }

//
//
//        initToggle(toggleDisplaySpeed, ModeSettings.DISPLAY_SPEED);
//
//        initToggle(toggleFDWS, ModeSettings.FBWS);

//
//        initModeTime();


    }


    private void newSpinnerCreate(int resourceTv, int resourceLv, String[] data, String key) {
        TextView tvVideoBitRates = (TextView) view.findViewById(resourceTv);
        ListView lvVideoBitRates = (ListView) view.findViewById(resourceLv);
        initNewSpinner(tvVideoBitRates, lvVideoBitRates, data, key);
        Utils.setListViewHeightBasedOnChildren(lvVideoBitRates);
    }

    private void initNewSpinner(final TextView tvOption, final ListView lvOptions, final String[] dataOptions, final String key) {
        if(key.equalsIgnoreCase(ModeSettings.WDR)){
            tvOption.setText(dataOptions[1].replace("P", "FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));
        } else if (!key.equalsIgnoreCase(ModeSettings.AUTO_ROTATE)){
            tvOption.setText(dataOptions[0].replace("P", "FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));
        } else {
            tvOption.setText(dataOptions[0]);
        }

//        if(((TextView)view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("HDR") ||
//                ((TextView)view.findViewById(R.id.tvTimeLapseVideo)).getText().toString().contains("sec") ){
//
//            ((TextView)view.findViewById(R.id.tvFieldOfView)).setText("155");
//        } else {
            getCurrentOption(tvOption, key);
//        }

        OptionsAdapter optionsAdapter = new OptionsAdapter(getActivity(), Arrays.asList(dataOptions), lvOptions);
        lvOptions.setAdapter(optionsAdapter);

        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lvOptions.getVisibility() == View.GONE){
                    lvOptions.setVisibility(View.VISIBLE);
                } else {
                    lvOptions.setVisibility(View.GONE);
                }

                if(ModeSettings.TIME_LAPSE_VIDEO.equalsIgnoreCase(key)){

                    mainScroll.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainScroll.scrollTo(0, mainScroll.getBottom());
                        }
                    }, 500);

                }
            }
        });

        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {



                if(((TextView)FragmentModeSettings.this.view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("HDR") ||
                        ((TextView)FragmentModeSettings.this.view.findViewById(R.id.tvTimeLapseVideo)).getText().toString().contains("sec") ||
                        ((TextView)FragmentModeSettings.this.view.findViewById(R.id.tvVideoResolutions)).getText().toString().contains("720 60") ){


                    FragmentModeSettings.this.view.findViewById(R.id.WDR).setVisibility(View.GONE);
                    FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfView).setVisibility(View.GONE);
                    FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.VISIBLE);
                    FragmentModeSettings.this.view.findViewById(R.id.TimeLapseVideo).setVisibility(View.GONE);

//            ((TextView)view.findViewById(R.id.tvFieldOfView)).setText("155");
                } else {
//            view.findViewById(R.id.FieldOfView).setVisibility(View.VISIBLE);
                    FragmentModeSettings.this.view.findViewById(R.id.WDR).setVisibility(View.VISIBLE);
                    FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfView).setVisibility(View.VISIBLE);
                    FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.GONE);
                    FragmentModeSettings.this.view.findViewById(R.id.TimeLapseVideo).setVisibility(View.VISIBLE);
                    newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, ModeSettings.dataSpinnerFieldOfView, ModeSettings.FIELD_OF_VIEW);

                }


                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(tvVideoResolutions.getText().toString().contains("HDR") /*|| tvTimeLapseVideo.getText().toString().contains("sec")*/ || tvVideoResolutions.getText().toString().contains("720 60")){

                            if(dataOptions[position].contains("HDR")){
                                Toast.makeText(getActivity(), "HDR mode can not support time lapse set up,or else camera may get hanged", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "HDR mode can not support FOV change,or else camera may get hanged", Toast.LENGTH_LONG).show();
                            }

                            FragmentModeSettings.this.view.findViewById(R.id.WDR).setVisibility(View.GONE);
                            FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfView).setVisibility(View.GONE);
                            FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.VISIBLE);
                            FragmentModeSettings.this.view.findViewById(R.id.TimeLapseVideo).setVisibility(View.GONE);
                        } else {
                            FragmentModeSettings.this.view.findViewById(R.id.WDR).setVisibility(View.VISIBLE);
                            FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfView).setVisibility(View.VISIBLE);
                            FragmentModeSettings.this.view.findViewById(R.id.tvFieldOfViewOff).setVisibility(View.GONE);
                            FragmentModeSettings.this.view.findViewById(R.id.TimeLapseVideo).setVisibility(View.VISIBLE);
                            newSpinnerCreate(R.id.tvFieldOfView, R.id.lvFieldOfView, ModeSettings.dataSpinnerFieldOfView, ModeSettings.FIELD_OF_VIEW);
                        }
//
                    }
                }, 500);


//                if(key.equalsIgnoreCase(ModeSettings.VIDEO_RESOLUTION)){
//                    if(dataOptions[position].contains("HDR")){
//                        FragmentModeSettings.this.view.findViewById(R.id.FieldOfView).setVisibility(View.GONE);
//                    } else {
//                        FragmentModeSettings.this.view.findViewById(R.id.FieldOfView).setVisibility(View.VISIBLE);
//                    }
//                }

                if (!key.equalsIgnoreCase(ModeSettings.AUTO_ROTATE)){
                    tvOption.setText(dataOptions[position].replace("0P", "0FPS").replace("5P", "5FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));
                } else {
                    tvOption.setText(dataOptions[position]);
                }

                lvOptions.setVisibility(View.GONE);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        if (isMode2) {
                            request = Camera.sendSettings(dataOptions[position], key + "_mode2");
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key + "_mode2", dataOptions[position]);
                        } else {
                            request = Camera.sendSettings(dataOptions[position], key);
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key, dataOptions[position]);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (getActivity().getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")) {
                            Toast.makeText(getActivity(), request, Toast.LENGTH_SHORT).show();
                        } else {
                            final Toast toast = Toast.makeText(getActivity(), " Setting updated", Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 500);
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });

    }

    private void getCurrentOption(final TextView tvOption, final String key) {
        ++settingsCount;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String result;
                if(isMode2){
//                    result = Camera.getCurrentOption(key + "_mode2");
                    result = ((MyApp)getActivity().getApplication()).getSettingsMap().get(key + "_mode2");
                } else {
//                    result = Camera.getCurrentOption(key);
                    result = ((MyApp)getActivity().getApplication()).getSettingsMap().get(key);
                }
                return result;

            }

            @Override
            protected void onPostExecute(String option) {

                if(paused){
                    return;
                }
                if(option != null){

                    if (!key.equalsIgnoreCase(ModeSettings.AUTO_ROTATE)){
                        tvOption.setText(option.replace("0P", "0FPS").replace("5P", "5FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));
                    } else {
                        tvOption.setText(option);
                    }


                } else {
//                    tvOption.setText("");
                }

                int asd = 132* 23;
//                if(--settingsCount == 0){
//                    progDailog.dismiss();
//                }
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



    }

    private void setCurrentOption(final Spinner spinner, final String[] data, final String key) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result;
                if(isMode2){
//                    result = Camera.getCurrentOption(key + "_mode2");
                    result = ((MyApp)getActivity().getApplication()).getSettingsMap().get(key + "_mode2");
                } else {
//                    result = Camera.getCurrentOption(key);
                    result = ((MyApp)getActivity().getApplication()).getSettingsMap().get(key);
                }

                return result;
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_simple_spinner, data);
                adapter.setDropDownViewResource(R.layout.item_spinner);
                spinner.setAdapter(adapter);
                spinner.setSelection(getOptionPosition(data, option));
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



    }

    private void setCurrentState(final ToggleButton toggle, final String key) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(key);
                return ((MyApp)getActivity().getApplication()).getSettingsMap().get(key);
            }

            @Override
            protected void onPostExecute(String option) {
                if (paused) {
                    return;
                }
                if(option == null){
                    toggle.setChecked(false);
                } else {
                    //if(!key.equalsIgnoreCase(ModeSettings.ROTATE_VIDEO_180_DEGREES))
                        toggle.setChecked(option.equalsIgnoreCase("on"));
                    //else
                    //    toggle.setChecked(option.equalsIgnoreCase("off"));
                }

                checkLogik(key, toggle);

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }


    private int getOptionPosition(String[] data, String option) {
        int resut = 0;
        for(int i = 0; i < data.length; i++) {
            if (option.equalsIgnoreCase(data[i])){
                resut = i;
                break;
            }
        }
        Log.d("myLogs",option +  "  data index = " + resut);
        return resut;
    }

    @Override
    public void onResume() {
        super.onResume();

//        final MyApp myApp = ((MyApp)getActivity().getApplication());
//        myApp.connect(getActivity());
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                while (myApp.isConnecting()){
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
                initSettings();
                paused = false;
                int i = 123 /88123;
//            }
//        }.execute();


    }

    @Override
    public void onPause() {
        paused = true;
        spinerTouched = false;
        super.onPause();
    }

//    private void initModeTime() {
//        TextView tvFromTime = (TextView) view.findViewById(R.id.tvFromTime);
//        int fromHours = settingsPref.getInt(ActivityModeSettings.TV_FROM_TIME_HOURS, 0);
//        int fromMinutes = settingsPref.getInt(ActivityModeSettings.TV_FROM_TIME_MINUTES, 0);
//
//        String hours = (fromHours < 10) ? "0"+fromHours : ""+fromHours;
//        String minutes = (fromMinutes < 10) ? "0"+fromMinutes : "" + fromMinutes;
//
//        tvFromTime.setText(hours + ":" + minutes);
//
//        tvFromTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().showDialog(ActivityModeSettings.FROM_TIME_DIALOG);
//            }
//        });
//
//
//        TextView tvToTime = (TextView) view.findViewById(R.id.tvToTime);
//        int toHours = settingsPref.getInt(ActivityModeSettings.TV_TO_TIME_HOURS, 0);
//        int toMminutes = settingsPref.getInt(ActivityModeSettings.TV_TO_TIME_MINUTES, 0);
//
//        hours = (toHours < 10) ? "0"+toHours : ""+toHours;
//        minutes = (toMminutes < 10) ? "0"+toMminutes : "" + toMminutes;
//
//        tvToTime.setText(hours + ":" + minutes);
//
//        tvToTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().showDialog(ActivityModeSettings.TO_TIME_DIALOG);
//            }
//        });
//
//    }


    private void initToggle(final ToggleButton toggle, final String key) {
        if(isMode2){
            setCurrentState(toggle, key + "_mode2");
        } else {
            setCurrentState(toggle, key);
        }
//        setCurrentState(toggle, key);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isChecked = ((ToggleButton) v).isChecked();
                final String value = isChecked ? "on" : "off";
//
                checkLogik(key, toggle);


                changedSettings.add(key);

                request = "";
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        if(isMode2){
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key + "_mode2", value);
                            request = Camera.sendSettings(value, key + "_mode2");

                            Log.d("SETTINGS SETTER", ((MyApp) getActivity().getApplication()).getSettingsMap().get(key + "_mode2"));
                        } else {
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key, value);
                            request = Camera.sendSettings(value, key);

                            Log.d("SETTINGS SETTER", ((MyApp) getActivity().getApplication()).getSettingsMap().get(key));
                        }

//                        request = Camera.sendSettings(value, key + ((ActivityModeSettings)getActivity()).selectedMode);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(getActivity().getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(FragmentModeSettings.this.getActivity(), request, Toast.LENGTH_SHORT).show();
                        } else {
                            final Toast toast = Toast.makeText(getActivity(), " Setting updated", Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 500);
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());


            }
        });
    }

    private void checkLogik(String key, ToggleButton toggle) {
        if (key.equals(ModeSettings.LOOP_RECORDING)) {
            View videoClipLenght = view.findViewById(R.id.VideoClipLenght);
            if (toggle.isChecked()) {
                videoClipLenght.setVisibility(View.VISIBLE);
            } else {
                videoClipLenght.setVisibility(View.GONE);
            }
        }

        if (key.equals(ModeSettings.TOGGLE_TIMED_MODE)) {
            View FromTime = view.findViewById(R.id.FromTime);
            View ToTime = view.findViewById(R.id.ToTime);
            if (toggle.isChecked()) {
                FromTime.setVisibility(View.VISIBLE);
                ToTime.setVisibility(View.VISIBLE);
            } else {
                FromTime.setVisibility(View.GONE);
                ToTime.setVisibility(View.GONE);
            }
        }
    }

    private void initSpinner(final String[] data, final Spinner spinner, final String key) {


        setCurrentOption(spinner, data, key);
//        final String[] value = {settingsPref.getString(key, "")};
//        int valuePosition = 0;
//        for(int i = 0; i < data.length; i++){
//            if (data[i].equalsIgnoreCase(value[0])){
//                valuePosition = i;
//                break;
//            }
//        }
//        spinner.setSelection(valuePosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                if(!spinerTouched){
                    return;
                }

                changedSettings.add(key);

                request = "";
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        if(isMode2){
                            request = Camera.sendSettings(data[position], key + "_mode2");
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key + "_mode2", data[position]);
                        } else {
                            request = Camera.sendSettings(data[position], key);
                            ((MyApp)getActivity().getApplication()).getSettingsMap().put(key, data[position]);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(getActivity().getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(getActivity(), request, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }


    public static FragmentModeSettings newInstance(String title)
    {


        FragmentModeSettings fragment = new FragmentModeSettings();
        fragment.isMode2 = title.equalsIgnoreCase("Mode 2");
        Log.d("myLogs", "Mode 2 = " + fragment.isMode2);
        Bundle bundle = new Bundle(2);
        bundle.putString(EXTRA_TITLE, title);
        fragment.setArguments(bundle);
        return fragment ;
    }


}
