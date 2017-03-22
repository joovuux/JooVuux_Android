package com.joovuux.settings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joovuux.ActiveActivitiesTracker;
import com.joovuux.ActivityWhiteBalance;
import com.joovuux.MyApp;
import com.joovuux.Utils;
import com.joovuux.connection.Camera;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import ua.net.lsoft.joovuux.R;

public class ActivityMainSettings extends Activity{

    public static final String CAR_NUMBER = "toggleCarNumber";

    public static final int FROM_TIME_DIALOG = 0;
    public static final int TO_TIME_DIALOG = 1;

    private static final int CAMERA_TIME_DIALOG = 3;
    private static final int CAMERA_DATE_DIALOG = 2;
    private static final String CAMERA_TIME = "camera_time";

    public static final String TV_FROM_TIME_HOURS = "tvFromTimeHours";
    public static final String TV_FROM_TIME_MINUTES = "tvFromTimeMinutes";
    public static final String TV_TO_TIME_HOURS = "tvToTimeHours";
    public static final String TV_TO_TIME_MINUTES = "tvToTimeMinutes";
    public static final String SPINNER_SPEED_UNIT = "speed_unit";
    public static final String TIME_MODE_START_TIME = "time_mode_start_time";
    public static final String TIME_MODE_FINISH_TIME = "time_mode_finish_time";
    private static final String WIFI_PASSWORD = "wifi_password";
    public static String dateFromat = "yyyy/MM/dd";


    private SharedPreferences settingsPref;

    private View toggleSyncFateAndTime;
    private Spinner spinnerDateFormat;
    private ToggleButton toggleBeepNoises;
    private ToggleButton toggleRecordingLEDindicator;
    private Spinner spinnerDefaultModeCameraStarts;
    private Spinner spinnerPowerOnAutoRecord;
    private Spinner spinnerStandbyTime;
    private Spinner spinnerPowerOffDisconnect;
    private Spinner spinnerPowerOnDelay;
    private ToggleButton toggleMotionDetection;
    private Spinner spinnerMotionDetectionSensitivity;
    private Spinner spinnerMotionTurnOff;
    private Spinner spinnerTVOut;
    private ToggleButton toggleSpeenStamp;
    private ToggleButton toggleGSensor;
    private ToggleButton toggleCarPlateStamp;
    private ToggleButton toggleConnectionLog;
    private ToggleButton toggleTimedMode;
    private ToggleButton toggleLowBatteryWarning;
    private ToggleButton toggleWifiAutoStart;
//    private Spinner spinnerArtificialLightFrequency;
    private Spinner spinnerSpeedUnit;

    private String request;
    private EditText etCarNumber;

    private boolean spinerTouched;
    View.OnTouchListener onTouchListener =  new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            spinerTouched = true;
            return false;
        }
    };
    private boolean paused;
    private String cameraTime;
    private TextView tvCameraTime;
    private ProgressDialog progDailog;
    private ProgressDialog resetDialog;
    private int settingsCount;
    private ToggleButton toggleParkingMode;
    private EditText etWiFiPassword;
    private ToggleButton toggleResetSettingsToDefault;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main_settings);

        toggleResetSettingsToDefault = (ToggleButton) findViewById(R.id.toggleResetSettingsToDefault);
        toggleResetSettingsToDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    resetSettings();
                }

            }
        });


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
               Camera.send260();
                return null;
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());

        for(Locale locale : Locale.getAvailableLocales()){
            if(locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE)){

            }
        }

        tvCameraTime = (TextView) findViewById(R.id.tvCameraTime);
        tvCameraTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CAMERA_DATE_DIALOG);
            }
        });

        settingsPref = getSharedPreferences("MainSettings", MODE_PRIVATE);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        toggleTimedMode = (ToggleButton) findViewById(R.id.toggleTimeMode);

        toggleLowBatteryWarning = (ToggleButton) findViewById(R.id.toggleLowBatteryWarning);

        toggleWifiAutoStart = (ToggleButton) findViewById(R.id.toggleWifiAutoStart);
