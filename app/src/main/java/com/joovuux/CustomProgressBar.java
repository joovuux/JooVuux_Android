package com.joovuux;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Created by Dima on 11.09.2015.
 */
public class CustomProgressBar extends ProgressBar {


    private SeekBar seekBar;

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }



    public CustomProgressBar(Context context) {
        super(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("ACTION on touch", ev.getAction() + " ::" );
        seekBar.dispatchTouchEvent(ev);
        return true;
    }

}
