package com.joovuux.connection;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.joovuux.gallery.ActivityGalleryDescription;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ua.net.lsoft.joovuux.R;

public class NewVideoActivity extends Fragment implements IVLCVout.Callback, LibVLC.HardwareAccelerationError {
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";

    public final static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";
    public static final Uri LIVE_STREAM = Uri.parse("RTSP://192.168.42.1/live");

    private View view;
    private boolean stillWatching;

    public void setStream(String stream) {
        this.stream = Uri.parse(stream);
    }

    private Uri stream = LIVE_STREAM;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;

    public void setDescAvtivity(ActivityGalleryDescription descAvtivity) {
        this.descAvtivity = descAvtivity;
    }

    private ActivityGalleryDescription descAvtivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sample, null);
        // Receive path to play from intent


        Log.d(TAG, "Playing back " + stream);

        mSurface = (SurfaceView) view.findViewById(R.id.surface);
        holder = mSurface.getHolder();
        //holder.addCallback(this);

        return view;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onResume() {
        super.onResume();
        createPlayer(stream);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = getActivity().getWindow().getDecorView().getWidth();
        int h = getActivity().getWindow().getDecorView().getHeight();

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
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    /*************
     * Player
     *************/

    private void createPlayer(Uri media) {
        releasePlayer();
        try {
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
//            options.add("--subsdec-encoding <encoding>");
            options.add(":network-caching=1000");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(options);

            libvlc.setOnHardwareAccelerationError(this);

            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
//            vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();


            Media m = new Media(libvlc, media);
            m.setHWDecoderEnabled(false, true);
            mMediaPlayer.setMedia(m);

            mMediaPlayer.play();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: handle this cleaner
    public void releasePlayer() {
        if (libvlc == null)
            return;

        if(stillWatching){
            mMediaPlayer.stop();
            mMediaPlayer.setTime(0);
            mMediaPlayer.play();
            return;
        }

        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /*************
     * Events
     *************/

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    @Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

    public void setStillWatching(boolean stillWatching) {
        this.stillWatching = stillWatching;
    }

    public void play() {
        if(mMediaPlayer != null){
            mMediaPlayer.play();
        }
    }

    public void pause() {
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    public void setTime(int progress) {
        mMediaPlayer.setTime(progress * 100);
    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<NewVideoActivity> mOwner;

        public MyPlayerListener(NewVideoActivity owner) {
            mOwner = new WeakReference<NewVideoActivity>(owner);
        }

        private String fromMillisToTime(long millis){
            return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            NewVideoActivity player = mOwner.get();

            switch(event.type) {
                case MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.TimeChanged:
                    long currentTime = player.mMediaPlayer.getTime();
                    if(player.descAvtivity != null) {
                        player.descAvtivity.getPbLoadVideo().setVisibility(View.GONE);
                        player.descAvtivity.getTimeSeek().setProgress((int) (currentTime/100));
                        player.descAvtivity.getTvCurrentTime().setText(fromMillisToTime(currentTime));
                    }
                    break;
                case MediaPlayer.Event.Playing:
                    long time = player.mMediaPlayer.getLength();
                    if(player.descAvtivity != null) {
                        player.descAvtivity.getTimeSeek().setMax((int) (time/100));
                        player.descAvtivity.getTvTime().setText(fromMillisToTime(time));
                    }
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:

                default:
                    break;
            }
        }
    }

    @Override
    public void eventHardwareAccelerationError() {
        // Handle errors with hardware acceleration
        Log.e(TAG, "Error with hardware acceleration");
        this.releasePlayer();
        Toast.makeText(getActivity(), "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }
}