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
    /**
     * The acceleration on X axis.
     */
    public final float mXAcc;
    /**
     * The acceleration on Y axis.
     */
    public final float mYAcc;
    /**
     * The acceleration on Z axis.
     */
    public final float mZAcc;


    private long mTimestamp;

    public SensorFilterBundle(int axis, long timestamp, float XAcc, float YAcc, float ZAcc) {
        mAxis = axis;
        mXAcc = XAcc;
        mYAcc = YAcc;
        mZAcc = ZAcc;
        mTimestamp = timestamp;
    }

    public int getAxis() {
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