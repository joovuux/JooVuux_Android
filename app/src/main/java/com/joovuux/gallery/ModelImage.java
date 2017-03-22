package com.joovuux.gallery;

import android.content.Context;
import android.util.Log;

import com.joovuux.connection.Camera;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class    ModelImage implements Serializable{

    public static final String VIDEO_TYPE = "video";
    public static final String PHOTO_TYPE = "photo";
    public static final String VIDEO_URL = "rtsp://192.168.42.1/tmp/fuse_d/DCIM/100MEDIA/";

    public boolean isCamera() {
        return isCamera;
    }

    public void setIsCamera(boolean isCamera) {
        this.isCamera = isCamera;
    }

    private boolean isCamera;

    public String getCameraPath() {
        return cameraPath;
    }

    private String cameraPath;
    private String imagePath;

    public void setSize(long size) {
        this.size = size;
    }

    private long size;

    public void setDate(long date) {
        this.date = date;
    }

    private long date;
    private String type;
    private int lenght = 0;
    private File file;

    public ModelImage(File file, Context context) {
        Log.e("FILE NAME", file.getName());
        String metadataString = Camera.filesMap.get(file.getName());
        if(metadataString!=null){
            String dateString = metadataString.substring(metadataString.indexOf("|") + 1, metadataString.length());
            String size = metadataString.substring(0, metadataString.indexOf(" "));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.file = file;
            this.size = Long.parseLong(size);
            this.date = date.getTime();
        } else {
            this.file = file;
            this.size = file.length();
            this.date = file.lastModified();

        }

        this.cameraPath = "DCIM/100MEDIA/" + file.getName();
        if (file.getPath().contains("jpg")){
            this.type = PHOTO_TYPE;
            this.lenght = 0;
        } else {
                Log.d("VIDEO PATH", file.getAbsolutePath());
                this.type = VIDEO_TYPE;
        }


    }

    public ModelImage(File file, Context context, String imagePath) {
        String metadataString = Camera.filesMap.get(file.getName());
        if(metadataString!=null){
            String dateString = metadataString.substring(metadataString.indexOf("|") + 1, metadataString.length());
            String size = metadataString.substring(0, metadataString.indexOf(" "));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.file = file;
            this.size = Long.parseLong(size);
            this.date = date.getTime();
        } else {
            this.file = file;
            this.size = file.length();
            this.date = file.lastModified();
        }

        this.imagePath = imagePath;
        Log.d("1122", file.getAbsolutePath() + "   : " + file.getName());
        this.cameraPath = "DCIM/100MEDIA/" + file.getName();

        if (file.getPath().contains("jpg")){
            this.type = PHOTO_TYPE;
            this.lenght = 0;
        }
        else { //if (file.getPath().contains("mp4")){
            Log.d("VIDEO PATH", file.getAbsolutePath());
            this.type = VIDEO_TYPE;
        }
    }

    public long getSize() {
        return size;
    }

    public long getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getLenght() {
        return lenght;
    }

    public File getFile() {
        return file;
    }


    @Override
    public String toString() {
        return "ModelImage{" +
                "imagePath='" + imagePath + '\'' +
                ", size=" + size +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", lenght=" + lenght +
                ", file=" + file +
                '}';
    }


}
