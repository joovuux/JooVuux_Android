package com.joovuux.gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import com.joovuux.ActiveActivitiesTracker;
import ua.net.lsoft.joovuux.R;

import com.joovuux.MyApp;
import com.joovuux.connection.Camera;
import com.joovuux.settings.MainSettings;

public class ActivityGallery extends Activity{

    public static final String BY_DATE = "date";
    public static final String BY_SIZE = "size";
    public static final String BY_LENGHT = "length";
    private static final int DESCRIPTION = 0;

    private static final int VIDEO_TYPE = 11;
    private static final int PHOTO_TYPE = 12;
    private static final int ALL_TYPE = 13;
    private static final int[] TYPES = {ALL_TYPE, VIDEO_TYPE, PHOTO_TYPE };

    private static final int SORT_BY_DATE = 21;
    private static final int SORT_BY_SIZE = 22;
    private static final int SORT_BY_LENGHT = 23;
    private static final int[] SORTS = {SORT_BY_DATE, SORT_BY_SIZE, SORT_BY_LENGHT};
    public static final int SORT = 31;
    public static final int TYPE = 41;


    private Integer currentType = ALL_TYPE;
    private Integer currentSort = SORT_BY_DATE;



    private GridView gvGallery;
    private ArrayList<ModelImage> imagesList;
    private AdapterGallery adapter;
    private ArrayList<ModelImage> tempImagesList;
    private String galleryPath;
    private ProgressDialog progDailog;
    private View downloadProgress;
    private boolean descriptionOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.new_activity_gallery);

        downloadProgress = findViewById(R.id.downloadProgress);


        TextView tvTypeTitle = (TextView) findViewById(R.id.tvTypeTitle);
        View btnLeftTypePick = findViewById(R.id.btnLeftTypePick);
        View btnRightTypePick = findViewById(R.id.btnRightTypePick);
        initSorted(btnLeftTypePick, btnRightTypePick, tvTypeTitle, TYPE, TYPES);

        TextView tvSortTitle = (TextView) findViewById(R.id.tvSortTitle);
        View btnLeftSortPick = findViewById(R.id.btnLeftSortPick);
        View btnRightSortPick = findViewById(R.id.btnRightSortPick);
        initSorted(btnLeftSortPick, btnRightSortPick, tvSortTitle, SORT, SORTS);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imagesList = new ArrayList<ModelImage>();
        tempImagesList = new ArrayList<ModelImage>();


        adapter = new AdapterGallery(this, imagesList);


        gvGallery = (GridView) findViewById(R.id.gvGallery);
        gvGallery.setAdapter(adapter);
        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ActivityGalleryDescription.class);
                intent.putExtra("myImage", imagesList.get(position));

                ActivityGallery.this.descriptionOpen = true;
                startActivityForResult(intent, DESCRIPTION);
            }
        });


        initFilters();
    }

    private void initFilters() {
        final ToggleButton toggleVideoFlter = (ToggleButton) findViewById(R.id.toggleVideoFlter);
        final ToggleButton togglePhotoFilter = (ToggleButton) findViewById(R.id.togglePhotoFilter);
        final ToggleButton toggleAllFilter = (ToggleButton) findViewById(R.id.toggleAllFilter);

        filterPicked(toggleAllFilter, togglePhotoFilter, toggleVideoFlter, "all");

        toggleVideoFlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPicked(toggleVideoFlter, togglePhotoFilter, toggleAllFilter, ModelImage.VIDEO_TYPE);
            }
        });
        togglePhotoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPicked(togglePhotoFilter, toggleVideoFlter, toggleAllFilter, ModelImage.PHOTO_TYPE);
            }
        });
        toggleAllFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPicked(toggleAllFilter, togglePhotoFilter, toggleVideoFlter, "all");
            }
        });

        final ToggleButton toggleSortByDate = (ToggleButton) findViewById(R.id.toggleSortByDate);
        final ToggleButton toggleSortBySize = (ToggleButton) findViewById(R.id.toggleSortBySize);
        final ToggleButton toggleSortByLenght = (ToggleButton) findViewById(R.id.toggleSortByLenght);

        sortePicked(toggleSortByDate, toggleSortBySize, toggleSortByLenght, BY_DATE);

        toggleSortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortePicked(toggleSortByDate, toggleSortBySize, toggleSortByLenght, BY_DATE);
            }
        });
        toggleSortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortePicked(toggleSortBySize, toggleSortByDate, toggleSortByLenght, BY_SIZE);
            }
        });
        toggleSortByLenght.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortePicked(toggleSortByLenght, toggleSortBySize, toggleSortByDate, BY_LENGHT);
            }
        });
    }

    private void initSorted(View leftButton, View rigthButton, final TextView tvTitle, final int sortOrType, final int[] data){
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionCurrentMode = 0;
                for (int i = 0; i < data.length; i++) {

                    if (sortOrType == SORT) {
                        if (currentSort == data[i]) {
                            positionCurrentMode = i;
                            break;
                        }
                    } else if (sortOrType == TYPE) {
                        if (currentType == data[i]) {
                            positionCurrentMode = i;
                            break;
                        }
                    }

                }

                switch (positionCurrentMode) {
                    case 0:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[2]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Length");
                            currentSort = data[2];
                            sortByLenght();
                        } else {
                            tvTitle.setText("Photo");
                            currentType = data[2];
                            filterByType(ModelImage.PHOTO_TYPE);

                        }
                        break;
                    case 1:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[0]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Date");
                            currentSort = data[0];
                            sortByDate();
                        } else {
                            tvTitle.setText("All");
                            currentType = data[0];

                            filterByType("All");
                        }
                        break;

                    case 2:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[1]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Size");
                            currentSort = data[1];
                            sortBySize();
                        } else {
                            tvTitle.setText("Video");
                            currentType = data[1];

                            filterByType(ModelImage.VIDEO_TYPE);
                        }
                        break;

                }

            }
        });
        rigthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionCurrentMode = 0;
                for (int i = 0; i < data.length; i++) {

                    if (sortOrType == SORT) {
                        if (currentSort == data[i]) {
                            positionCurrentMode = i;
                            break;
                        }
                    } else {
                        if (currentType == data[i]) {
                            positionCurrentMode = i;
                            break;
                        }
                    }

                }

                switch (positionCurrentMode) {
                    case 0:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[1]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Size");
                            currentSort = data[1];
                            sortBySize();
                        } else {
                            tvTitle.setText("Video");
                            currentType = data[1];
                            filterByType(ModelImage.VIDEO_TYPE);
                        }
                        break;


                    case 1:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[2]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Length");
                            currentSort = data[2];
                            sortByLenght();
                        } else {
                            tvTitle.setText("Photo");
                            currentType = data[2];
                            filterByType(ModelImage.PHOTO_TYPE);
                        }
                        break;


                    case 2:
                        Log.d("myLogs", "posotion is " + positionCurrentMode + " will show " + data[0]);
                        if (sortOrType == SORT) {
                            tvTitle.setText("By Date");
                            currentSort = data[0];
                            sortByDate();
                        } else {
                            tvTitle.setText("All");
                            currentType = data[0];
                            filterByType("All");
                        }
                        break;

                }

            }
        });
    }

    private void filterPicked(ToggleButton pickedModeBtn, ToggleButton btn2, ToggleButton btn3, String filterType) {
        pickedModeBtn.setChecked(true);
        pickedModeBtn.setTextColor(Color.DKGRAY);
        btn2.setChecked(false);
        btn2.setTextColor(Color.WHITE);
        btn3.setChecked(false);
        btn3.setTextColor(Color.WHITE);

        filterByType(filterType);
    }

    private void sortePicked(ToggleButton pickedModeBtn, ToggleButton btn2, ToggleButton btn3, String sorteType) {
        pickedModeBtn.setChecked(true);
        pickedModeBtn.setTextColor(Color.DKGRAY);
        btn2.setChecked(false);
        btn2.setTextColor(Color.WHITE);
        btn3.setChecked(false);
        btn3.setTextColor(Color.WHITE);

        switch (sorteType) {
            case BY_DATE:
                sortByDate();
                break;
            case BY_LENGHT:
                sortByLenght();
                break;
            case BY_SIZE:
                sortBySize();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final MyApp myApp = ((MyApp)getApplication());
        myApp.connect(this);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (myApp.isConnecting()){
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                downloadProgress.setVisibility(View.VISIBLE);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        File galleryDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/joovuux/gallery");
                        if (!galleryDir.exists()) {
                            galleryDir.mkdirs();
                        }

                        galleryPath = galleryDir.getAbsolutePath();
                        //scanImages(galleryPath, imagesList);

                       // if(!descriptionOpen){
                            tempImagesList.clear();
                            imagesList.clear();
                            Camera.isCanDownload(true);
                            imagesList.addAll(Camera.getListOfFiles(imagesList));
                            scanImages(galleryPath, imagesList);
                            tempImagesList.addAll(imagesList);
                            descriptionOpen = false;
                        //}


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        adapter.notifyDataSetChanged();

                        downloadProgress.setVisibility(View.GONE);
//                        tempImagesList.addAll(imagesList);

                        sortByDate();

                        if(getSharedPreferences("MainSettings", Context.MODE_PRIVATE).getString(MainSettings.CONNECTION_LOG, "off").equalsIgnoreCase("on")){
                            ((MyApp)ActivityGallery.this.getApplication()).showLogDialog(ActivityGallery.this);
                        }

                    }
                }.executeOnExecutor(Camera.getExecutorCameraCommands());
            }
        }.execute();

    }

    @Override
    protected void onStop() {
        super.onStop();
        ActiveActivitiesTracker.activityStopped(this);
    }

    @Override
    protected void onPause() {
        Camera.isCanDownload(false);
        super.onPause();
    }

    public void sortByDate(){
        Collections.sort(imagesList, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
//                Log.d("COMPARE ", ((ModelImage) o1).getFile().getName() + " : DATE = " + ((ModelImage) o1).getDate());
                long date1 = ((ModelImage) o1).getDate();
                long date2 = ((ModelImage) o2).getDate();

                Log.e("compare", "date 1: " + date1 + " == date2 2: " + date2);
                if (date1 > date2) {
                    return -1;
                } else if (date1 < date2) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void sortBySize(){
        Collections.sort(imagesList, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {


                long size1 = ((ModelImage) o1).getSize();
                long size2 = ((ModelImage) o2).getSize();
                Log.e("compare", "size 1: " + size1 + " == size 2: " + size2);
                if (size1 > size2) {
                    return -1;
                }else if (size1 < size2) {
                    return +1;
                }else{
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged();

    }

    public void sortByLenght(){
        Collections.sort(imagesList, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {

                long lenght1 = ((ModelImage) o1).getSize();
                long lenght2 = ((ModelImage) o2).getSize();
//                long lenght1 = ((ModelImage) o1).getLenght();
//                long lenght2 = ((ModelImage) o2).getLenght();
                Log.e("compare", "lenght 1: " + lenght1 + " == lenght 2: " + lenght2);
                if (lenght1 > lenght2) {
                    return -1;
                }else if (lenght1 < lenght2) {
                    return +1;
                }else{
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged();

    }

    public void filterByType(String type){
        switch (type) {
            case ModelImage.PHOTO_TYPE:

                imagesList.clear();

                for (ModelImage image : tempImagesList) {
                    if (image.getType().equals(ModelImage.PHOTO_TYPE)) {
                        imagesList.add(image);
                    }
                }
                break;
            case ModelImage.VIDEO_TYPE:

                imagesList.clear();

                for (ModelImage image : tempImagesList) {
                    if (image.getType().equals(ModelImage.VIDEO_TYPE)) {
                        imagesList.add(image);
                    }
                }
                break;
            default:
                imagesList.clear();
                imagesList.addAll(tempImagesList);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DESCRIPTION && resultCode == RESULT_OK){
            imagesList.clear();
            tempImagesList.clear();
            scanImages(galleryPath, imagesList);
            tempImagesList.addAll(imagesList);
            adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ModelImage> scanImages(String directoryName, ArrayList<ModelImage> imagesList) {
        File directory = new File(directoryName);
//        imagesList.clear();

        File[] fList = directory.listFiles();
        if(fList == null){
            return imagesList;
        }

        for (File file : fList) {
            if ((file != null) && (file.isFile()) && !file.isDirectory()) {
//                if(!file.getAbsoluteFile().toString().contains("MOV")){
                    imagesList.add(new ModelImage(file, this));
//                }

            } else if (file.isDirectory()) {
                scanImages(file.getAbsolutePath(), imagesList);
            }
        }
        return imagesList;
    }



}
