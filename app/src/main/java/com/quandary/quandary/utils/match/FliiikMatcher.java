package com.quandary.quandary.utils.match;

import com.quandary.quandary.FliiikConstant;
import com.quandary.quandary.detector.SensorFilterBundle;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by lim on 11/17/16.
 */

public class FliiikMatcher {

    public static int FLIIIK_NOT_FOUND = -100;

    public static int match(String actionString, ArrayList<SensorFilterBundle> allList) {
        if (actionString.length() != 3) return FLIIIK_NOT_FOUND;

        String compareString = actionString;

        FliiikRef refObj = new FliiikRef();

        int startPosition = allList.size() - 1;

        int result[] = {
            -1, -1, -1
        };


        for (int i=0; i<3; i++) {
            int action = Integer.parseInt(compareString.substring(2-i));
            if (i < 2 ) {
                compareString = compareString.substring(0, 2-i);
            }

            int currResult = -1;

            refObj.testAgainst = 0;
            refObj.matchResult = 0;

            switch (action) {
                case FliiikConstant.GESTURE_CHOP:
                    refObj.testAgainst = 0;
                    currResult = ChopMatcher.granualMatch(startPosition,0,allList,2-i ,refObj);
                    break;
                case FliiikConstant.GESTURE_SPIN:
                    refObj.testAgainst = 9.8f;
                    currResult = ChopMatcher.granualMatch(startPosition,0,allList,2-i ,refObj);
                    break;
                case FliiikConstant.GESTURE_TAP:
                    currResult = TapMatcher.granualMatch(startPosition,0,allList,2-i);
                    break;
                case FliiikConstant.GESTURE_ROLL:
                    currResult = RollMatcher.granualMatch(startPosition,0,allList,2-i);
                    break;
            }

            if (currResult != -1) {
                startPosition = currResult - 1 ;
                result[i] = currResult;
            } else {
                return FLIIIK_NOT_FOUND;
            }
        }


        //Todo -  REVIEW PROPOSITION FORMULA

        int totalLength = allList.size();
        return abs(totalLength - 12 - result[2]) * 10 + abs(result[0] + result[2] - result[1]*2) * 5;
    }
}
