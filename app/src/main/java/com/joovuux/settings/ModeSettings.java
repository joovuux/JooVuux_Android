package com.joovuux.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class ModeSettings {

    public static String MODE1 = "Mode 1";
    public static String MODE2 = "Mode 2";
    public static String MODE3 = "Mode 3";


    public static final String VIDEO_RESOLUTION = "video_resolution";
    public static final String VIDEO_TIME_STAMP = "video_timestamp";
    public static final String AUDIO = "audio";
    public static final String LOOP_RECORDING = "loop_record";
    public static final String VIDEO_BITRATES = "video_quality";
    public static final String VIDEO_CLIP_LENGHT = "video_length";
    public static final String WDR = "wdr";
    public static final String FIELD_OF_VIEW = "fleld_view";
    public static final String TIME_LAPSE_VIDEO = "timelapse_video";
    public static final String TOGGLE_TIMED_MODE = "time_mode";
    public static final String DISPLAY_SPEED = "speed_stamp";
    public static final String SPINNER_BURST_PHOTO_MODE = "spinnerBurstPhotoMode";//!!
    public static final String ARTIFICIAL_LIGHT_FREQUENCY = "flicker";
    public static final String LDWS = "ldws";
    public static final String FBWS = "fcws";
    public static final String DATE_TIMESTAMP = "date_time_stamp";
    public static final String PHOTO_RESOLUTION = "photo_resolution";
    public static final String ROTATE_PHOTOS_180_DEGREES = "rotate_photo_180_degrees";
    public static final String TIME_LAPSE_PHOTO = "time_lapse_photo";
    public static final String AUTO_ROTATE = "rotate_video";
    public static final String LOW_BATTERY_WARNING = "low_battery_warning";
    public static final String WIFI_AUTO_START = "wifi_auto_start";

    public static final String[] KEYS = {VIDEO_RESOLUTION, VIDEO_TIME_STAMP, AUDIO, LOOP_RECORDING,
            VIDEO_BITRATES, VIDEO_CLIP_LENGHT, WDR, FIELD_OF_VIEW, TIME_LAPSE_VIDEO, TOGGLE_TIMED_MODE, DISPLAY_SPEED, SPINNER_BURST_PHOTO_MODE,
            ARTIFICIAL_LIGHT_FREQUENCY, LDWS, FBWS, DATE_TIMESTAMP, PHOTO_RESOLUTION, ROTATE_PHOTOS_180_DEGREES, TIME_LAPSE_PHOTO, AUTO_ROTATE, LOW_BATTERY_WARNING};


    public static final String[] dataSpinnerBurstPhotoMode = {"off", "5", "10"};
//    public static final String[] dataSpinnerVideoResolutions = {"2560x1080 30P 21:9", "2304x1296 30P 16:9", "1920x1080 60P 16:9", "1920x1080 45P 16:9", "HDR 1920x1080 30P 16:9", "1920x1080 30P 16:9", "1280x720 60P 16:9", "1280x720 30P 16:9"};
    public static final String[] dataSpinnerVideoResolutions = {"2560x1080 30P 21:9","2304x1296 30P 16:9","1920x1080 60P 16:9", "1920x1080 45P 16:9", "1920x1080 30P 16:9","HDR 1920x1080 30P 16:9","1280x720 60P 16:9","1280x720 30P 16:9"};
    public static final String[] dataSpinnerVideoBitRates = {"S.Fine", "Fine", "Normal"};
    public static final String[] dataSpinnerVideoClipLenght = {"1_min", "2_min", "3_min", "5_min", "10_min", "continuous"};
    public static final String[] dataSpinnerWDR = {"off", "on"};
    public static final String[] dataSpinnerFieldOfView = {"155", "120", "90", "60"};
    public static final String[] dataSpinnerTimeLapseVideo = {"off", "1_sec", "5_sec", "10_sec", "30_sec"};
    public static final String[] dataSpinnerArtificialLightFrequency = {"AUTO", "50HZ", "60HZ"};
    public static final String[] dataSpinnerPhotoResolution = {"13M", "8M", "5M"};
    private static SharedPreferences[] prefs;
    public static final String[] dataSpinnerAutoRotate = {"normal", "180 degree", "auto rotation"};


    public static final void defaultSettings(Context context) {
        initSpinner(dataSpinnerVideoResolutions,  VIDEO_RESOLUTION, context);

        initToggle( ModeSettings.VIDEO_TIME_STAMP, context);

        initToggle( ModeSettings.AUDIO, context);

        initToggle( ModeSettings.LOOP_RECORDING, context);

        initSpinner(ModeSettings.dataSpinnerVideoBitRates,  ModeSettings.VIDEO_BITRATES, context);
        initSpinner(ModeSettings.dataSpinnerAutoRotate,  ModeSettings.AUTO_ROTATE, context);

        initSpinner(ModeSettings.dataSpinnerVideoClipLenght,  ModeSettings.VIDEO_CLIP_LENGHT, context);

        initSpinner(ModeSettings.dataSpinnerWDR,  ModeSettings.WDR, context);

        initSpinner(ModeSettings.dataSpinnerFieldOfView,  ModeSettings.FIELD_OF_VIEW, context);

        initSpinner(ModeSettings.dataSpinnerTimeLapseVideo,  ModeSettings.TIME_LAPSE_VIDEO, context);

        initToggle( ModeSettings.TOGGLE_TIMED_MODE, context);

        initToggle( ModeSettings.DISPLAY_SPEED, context);

        initSpinner(ModeSettings.dataSpinnerBurstPhotoMode,  ModeSettings.SPINNER_BURST_PHOTO_MODE, context);

        initSpinner(ModeSettings.dataSpinnerArtificialLightFrequency,  ModeSettings.ARTIFICIAL_LIGHT_FREQUENCY, context);

        initToggle( ModeSettings.LDWS, context);

        initToggle( ModeSettings.FBWS, context);

        initToggle( ModeSettings.LOW_BATTERY_WARNING, context);

        initToggle( ModeSettings.WIFI_AUTO_START, context);

    }

    private static void initToggle(final String key, Context context) {
       prefs = new SharedPreferences[]{context.getSharedPreferences(MODE1, Context.MODE_PRIVATE),
               context.getSharedPreferences(MODE2, Context.MODE_PRIVATE),
               context.getSharedPreferences(MODE3, Context.MODE_PRIVATE)};

        for(SharedPreferences pref: prefs) {

            if(key.equalsIgnoreCase(LOOP_RECORDING)){
                pref.edit().putString(key, "on").apply();
            } else {
                pref.edit().putString(key, "off").apply();
            }

        }
    }

    private static void initSpinner(final String[] data, final String key, Context context) {
        prefs = new SharedPreferences[]{context.getSharedPreferences(MODE1, Context.MODE_PRIVATE),
                context.getSharedPreferences(MODE2, Context.MODE_PRIVATE),
                context.getSharedPreferences(MODE3, Context.MODE_PRIVATE)};

        for(SharedPreferences pref: prefs) {
            if(ModeSettings.VIDEO_CLIP_LENGHT.equalsIgnoreCase(key)){
                pref.edit().putString(key, data[2]).apply();
            } else {
                pref.edit().putString(key, data[0]).apply();
            }

        }
    }

}
