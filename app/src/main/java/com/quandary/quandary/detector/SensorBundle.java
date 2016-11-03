package com.quandary.quandary.detector;

/**
 * Created by lim on 10/22/16.
 */

/**
 * Convenient object used to store the 3 axis accelerations of the device as well as the current
 * captured time.
 */
public class SensorBundle {
    /**
     * The acceleration on X axis.
     */
    private final float mXAcc;
    /**
     * The acceleration on Y axis.
     */
    private final float mYAcc;
    /**
     * The acceleration on Z axis.
     */
    private final float mZAcc;
    /**
     * The timestamp when to record was captured.
     */
    private final long mTimestamp;

    public SensorBundle(float XAcc, float YAcc, float ZAcc, long timestamp) {
        mXAcc = XAcc;
        mYAcc = YAcc;
        mZAcc = ZAcc;
        mTimestamp = timestamp;
    }

    public float getXAcc() {
        return mXAcc;
    }

    public float getYAcc() {
        return mYAcc;
    }

    public float getZAcc() {
        return mZAcc;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public String toString() {
        return "SensorBundle{" +
                "mXAcc=" + mXAcc +
                ", mYAcc=" + mYAcc +
                ", mZAcc=" + mZAcc +
                ", mTimestamp=" + mTimestamp +
                '}';
    }
}