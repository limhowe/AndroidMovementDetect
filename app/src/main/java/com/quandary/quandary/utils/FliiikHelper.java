package com.quandary.quandary.utils;

import com.quandary.quandary.FliiikConstant;

import java.util.ArrayList;

import static com.quandary.quandary.FliiikConstant.GESTURE_CHOP;
import static com.quandary.quandary.FliiikConstant.GESTURE_ROLL;
import static com.quandary.quandary.FliiikConstant.GESTURE_SPIN;
import static com.quandary.quandary.FliiikConstant.GESTURE_TAP;
import static com.quandary.quandary.FliiikConstant.X_POSITIVE;
import static com.quandary.quandary.FliiikConstant.X_NEGATIVE;
import static com.quandary.quandary.FliiikConstant.Y_POSITIVE;
import static com.quandary.quandary.FliiikConstant.Y_NEGATIVE;
import static com.quandary.quandary.FliiikConstant.Z_POSITIVE;
import static com.quandary.quandary.FliiikConstant.Z_NEGATIVE;


/**
 * Created by lim on 11/3/16.
 */

public class FliiikHelper {

    public static String decodeActionString(String encodedString) {
        String decodedString = encodedString.replace(String.valueOf(FliiikConstant.GESTURE_TAP), "TAP ");
        decodedString = decodedString.replace(String.valueOf(GESTURE_CHOP), "CHOP ");
        decodedString = decodedString.replace(String.valueOf(GESTURE_ROLL), "ROLL ");
        decodedString = decodedString.replace(String.valueOf(GESTURE_SPIN), "SPIN ");

        decodedString = decodedString.trim();

        return  decodedString;
    }

    public static String getActionAxisString(String action) {
        int action1 = Integer.parseInt(action.substring(0,1));
        int action2 = Integer.parseInt(action.substring(1,2));
        int action3 = Integer.parseInt(action.substring(2));

        return getAxisString(action1) + getAxisString(action2) + getAxisString(action3);
    }

    public static String getAxisValueAsString(int axisValue) {
        switch (axisValue) {
            case X_POSITIVE:
                return "X";
            case X_NEGATIVE:
                return "-X";
            case Y_POSITIVE:
                return "Y";
            case Y_NEGATIVE:
                return "-Y";
            case Z_POSITIVE:
                return "Z";
            case Z_NEGATIVE:
                return "-Z";
            default:
                return "N/A";
        }
    }

    public static String getAxisString(int gesture) {
        switch (gesture) {
            case GESTURE_CHOP:
                return "" + X_POSITIVE + Z_NEGATIVE + X_NEGATIVE + Z_POSITIVE;
            case GESTURE_TAP:
                return "" + Z_NEGATIVE + Y_POSITIVE + Z_POSITIVE + Y_NEGATIVE;
            case GESTURE_ROLL:
                return "" + Z_NEGATIVE + X_POSITIVE + X_NEGATIVE + Z_POSITIVE;
        }

        return "None";
    }

    public static boolean compareActionString (String compareStr, String action) {
        StringBuilder gestureStr = new StringBuilder(FliiikHelper.getActionAxisString(action));
        StringBuilder objectStr = new StringBuilder(compareStr);

        int targetLength = gestureStr.length();
        int length = objectStr.length();

        int targetIndex = 0;

        for (int i=0; i< length; i++ ) {
            if (gestureStr.charAt(targetIndex) == objectStr.charAt(i)) {
                targetIndex++;

                if (targetIndex >= targetLength) return true;
            }
        }

        return false;
    }
}