//        spinnerArtificialLightFrequency = (Spinner) findViewById(R.id.spinnerArtificialLightFrequency);
//        spinnerArtificialLightFrequency.setOnTouchListener(onTouchListener);

        final ArrayList<Character> characters = new ArrayList<Character>();
        characters.add('丶');
        characters.add('丿');
        characters.add('乙');
        characters.add('乚');
        characters.add('亅');
        characters.add('二');
        characters.add('亠');
        characters.add('人');
        characters.add('儿');
        characters.add('亻');
        characters.add('入');
        characters.add('八');
        characters.add('冂');
        characters.add('冖');
        characters.add('冫');
        characters.add('几');
        characters.add('凵');
        characters.add('刀');
        characters.add('力');
        characters.add('勹');
        characters.add('匕');
        characters.add('匚');
        characters.add('匸');
        characters.add('十');
        characters.add('刂');
        characters.add('士');
        characters.add('夂');
        characters.add('夊');
        characters.add('夕');
        characters.add('大');
        characters.add('女');
        characters.add('子');
        characters.add('宀');
        characters.add('寸');
        characters.add('小');
        characters.add('⺌');
        characters.add('⺍');
        characters.add('尢');
        characters.add('尣');
        characters.add('尸');
        characters.add('屮');
        characters.add('山');
        characters.add('巛');
        characters.add('川');
        characters.add('巜');
        characters.add('工');
        characters.add('己');
        characters.add('已');
        characters.add('巳');
        characters.add('巾');
        characters.add('干');
        characters.add('幺');
        characters.add('广');
        characters.add('廴');
        characters.add('廾');
        characters.add('弋');
        characters.add('弓');
        characters.add('彐');
        characters.add('彑');
        characters.add('彡');
        characters.add('彳');
        characters.add('心');
        characters.add('忄');
        characters.add('⺗');
        characters.add('戈');
        characters.add('户');
        characters.add('戸');
        characters.add('戶');
        characters.add('手');
        characters.add('扌');
        characters.add('龵');
        characters.add('支');
        characters.add('攴');
        characters.add('攵');
        characters.add('文');
        characters.add('斗');
        characters.add('斤');
        characters.add('方');
        characters.add('无');
        characters.add('日');
        characters.add('曰');
        characters.add('月');
        characters.add('木');
        characters.add('欠');
        characters.add('止');
        characters.add('歹');
        characters.add('歺');
        characters.add('殳');
        characters.add('毋');
        characters.add('比');
        characters.add('毛');
        characters.add('氏');
        characters.add('气');
        characters.add('水');
        characters.add('氵');
        characters.add('氺');
        characters.add('火');
        characters.add('灬');
        characters.add('爪');
        characters.add('爫');
        characters.add('父');
        characters.add('爻');
        characters.add('爿');
        characters.add('丬');
        characters.add('片');

        characters.add('牙');

        characters.add('牛');
        characters.add('⺧');
        characters.add('犬');
        characters.add('犭');
        characters.add('玄');
        characters.add('玉');
        characters.add('瓜');
        characters.add('瓦');
        characters.add('甘');
        characters.add('生');
        characters.add('用');
        characters.add('甩');
        characters.add('田');
        characters.add('疋');
        characters.add('⺪');
        characters.add('疒');
        characters.add('癶');
        characters.add('白');
        characters.add('皮');
        characters.add('皿');
        characters.add('目');
        characters.add('矛');
        characters.add('矢');
        characters.add('石');
        characters.add('示');
        characters.add('礻');
        characters.add('禸');
        characters.add('禾');
        characters.add('穴');
        characters.add('立');
        characters.add('竹');
        characters.add('⺮');
        characters.add('米');
        characters.add('糸');
        characters.add('糹');
        characters.add('纟');
        characters.add('缶');
        characters.add('网');
        characters.add('罒');
        characters.add('罓');
        characters.add('⺳');
        characters.add('羊');
        characters.add('⺶');
        characters.add('⺷');
        characters.add('羽');
        characters.add('老');
        characters.add('耂');
        characters.add('而');
        characters.add('耒');
        characters.add('耳');
        characters.add('聿');
        characters.add('⺻');
        characters.add('肀');
        characters.add('肉');
        characters.add('⺼');
        characters.add('臣');
        characters.add('自');
        characters.add('至');
        characters.add('臼');
        characters.add('舌');
        characters.add('舛');
        characters.add('舟');
        characters.add('艮');
        characters.add('色');
        characters.add('艸');
        characters.add('艹');
        characters.add('虍');
        characters.add('虫');
        characters.add('血');
        characters.add('行');
        characters.add('衣');
        characters.add('衤');
        characters.add('襾');
        characters.add('西');
        characters.add('覀');
        characters.add('見');
        characters.add('见');
        characters.add('角');
        characters.add('言');
        characters.add('訁');
        characters.add('讠');
        characters.add('谷');
        characters.add('豆');
        characters.add('豕');
        characters.add('豸');
        characters.add('貝');
        characters.add('贝');
        characters.add('赤');
        characters.add('走');
        characters.add('赱');
        characters.add('足');
        characters.add('⻊');
        characters.add('身');
        characters.add('車');
        characters.add('车');
        characters.add('辛');
        characters.add('辰');
        characters.add('辵');
        characters.add('辶');
        characters.add('⻌');
        characters.add('⻍');
        characters.add('邑');
        characters.add('阝');
        characters.add('酉');
        characters.add('釆');
        characters.add('里');
        characters.add('金');
        characters.add('釒');
        characters.add('钅');
        characters.add('長');
        characters.add('镸');
        characters.add('长');
        characters.add('門');
        characters.add('门');
        characters.add('阜');
        characters.add('阝');
        characters.add('隶');
        characters.add('隹');
        characters.add('雨');
        characters.add('青');
        characters.add('靑');
        characters.add('非');
        characters.add('面');
        characters.add('靣');
        characters.add('革');
        characters.add('韋');
        characters.add('韦');
        characters.add('韭');
        characters.add('音');
        characters.add('頁');
        characters.add('页');
        characters.add('風');
        characters.add('风');
        characters.add('飛');
        characters.add('飞');
        characters.add('食');
        characters.add('飠');
        characters.add('饣');
        characters.add('首');
        characters.add('香');
        characters.add('馬');
        characters.add('马');
        characters.add('骨');
        characters.add('高');
        characters.add('髙');
        characters.add('髟');
        characters.add('鬥');
        characters.add('斗');
        characters.add('鬯');
        characters.add('鬲');
        characters.add('鬼');
        characters.add('魚');
        characters.add('鱼');
        characters.add('鳥');
        characters.add('鸟');
        characters.add('鹵');
        characters.add('卤');
        characters.add('鹿');
        characters.add('麥');
        characters.add('麦');
        characters.add('麻');
        characters.add('黃');
        characters.add('黍');
        characters.add('黑');
        characters.add('黹');
        characters.add('黽');
        characters.add('黾');
        characters.add('鼎');
        characters.add('鼓');
        characters.add('鼠');
        characters.add('鼻');
        characters.add('齊');
        characters.add('齐');
        characters.add('齒');
        characters.add('齿');
        characters.add('龍');
        characters.add('龙');
        characters.add('龜');
        characters.add('龟');
        characters.add('頁');


        etCarNumber = (EditText) findViewById(R.id.etCarNumber);

        etCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String carNumber = etCarNumber.getText().toString();
                if(carNumber.length() != 0){
                    if(characters.contains(carNumber.charAt(carNumber.length()-1))){
                        carNumber = carNumber.replace(carNumber.charAt(carNumber.length()-1)+"", "");
                        etCarNumber.setText(carNumber);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String carNumber = etCarNumber.getText().toString();
                if(carNumber.length() != 0){
                    if(characters.contains(carNumber.charAt(carNumber.length()-1))){
                        carNumber = carNumber.replace(carNumber.charAt(carNumber.length()-1)+"", "");
                        etCarNumber.setText(carNumber);
                    }
                }
            }
        });


        etWiFiPassword = (EditText) findViewById(R.id.etWiFiPassword);


        toggleSyncFateAndTime = findViewById(R.id.toggleSyncFateAndTime);
        toggleBeepNoises = (ToggleButton) findViewById(R.id.toggleBeepNoises);
        toggleRecordingLEDindicator = (ToggleButton) findViewById(R.id.toggleRecordingLEDindicator);
        toggleMotionDetection = (ToggleButton) findViewById(R.id.toggleMotionDetection);
        toggleGSensor = (ToggleButton) findViewById(R.id.toggleGSensor);
        toggleCarPlateStamp = (ToggleButton) findViewById(R.id.toggleCarPlateStamp);
        toggleConnectionLog = (ToggleButton) findViewById(R.id.toggleConnectionLog);
