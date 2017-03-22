package com.joovuux.youtube;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.joovuux.MyApp;
import com.joovuux.connection.Camera;

import ua.net.lsoft.joovuux.R;

public class ActivityYoutubeUpload extends FragmentActivity {

    private static final String TAG = "ActivityYoutubeUpload";
    private static final String LOGIN_USERNAME = "username";
    private static final String LOGIN_PASSWORD = "password";

    private ProgressBar progressBar;
    private String userName;
    private String password;
    private SharedPreferences youtubeLoginPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_upload);
        youtubeLoginPref = getPreferences(MODE_PRIVATE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (youtubeLoginPref.getString(LOGIN_USERNAME, null) == null){
            createLoginDialog().show();
        } else {
            userName = youtubeLoginPref.getString(LOGIN_USERNAME, null);
            password = youtubeLoginPref.getString(LOGIN_PASSWORD, null);
            uploadYoutube(Uri.parse(getIntent().getStringExtra("uri")), userName, password);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApp)getApplication()).connect(this);
    }

    public Dialog createLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_youtube_login, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        login(dialog, view);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private void login(final DialogInterface dialog, final View loginView) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                userName = ((EditText) loginView.findViewById(R.id.username)).getText().toString();
                password = ((EditText) loginView.findViewById(R.id.password)).getText().toString();
                return YoutubeUploader.getClientAuthToken(userName, password);
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(ActivityYoutubeUpload.this, "Login is failed", Toast.LENGTH_LONG).show();
                    createLoginDialog().show();
                } else {
                    youtubeLoginPref.edit()
                            .putString(LOGIN_USERNAME, userName)
                            .putString(LOGIN_PASSWORD, password)
                            .commit();

                    dialog.dismiss();
                    dialog.cancel();
                    uploadYoutube(Uri.parse(getIntent().getStringExtra("uri")), userName, password);
                }

                super.onPostExecute(result);

            }
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }

    private void uploadYoutube(final Uri data, final String userName, final String password) {

        new AsyncTask<Void, Integer, Void>() {



            @Override
            protected Void doInBackground(Void... params) {
                YoutubeUploadRequest request = new YoutubeUploadRequest();
                request.setUri(data);
                //request.setCategory(category);
                //request.setTags(tags);
                request.setTitle("MPRJ Video Tite");
                request.setDescription("MPRJ Video Test");

                YoutubeUploader.upload(request, new YoutubeUploader.ProgressListner() {

                    @Override
                    public void onUploadProgressUpdate(int progress) {

                        publishProgress(progress);
                    }
                }, ActivityYoutubeUpload.this, userName, password);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBar.setProgress(values[0]);

                if(values[0] == 100) {
                    progressBar.setVisibility(View.GONE);

                }
            };
        }.executeOnExecutor(Camera.getExecutorCameraCommands());
    }
}
