package com.quandary.quandary.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.quandary.quandary.ConfigurationManager;
import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.detector.FliiikMoveDetector;

/**
 * Created by lim on 9/21/16.
 */

public class FliiikService extends Service implements FliiikMoveDetector.OnFliiikMoveListener, ScreenListener {
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */

    private static final float SENSIBILITY = 3;
    private static final int SHAKE_NUMBER = 2;

    ScreenReceiver mScreenStateReceiver;

    boolean shakeStarted;

    ConfigurationManager sharedConfigurationManager;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedConfigurationManager = new ConfigurationManager(getApplicationContext());

        float motionSensor = sharedConfigurationManager.getMotionSensibility();
        float distanceSensor = sharedConfigurationManager.getDistanceSensibility();

        if (FliiikMoveDetector.create(getApplicationContext(), this)) {
            FliiikMoveDetector.updateConfiguration(motionSensor, distanceSensor);
        }

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenStateReceiver = new ScreenReceiver(this);
        registerReceiver(mScreenStateReceiver, screenStateFilter);

        shakeStarted = true;
    }

    @Override
    public void onDestroy() {
        FliiikMoveDetector.stop();
        FliiikMoveDetector.destroy();

        unregisterReceiver(mScreenStateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("Fliiik Service", "Start Command Accepted");

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("FliiikCommand")) {
                    FliiikMoveDetector.stop();

                    String command = extras.getString("FliiikCommand");

                    if (command.equalsIgnoreCase("config")) {
                        float motionSensor = sharedConfigurationManager.getMotionSensibility();
                        float distanceSensor = sharedConfigurationManager.getDistanceSensibility();
                        FliiikMoveDetector.updateConfiguration(motionSensor, distanceSensor);
                    }

                    FliiikMoveDetector.start();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void OnFliiikMove(FliiikGesture move) {

        Log.d("Fliiik Service", move.action + " detected");
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(move.packageName);
        if (launchIntent != null) {
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

    public void onScreenOff() {

        if (shakeStarted == true) {
            FliiikMoveDetector.stop();
            shakeStarted = false;
        }
    }

    public void onScreenOn() {
        if (shakeStarted == false) {
            FliiikMoveDetector.start();
            shakeStarted = true;
        }
    }
}