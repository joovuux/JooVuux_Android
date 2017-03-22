package com.joovuux.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joovuux.MyApp;
import com.joovuux.Utils;
import com.joovuux.connection.Camera;

import java.util.Arrays;

import ua.net.lsoft.joovuux.R;

/**
 * Created by Dobromir on 17.06.2015.
 */
public class FragmentPhotoModeSettings extends Fragment {

    private static final String EXTRA_TITLE = "title";
    private SharedPreferences settingsPref;
    private String mode;
    private View view;

    private ToggleButton toggleDateTimestamp;
    private Spinner spinnerPhotoResolution;
    private ToggleButton toggleRotatePhotos180Degrees;
    private ToggleButton toggleTimeLapsePhoto;
    private ToggleButton toggleTimeLapseVideo;
    private Spinner spinnerBurstPhotoMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_fragment_photo_mode_settings, null);
        mode = getArguments().getString(EXTRA_TITLE);

        settingsPref = getActivity().getSharedPreferences(mode, getActivity().MODE_PRIVATE);

//        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//        tvTitle.setText(mode);

        toggleDateTimestamp = (ToggleButton) view.findViewById(R.id.toggleDateTimestamp);
        spinnerPhotoResolution = (Spinner) view.findViewById(R.id.spinnerPhotoResolution);
        toggleRotatePhotos180Degrees = (ToggleButton) view.findViewById(R.id.toggleRotatePhotos180Degrees);
        toggleTimeLapsePhoto = (ToggleButton) view.findViewById(R.id.toggleTimeLapsePhoto);
        toggleTimeLapseVideo = (ToggleButton) view.findViewById(R.id.toggleTimeLapseVideo);


        initToggle(toggleDateTimestamp, ModeSettings.DATE_TIMESTAMP);



        TextView tvPhotoResolution = (TextView) view.findViewById(R.id.tvPhotoResolution);
        ListView lvPhotoResolution = (ListView) view.findViewById(R.id.lvPhotoResolution);
        initNewSpinner(tvPhotoResolution, lvPhotoResolution, ModeSettings.dataSpinnerPhotoResolution, ModeSettings.PHOTO_RESOLUTION);
        Utils.setListViewHeightBasedOnChildren(lvPhotoResolution);
        initSpinner(ModeSettings.dataSpinnerPhotoResolution, spinnerPhotoResolution, ModeSettings.PHOTO_RESOLUTION);

        initToggle(toggleRotatePhotos180Degrees, ModeSettings.ROTATE_PHOTOS_180_DEGREES);

        initToggle(toggleTimeLapsePhoto, ModeSettings.TIME_LAPSE_PHOTO);

        initToggle(toggleTimeLapseVideo, ModeSettings.TIME_LAPSE_VIDEO);

        spinnerBurstPhotoMode = (Spinner) view.findViewById(R.id.spinnerBurstPhotoMode);
//        spinnerBurstPhotoMode.setOnTouchListener(onTouchListener);

        TextView tvBurstPhotoMode = (TextView) view.findViewById(R.id.tvBurstPhotoMode);
        ListView lvBurstPhotoMode = (ListView) view.findViewById(R.id.lvBurstPhotoMode);
        initNewSpinner(tvBurstPhotoMode, lvBurstPhotoMode, ModeSettings.dataSpinnerBurstPhotoMode, ModeSettings.SPINNER_BURST_PHOTO_MODE);
        Utils.setListViewHeightBasedOnChildren(lvBurstPhotoMode);
        initSpinner(ModeSettings.dataSpinnerBurstPhotoMode, spinnerBurstPhotoMode, ModeSettings.SPINNER_BURST_PHOTO_MODE);



           return view;
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
                if (toggle == null) {
                    return;
                }
                if(option!=null){
                    toggle.setChecked(option.equalsIgnoreCase("on"));
                } else {
                    toggle.setChecked(false);
                }

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private void initToggle(final ToggleButton toggle, final String key) {
        setCurrentState(toggle, key);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isChecked = ((ToggleButton) v).isChecked();
                final String value = isChecked ? "on" : "off";

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.sendSettings(value, key + ((ActivityModeSettings) getActivity()).selectedMode);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        getActivity();
                        if(getActivity().getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(FragmentPhotoModeSettings.this.getActivity(), aVoid, Toast.LENGTH_SHORT).show();
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());


            }
        });
    }

    private void getCurrentOption(final TextView tvOption, final String key) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(key);
                return ((MyApp)getActivity().getApplication()).getSettingsMap().get(key);
            }

            @Override
            protected void onPostExecute(String option) {
                if(tvOption == null){
                    return;
                }
                tvOption.setText(option);
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



    }

    private void initNewSpinner(final TextView tvOption, final ListView lvOptions, final String[] dataOptions, final String key) {
        tvOption.setText(dataOptions[0]);
        getCurrentOption(tvOption, key);
        OptionsAdapter optionsAdapter = new OptionsAdapter(getActivity(), Arrays.asList(dataOptions), lvOptions);
        lvOptions.setAdapter(optionsAdapter);

        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvOptions.getVisibility() == View.GONE) {
                    lvOptions.setVisibility(View.VISIBLE);
                } else {
                    lvOptions.setVisibility(View.GONE);
                }
            }
        });

        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                tvOption.setText(dataOptions[position]);
                lvOptions.setVisibility(View.GONE);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.sendSettings(dataOptions[position], key);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        if(getActivity().getSharedPreferences("MainSettings", getActivity().MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(getActivity(), aVoid, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });

    }



    private void initSpinner(String[] data, Spinner spinner, final String key) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_simple_spinner, data);
        adapter.setDropDownViewResource(R.layout.item_spinner);

        spinner.setAdapter(adapter);
        spinner.setSelection(settingsPref.getInt(key, 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settingsPref.edit().putInt(key, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }

    public static final FragmentPhotoModeSettings newInstance(String title)
    {
        FragmentPhotoModeSettings fragment = new FragmentPhotoModeSettings();
        Bundle bundle = new Bundle(2);
        bundle.putString(EXTRA_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }
}