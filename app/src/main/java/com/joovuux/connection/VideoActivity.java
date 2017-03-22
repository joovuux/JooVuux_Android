package com.joovuux.connection;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;

public class VideoActivity extends Fragment implements SurfaceHolder.Callback,IVideoPlayer {
    public final static String TAG = "vlc/VideoActivity";
    public static final String LIVE_STREAM = "rtsp://192.168.42.1/live";
    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private final static int VideoSizeChanged = -1;
    private View view;
    private int mVideoWidth;
    private int mVideoHeight;

    public void setStillWatching(boolean stillWatching) {
        this.stillWatching = stillWatching;
        mHandler.setStillWatching(stillWatching);
    }

    private boolean stillWatching;

    public void setStream(String stream) {
        this.stream = stream;
    }

    private String stream = LIVE_STREAM;

    public void setIsShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    private boolean isShowing;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("STREAM", "onCreateView");
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sample, null);

        mSurface = (SurfaceView) view.findViewById(R.id.surface);
        holder = mSurface.getHolder();
        holder.addCallback(this);
        //createPlayer("rtsp://192.168.42.1/live");
        this.view = view;
        return view;
    }



    @Override
    public void onResume() {
        Log.e("STREAM", "onResume");
        super.onResume();
        holder = mSurface.getHolder();
        holder.addCallback(this);
        createPlayer(stream);
    }

    @Override
    public void onDestroyView() {
        Log.e("STREAM", "onDestroyView");
        super.onDestroyView();

        releasePlayer();
    }

    @Override
    public void onPause() {
        Log.e("STREAM", "onPause");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
//                Camera.send260();
                return null;
            }

        }.executeOnExecutor(Camera.getExecutorCameraCommands());


        releasePlayer();

        super.onPause();


    }



    /*************
     * Surface
     *************/

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("STREAM", "surfaceCreated");
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
            int width, int height) {
        Log.e("STREAM", "surfaceChanged");
//        if (libvlc != null)
//            libvlc.attachSurface(holder.getSurface(), this);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.e("STREAM", "surfaceDestroyed");
        if (libvlc != null){
//            libvlc.detachSurface();
        }
    }

    private void setSize(int width, int height) {
        Log.e("STREAM", "setSize");
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = view.getWidth();
        int h = view.getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    @Override
    public void setSurfaceLayout(int width, int height, int visible_width,
            int visible_height, int sar_num, int sar_den) {
        Log.e("STREAM", "setSurfaceLayout");
        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
        msg.sendToTarget();
    }

    /*************
     * Player
     *************/

    public void createPlayer(String media) {
        Log.e("STREAM", "createPlayer");
        releasePlayer();
        try {
            Log.i("Video", "Create Player");
            /*if (media.length() > 0) {
                Toast toast = Toast.makeText(getActivity(), media, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }*/

            // Create a new media player
//            libvlc = LibVLC.getInstance();
//            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
//            libvlc.setSubtitlesEncoding("");
//            libvlc.setAout(LibVLC.AOUT_OPENSLES);
//            libvlc.setFrameSkip(true);
//            libvlc.setTimeStretching(true);
//            libvlc.setVerboseMode(true);
//            if(LibVlcUtil.isGingerbreadOrLater())
//                libvlc.setVout(LibVLC.VOUT_ANDROID_WINDOW);
//            else
//                libvlc.setVout(LibVLC.VOUT_ANDROID_SURFACE);
//            LibVLC.restart(getActivity());
//            EventHandler.getInstance().addHandler(mHandler);
//            holder.setKeepScreenOn(true);
//            MediaList list = libvlc.getMediaList();
//            list.clear();
//            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
//            libvlc.playIndex(0);

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }

    public void play(){
//        libvlc.play();
    }

    public void pause(){
//        libvlc.pause();
    }

    public float getLenght(){
        return 0;
//        libvlc.getLength();
    }

    public float getPosition(){
        return 0;
//        libvlc.getPosition();
    }

    public void setPosition(float pos){
//        libvlc.setPosition(pos);
    }




    public void releasePlayer() {

        if(stillWatching){
            return;
        }

        Log.e("STREAM", "releasePlayer");
        if (libvlc == null)
            return;

        int i = 98*98;

        EventHandler.getInstance().removeHandler(mHandler);
//        libvlc.stop();
//        libvlc.detachSurface();
        holder = null;
        libvlc = null;

    }

    /*************
     * Events
     *************/

    private MyHandler mHandler = new MyHandler(this);

    public void refresh() {
        Log.e("STREAM", "refresh");

        createPlayer("rtsp://192.168.42.1/live");

    }

    private static class MyHandler extends Handler {

        public void setStillWatching(boolean stillWatching) {
            this.stillWatching = stillWatching;
        }

        private boolean stillWatching;

        private WeakReference<VideoActivity> mOwner;

        public MyHandler(VideoActivity owner) {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void handleMessage(Message msg) {

            VideoActivity player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
            case EventHandler.MediaPlayerEndReached:

                if(stillWatching){
                    player.createPlayer(player.stream);
                } else {
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.releasePlayer();
                }


                break;
            case EventHandler.MediaPlayerPlaying:
            case EventHandler.MediaPlayerPaused:
            case EventHandler.MediaPlayerStopped:
            default:
                break;
            }
        }
    }



    @Override
    public void eventHardwareAccelerationError() {
        Log.e("STREAM", "eventHardwareAccelerationError");
        // Handle errors with hardware acceleration
        Log.e(TAG, "Error with hardware acceleration");
        this.releasePlayer();
        Toast.makeText(getActivity(), "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    @Override
    public int configureSurface(Surface surface, int width, int height, int hal) {
        Log.e("STREAM", "configureSurface");
        Log.d(TAG, "configureSurface: width = " + width + ", height = " + height);
        if (LibVlcUtil.isICSOrLater() || surface == null)
            return -1;
        if (width * height == 0)
            return 0;
        if(hal != 0)
            holder.setFormat(hal);
        holder.setFixedSize(width, height);
        return 1;
    }
}
