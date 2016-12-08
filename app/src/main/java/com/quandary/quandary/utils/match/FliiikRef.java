package com.quandary.quandary.utils.match;

import static java.lang.Math.abs;

/**
 * Created by lim on 12/9/16.
 */

public class FliiikRef {
    public float testAgainst;
    public float matchResult;

    private final float threshhold = 4.8f;

    public FliiikRef() {
        testAgainst = 9.8f;
        matchResult = 9.8f;
    }

    public boolean isNoise() {
        return  (abs(matchResult - testAgainst) > threshhold);
    }
}
