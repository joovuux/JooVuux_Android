package com.joovuux.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.joovuux.ActiveActivitiesTracker;
import com.joovuux.MyApp;
import com.joovuux.connection.Camera;
import com.joovuux.connection.NewVideoActivity;
import com.joovuux.connection.VideoActivity;
//import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


import java.io.File;

import io.fabric.sdk.android.Fabric;
import ua.net.lsoft.joovuux.R;


public class ActivityGalleryDescription extends Activity{

    public static final String FACEBOOK = "Facebook";
    public static final String TWITTER = "Twitter";
    public static final String YOUTUBE = "Youtube";
    private static final int DIALOG_DELETE_PHOTO = 1;
    private VideoView videoView;
    private SubsamplingScaleImageView imageView;

    private ShareDialog fbShareDialog;
    private ModelImage image;
    private NewVideoActivity videoActivity;

    public ProgressBar getPbLoadVideo() {
        return pbLoadVideo;
    }

    private ProgressBar pbLoadVideo;

    public TextView getTvCurrentTime() {
        return tvCurrentTime;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    private TextView tvCurrentTime;
    private TextView tvTime;

    public SeekBar getTimeSeek() {
        return timeSeek;
    }

    private SeekBar timeSeek;

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoActivity != null) {
                    videoActivity.pause();
                }
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoActivity != null) {
                    videoActivity.pause();
                }
            }
        }, 3000);
//        ((MyApp)getApplication()).connect(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Fabric.with(this, new TweetComposer());
//
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        fbShareDialog = new ShareDialog(this);

//        AdapterSocial.socials.clear();
//        AdapterSocial.socials.add(new AdapterSocial.Social(R.drawable.facebook_ico, FACEBOOK));
//        AdapterSocial.socials.add(new AdapterSocial.Social(R.drawable.twitter_ico, TWITTER));
//        AdapterSocial.socials.add(new AdapterSocial.Social(R.drawable.youtube_ico, YOUTUBE));



//        final AdapterSocial adapterSocial = new AdapterSocial(this);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_gallery_description);

        image = (ModelImage) getIntent().getSerializableExtra("myImage");


//        videoView = (VideoView) findViewById(R.id.videoView);

//        final OnItemClickListener photoOnCLick = new OnItemClickListener() {
//            @Override
//            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//                if (AdapterSocial.socials.get(position).title == FACEBOOK) {
//                    fbSharePhoto(image.getFile());
//                } else if (AdapterSocial.socials.get(position).title == TWITTER) {
//                    tweePhoto(image.getFile());
//                }
//            }
//        };
//
//        final OnItemClickListener videoOnClick = new OnItemClickListener() {
//            @Override
//            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//                if (AdapterSocial.socials.get(position).title == FACEBOOK) {
//                    fbShareVideo(image.getFile());
//                } else if (AdapterSocial.socials.get(position).title == YOUTUBE) {
//                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubeUpload.class);
//                    intent.putExtra("uri", Uri.fromFile(image.getFile()).toString());
//                    startActivity(intent);
//                }
//            }
//        };

//        findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogPlus dialog = new DialogPlus.Builder(ActivityGalleryDescription.this)
//                        .setAdapter(adapterSocial)
//                        .setOnItemClickListener(image.getType().equals(ModelImage.PHOTO_TYPE) ? photoOnCLick : videoOnClick)
//
//                                .create();
//                dialog.show();
//            }
//        });
//

