package com.quandary.quandary.ui;

import android.graphics.drawable.Drawable;

/**
 * Created by lim on 9/20/16.
 */

public class PackageItem {

    private Drawable icon;

    private String name;

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}