//        toggleParkingMode = (ToggleButton) findViewById(R.id.toggleParkingMode);

        spinnerDateFormat = (Spinner) findViewById(R.id.spinnerDateFormat);
        spinnerDateFormat.setOnTouchListener(onTouchListener);

        spinnerDefaultModeCameraStarts = (Spinner) findViewById(R.id.spinnerDefaultModeCameraStarts);
        spinnerDefaultModeCameraStarts.setOnTouchListener(onTouchListener);

        spinnerPowerOnAutoRecord = (Spinner) findViewById(R.id.spinnerPowerOnAutoRecord);
        spinnerPowerOnAutoRecord.setOnTouchListener(onTouchListener);

        spinnerStandbyTime = (Spinner) findViewById(R.id.spinnerStandbyTime);
        spinnerStandbyTime.setOnTouchListener(onTouchListener);

        spinnerPowerOffDisconnect = (Spinner) findViewById(R.id.spinnerPowerOffDisconnect);
        spinnerPowerOffDisconnect.setOnTouchListener(onTouchListener);

        spinnerPowerOnDelay = (Spinner) findViewById(R.id.spinnerPowerOnDelay);
        spinnerPowerOnDelay.setOnTouchListener(onTouchListener);

        spinnerMotionDetectionSensitivity = (Spinner) findViewById(R.id.spinnerMotionDetectionSensitivity);
        spinnerMotionDetectionSensitivity.setOnTouchListener(onTouchListener);

        spinnerMotionTurnOff = (Spinner) findViewById(R.id.spinnerMotionTurnOff);
        spinnerMotionTurnOff.setOnTouchListener(onTouchListener);

        spinnerTVOut = (Spinner) findViewById(R.id.spinnerTVOut);
        spinnerTVOut.setOnTouchListener(onTouchListener);

        toggleSpeenStamp = (ToggleButton) findViewById(R.id.toggleSpeenStamp);
        spinnerSpeedUnit = (Spinner) findViewById(R.id.spinnerSpeedUnit);
        spinnerSpeedUnit.setOnTouchListener(onTouchListener);

        toggleSyncFateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTimeAsync();

            }
        });

        findViewById(R.id.btnResetAllSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSettings();
            }


        });

        if(checkSettingsIsDefault()){
            toggleResetSettingsToDefault.setEnabled(false);
            toggleResetSettingsToDefault.setChecked(true);
        } else {
            toggleResetSettingsToDefault.setEnabled(true);
            toggleResetSettingsToDefault.setChecked(false);
        }


    }

    private void syncTimeAsync() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                return Camera.sendSettings(dateFormat.format(new Date()), MainSettings.TOGGLE_SYNC_FATE_AND_TIME);
            }

            @Override
            protected void onPostExecute(String aVoid) {
                if (getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")) {
                    Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                }


                // Setup current time from camera
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.getCurrentOption(CAMERA_TIME);
//                                return ((MyApp) getApplication()).getSettingsMap().get(CAMERA_TIME);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {

                        setDateFormat();
                        SimpleDateFormat cameraDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        SimpleDateFormat userDateFormat = new SimpleDateFormat(dateFromat +" HH:mm:ss");
                        Date date = new Date();
                        try {
                            date = cameraDateFormat.parse(aVoid);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        tvCameraTime.setText(userDateFormat.format(date));
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());


            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private void resetSettings() {
        resetDialog = ProgressDialog.show(ActivityMainSettings.this, null, "reset all settings", true);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                Camera.resetSetting();
//                        Camera.sendSettings(MainSettings.dataSpinnerDateFormat[1], MainSettings.SPINNER_DATE_FORMAT);


                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_DATE_FORMAT, MainSettings.dataSpinnerDateFormat[1]);

//                        Camera.sendSettings("on", MainSettings.TOGGLE_BEEP_NOISES);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_BEEP_NOISES, "on");

//                        Camera.sendSettings("on", MainSettings.TOGGLE_RECORDING_LED_INDICATOR);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_RECORDING_LED_INDICATOR, "on");

//                        Camera.sendSettings(MainSettings.dataSpinnerDefaultModeCameraStarts[0], MainSettings.SPINNER_DEFAULT_MODE_CAMERA_STATS);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_DEFAULT_MODE_CAMERA_STATS, MainSettings.dataSpinnerDefaultModeCameraStarts[0]);

//                        Camera.sendSettings(MainSettings.dataSpinnerPowerOnAutoRecord[1], MainSettings.SPINNER_POWER_ON_AUTO_RECORD);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_POWER_ON_AUTO_RECORD, MainSettings.dataSpinnerPowerOnAutoRecord[1]);

//                        Camera.sendSettings(MainSettings.dataSpinnerStandbyTime[1], MainSettings.SPINNER_STANDBY_TIME);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_STANDBY_TIME, MainSettings.dataSpinnerStandbyTime[1]);

//                        Camera.sendSettings(MainSettings.dataSpinnerPowerOffDisconnect[0], MainSettings.SPINNER_POWER_OFF_DISCONNECT);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_POWER_OFF_DISCONNECT, MainSettings.dataSpinnerPowerOffDisconnect[1]);

                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_POWER_ON_DELAY, MainSettings.dataSpinnerPowerOnDelay[0]);

