package com.quandary.quandary.ui;

import com.quandary.quandary.FliiikConstant;

/**
 * Created by lim on 11/3/16.
 */

public class FliiikHelper {

    public static String decodeActionString(String encodedString) {
        String decodedString = encodedString.replace(String.valueOf(FliiikConstant.GESTURE_TAP), "TAP ");
        decodedString = decodedString.replace(String.valueOf(FliiikConstant.GESTURE_CHOP), "CHOP ");

        decodedString = decodedString.trim();

        return  decodedString;
    }
}
