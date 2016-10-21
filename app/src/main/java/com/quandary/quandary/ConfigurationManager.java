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

    public void setActionPackage(String packageName) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fShakePackage", packageName);
        editor.commit();
    }

    public void setServiceEnabled(Boolean isEnabled) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("fShakeEnabled", isEnabled);
        editor.commit();
    }

    public String getActionPackage() {
        return  pref.getString("fShakePackage", "");
    }

    public Boolean getServiceEnabled() {
        return  pref.getBoolean("fShakeEnabled", false);
    }
}
