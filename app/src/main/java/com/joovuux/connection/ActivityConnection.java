package com.joovuux.connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.joovuux.ActiveActivitiesTracker;

import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import ua.net.lsoft.joovuux.R;

public class ActivityConnection extends Activity {

    private ProgressDialog progDailog;
    private int chekConnectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }

        });

    }

    private void connect() {
        progDailog = ProgressDialog.show(ActivityConnection.this, null, "Connecting...", true);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
//                        if(!Camera.checkConnection(ActivityConnection.this)){
//                            return false;
//                        }
                chekConnectCount = 0;
                Camera.connectToWifi(ActivityConnection.this);
                Camera.setWifiConnectingNow(true);


                return checkTokenGetting();
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                progDailog.dismiss();

                if (aVoid) {
                    ActivityConnection.this.finish();
                    Camera.setWifiConnectingNow(false);
                } else {
                    showNoConnectionDialog(ActivityConnection.this);
                }
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private boolean checkTokenGetting() {
        if(Camera.checkStillConnecting(ActivityConnection.this)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chekConnectCount++;

            if(chekConnectCount < 6){
                checkTokenGetting();
            }

        }
        return Camera.getToken(ActivityConnection.this);
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

}
