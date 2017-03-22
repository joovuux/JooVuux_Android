package com.joovuux.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.joovuux.GrandAdapter;
//import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ua.net.lsoft.joovuux.R;

/**
 * Created by Dima on 08.09.2015.
 */
public class OptionsAdapter extends GrandAdapter<String> {

//    private ImageLoader imageLoader;
    private  List<String> data;

    public OptionsAdapter(Context mainActivity, List<String> data, ListView listView) {
        super(mainActivity, data, R.layout.item_fore_fore_option_spinner, listView);
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;



        view = getLayout();
        TextView tvOption = (TextView) view.findViewById(R.id.tvOption);
        if(data.size()>1 && data.get(1)!=null && data.get(1).equalsIgnoreCase("180")){
            tvOption.setText(data.get(position));
        } else {
            tvOption.setText(data.get(position).replace("0P", "0FPS").replace("5P", "5FPS").replace("_", " ").replace("S.Fine", "High").replace("Fine", "Medium").replace("Normal", "Low"));

        }


        return view;
    }

}