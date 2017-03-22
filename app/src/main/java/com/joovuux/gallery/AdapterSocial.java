package com.joovuux.gallery;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import com.joovuux.GrandAdapter;
import ua.net.lsoft.joovuux.R;

/**
 * Created by Dobromir on 17.06.2015.
 */
public class AdapterSocial extends GrandAdapter<AdapterSocial.Social> {

//    private ImageLoader imageLoader;
    public static ArrayList<Social> socials = new ArrayList<>();

public AdapterSocial(Context mainActivity) {
        super(mainActivity, socials, R.layout.item_social, null);
//        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(mainActivity).build());
//        imageLoader = ImageLoader.getInstance();
        }

@Override
public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;



            view = getLayout();
    ImageView ivIco = (ImageView) view.findViewById(R.id.ivIco);
    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);



        if (getItem(position).imageResource != 9000){
            ivIco.setImageResource(getItem(position).imageResource);
           tvTitle.setText(getItem(position).title);
        } else {
            ivIco.setVisibility(View.GONE);
            tvTitle.setText("Cancel");
            tvTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        }



        return view;
        }

static class ViewHolder {
    ImageView ivIco;
    TextView tvTitle;
}

public static class Social {

    public int imageResource;
    public String title;

    public Social(int imageResource, String title) {
        this.imageResource = imageResource;
        this.title = title;
    }
}

}
