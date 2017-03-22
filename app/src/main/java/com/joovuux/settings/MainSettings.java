package com.joovuux.settings;


public class MainSettings {

    public static final String TOGGLE_SYNC_FATE_AND_TIME = "toggleSyncFateAndTime"; ///
    public static final String SPINNER_DATE_FORMAT = "data_format";
    public static final String TOGGLE_BEEP_NOISES = "toggleBeepNoises"; //
    public static final String TOGGLE_RECORDING_LED_INDICATOR = "toggleRecordingLEDindicator"; //
    public static final String SPINNER_DEFAULT_MODE_CAMERA_STATS = "default_mode";
    public static final String SPINNER_POWER_ON_AUTO_RECORD = "spinnerPowerOnAutoRecord"; //
    public static final String SPINNER_STANDBY_TIME = "standby_time";
    public static final String SPINNER_POWER_OFF_DISCONNECT = "power_off_disconnect";
    public static final String SPINNER_POWER_ON_DELAY = "power_on_delay";
    public static final String TOGGLE_MOTION_DETECTION = "motion_detection";
    public static final String SPINNER_MOTION_DETECTION_SENSIVITY = "motion_det_sens";
    public static final String SPINNER_MOTION_TURN_OFF = "spinnerMotionTurnOff"; //
    public static final String SPINNER_TV_OUT = "tv_type";
    public static final String TOGGLE_G_SENSOR = "g_sensor";
    public static final String SPINNER_G_SENSOR_SENSIVITY = "g_sensor_sensitivity";
    public static final String TOGGLE_CAR_PLATE_STAMP = "car_plate_stamp";
    public static final String TOGGLE_SPEED_STAMP = "speed_stamp";
    public static final String CONNECTION_LOG= "toggleConnectionLog"; //
    public static final String TOGGLE_PARKING_MODE = "parking_mode";

    public static final String CAMERA_TIME = "camera_time";

    public static final String SPINNER_SPEED_UNIT = "speed_unit";
    public static final String TIME_MODE_START_TIME = "time_mode_start_time";
    public static final String TIME_MODE_FINISH_TIME = "time_mode_finish_time";
    public static final String WIFI_PASSWORD = "wifi_password";
    public static final String CAR_NUMBER = "toggleCarNumber";


    public static final String[] getDataSpinnerGSensorSensitivity = {"High", "Medium", "Low"};
    public static final String[] dataSpinnerDateFormat = {"YYYYMMDD", "DDMMYYYY", "MMDDYYYY"};
    public static final String[] dataSpinnerDefaultModeCameraStarts = {"mode1", "mode2"};
    public static final String[] dataSpinnerPowerOnAutoRecord = {"Button only", "Auto Start"};
    public static final String[] dataSpinnerStandbyTime = {"off", "15_sec", "30_sec", "1_min" , "3_min", "5_min"};
    public static final String[] dataSpinnerPowerOffDisconnect = {"immediate", "3_sec", "10_sec"};
    public static final String[] dataSpinnerPowerOnDelay = {"off", "5_sec", "10_sec"};
    public static final String[] dataSpinnerMotionDetectionSensitivity = {"high", "medium", "low"};
    public static final String[] dataSpinnerMotionTurnOff = {"5s", "10s", "20s", "30s", "1 minute"};
    public static final String[] dataSpinnerTVOut = {"NTSC", "PAL"};
    public static final String[] dataSpinnerUnitSpeed = {"KPH", "MPH"};



}
