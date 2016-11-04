package com.quandary.quandary.db;

import com.quandary.quandary.ui.FliiikHelper;

/**
 * Created by lim on 11/3/16.
 */

public class FliiikGesture {

    public Integer id;
    public String packageName;
    public String action;
    public Boolean status;

    public FliiikGesture() {
        id = 0;
        packageName = "";
        action = "";
        status = false;
    }
}
