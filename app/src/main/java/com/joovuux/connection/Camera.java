package com.joovuux.connection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
//import org.jcodec.common.model.Picture;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joovuux.MyApp;
//
//import org.jcodec.codecs.h264.H264Decoder;
//import org.jcodec.common.JCodecUtil;
//import org.jcodec.common.model.ColorSpace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.joovuux.gallery.ModelImage;

import ua.net.lsoft.joovuux.R;


public final class Camera {
    public static final String TAG = "Camera";
    public static final String CAMERA_IP = "192.168.42.1";
    public static final int CAMERA_SETTINGS_PORT = 7878;
    public static final int CAMERA_DOWNLOAD_PORT = 8787;


    public static final String TOKEN = "token";
    public static final String MSG_ID = "msg_id";
    public static final String PARAM = "param";
    public static final String TYPE = "type";
    public static final String DCIM_100_MEDIA = "/tmp/fuse_d/DCIM/100MEDIA/";
    public static final String CAMERA_WIFI_NAME = "JooVuuX";

    private static boolean canDownload;



    private static StringBuilder log = new StringBuilder();


    private static boolean downloaded;
    private static WifiManager wifiMgr;
    private static MyApp appContext;
    private static int getTokenCount;
    private static WifiInfo wifiInfo;



    private static StringBuilder logmessage = new StringBuilder();

    public static String getLogmessage() {
        String log = logmessage.toString();
        logmessage = new StringBuilder();
        return log;
    }
    private MediaCodec mDecodeMediaCodec;

    public static void setWifiConnectingNow(boolean wifiConnectingNow) {
        Camera.wifiConnectingNow = wifiConnectingNow;
        getTokenCount = 0;
    }

    private static boolean wifiConnectingNow;

    public static boolean isDownloaded() {
        return downloaded;
    }


    public static ExecutorService getExecutorCameraStatus() {
        return executorCameraStatus;
    }

    private static ExecutorService executorCameraStatus =  Executors.newFixedThreadPool(1);

    public static ExecutorService getExecutorCameraCommands() {
        return executorCameraStatus;
    }

    private static ExecutorService executorCameraCommands =  Executors.newFixedThreadPool(5);

    public static StringBuilder getLog() {
        return log;
    }

    public static void setLog(StringBuilder log) {
        Camera.log = log;
    }

    public static int token = 0;


    private static Socket settingsSocket;


    private static String networkSSID = CAMERA_WIFI_NAME;
    private static String networkPass = "1234567890";

    public static void connectToWifi(Context context){
        Log.e("CAMERA", "connectToWifi");
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = String.format("\"%s\"", networkSSID);
        wc.preSharedKey = String.format("\"%s\"", networkPass);


            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> networks = wm.getConfiguredNetworks();
            if(networks == null){
                return;
            }
            for (WifiConfiguration wifiConfig : networks) {
                if (wifiConfig.SSID.replace("\"", "").equals(wc.SSID.replace("\"", ""))){
                    wifiConfig.priority = 200;
                    wm.enableNetwork(wifiConfig.networkId, true);
                    wm.addNetwork(wifiConfig);
                } else {
                    wm.disableNetwork(wifiConfig.networkId);
//                    wm.removeNetwork(wifiConfig.networkId);
                }
            }
            if (!checkConnection(context)){
                wm.reconnect();
                Log.e("Connecting", "reconnect");
            }


//        } else {
//
//            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//            int netId = wifiManager.addNetwork(wc);
//            wifiManager.disconnect();
//            wifiManager.enableNetwork(netId, true);
//            wifiManager.reconnect();
//
//        }

//        while (true){
//            Log.e("Connecting", "PING");
//            if(!pingCamera()){
//             connectToWifi(context);
//            }else{
//                return;
//            }
//
//        }
    }

