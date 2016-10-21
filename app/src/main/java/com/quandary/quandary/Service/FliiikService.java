package com.quandary.quandary.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.quandary.quandary.ConfigurationManager;
import com.quandary.quandary.Detector.ShakeDetector;

/**
 * Created by lim on 9/21/16.
 */

public class FliiikService extends Service implements ShakeDetector.OnShakeListener, ScreenListener {
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

        if (ShakeDetector.create(this, this)) {
            ShakeDetector.updateConfiguration(SENSIBILITY, SHAKE_NUMBER);
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
        ShakeDetector.stop();
        ShakeDetector.destroy();

        unregisterReceiver(mScreenStateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void OnShake() {
        // This callback is triggered by the ShakeDetector. In a real implementation, you should
        // do here a real action.

        try {
            Boolean isEnabled = sharedConfigurationManager.getServiceEnabled();
            String packageName = sharedConfigurationManager.getActionPackage();

            if (isEnabled && packageName != "") {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        } catch (Exception e) {

        } catch (Error er) {

        }

    }

    public void onScreenOff() {

        if (shakeStarted == true) {
            ShakeDetector.stop();
            shakeStarted = false;
        }
    }

    public void onScreenOn() {
        if (shakeStarted == false) {
            ShakeDetector.start();
            shakeStarted = true;
        }
    }

}