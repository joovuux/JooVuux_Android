package com.joovuux;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
import com.joovuux.settings.MainSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;


//@ReportsCrashes(formUri = "http://lsoft.net.ua/acra/acra.php", socketTimeout = 30000)
public class MyApp extends Application {


    private String versionFV = "";
    private ProgressDialog progDailog;
    private int chekConnectCount;
    private HashMap<String, String> settingsMap;

    public boolean isConnected() {
        return connected;
    }

    private boolean connected;

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    private boolean connecting;

    private HashMap<String, String> settings = new HashMap<>();

    public String getVersionFV() {
        return versionFV;
    }

    public void setVersionFV(String versionFV) {
        this.versionFV = versionFV;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ACRA.init(this);
    }




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
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TOKEN", error.getMessage());
                    }
                }){


            @Override
            protected Map<String,String> getParams(){

                String did = Settings.Secure.getString(MyApp.this.getContentResolver(),
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

    public void connect(final Context context) {
        setConnecting(true);
        progDailog = ProgressDialog.show(context, null, "Connecting...", true);
        progDailog.setCancelable(true);


        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                if(!Camera.checkWiFiConnectedName(MyApp.this)){
                    InstanceID instanceID = InstanceID.getInstance(MyApp.this);
                    try {
                        String token = instanceID.getToken("376066628724", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        registerToken(token);
                        TimeUnit.SECONDS.sleep(3);
                        Log.d("TOKEN", token);
                    } catch (IOException e) {
                        progDailog.dismiss();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        progDailog.dismiss();
                        e.printStackTrace();
                    }
                }

                if(Camera.checkWiFiConnectedName(MyApp.this) && Camera.getToken(context)){
                    return Camera.pingCamera();
                }

                chekConnectCount = 0;
                Camera.connectToWifi(context);
                Camera.setWifiConnectingNow(true);

                return checkTokenGetting();
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                progDailog.dismiss();

                if (aVoid) {
                    connected = true;
                    Camera.setWifiConnectingNow(false);

                    Log.e("Settings", getSettingsMap().toString());

                    if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
//                        Toast.makeText(MyApp.this, Camera.getLogmessage(), Toast.LENGTH_LONG).show();
//                        showLogDialog(context);
                    }

                } else {
                    connected = false;
                    showNoConnectionDialog(context);
                }

                setConnecting(false);
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    public void showLogDialog(Context context) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.logs_dialog, null);

        TextView textview=(TextView)view.findViewById(R.id.textmsg);
        textview.setText(Camera.getLogmessage());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Log");
        //alertDialog.setMessage("Here is a really long message.");
        alertDialog.setView(view);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        try{
            alert.show();
        }
        catch (Exception ex){

        }

    }

    private boolean checkTokenGetting() {
        if(Camera.checkStillConnecting(this) || !Camera.getToken(this)) {
            Camera.token = 0;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int i = 454 / 98;
            chekConnectCount++;

            if(chekConnectCount < 15){

                return checkTokenGetting();
            }

            return false;
        } else {
            return true;
        }

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
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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


    public void setSettingsMap(HashMap<String, String> settingsMap) {
        this.settingsMap = settingsMap;
    }

    public HashMap<String, String> getSettingsMap() {
        return settingsMap;
    }
}
