package com.joovuux.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joovuux.GrandAdapter;
import com.squareup.picasso.Picasso;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import ua.net.lsoft.joovuux.R;

public class AdapterGallery extends GrandAdapter<ModelImage> {
//
    private final DisplayImageOptions options;
    private ImageLoader imageLoader;
    private Context mainActivity;

    public AdapterGallery(Context mainActivity, List<ModelImage> list) {
        super(mainActivity, list, R.layout.item_gallery, null);
        this.mainActivity = mainActivity;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mainActivity)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(10) // default
                .build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();

        imageLoader = ImageLoader.getInstance();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = new ViewHolder();

        if (view == null || convertView.getTag() == null) {
            view = getLayout();
            holder.ivImage = (ImageView) view.findViewById(R.id.itemGalleryImage);
            view.setTag(holder);
        }

        holder = (ViewHolder) view.getTag();

        if(getItem(position) == null){
            return  view;
        }

        String path = Uri.fromFile(getItem(position).getFile()).toString();
        if(path.contains("MOV") || path.contains("MP4")){
            Log.d("VIDEO", path);
            LayoutInflater inf = LayoutInflater.from(mainActivity);
            View videoView = inf.inflate(R.layout.item_gallery_video, null);
            ((TextView)videoView.findViewById(R.id.tvName)).setText(getItem(position).getFile().getName().replace("_thm", ""));
            return videoView;
        } else {
//            Picasso.with(mainActivity)
//                    .load(getItem(position).getFile())
//                    .resize(1280, 720)
//                    .into(holder.ivImage);

//            Picasso.with(mainActivity).load(getItem(position).getFile()).into(holder.ivImage);
            imageLoader.displayImage(path, holder.ivImage, options);
        }


        return view;
    }

    static class ViewHolder {
        ImageView ivImage;
    }

}