//                        Camera.sendSettings("off", MainSettings.TOGGLE_MOTION_DETECTION);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_MOTION_DETECTION, "off");

//                        Camera.sendSettings(MainSettings.dataSpinnerMotionDetectionSensitivity[0], MainSettings.SPINNER_MOTION_DETECTION_SENSIVITY);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_MOTION_DETECTION_SENSIVITY, MainSettings.dataSpinnerMotionDetectionSensitivity[0]);

//                        Camera.sendSettings(MainSettings.dataSpinnerMotionTurnOff[0], MainSettings.SPINNER_MOTION_TURN_OFF);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_MOTION_TURN_OFF, MainSettings.dataSpinnerMotionTurnOff[0]);

//                        Camera.sendSettings(MainSettings.dataSpinnerTVOut[1], MainSettings.SPINNER_TV_OUT);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_TV_OUT, MainSettings.dataSpinnerTVOut[1]);

//                        Camera.sendSettings(MainSettings.getDataSpinnerGSensorSensitivity[0], MainSettings.SPINNER_G_SENSOR_SENSIVITY);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.SPINNER_G_SENSOR_SENSIVITY, MainSettings.getDataSpinnerGSensorSensitivity[0]);

//                        Camera.sendSettings("on", MainSettings.TOGGLE_G_SENSOR);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_G_SENSOR, "on");

//                        Camera.sendSettings("off", MainSettings.TOGGLE_CAR_PLATE_STAMP);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_CAR_PLATE_STAMP, "off");

//                        Camera.sendSettings("off", MainSettings.TOGGLE_SPEED_STAMP);
                ((MyApp) getApplication()).getSettingsMap().put(MainSettings.TOGGLE_SPEED_STAMP, "off");

//                        Camera.sendSettings(MainSettings.dataSpinnerUnitSpeed[0], SPINNER_SPEED_UNIT);
                ((MyApp) getApplication()).getSettingsMap().put(SPINNER_SPEED_UNIT, MainSettings.dataSpinnerUnitSpeed[0]);

//                        Camera.sendSettings("off", ModeSettings.TOGGLE_TIMED_MODE);
                ((MyApp) getApplication()).getSettingsMap().put(ModeSettings.TOGGLE_TIMED_MODE, "off");

                ((MyApp) getApplication()).getSettingsMap().put(ModeSettings.LOW_BATTERY_WARNING, "off");

                ((MyApp) getApplication()).getSettingsMap().put(ModeSettings.WIFI_AUTO_START, "off");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                resetDialog.dismiss();
                initSettings();

                if(checkSettingsIsDefault()){
                    toggleResetSettingsToDefault.setEnabled(false);
                    toggleResetSettingsToDefault.setChecked(true);
                } else {
                    toggleResetSettingsToDefault.setEnabled(true);
                    toggleResetSettingsToDefault.setChecked(false);
                }


