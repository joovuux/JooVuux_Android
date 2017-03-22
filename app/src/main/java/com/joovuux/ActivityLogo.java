package com.joovuux;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ua.net.lsoft.joovuux.R;

import com.jaredrummler.android.device.DeviceName;
import com.joovuux.connection.Camera;
import com.utility.MemorySize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ActivityLogo extends Activity{


    private String ReadCPUinfo()
    {
        ProcessBuilder cmd;
        String result="";

        try{
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApp)getApplication()).connect(this);
    }

    public String getRAMSize(){
        ActivityManager actManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return "Total RAM: " + MemorySize.formatSize(totalMemory);
    }

    public String getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return "Screen size: " + size.x + "x" + size.y;
    }

    public String getAndroidVesrion() {
        StringBuilder builder = new StringBuilder();
        builder.append("android : ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(" : ").append(fieldName).append(" : ");
                builder.append("sdk=").append(fieldValue);
            }
        }
        return "OS: " + builder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView tvFWVersion = (TextView) findViewById(R.id.tvFWVersion);
        tvFWVersion.setText(((MyApp) getApplication()).getVersionFV());

        findViewById(R.id.btnResetSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return  Camera.resetSetting();
                    }

                    @Override
                    protected void onPostExecute(String s) {

                        Toast.makeText(ActivityLogo.this, s, Toast.LENGTH_LONG).show();

                        super.onPostExecute(s);
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        });


        Button btnSendReport = (Button) findViewById(R.id.btnSendReport);
        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "reports@joovuu.com", null));
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL   , new String[]{"reports@joovuu.com"});
                i.putExtra(Intent.EXTRA_SUBJECT , "report");
                i.putExtra(Intent.EXTRA_TEXT    , getAndroidVesrion() + "\n" +
                                                  "Device name " + DeviceName.getDeviceName() + "\n" +
                                                  getScreenSize() + "\n" +
                                                  getRAMSize() + "\n" +
                                                  "Free memory: " + MemorySize.getTotalExternalMemorySize() + "\n" +
                                                  "Cpu Info: " + ReadCPUinfo() + "\n" +
                                                  "Settings: " + ((MyApp)getApplication()).getSettingsMap().toString()

                );

                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ActivityLogo.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button btFormatSdCad = (Button) findViewById(R.id.btnFormatSdCard);
        btFormatSdCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        return Camera.formatSDCard(ActivityLogo.this);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if(aBoolean){
                            deleteImages();
                            Toast.makeText(ActivityLogo.this, "Formatting successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ActivityLogo.this, "Formatting failed", Toast.LENGTH_SHORT).show();
                        }

                        super.onPostExecute(aBoolean);
                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());

            }
        });

        findViewById(R.id.btnWhiteBalance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityWhiteBalance.class);
                startActivity(intent);
            }
        });

//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                return Camera.getFWVersion();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                tvFWVersion.setText(s);
//                super.onPostExecute(s);
//
//            }
//        }.executeOnExecutor(Camera.getExecutorCameraCommands());

    }

    public void deleteImages() {
        File directory = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery");

        File[] fList = directory.listFiles();

        if(fList == null){
            return;
        }
        Log.e("FILES TO DELTE", Arrays.toString(fList));

        for (File file : fList) {
            if ((file != null) && (file.isFile())) {
                if(file.getAbsoluteFile().toString().contains("jpg")){
                    file.delete();
                }
            }
        }
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

}
