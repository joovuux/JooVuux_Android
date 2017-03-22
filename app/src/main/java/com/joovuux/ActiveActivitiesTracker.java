package com.joovuux;

import android.content.Context;
import android.os.AsyncTask;

import com.joovuux.connection.Camera;

import java.util.concurrent.TimeUnit;

public class ActiveActivitiesTracker {
    private static int sActiveActivities = 0;

    public static void activityStarted()
    {
        sActiveActivities++;
    }

    public static void activityStopped(final Context context)
    {
        sActiveActivities--;

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        // weith for start any activity
                        TimeUnit.MILLISECONDS.sleep(1000);

                        if (sActiveActivities == 0) {
                            Camera.releaseToken();
//                            Camera.disconnectFromWifi(context);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(Camera.getExecutorCameraCommands());


    }
}