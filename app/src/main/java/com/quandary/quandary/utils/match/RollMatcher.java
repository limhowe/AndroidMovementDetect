package com.quandary.quandary.utils.match;

import com.quandary.quandary.FliiikConstant;
import com.quandary.quandary.detector.SensorFilterBundle;

import java.util.ArrayList;

/**
 * Created by lim on 11/17/16.
 */

public class RollMatcher {
    /*
    *
    * PATTERNS
    * -Z X -X Z -- 1
    * X -Z -X Z -- 2
    * -Z X Z -X -- 3
    * X -Z Z -X -- 4
     */

    private static String PATTERN1 = "" + FliiikConstant.Z_POSITIVE + FliiikConstant.X_NEGATIVE + FliiikConstant.X_POSITIVE + FliiikConstant.Z_NEGATIVE;
    private static String PATTERN2 = "" + FliiikConstant.Z_POSITIVE + FliiikConstant.X_NEGATIVE + FliiikConstant.Z_NEGATIVE + FliiikConstant.X_POSITIVE;
    private static String PATTERN3 = "" + FliiikConstant.X_NEGATIVE + FliiikConstant.Z_POSITIVE + FliiikConstant.X_POSITIVE + FliiikConstant.Z_NEGATIVE;
    private static String PATTERN4 = "" + FliiikConstant.X_NEGATIVE + FliiikConstant.Z_POSITIVE + FliiikConstant.Z_NEGATIVE + FliiikConstant.X_POSITIVE;

    private static String REVERSE_PATTERN1 = new StringBuilder(PATTERN1).reverse().toString();
    private static String REVERSE_PATTERN2 = new StringBuilder(PATTERN2).reverse().toString();
    private static String REVERSE_PATTERN3 = new StringBuilder(PATTERN3).reverse().toString();
    private static String REVERSE_PATTERN4 = new StringBuilder(PATTERN4).reverse().toString();

    public static int granualMatch(int startPosition, int startTimestamp, ArrayList<SensorFilterBundle> allList, int matchSequence) {

        int endPositionLimit = matchSequence * 4;
        if (endPositionLimit + 3> startPosition) return -1; // No need to check;

        for (int i = startPosition; i >= endPositionLimit; i--) {
            int count = 0;
            String matchStr = "";
            int currIndex = i;
            while (count < 4 && currIndex >= endPositionLimit) {
                SensorFilterBundle bundle = allList.get(currIndex);
                int currAction = bundle.getAxis();

                if (currAction != FliiikConstant.Y_NEGATIVE && currAction != FliiikConstant.Y_POSITIVE) {
                    matchStr = matchStr + currAction;
                    count++;
                }
                currIndex--;
            }

            if (count >= 4 && (matchStr.equalsIgnoreCase(PATTERN1) || matchStr.equalsIgnoreCase(PATTERN2) || matchStr.equalsIgnoreCase(PATTERN3) || matchStr.equalsIgnoreCase(PATTERN4) ||
                    matchStr.equalsIgnoreCase(REVERSE_PATTERN1) || matchStr.equalsIgnoreCase(REVERSE_PATTERN2) || matchStr.equalsIgnoreCase(REVERSE_PATTERN3) || matchStr.equalsIgnoreCase(REVERSE_PATTERN4) )) {
                return currIndex + 1;
            }
        }

        return -1;
    }
}