//                        recreate();


            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private boolean checkSettingsIsDefault() {

        if(!((TextView) findViewById(R.id.tvDateFormat)).getText().toString().equals(MainSettings.dataSpinnerDateFormat[1])){
            return false;
        }

        if(!toggleBeepNoises.isChecked()){
            return false;
        }

        if(!toggleRecordingLEDindicator.isChecked()){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvDefaultModeCameraStarts)).getText().toString().equals(MainSettings.dataSpinnerDefaultModeCameraStarts[0])){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvPowerOnAutoRecord)).getText().toString().equals(MainSettings.dataSpinnerPowerOnAutoRecord[1])){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvStandbyTime)).getText().toString().equals(MainSettings.dataSpinnerStandbyTime[1])){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvPowerOffDisconnect)).getText().toString().equals(MainSettings.dataSpinnerPowerOffDisconnect[1])){
            return false;
        }
        if(!((TextView) findViewById(R.id.tvPowerOnDelay)).getText().toString().equals(MainSettings.dataSpinnerPowerOnDelay[0])){
            return false;
        }

        if(toggleMotionDetection.isChecked()){
            return false;
        }


        if(!((TextView) findViewById(R.id.tvMotionDetectionSensitivity)).getText().toString().equals(MainSettings.dataSpinnerMotionDetectionSensitivity[0])){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvMotionTurnOff)).getText().toString().equals(MainSettings.dataSpinnerMotionTurnOff[0])){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvTVOut)).getText().toString().equals(MainSettings.dataSpinnerTVOut[1])){
            return false;
        }


        if(!((TextView) findViewById(R.id.tvGSensorSensivity)).getText().toString().equals(MainSettings.getDataSpinnerGSensorSensitivity[0])){
            return false;
        }


        if(!toggleGSensor.isChecked()){
            return false;
        }

        if(toggleCarPlateStamp.isChecked()){
            return false;
        }

        if(toggleSpeenStamp.isChecked()){
            return false;
        }

        if(!((TextView) findViewById(R.id.tvSpeedUnits)).getText().toString().equals(MainSettings.dataSpinnerUnitSpeed[0])){
            return false;
        }

        if(toggleTimedMode.isChecked()){
            return false;
        }

        if(toggleLowBatteryWarning.isChecked()){
            return false;
        }

        if(toggleWifiAutoStart.isChecked()){
            return false;
        }
        return true;

    }



    private void setDateFormat(){
        if(((TextView)findViewById(R.id.tvDateFormat)).getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[0])){
            dateFromat = "yyyy/MM/dd";
        } else if(((TextView)findViewById(R.id.tvDateFormat)).getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[1])){
            dateFromat = "dd/MM/yyyy";
        } else if(((TextView)findViewById(R.id.tvDateFormat)).getText().toString().equalsIgnoreCase(MainSettings.dataSpinnerDateFormat[2])){
            dateFromat = "MM/dd/yyyy";
        }
    }

    private void initSettings() {
        progDailog = ProgressDialog.show(this, null, "loading current settings", true);

        initToggle(toggleTimedMode, ModeSettings.TOGGLE_TIMED_MODE);
        initToggle(toggleLowBatteryWarning, ModeSettings.LOW_BATTERY_WARNING);
        initToggle(toggleWifiAutoStart, ModeSettings.WIFI_AUTO_START);

        initModeTime();
//        newSpinnerCreate(R.id.tvArtificialLightFrequency, R.id.lvArtificialLightFrequency, ModeSettings.dataSpinnerArtificialLightFrequency, ModeSettings.ARTIFICIAL_LIGHT_FREQUENCY);
//        initSpinner(ModeSettings.dataSpinnerArtificialLightFrequency, spinnerArtificialLightFrequency, ModeSettings.ARTIFICIAL_LIGHT_FREQUENCY);
        initEditCarNumber();
        initWiFiPassword();

        newSpinnerCreate(R.id.tvDateFormat, R.id.lvDateFormat, MainSettings.dataSpinnerDateFormat, MainSettings.SPINNER_DATE_FORMAT);
//        initSpinner(dataSpinnerDateFormat, spinnerDateFormat, SPINNER_DATE_FORMAT);



        initToggle(toggleBeepNoises, MainSettings.TOGGLE_BEEP_NOISES);

        initToggle(toggleRecordingLEDindicator, MainSettings.TOGGLE_RECORDING_LED_INDICATOR);

//        newSpinnerCreate(R.id.tvGs, R.id.lvDefaultModeCameraStarts, dataSpinnerDefaultModeCameraStarts, SPINNER_DEFAULT_MODE_CAMERA_STATS);

        newSpinnerCreate(R.id.tvDefaultModeCameraStarts, R.id.lvDefaultModeCameraStarts, MainSettings.dataSpinnerDefaultModeCameraStarts, MainSettings.SPINNER_DEFAULT_MODE_CAMERA_STATS);
//        initSpinner(dataSpinnerDefaultModeCameraStarts, spinnerDefaultModeCameraStarts, SPINNER_DEFAULT_MODE_CAMERA_STATS);

        newSpinnerCreate(R.id.tvPowerOnAutoRecord, R.id.lvPowerOnAutoRecord, MainSettings.dataSpinnerPowerOnAutoRecord, MainSettings.SPINNER_POWER_ON_AUTO_RECORD);
//        initSpinner(dataSpinnerPowerOnAutoRecord, spinnerPowerOnAutoRecord, SPINNER_POWER_ON_AUTO_RECORD);

        newSpinnerCreate(R.id.tvStandbyTime, R.id.lvStandbyTime, MainSettings.dataSpinnerStandbyTime, MainSettings.SPINNER_STANDBY_TIME);
//        initSpinner(dataSpinnerStandbyTime, spinnerStandbyTime, SPINNER_STANDBY_TIME);

        newSpinnerCreate(R.id.tvPowerOffDisconnect, R.id.lvPowerOffDisconnect, MainSettings.dataSpinnerPowerOffDisconnect, MainSettings.SPINNER_POWER_OFF_DISCONNECT);
//        initSpinner(dataSpinnerPowerOffDisconnect, spinnerPowerOffDisconnect, SPINNER_POWER_OFF_DISCONNECT);

        newSpinnerCreate(R.id.tvPowerOnDelay, R.id.lvPowerOnDelay, MainSettings.dataSpinnerPowerOnDelay, MainSettings.SPINNER_POWER_ON_DELAY);

        initToggle(toggleMotionDetection, MainSettings.TOGGLE_MOTION_DETECTION);

        newSpinnerCreate(R.id.tvMotionDetectionSensitivity, R.id.lvMotionDetectionSensitivity, MainSettings.dataSpinnerMotionDetectionSensitivity, MainSettings.SPINNER_MOTION_DETECTION_SENSIVITY);
//        initSpinner(dataSpinnerMotionDetectionSensitivity, spinnerMotionDetectionSensitivity, SPINNER_MOTION_DETECTION_SENSIVITY);

        newSpinnerCreate(R.id.tvMotionTurnOff, R.id.lvMotionTurnOff, MainSettings.dataSpinnerMotionTurnOff, MainSettings.SPINNER_MOTION_TURN_OFF);
//        initSpinner(dataSpinnerMotionTurnOff, spinnerMotionTurnOff, SPINNER_MOTION_TURN_OFF);

        newSpinnerCreate(R.id.tvTVOut, R.id.lvTVOut, MainSettings.dataSpinnerTVOut, MainSettings.SPINNER_TV_OUT);
//        initSpinner(dataSpinnerTVOut, spinnerTVOut, SPINNER_TV_OUT);

        newSpinnerCreate(R.id.tvGSensorSensivity, R.id.lvGSensorSensivity, MainSettings.getDataSpinnerGSensorSensitivity, MainSettings.SPINNER_G_SENSOR_SENSIVITY);

        initToggle(toggleGSensor, MainSettings.TOGGLE_G_SENSOR);

        initToggle(toggleCarPlateStamp, MainSettings.TOGGLE_CAR_PLATE_STAMP);

        initToggle(toggleConnectionLog, MainSettings.CONNECTION_LOG);



        initToggle(toggleSpeenStamp, MainSettings.TOGGLE_SPEED_STAMP);

//        initToggle(toggleParkingMode, MainSettings.TOGGLE_PARKING_MODE);

        newSpinnerCreate(R.id.tvSpeedUnits, R.id.lvSpeedUnits, MainSettings.dataSpinnerUnitSpeed, SPINNER_SPEED_UNIT);
//        initSpinner(dataSpinnerUnitSpeed ,spinnerSpeedUnit , SPINNER_SPEED_UNIT);
    }

    private void newSpinnerCreate(int resourceTv, int resourceLv, String[] data, String key) {
        TextView textView = (TextView) findViewById(resourceTv);
        ListView listView = (ListView) findViewById(resourceLv);
        initNewSpinner(textView, listView, data, key);
        Utils.setListViewHeightBasedOnChildren(listView);
    }

    private void getCurrentOption(final TextView tvOption, final String key) {
        settingsCount++;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

//                return Camera.getCurrentOption(key);
                return ((MyApp)getApplication()).getSettingsMap().get(key);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }

                if(option!= null){
                    tvOption.setText(option.replace("_sec", " sec").replace("_min", " min"));
                }

                    if(--settingsCount == 0){
                        progDailog.dismiss();

                        if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            ((MyApp)ActivityMainSettings.this.getApplication()).showLogDialog(ActivityMainSettings.this);
                        }
                    }

                if(key.equalsIgnoreCase(MainSettings.SPINNER_DATE_FORMAT)){
                    setDateFormat();


                }

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



    }

    private void initNewSpinner(final TextView tvOption, final ListView lvOptions, final String[] dataOptions, final String key) {
        if(key.equalsIgnoreCase(MainSettings.SPINNER_DATE_FORMAT)) {
            tvOption.setText(dataOptions[1].replace("_sec", " sec").replace("_min", " min"));
        } else if(key.equalsIgnoreCase(MainSettings.SPINNER_POWER_ON_AUTO_RECORD)) {
            tvOption.setText(dataOptions[1].replace("_sec", " sec").replace("_min", " min"));
        } else {
            tvOption.setText(dataOptions[0].replace("_sec", " sec").replace("_min", " min"));
        }

//        if(!key.equalsIgnoreCase(MainSettings.SPINNER_MOTION_TURN_OFF)){
            getCurrentOption(tvOption, key);
//        }

        OptionsAdapter optionsAdapter = new OptionsAdapter(this, Arrays.asList(dataOptions), lvOptions);
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
//                        Camera.send260();

                        return Camera.sendSettings(dataOptions[position], key);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        ((MyApp) getApplication()).getSettingsMap().put(key, dataOptions[position]);
                        if (getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")) {
                            Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                        } else {
                            final Toast toast = Toast.makeText(ActivityMainSettings.this, " Setting updated", Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 500);
                        }

                        if (checkSettingsIsDefault()) {
                            toggleResetSettingsToDefault.setEnabled(false);
                            toggleResetSettingsToDefault.setChecked(true);
                        } else {
                            toggleResetSettingsToDefault.setEnabled(true);
                            toggleResetSettingsToDefault.setChecked(false);
                        }

                        if (key.equalsIgnoreCase(MainSettings.SPINNER_DATE_FORMAT)) {
                            syncTimeAsync();

                        }
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        TimePickerDialog.OnTimeSetListener myCallBack = null;

        if (id == FROM_TIME_DIALOG){
//            myCallBack = new TimePickerDialog.OnTimeSetListener() {
//                public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
//
//                    new AsyncTask<Void, Void, String>() {
//                        @Override
//                        protected String doInBackground(Void... params) {
//                            return Camera.sendSettings(hourOfDay + "", TIME_MODE_START_TIME);
//                        }
//
//                        @Override
//                        protected void onPostExecute(String aVoid) {
//                            ((MyApp)getApplication()).getSettingsMap().put(TIME_MODE_START_TIME, hourOfDay + "");
//                            if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
//                                Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    }.executeOnExecutor(Camera.getExecutorCameraCommands());
//
////                    settingsPref.edit()
////                            .putInt(TV_FROM_TIME_HOURS, hourOfDay)
////                            .putInt(TV_FROM_TIME_MINUTES, minute)
////                            .apply();
//
//                    initModeTime();
//
//                }
//            };
            final Dialog numberPickerDialog = new Dialog(this);
            numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            numberPickerDialog.setContentView(R.layout.number_picker_dialog);
            final NumberPicker numPicker = (NumberPicker) numberPickerDialog.findViewById(R.id.numPicker);

            numPicker.setMaxValue(24);
            numPicker.setMinValue(1);

            numPicker.setValue(Integer.parseInt(((MyApp) getApplication()).getSettingsMap().get(TIME_MODE_START_TIME)));

            Button btnOk = (Button) numberPickerDialog.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int value = numPicker.getValue();

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            return Camera.sendSettings(value + "", TIME_MODE_START_TIME);
                        }

                        @Override
                        protected void onPostExecute(String aVoid) {
                            ((MyApp)getApplication()).getSettingsMap().put(TIME_MODE_START_TIME, value + "");
                            if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                                Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                            }
                            numberPickerDialog.cancel();
                            initModeTime();

                        }
                    }.executeOnExecutor(Camera.getExecutorCameraCommands());


                }
            });

            return numberPickerDialog;

