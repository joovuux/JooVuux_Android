package com.joovuux;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by sergey on 8/17/15.
 */
public class Serv extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Serv(String name) {
        super("Serv");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }
}