    public static void disconnectFromWifi(Context context){
        Log.e("CAMERA", "disconnectFromWifi");
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = String.format("\"%s\"", networkSSID);
        wc.preSharedKey = String.format("\"%s\"", networkPass);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> networks = wm.getConfiguredNetworks();
            if(networks == null){
                return;
            }
            for (WifiConfiguration wifiConfig : networks) {
                if (wifiConfig.SSID.replace("\"", "").equals(wc.SSID.replace("\"", ""))) {
                    wifiConfig.priority = 0;
                    wm.disableNetwork(wifiConfig.networkId);
                } else {
                    wifiConfig.priority = 100;
                    wm.enableNetwork(wifiConfig.networkId,true);
//                    wm.removeNetwork(wifiConfig.networkId);
                }
            }
            wm.reconnect();
        } else {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            int netId = wifiManager.addNetwork(wc);
            wifiManager.disconnect();
            wifiManager.disableNetwork(netId);
            wifiManager.reconnect();

        }

    }



    public static String getCurrentOption(String type){
        Log.e("CAMERA", "getCurrentOption");
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(TOKEN, token);
            jsonObject.put(MSG_ID, 1);
            jsonObject.put(TYPE, type);
            JSONObject j = makeServerRequest(jsonObject);
            return j.getString(PARAM) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCameraStatus(){
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(TOKEN, token);
            jsonObject.put(MSG_ID, 1);
            jsonObject.put(TYPE, "app_status");
            JSONObject j = makeServerRequest(jsonObject);
//            Log.d(TAG, j.getString(PARAM));
            return j.getString(PARAM);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "";
    }

    public static String setOutTypeRTSP() {
        return sendSettings("rtsp", "stream_out_type");
    }

    public static String  saveLowResolutionClip() {
        return sendSettings("on", "save_low_resolution_clip");
    }

    public static boolean checkConnection(Context context){
        Log.e("CAMERA", "checkConnection");
        if(wifiMgr == null){
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

            wifiInfo = wifiMgr.getConnectionInfo();


        String name = wifiInfo.getSSID();
        Log.i(TAG, "Camera wifi " + name);
        return name.contains(CAMERA_WIFI_NAME) || name.contains("0x") || name.contains("");

    }

    public static boolean checkWiFiConnectedName(Context context){
        Log.e("CAMERA", "checkWiFiConnectedName");
        if(wifiMgr == null){
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        wifiInfo = wifiMgr.getConnectionInfo();


        String name = wifiInfo.getSSID();
        Log.i(TAG, "Camera wifi " + name);
        return name.contains(CAMERA_WIFI_NAME);

    }

    public static boolean checkStillConnecting(Context context){
        Log.e("CAMERA", "checkStillConnecting");
        if(wifiMgr == null){
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
//        if(wifiInfo == null){
            wifiInfo = wifiMgr.getConnectionInfo();
//        }
        String name = wifiInfo.getSSID();
        Log.i(TAG, "Camera wifi " + name);
        return !name.contains(CAMERA_WIFI_NAME);

    }

    public static boolean networkTypeIsWiFi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return false; //not connected
        if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            return false;
        }
        return false;
    }


    public static boolean pingCamera(){
//        Log.e("CAMERA", "pingCamera");
//        String str;
//        try {
//            Process process = Runtime.getRuntime().exec( "/system/bin/ping -c 1 " + CAMERA_IP);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            int i;
//            char[] buffer = new char[4096];
//            StringBuffer output = new StringBuffer();
//            while ((i = reader.read(buffer)) > 0)
//                output.append(buffer, 0, i);
//            reader.close();
//
//            str = output.toString();
//            if(str.contains("100% packet loss")){
//                return false;
//            } else {
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return true;
//        }
//


        return !getCameraStatus().equalsIgnoreCase("");

    }

    public static boolean getToken(Context context) {
        Log.e("CAMERA", "getToken");

        if(!networkTypeIsWiFi(context)){
            return false;
        }
        if (token != 0) {
            return true;
        }



        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TOKEN, 0);
            jsonObject.put(MSG_ID, 257);
            Log.i("Camera", "Request " + jsonObject.toString());

            JSONObject j = makeServerRequest(jsonObject);
            if (j != null) {
                token = j.getInt(PARAM);
                Log.i("Camera", "Token " + token);

            }
            appContext = ((MyApp)context.getApplicationContext());
            appContext.setVersionFV(getFWVersion());
            appContext.setSettingsMap(Camera.getCurrentSettings());
            Log.d("SETTINGS MAP", appContext.getSettingsMap().toString());
            Camera.saveLowResolutionClip();
            return token != 0;
        } catch (Exception e) {
//            Log.i(TAG, "Exception getToken " + e.getMessage());
//            if(checkConnection(context)){
//                try {
//                    if (wifiConnectingNow){
//                        TimeUnit.SECONDS.sleep(1);
//                        getTokenCount++;
//                        if(getTokenCount < 10){
//                            getToken(context);
//                        }
//                    }
//                } catch (InterruptedException ignored) {}
//            }

            return token != 0;
        }

    }

    public static void releaseToken(){
        Log.e("CAMERA", "releaseToken");

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            jsonObject.put("msg_id", 258);
            Log.i("Camera", "Request " + jsonObject.toString());
            JSONObject j = makeServerRequest(jsonObject);
            if (j != null) {
                Log.i("Camera", "" + token + "Response " + j.toString());
            }

            if (settingsSocket != null) {
                settingsSocket.close();
                settingsSocket = null;
            }

            token = 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String resetSetting() {
        Log.e("CAMERA", "sendSettings");
        try {
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put(TOKEN, token);
            jsonObject.put(MSG_ID, 2);
            jsonObject.put(TYPE, "Reset all settings");
            jsonObject.put(PARAM, "Yes");

            Log.e("SEND SETTING", jsonObject.toString());

            JSONObject j = makeServerRequest(jsonObject);
            String s = "";
            if (j!= null){
                s = j.toString();
            }
            return "request " + jsonObject.toString() + " response " + s ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendSettings(String value, String type) {
        Log.e("CAMERA", "sendSettings");
        try {
            final JSONObject jsonObject = new JSONObject();
            if (type.contains("toggleSyncFateAndTime") && value.contains("on")){
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date date = new Date();
                value = fmt.format(date);
            }
            jsonObject.put(TOKEN, token);
            jsonObject.put(MSG_ID, 2);
            jsonObject.put(TYPE, type);
            jsonObject.put(PARAM, value);

            Log.e("SEND SETTING", jsonObject.toString());

            JSONObject j = makeServerRequest(jsonObject);
            String s = "";
            if (j!= null){
                s = j.toString();
            }
            return "request " + jsonObject.toString() + " response " + s ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HashMap<String, String> getCurrentSettings() {
        Log.e("CAMERA", "sendSettings");

        HashMap<String, String> result = new HashMap<>();
        try {
            final JSONObject SETUP = new JSONObject();

            SETUP.put(TOKEN, token);
            SETUP.put(MSG_ID, 1794);

            JSONObject jsonSETUP = makeServerRequest(SETUP);

            result.putAll(toMap(jsonSETUP.getJSONArray("param")));

            final JSONObject MODE1 = new JSONObject();

            MODE1.put(TOKEN, token);
            MODE1.put(MSG_ID, 1795);

            JSONObject jsonMODE1 = makeServerRequest(MODE1);

            result.putAll(toMap(jsonMODE1.getJSONArray("param")));

            final JSONObject MODE2 = new JSONObject();

            MODE2.put(TOKEN, token);
            MODE2.put(MSG_ID, 1796);

            JSONObject jsonMODE2 = makeServerRequest(MODE2);

            result.putAll(toMap(jsonMODE2.getJSONArray("param")));

            final JSONObject MODE3 = new JSONObject();

            MODE3.put(TOKEN, token);
            MODE3.put(MSG_ID, 1797);

            JSONObject jsonMODE3 = makeServerRequest(MODE3);

            result.putAll(toMap(jsonMODE3.getJSONArray("param")));

            int i = 12312 / 22223;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static HashMap<String, String> toMap(JSONArray array) throws JSONException {
        HashMap<String, String> map = new HashMap();
        for(JSONObject object : toList(array)){
            Iterator keys = object.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                map.put(key, (String) object.get(key));
            }
        }
        return map;
    }



    public static void sendBroadcast(String messageStr, final Activity context) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] recvBuf = new byte[15000];
            final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

            byte[] sendData = messageStr.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(context), 7877);
            socket.send(sendPacket);
            System.out.println("Broadcast packet sent to: " + getBroadcastAddress(context).getHostAddress());
            socket.receive(packet);
            Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
            context.findViewById(R.id.linearLaoutMain).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, packet.getAddress().getHostAddress(), Toast.LENGTH_LONG);
                }
            });
            socket.receive(packet);
            Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
            context.findViewById(R.id.linearLaoutMain).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, packet.getAddress().getHostAddress(), Toast.LENGTH_LONG);
                }
            });
            socket.receive(packet);
            Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
            context.findViewById(R.id.linearLaoutMain).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, packet.getAddress().getHostAddress(), Toast.LENGTH_LONG);
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }



    private static InetAddress getBroadcastAddress(Context context) throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    private static void reciveDataFromBroadcast(final DatagramSocket socket) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