//            return new TimePickerDialog(this, myCallBack, 0, 0, true);
        } else if (id == TO_TIME_DIALOG) {
//            myCallBack = new TimePickerDialog.OnTimeSetListener() {
//                public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
//
//                    new AsyncTask<Void, Void, String>() {
//                        @Override
//                        protected String doInBackground(Void... params) {
//                            return Camera.sendSettings(hourOfDay + "", TIME_MODE_FINISH_TIME);
//                        }
//
//                        @Override
//                        protected void onPostExecute(String aVoid) {
//                            ((MyApp)getApplication()).getSettingsMap().put(TIME_MODE_FINISH_TIME, hourOfDay + "");
//                            if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
//                                Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }.executeOnExecutor(Camera.getExecutorCameraCommands());
//
////                    settingsPref.edit()
////                            .putInt(TV_TO_TIME_HOURS, hourOfDay)
////                            .putInt(TV_TO_TIME_MINUTES, minute)
////                            .apply();
//
//                    initModeTime();
//                }
//            };


            final Dialog numberPickerDialog = new Dialog(this);
            numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            numberPickerDialog.setContentView(R.layout.number_picker_dialog);
            final NumberPicker numPicker = (NumberPicker) numberPickerDialog.findViewById(R.id.numPicker);

            numPicker.setMaxValue(24);
            numPicker.setMinValue(1);

            numPicker.setValue(Integer.parseInt(((MyApp) getApplication()).getSettingsMap().get(TIME_MODE_FINISH_TIME)));
            Button btnOk = (Button) numberPickerDialog.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int value = numPicker.getValue();

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            return Camera.sendSettings(value + "", TIME_MODE_FINISH_TIME);
                        }

                        @Override
                        protected void onPostExecute(String aVoid) {
                            ((MyApp)getApplication()).getSettingsMap().put(TIME_MODE_FINISH_TIME, value + "");
                            if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                                Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                            }
                            numberPickerDialog.cancel();
                            initModeTime();

                        }
                    }.executeOnExecutor(Camera.getExecutorCameraCommands());


                }
            });

            return numberPickerDialog;
