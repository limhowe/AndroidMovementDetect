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

    public void setActionPackageForTap(String packageName) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fTapPackage", packageName);
        editor.commit();
    }

    public void setActionPackageForChop(String packageName) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fChopPackage", packageName);
        editor.commit();
    }

    public void setTapServiceEnabled(Boolean isEnabled) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("fTapEnabled", isEnabled);
        editor.commit();
    }

    public void setChopServiceEnabled(Boolean isEnabled) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("fChopEnabled", isEnabled);
        editor.commit();
    }

    public String getActionPackageForTap() {
        return  pref.getString("fTapPackage", "");
    }

    public String getActionPackageForChop() {
        return  pref.getString("fChopPackage", "");
    }

    public Boolean getTapServiceEnabled() {
        return  pref.getBoolean("fTapEnabled", false);
    }
    public Boolean getChopServiceEnabled() {
        return  pref.getBoolean("fChopEnabled", false);
    }
}
