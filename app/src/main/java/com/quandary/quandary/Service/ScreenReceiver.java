package com.quandary.quandary.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lim on 9/22/16.
 */

public class ScreenReceiver extends BroadcastReceiver{

    private boolean screenOff;

    ScreenListener listener;

    ScreenReceiver(ScreenListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            listener.onScreenOff();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
            listener.onScreenOn();
        }
    }

}