//                    DatagramSocket socket = new DatagramSocket(7877, InetAddress.getByName("0.0.0.0"));

                    while (true) {
                        Log.i(TAG, "Ready to receive broadcast packets!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        socket.receive(packet);

                        //Packet received
                        Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                        String data = new String(packet.getData()).trim();
                        Log.i(TAG, "Packet received; data: " + data);

                    }
                } catch (IOException ex) {
                    Log.i(TAG, "Oops" + ex.getMessage());
                }
                return null;
            }
        }.execute();
    }



    public static List<JSONObject> toList(JSONArray array) throws JSONException {
        List<JSONObject> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add((JSONObject) array.get(i));
        }
        return list;
    }


    private static String createImage(byte[] data, String name) {

        try {

            MediaCodec asd = MediaCodec.createDecoderByType("video/avc");
            final YuvImage image = new YuvImage(data, ImageFormat.NV21,
                    380, 264, null);
            File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/" + name.replace("MP4", "JPEG"));
            final FileOutputStream filecon = new FileOutputStream(file);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    image.compressToJpeg(
                            new Rect(0, 0, image.getWidth(), image.getHeight()), 100,
                            filecon);
                    return null;
                }
            }.execute();

            return file.getAbsolutePath();
        } catch (Exception e) {
            Toast toast = Toast
                    .makeText(appContext, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
        return "";
    }


    public static final HashMap<String, String> filesMap = new HashMap<>();

    public static ArrayList<ModelImage> getListOfFiles(ArrayList<ModelImage> imagesList){
        ArrayList<ModelImage> models = new ArrayList<ModelImage>();
        try {

            final JSONObject send1284 = new JSONObject();
            send1284.put(TOKEN, token);
            send1284.put(MSG_ID, 1284);

            final JSONObject send1283DCIM = new JSONObject();
            send1283DCIM.put(TOKEN, token);
            send1283DCIM.put(MSG_ID, 1283);
            send1283DCIM.put(PARAM, "/tmp/fuse_d/DCIM/");

            final JSONObject send1282 = new JSONObject();
            send1282.put(TOKEN, token);
            send1282.put(MSG_ID, 1282);
            send1282.put(PARAM, "-D -S");


            final JSONObject send1283MEDIA = new JSONObject();
            send1283MEDIA.put(TOKEN, token);
            send1283MEDIA.put(MSG_ID, 1283);
            send1283MEDIA.put(PARAM, DCIM_100_MEDIA);


            final LinkedList<String> fileNames = new LinkedList<>();

            makeServerRequest(send1284);
            makeServerRequest(send1283DCIM);
            makeServerRequest(send1282);
            makeServerRequest(send1284);
            makeServerRequest(send1283MEDIA);

            final File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/");
            if(!dir.exists()){
                dir.mkdirs();
            }

            filesMap.clear();
            JSONObject jsonObject = makeServerRequest(send1282);
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("listing");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject file = (JSONObject) jsonArray.get(i);
                    String key = file.keys().next();
                    filesMap.put(key, file.getString(key));
                    fileNames.add(key);
                    if (key.contains("MP4") || key.contains("MOV")){
                        String metadata = ((JSONObject) jsonArray.get(i)).getString(key);

                        String size = metadata.substring(0, metadata.indexOf(" "));
                        String stringDate = metadata.substring(metadata.indexOf("|") + 1, metadata.length());
                        long date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(stringDate).getTime();
                        Log.d("8797", " DATE  = " + stringDate);
                        ModelImage image = new ModelImage(new File(ModelImage.VIDEO_URL + key), null, ""); //downloadImageForVide(key.replace("_thm", "")));
                        image.setIsCamera(true);
                        image.setSize(Long.parseLong(size));
                        image.setDate(date);

                        boolean contains = false;

//                        for(ModelImage modelImage : imagesList){
//                            if(modelImage.getFile().getAbsolutePath().contains(image.getFile().getName())){
//                                contains = true;
//                            }
                            final File m_file = new File(dir.getAbsolutePath() + "/" + image.getFile().getName());
                            if (!m_file.exists()) {
                                models.add(image);
                            }
//                        }
//                        if(!contains) {
//                            models.add(image);
//                        }

//                        downloadImageForVide(key.replace("_thm", ""));
                    }
                }

                Log.d("LIST OF FILES", fileNames.toString());
                Log.e("FILES MAP", filesMap.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


//          DWONLOAD FILES
            for (int i = 0; i < fileNames.size(); i++) {
                downloadFileFromIndex(i, fileNames);
                if(!canDownload) {
                    downloaded = false;
                    break;

                }
            }

            downloaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }

    public static void deleteFile(String cameraPath) {
        cameraPath = "/tmp/fuse_d/" + cameraPath;
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(TOKEN, token);
            jsonObject.put(MSG_ID, 1281);
            jsonObject.put(PARAM, cameraPath);
            Log.d("CAMERA PATH", "--------------------------------" + jsonObject.toString());
            makeServerRequest(jsonObject);
        } catch (Exception ignored) {}
    }

    public static void downloadFile(String fileName, final ProgressBar progressBar, final TextView tvProgress, final Dialog downloadProgress) {
        try {

            final File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/");
            if(!dir.exists()){
                dir.mkdirs();
            }
            final File file = new File(dir.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
                return;
            }


            final JSONObject startDownload = new JSONObject();
            startDownload.put("fetch_size", 0);
            startDownload.put(MSG_ID, 1285);
            startDownload.put("offset", 0);
            startDownload.put(PARAM, fileName);
            startDownload.put(TOKEN, token);

            send260();

            JSONObject json = makeServerRequest(startDownload);
            if(!(json.getInt("rval") == 0)){
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadProgress.dismiss();
                    }
                });
                return;
            }

            OutputStream outPutStream = new FileOutputStream(file);
            Socket downloadSocket = new Socket(CAMERA_IP, CAMERA_DOWNLOAD_PORT);

            byte[] bytes = new byte[30000];
            Log.d("download:", "start: " + fileName);
            long size = json.getLong("size");

            int percentsOfDownload = 0;

            while (true) {
                if(!canDownload) {
                    downloaded = false;
                    final JSONObject cancelDwonload = new JSONObject();
                    cancelDwonload.put("token", token);
                    cancelDwonload.put("msg_id", 1287);
                    cancelDwonload.put("param", fileName);
                    makeServerRequest(cancelDwonload);
                    file.delete();
                    break;
                }

                outPutStream.write(bytes, 0, downloadSocket.getInputStream().read(bytes));

                final int currentPercent = (int) getPercentOfDownload(size, file.length());
                if(currentPercent > percentsOfDownload){
                    percentsOfDownload = currentPercent;
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentPercent);
                            tvProgress.setText(currentPercent + "");
                        }
                    });
                }


                if (file.length() == size) {
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.dismiss();
                        }
                    });
                    break;
                }


            }
            outPutStream.close();



            byte[] bytes1 = new byte[1];
            //Waiting something
            while ((settingsSocket.getInputStream().read(bytes1)) >= 0) {
                if (settingsSocket.getInputStream().available() == 0) {
                    break;
                }
            }

            Log.d("download:", "finish: " + fileName);
            downloadSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    downloadProgress.dismiss();
                }
            });
        }
    }

    private static float getPercentOfDownload(long fileSize, long current) {
        return current*100/(float)fileSize;
    }

    private static void downloadFileFromIndex(int i, LinkedList<String> fileNames) {
        try {

            String fileName = fileNames.get(i);
            if(fileName.contains("MOV") || fileName.contains("MP4")){
                return;
            }

            final File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/");
            if(!dir.exists()){
                dir.mkdirs();
            }
            final File file = new File(dir.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
                return;
            }


            final JSONObject startDownload = new JSONObject();
            startDownload.put("fetch_size", 0);
            startDownload.put(MSG_ID, 1285);
            startDownload.put("offset", 0);
            startDownload.put(PARAM, fileName);
            startDownload.put(TOKEN, token);


            send260();

            JSONObject json = makeServerRequest(startDownload);
            if(!(json.getInt("rval") == 0)){
                return;
            }

            OutputStream outPutStream = new FileOutputStream(file);
            Socket downloadSocket = new Socket(CAMERA_IP, CAMERA_DOWNLOAD_PORT);

            byte[] bytes = new byte[10240];
            Log.d("download:", "start: " + fileName);
            long size = json.getLong("size");
            while (true) {
                if(!canDownload) {
                    downloaded = false;
                    final JSONObject cancelDwonload = new JSONObject();
                    cancelDwonload.put("token", token);
                    cancelDwonload.put("msg_id", 1287);
                    cancelDwonload.put("param", fileName);
                    makeServerRequest(cancelDwonload);
                    file.delete();
                    break;
                }
                outPutStream.write(bytes, 0, downloadSocket.getInputStream().read(bytes));
                    if (file.length() == size) {
                        break;
                    }


            }
            outPutStream.close();

            byte[] bytes1 = new byte[1];
            //Waiting something
            while ((settingsSocket.getInputStream().read(bytes1)) >= 0) {
                    if (settingsSocket.getInputStream().available() == 0) {
                        break;
                    }
            }

            Log.d("download:", "finish: " + fileName);
            downloadSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] read(File file) throws IOException {

        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException(
                        "EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return buffer;
    }



    private static String downloadImageForVide(String name) {
        try {

            final File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/");
            if(!dir.exists()){
                dir.mkdirs();
            }
//            final File tempFile = new File(dir.getAbsolutePath() + "/" + "temp" + name.replace("MOV", "jpg")  );
            final File tempFile = new File(dir.getAbsolutePath() + "/" + "temp" + name.replace(".MP4", "")  );
//            final File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/" + fileName.replace("MOV", "JPEG"));
            if (tempFile.exists()) {
                return "";
            }

            final JSONObject getVideoThumb = new JSONObject();
            getVideoThumb.put(MSG_ID, 1025);
            getVideoThumb.put(PARAM, DCIM_100_MEDIA + name);
            getVideoThumb.put(TOKEN, token);
            getVideoThumb.put(TYPE, "idr");

            JSONObject json = makeServerRequest(getVideoThumb);

            FileOutputStream outPutStream = new FileOutputStream(tempFile);
            Socket downloadSocket = new Socket(CAMERA_IP, CAMERA_DOWNLOAD_PORT);

            byte[] bytes = new byte[1];
            Log.d("download:", "start: " + name);
            int size = json.getInt("size");
            while (true) {
                if(!canDownload) {
                    downloaded = false;
                    final JSONObject cancelDwonload = new JSONObject();
                    cancelDwonload.put("token", token);
                    cancelDwonload.put("msg_id", 1287);
                    cancelDwonload.put("param", name);
                    makeServerRequest(cancelDwonload);
                    tempFile.delete();
                    break;

                }
                outPutStream.write(bytes, 0, downloadSocket.getInputStream().read(bytes));


                if (tempFile.length() == size) {
                    break;
                }


            }
//           byte[] image = (outPutStream.toByteArray());
//           outPutStream.close();
//            Log.d("image", image.toString());
//
//            (new YuvImage(image, 17, 440, 240, null)).compressToJpeg(new Rect(0, 0, 440, 240), 50, new FileOutputStream(tempFile));
//
//
//
//            Log.d("download:", "finish: " + name);
//            downloadSocket.close();
//
//            Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//            Log.d("imageBitmap", imageBitmap.toString());

            return tempFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




    public static synchronized JSONObject makeServerRequest(JSONObject jsonObject) throws Exception {
        Log.e("CAMERA", "makeServerRequest " + jsonObject);

        logmessage.append("Request \n" + jsonObject.toString() + "\n\n");
        StringBuilder x = new StringBuilder();

        TimeUnit.MILLISECONDS.sleep(50 );

        if(wifiMgr != null){
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String name = wifiInfo.getSSID();
            if(!name.contains(CAMERA_WIFI_NAME) || name.contains("0x")){
                return null;
            }
        }


        if ((settingsSocket == null) || (!settingsSocket.isConnected())) {
            settingsSocket = new Socket(CAMERA_IP, CAMERA_SETTINGS_PORT);
            settingsSocket.setKeepAlive(true);
            settingsSocket.setSoTimeout(15000);
            settingsSocket.setSoLinger(true, 15000);
        }

        try {
            settingsSocket.getOutputStream().write(jsonObject.toString().getBytes());


            byte[] bytes1 = new byte[1];

            int numRead;
            //Waiting something
//        boolean startRead = false;
            int countOpens = 0;


            while ((numRead = settingsSocket.getInputStream().read(bytes1)) >= 0 || true) {



//                if (numRead != 0 && bytes1[0] != -1) {
//                if (settingsSocket.getInputStream().available() == 0){
//
//                } else {
//                    numRead = settingsSocket.getInputStream().read(bytes1);

                    if (bytes1[0] == 123) {
                        countOpens = countOpens + 1;
                    } else if (bytes1[0] == 125) {
                        countOpens = countOpens - 1;
                    }

                    x.append(new String(bytes1, 0, numRead));

                    if (countOpens == 0) {
                        break;
                    }
//                }


//                startRead = true;
//                if (settingsSocket.getInputStream().available() == 0){
//                    TimeUnit.MILLISECONDS.sleep(50);
//                    if (settingsSocket.getInputStream().available() == 0){
//                        break;
//                    }
//                }
//                }
            }
            String response = x.toString();

            if(jsonObject.getInt("msg_id") == 1 && !response.contains(jsonObject.getString("type"))){
                while ((numRead = settingsSocket.getInputStream().read(bytes1)) >= 0 || true) {
                    if (bytes1[0] == 123) {
                        countOpens = countOpens + 1;
                    } else if (bytes1[0] == 125) {
                        countOpens = countOpens - 1;
                    }
                    x.append(new String(bytes1, 0, numRead));
                    if (countOpens == 0) {
                        break;
                    }
                }
            }

            if(response.contains("-4")){
                token = 0;
            }

            Log.e("Camera requests", "request: " + jsonObject.toString() + " response " + response);
            logmessage.append("Resonse \n" + new JSONObject(response).toString() + "\n\n\n");
            return new JSONObject(response);


        }catch (SocketException ex){

            ex.printStackTrace();

            Log.e("asdasdasdas", "ASDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDAAAAAAAAAAAAWDQDQWDQWD");
            token = 0;
            settingsSocket.close();
            settingsSocket = null;
            settingsSocket = new Socket(CAMERA_IP, CAMERA_SETTINGS_PORT);
            settingsSocket.setKeepAlive(true);
            settingsSocket.setSoTimeout(3000);
            settingsSocket.setSoLinger(true, 3000);
            settingsSocket.getOutputStream().write(jsonObject.toString().getBytes());
            getToken(appContext);
//            JSONObject json = new JSONObject();
//            json.put(TOKEN, 0);
//            json.put(MSG_ID, 257);


            return makeServerRequest(jsonObject);
//            return  new JSONObject("");
        }



    }


    public static boolean send260() {
        Log.e("CAMERA", "send260 ");

        try {
            final JSONObject sends260 = new JSONObject();
            sends260.put(TOKEN, token);
            sends260.put(MSG_ID, 260);

            makeServerRequest(sends260);
            return true;
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String getFWVersion() {
        Log.e("CAMERA", "getFWVersion ");

        try {
            final JSONObject getFWVersion = new JSONObject();
            getFWVersion.put(TOKEN, token);
            getFWVersion.put(MSG_ID, 1);
            getFWVersion.put(TYPE, "fw_version");

            JSONObject  json = makeServerRequest(getFWVersion);
            return json.getString("param");
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }



    public static String makePhoto() {
        Log.e("CAMERA", "makePhoto ");
        try {
            final JSONObject makePhoto = new JSONObject();
            makePhoto.put(TOKEN, token);
            makePhoto.put(MSG_ID, 769);
           return makeServerRequest(makePhoto).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void startRecord(final Context context) {
        Log.e("CAMERA", "startRecord ");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                Log.e("Camera", "startRecord doInBackground");

                String result = "";
                try {
                    final JSONObject makePhoto = new JSONObject();
                    makePhoto.put(TOKEN, token);
                    makePhoto.put(MSG_ID, 513);
                    Log.i("Camera", "Request " + makePhoto.toString());
                    JSONObject r1 = makeServerRequest(makePhoto);
                    if (r1 != null) {
                        result = result + r1.toString();
                    }

                    final JSONObject getTime = new JSONObject();
                    getTime.put(TOKEN, token);
                    getTime.put(MSG_ID, 515);
                    JSONObject r2 = makeServerRequest(getTime);
                    if (r2 != null) {
                        result = result + r2.toString();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String res) {
                Toast.makeText(context, res, Toast.LENGTH_LONG).show();
            }


        }.executeOnExecutor(Camera.getExecutorCameraCommands());

    }

    public static void stopRecord(final Context context) {
        Log.e("CAMERA", "stopRecord ");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String result = "";

                try {
                    final JSONObject makePhoto = new JSONObject();
                    makePhoto.put(TOKEN, token);
                    makePhoto.put(MSG_ID, 514);
                    JSONObject r = makeServerRequest(makePhoto);
                    if (r != null) {
                        result = result + r.toString();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());

    }

    public static boolean formatSDCard(final Context context) {
        Log.e("CAMERA", "formatSDCard ");

        try {
            final JSONObject format = new JSONObject();
            format.put(TOKEN, token);
            format.put(MSG_ID, 4);
            format.put(PARAM, "D:");
            JSONObject r = makeServerRequest(format);

            if (r.getInt("rval") == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String send259() {
        Log.e("CAMERA", "send259 ");
        try {
            final JSONObject sends269 = new JSONObject();
            sends269.put(TOKEN, token);
            sends269.put(MSG_ID, 259);
            sends269.put(PARAM, "none_force");
            String s = "";
            JSONObject j = makeServerRequest(sends269);
            if (j!=null) {
                s = j.toString();
            }
            return "request " + sends269.toString() + " response " + s;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




    public static void isCanDownload(boolean canDownload) {
        Log.e("CAMERA", "isCanDownload ");
        Camera.canDownload = canDownload;
    }
}
