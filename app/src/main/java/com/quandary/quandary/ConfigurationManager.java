package com.quandary.quandary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lim on 9/21/16.
 */

public class ConfigurationManager {
    private static final String PREFERENCE_NAME = "MyPreferenceFileName";
    SharedPreferences pref;

    public ConfigurationManager(Context context) {
        pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
}

    public void setMotionSensibility(float motionSensibility) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("fMotionSensor", motionSensibility);
        editor.commit();
    }

    public void setDistanceSensibility(float distanceSensibility) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("fDistanceSensor", distanceSensibility);
        editor.commit();
    }

    public float getMotionSensibility() {
        return  pref.getFloat("fMotionSensor", 2.7f);
    }
    public float getDistanceSensibility() {
        return  pref.getFloat("fDistanceSensor", 2.9f);
    }
}
