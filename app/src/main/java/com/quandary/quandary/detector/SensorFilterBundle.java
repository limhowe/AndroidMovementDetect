package com.quandary.quandary.detector;

/**
 * Created by lim on 10/22/16.
 */

/**
 * Convenient object used to store the 3 axis accelerations of the device as well as the current
 * captured time.
 */

public class SensorFilterBundle {
    /**
     * The acceleration on X axis.
     */
    private final int mAxis;

    private long mTimestamp;

    public SensorFilterBundle(int axis, long timestamp) {
        mAxis = axis;
        mTimestamp = timestamp;
    }

    public int getAcc() {
        return mAxis;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return "T" + mTimestamp +
                ";" + mAxis + ";";
    }
}