//        fbShareDialog = new ShareDialog(this);

        findViewById(R.id.deleteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DELETE_PHOTO);
            }
        });
        if ( image.getType().equals(ModelImage.PHOTO_TYPE)) {
            Log.d("FILE TYPE ", "PHOTO");
            imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImage(ImageSource.uri(image.getFile().getAbsolutePath()));
//            Picasso.with(this).load(image.getFile()).into(imageView);
//            ImageLoader.getInstance().displayImage(Uri.fromFile(image.getFile()).toString(), imageView);

        } else {
            Log.d("FILE TYPE ", "VIDEO");


            Button btnDownload = (Button) findViewById(R.id.btnDownload);

            if(!image.getFile().getAbsolutePath().contains("rtsp")){
                btnDownload.setVisibility(View.GONE);
            }

            btnDownload.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    View dialog = getLayoutInflater().inflate(R.layout.alert_download_file, null);

                    final Dialog downloadProgress = new Dialog(ActivityGalleryDescription.this);
                    downloadProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    downloadProgress.setContentView(dialog);
                    downloadProgress.show();

                    final ProgressBar pbProgress = (ProgressBar) dialog.findViewById(R.id.pbProgress);
                    final TextView tvProgress = (TextView) dialog.findViewById(R.id.tvProgress);


                    new AsyncTask<Void, Integer, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            Camera.isCanDownload(true);
                            Camera.downloadFile(image.getFile().getName().replace("_thm", ""), pbProgress, tvProgress, downloadProgress);
                            Camera.isCanDownload(false);

                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            Log.d("PROGRESS", values[0] + "::");
                            super.onProgressUpdate(values);
                        }
                    }.executeOnExecutor(Camera.getExecutorCameraCommands());
                }
            });

            findViewById(R.id.videoIterface).setVisibility(View.VISIBLE);


            pbLoadVideo = (ProgressBar) findViewById(R.id.pbLoadVideo);

            timeSeek = (SeekBar) findViewById(R.id.timeSeek);
            timeSeek.setVisibility(View.VISIBLE);
            timeSeek.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        videoActivity.setTime(timeSeek.getProgress());
                    }
                    return false;
                }
            });

            tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
            tvTime = (TextView) findViewById(R.id.tvTime);


            videoActivity = new NewVideoActivity();
            videoActivity.setStillWatching(true);
            videoActivity.setDescAvtivity(this);

            String path = (image.getFile().getAbsolutePath().contains("rtsp")) ? image.getFile().getAbsolutePath().replace("/rtsp:/", "RTSP://") : "file://" + image.getFile().getAbsolutePath();

            videoActivity.setStream(path);

            Button btnPlay = (Button) findViewById(R.id.btnPlay);
            btnPlay.setVisibility(View.VISIBLE);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoActivity.play();
                }
            });


            Button btnPause = (Button) findViewById(R.id.btnPause);
            btnPause.setVisibility(View.VISIBLE);
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoActivity.pause();
                }
            });

            getFragmentManager().beginTransaction().replace(R.id.frame, videoActivity).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoActivity != null){
            videoActivity.setStillWatching(false);
            videoActivity.setDescAvtivity(null);
            videoActivity.releasePlayer();
        }
        ActiveActivitiesTracker.activityStopped(this);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Camera.send260();
                return null;
            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DELETE_PHOTO) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Delete");
            adb.setMessage("Are you sure that want to delete this?");
            adb.setIcon(android.R.drawable.ic_dialog_info);
            adb.setPositiveButton("YES", myClickListener);
            adb.setNegativeButton("CANCEL", myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }


    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    setResult(RESULT_OK);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            final File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery/");
                            if(!dir.exists()){
                                dir.mkdirs();
                            }
                            final File file = new File(dir.getAbsolutePath() + "/" + image.getFile().getName());
                            if (file.exists()) {
                                file.delete();
                            }
                            Camera.deleteFile(image.getCameraPath());
//                            Camera.deleteFile(image.getCameraPath().replace("_thm", ""));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                        }
                    }.executeOnExecutor(Camera.getExecutorCameraCommands());

                    image.getFile().delete();
                    finish();
                    ActivityGalleryDescription.this.finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;

            }
        }
    };
    private void tweePhoto(File file) {
        TweetComposer.Builder builder = new TweetComposer.Builder(ActivityGalleryDescription.this)
                .text("The best image of images")
                .image(Uri.fromFile(file));
        builder.show();
    }

    private void fbShareVideo(File file) {
        ShareVideo shareVideo = new ShareVideo.Builder()
                .setLocalUrl(Uri.fromFile(file))
                .build();
        ShareVideoContent videoContent = new ShareVideoContent.Builder()
                .setVideo(shareVideo)
                .build();

        fbShareDialog.show(videoContent);
    }

    private void fbSharePhoto(File file) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Uri imageUri = Uri.fromFile(file);

        SharePhoto photo = new SharePhoto.Builder()
                .setImageUrl(imageUri)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        fbShareDialog.show(content);
    }
}
