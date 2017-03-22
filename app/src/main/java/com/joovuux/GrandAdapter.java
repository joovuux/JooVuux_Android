package com.joovuux;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class GrandAdapter<T> extends BaseAdapter {

    private ListView listView;
    private Context mContext;
    private List<T> mList;
    private int mLayout;

    public GrandAdapter(Context context, List<T> list, int layout, ListView listView) {
        this.listView = listView;
        mContext = context;
        mList = list;
        mLayout = layout;
    }

    public int getCount() {
        if (mList!=null) {
            return mList.size();
        }
        return 0;
    }

    public T getItem(int position) {
        return position >= 0 && position < mList.size() ? mList.get(position) : null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getLayout() {
        LayoutInflater inf = LayoutInflater.from(mContext);
        if(listView == null) {
            return inf.inflate(mLayout, null);
        } else {
            return inf.inflate(mLayout, listView, false);
        }

    }

    public Context getContext() {
        return mContext;
    }

}