//            return new TimePickerDialog(this, myCallBack, 0, 0, true);
        }


        if(id == CAMERA_DATE_DIALOG) {

            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    int i = 123 /12;

                    Log.d("TIME: ", "YEAR = " + year + " MONTH = " + monthOfYear + " DAY = " + dayOfMonth);

                    monthOfYear++;
                    String day = (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth + "";
                    String month = (monthOfYear < 10)? "0" + monthOfYear: monthOfYear +"";

                    cameraTime = year + "/" + month + "/" + day;
                    showDialog(CAMERA_TIME_DIALOG);
                }
            };

            return new DatePickerDialog(this, onDateSetListener, 2015, 8, 0);
        }

        if(id == CAMERA_TIME_DIALOG) {

            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hourS = (hourOfDay < 10) ? "0" + hourOfDay: hourOfDay + "";
                    String minuteS = (minute < 10) ? "0" + minute: minute + "";
                    cameraTime = cameraTime + " " + hourS + ":" + minuteS + ":00";
                    tvCameraTime.setText(cameraTime);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {

                            setDateFormat();
                            SimpleDateFormat cameraDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            SimpleDateFormat userDateFormat = new SimpleDateFormat(dateFromat + " HH:mm:ss");
                            Date date = new Date();
                            try {
                                date = userDateFormat.parse(cameraTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Camera.sendSettings(cameraDateFormat.format(date), CAMERA_TIME);
                            ((MyApp)getApplication()).getSettingsMap().put(CAMERA_TIME, cameraTime);
                            return null;
                        }
                    }.executeOnExecutor(Camera.getExecutorCameraCommands());

                }
            };
            return new TimePickerDialog(this, onTimeSetListener, 0, 0, true);

        }

        return new TimePickerDialog(this, myCallBack, 0, 0, true);
    }

    private void initModeTime() {
        final TextView tvFromTime = (TextView) findViewById(R.id.tvFromTime);


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(TIME_MODE_START_TIME);
                return ((MyApp)getApplication()).getSettingsMap().get(TIME_MODE_START_TIME);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }
                if(option!= null){
                    tvFromTime.setText(option);
                }

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



        tvFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(FROM_TIME_DIALOG);
            }
        });



        final TextView tvToTime = (TextView) findViewById(R.id.tvToTime);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(TIME_MODE_FINISH_TIME);
                return ((MyApp)getApplication()).getSettingsMap().get(TIME_MODE_FINISH_TIME);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }

                if(option!=null){
                    tvToTime.setText(option);
                }

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());


        tvToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TO_TIME_DIALOG);
            }
        });

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(CAMERA_TIME);
                return ((MyApp)getApplication()).getSettingsMap().get(CAMERA_TIME);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }
                tvCameraTime.setText(option);
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());

    }

    private void initEditCarNumber() {
        setCurrentCarNumber();

        findViewById(R.id.sendCarNumberBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                View view = ActivityMainSettings.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(etCarNumber.getText().toString().length() != 10){
                    Toast.makeText(ActivityMainSettings.this, "Car Number field must be 10 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String value = etCarNumber.getText().toString().toUpperCase();

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.sendSettings(value, CAR_NUMBER);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        ((MyApp)getApplication()).getSettingsMap().put(CAR_NUMBER, value);
                        if (getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")) {
                            Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });

    }

    private void initWiFiPassword() {
        setCurrentPassword();

        findViewById(R.id.sendWiFiPasswordBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                View view = ActivityMainSettings.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(etWiFiPassword.getText().toString().length() != 10){
                    Toast.makeText(ActivityMainSettings.this, "WiFi password field must be 10 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String value = etWiFiPassword.getText().toString();

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return Camera.sendSettings(value, WIFI_PASSWORD);
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        ((MyApp)getApplication()).getSettingsMap().put(WIFI_PASSWORD, value);
                        if (getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")) {
                            Toast.makeText(ActivityMainSettings.this, aVoid, Toast.LENGTH_SHORT).show();
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });

    }

    private void setCurrentCarNumber() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(CAR_NUMBER);
                return ((MyApp)getApplication()).getSettingsMap().get(CAR_NUMBER);
            }

            @Override
            protected void onPostExecute(String s) {
                if(s!=null){
                    etCarNumber.setText(s.toUpperCase());
                }
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private void setCurrentPassword() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(WIFI_PASSWORD);
                return ((MyApp)getApplication()).getSettingsMap().get(WIFI_PASSWORD);
            }

            @Override
            protected void onPostExecute(String s) {
                etWiFiPassword.setText(s);
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }


    private void initToggle(final ToggleButton toggle, final String key) {

        if ((toggle == toggleConnectionLog)) {
            String value = settingsPref.getString(key, "off");
            toggle.setChecked(value != null && value.equalsIgnoreCase("on"));
        } else {
            setCurrentState(toggle, key);
        }
        checkLogik(key, toggle);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogik(key, toggle);
                final String value = ((ToggleButton) v).isChecked() ? "on" : "off";


                if ((toggle == toggleConnectionLog)) {
                    settingsPref.edit().putString(key, value).apply();
                    return;
                }


                request = "";
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        request = Camera.sendSettings(value, key);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        ((MyApp)getApplication()).getSettingsMap().put(key, value);


                        if(toggle.equals(toggleTimedMode)){
                            Toast.makeText(ActivityMainSettings.this, "Mode 2 will be activated between the specified times", Toast.LENGTH_LONG).show();
                        }

                        if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(ActivityMainSettings.this, request, Toast.LENGTH_SHORT).show();
                        } else {
                            final Toast toast = Toast.makeText(ActivityMainSettings.this, " Setting updated", Toast.LENGTH_SHORT);
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

                if(checkSettingsIsDefault()){
                    toggleResetSettingsToDefault.setEnabled(false);
                    toggleResetSettingsToDefault.setChecked(true);
                } else {
                    toggleResetSettingsToDefault.setEnabled(true);
                    toggleResetSettingsToDefault.setChecked(false);
                }
            }
        });

    }

    private void initSpinner(final String[] data, final Spinner spinner, final String key) {

//        if ((spinner == spinnerMotionTurnOff)) {
//            //
//        } else {
            setCurrentOption(spinner, data, key);
//        }



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                if(!spinerTouched){
                    return;
                }

//                settingsPref.edit().putString(key, data[position]).commit();

                if ((spinner == spinnerMotionTurnOff)) {
                    return;
                }

                request = "";
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        request = Camera.sendSettings(data[position], key);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        ((MyApp)getApplication()).getSettingsMap().put(key, data[position]);
                        if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            Toast.makeText(ActivityMainSettings.this, request, Toast.LENGTH_SHORT).show();
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_simple_spinner, data);
//        adapter.setDropDownViewResource(R.layout.item_spinner);
//
//        spinner.setAdapter(adapter);
//        spinner.setSelection(settingsPref.getInt(key, 0));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                settingsPref.edit().putInt(key, position).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });

    }

    private void checkLogik(String key, ToggleButton toggle) {
        if (key.equals(MainSettings.TOGGLE_MOTION_DETECTION)) {
            View MotionDetectionSensitivity = findViewById(R.id.MotionDetectionSensitivity);
//            View MotionTurnOff = findViewById(R.id.MotionTurnOff);
            if (toggle.isChecked()) {
                MotionDetectionSensitivity.setVisibility(View.VISIBLE);
//                MotionTurnOff.setVisibility(View.VISIBLE);
            } else {
                MotionDetectionSensitivity.setVisibility(View.GONE);
//                MotionTurnOff.setVisibility(View.GONE);
            }
        }

        if (key.equals(MainSettings.TOGGLE_G_SENSOR)){
            View GSensorSensivity = findViewById(R.id.GSensorSensivity);
//            View MotionTurnOff = findViewById(R.id.MotionTurnOff);
            if (toggle.isChecked()) {
                GSensorSensivity.setVisibility(View.VISIBLE);
//                MotionTurnOff.setVisibility(View.VISIBLE);
            } else {
                GSensorSensivity.setVisibility(View.GONE);
//                MotionTurnOff.setVisibility(View.GONE);
            }
        }

        if (key.equals(MainSettings.TOGGLE_CAR_PLATE_STAMP)) {
            View carNumber = findViewById(R.id.carNumber);
            if (toggle.isChecked()) {
                carNumber.setVisibility(View.VISIBLE);
            } else {
                carNumber.setVisibility(View.GONE);
            }
        }

        if(key.equals(MainSettings.TOGGLE_SPEED_STAMP)) {
            View SpeedUnits = findViewById(R.id.SpeedUnits);
            if (toggle.isChecked()) {
                SpeedUnits.setVisibility(View.VISIBLE);
            } else {
                SpeedUnits.setVisibility(View.GONE);
            }
        }

        if(key.equals(ModeSettings.TOGGLE_TIMED_MODE)){
            View FromTime = findViewById(R.id.FromTime);
            View ToTime = findViewById(R.id.ToTime);
            if (toggle.isChecked()) {
                FromTime.setVisibility(View.VISIBLE);
                ToTime.setVisibility(View.VISIBLE);
            } else {
                FromTime.setVisibility(View.GONE);
                ToTime.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActiveActivitiesTracker.activityStopped(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted();
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();

    }

    @Override
    protected void onResume() {
        paused = false;
        super.onResume();
//        final MyApp myApp = ((MyApp)getApplication());
//        myApp.connect(this);
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
//            }
//        }.execute();

    }

    private void setCurrentOption(final Spinner spinner, final String[] data, final String key) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
//                return Camera.getCurrentOption(key);
                return ((MyApp)getApplication()).getSettingsMap().get(key);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityMainSettings.this, R.layout.item_simple_spinner, data);
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
                return ((MyApp)getApplication()).getSettingsMap().get(key);
            }

            @Override
            protected void onPostExecute(String option) {
                if(paused){
                    return;
                }
                if(option!= null){
                    toggle.setChecked(option.equalsIgnoreCase("on"));
                } else {
                    toggle.setChecked(false);
                }

                checkLogik(key, toggle);
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());



    }

    private int getOptionPosition(String[] data, String option) {
        int result = 0;
        for(int i = 0; i < data.length; i++) {
            if (option.equalsIgnoreCase(data[i])){
                result = i;
                break;
            }
        }

        Log.d("myLogs", option + "  data index = " + result);
        return result;
    }


}
