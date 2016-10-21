package com.quandary.quandary.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lim on 9/21/16.
 */

public class ServiceBooter extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Intent intent = new Intent(arg0,FliiikService.class);
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}