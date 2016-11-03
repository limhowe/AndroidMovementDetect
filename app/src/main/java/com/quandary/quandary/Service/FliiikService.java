package com.quandary.quandary.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.quandary.quandary.ConfigurationManager;
import com.quandary.quandary.detector.FliiikDetector;

/**
 * Created by lim on 9/21/16.
 */

public class FliiikService extends Service implements FliiikDetector.OnFliiikListener, ScreenListener {
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */

    private static final float SENSIBILITY = 3;
    private static final int SHAKE_NUMBER = 2;

    ConfigurationManager sharedConfigurationManager;
    ScreenReceiver mScreenStateReceiver;

    boolean shakeStarted;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedConfigurationManager = new ConfigurationManager(getApplicationContext());

        if (FliiikDetector.create(this, this)) {
            FliiikDetector.updateConfiguration(SENSIBILITY, SHAKE_NUMBER);
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
        FliiikDetector.stop();
        FliiikDetector.destroy();

        unregisterReceiver(mScreenStateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void OnFliiik(int index) {
        if (index == 0) { // TAPTAPTAP
            try {
                Boolean isTapEnabled = sharedConfigurationManager.getTapServiceEnabled();
                String packageNameTap = sharedConfigurationManager.getActionPackageForTap();

                if (isTapEnabled && packageNameTap != "") {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageNameTap);
                    if (launchIntent != null) {
                        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }
            } catch (Exception e) {

            } catch (Error er) {

            }
        } else {
            try {
                Boolean isChopEnabled = sharedConfigurationManager.getChopServiceEnabled();
                String packageNameChop = sharedConfigurationManager.getActionPackageForChop();

                if (isChopEnabled && packageNameChop != "") {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageNameChop);
                    if (launchIntent != null) {
                        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }
            } catch (Exception e) {

            } catch (Error er) {

            }
        }
    }

    public void onScreenOff() {

        if (shakeStarted == true) {
            FliiikDetector.stop();
            shakeStarted = false;
        }
    }

    public void onScreenOn() {
        if (shakeStarted == false) {
            FliiikDetector.start();
            shakeStarted = true;
        }
    }

}