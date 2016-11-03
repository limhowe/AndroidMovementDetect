package com.quandary.quandary.detector;

import android.content.Context;
import android.graphics.LinearGradient;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lim on 9/21/16.
 */

public class FliiikDetector implements SensorEventListener {

    private static final float DEFAULT_THRESHOLD_ACCELERATION = 2.0f;
    private static final int DEFAULT_THRESHOLD_FLIIIK_NUMBER = 3;
    private static final int INTERVAL = 200;

    private static SensorManager mSensorManager;
    private static FliiikDetector mSensorEventListener;

    private OnFliiikListener mfliiikListener;
    private ArrayList<SensorBundle> mSensorBundles;
    private Object mLock;
    private float mThresholdAcceleration;
    private int mThresholdfliiikNumber;


    //Test purpose
    private int TAP = 0;
    private int CHOP = 1;

    private int[][] mActions = {
            { TAP, TAP, TAP },
            { CHOP, CHOP, CHOP },
    };

    private int mTargetAction = 1;


    /**
     * Interface definition for a callback to be invoked when the device has been fliiikn.
     */
    public static interface OnFliiikListener {
        /**
         * Called when the device has been fliiikn.
         */
        public void OnFliiik(int index);
    }

    /**
     * Creates a fliiik detector and starts listening for device fliiiks. Neither {@code context} nor
     * {@code listener} can be null. In that case, a {@link IllegalArgumentException} will be thrown.
     *
     * @param context The current Android context.
     * @param listener The callback triggered when the device is fliiikn.
     * @return true if the fliiik detector has been created and started correctly, false otherwise.
     */
    public static boolean create(Context context, OnFliiikListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        mSensorEventListener = new FliiikDetector(listener);

        return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Starts a previously created fliiik detector. If no detector has been created before, the method
     * won't create one and will return false.
     *
     * @return true if the fliiik detector has been started correctly, false otherwise.
     */
    public static boolean start() {
        if (mSensorManager != null && mSensorEventListener != null) {
            return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
        return false;
    }

    /**
     * Stops a previously created fliiik detector. If no detector has been created before, the method
     * will do anything.
     */
    public static void stop() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    /**
     * Releases all resources previously created.
     */
    public static void destroy() {
        mSensorManager = null;
        mSensorEventListener = null;
    }

    /**
     * You can update the configuration of the fliiik detector based on your usage but the default settings
     * should work for the majority of cases. It uses {@link FliiikDetector#DEFAULT_THRESHOLD_ACCELERATION}
     * for the sensibility and {@link FliiikDetector#DEFAULT_THRESHOLD_FLIIIK_NUMBER} for the number
     * of fliiik required.
     *
     * @param sensibility The sensibility, in G, is the minimum acceleration need to be considered
     *                    as a fliiik. The higher number you go, the harder you have to fliiik your
     *                    device to trigger a fliiik.
     * @param targetAction index of Action List
     */
    public static void updateConfiguration(float sensibility, int targetAction) {
        mSensorEventListener.setConfiguration(sensibility, targetAction);
    }

    private FliiikDetector(OnFliiikListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("fliiik listener must not be null");
        }
        mfliiikListener = listener;
        mSensorBundles = new ArrayList<SensorBundle>();
        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;
        mThresholdfliiikNumber = DEFAULT_THRESHOLD_FLIIIK_NUMBER;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SensorBundle sensorBundle = new SensorBundle(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.timestamp);

        synchronized (mLock) {
            if (mSensorBundles.size() == 0) {
                mSensorBundles.add(sensorBundle);
            } else if (sensorBundle.getTimestamp() - mSensorBundles.get(mSensorBundles.size() - 1).getTimestamp() > INTERVAL) {
                mSensorBundles.add(sensorBundle);
            }
        }

        performCheck();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // The accuracy is not likely to change on a real device. Just ignore it.
    }

    private void setConfiguration(float sensibility, int targetAction) {
        mThresholdAcceleration = sensibility;
        mTargetAction = targetAction;
        synchronized (mLock) {
            mSensorBundles.clear();
        }
    }

    int taptaptap[] = {
            0, 0
    };

    long taptaptapTimestamp[] = {
            0, 0
    };

    int chopchopchop[] = {
            0, 0
    };

    long chopchopchopTimestamp[] = {
            0, 0
    };

    int[] tapNoise = { 0, 0 };
    int[] chopNoise = { 0, 0 };

    int[] noiseVector = {0, 0};
    int[] vector = {0, 0};

    private void resetTempValues() {
        taptaptap[0] = 0;
        taptaptap[1] = 0;

        chopchopchop[0] = 0;
        chopchopchop[1] = 0;

        noiseVector[0] = 0;
        noiseVector[1] = 0;

        vector[0] = 0;
        vector[1] = 0;

        tapNoise[0] = 0;
        tapNoise[1] = 0;

        chopNoise[0] = 0;
        chopNoise[1] = 0;

    }

    private void performCheck() {
        synchronized (mLock) {

            float tapXLimit = mThresholdAcceleration;
            double taptapZ = mThresholdAcceleration * 0.9;

            float chopZLimit = mThresholdAcceleration;
            double chopchopchopY = mThresholdAcceleration * 1.5;
            double chopchopchopX = mThresholdAcceleration * 1.5;

            int timestampLimit = 4;


            //vector [0] = taptaptap
            //vector [0] = chopchopchop


            for (SensorBundle sensorBundle : mSensorBundles) {

                //Check tap tap tap

                if (sensorBundle.getXAcc() > tapXLimit && noiseVector[0] < 1) {
                    tapNoise[0] ++;
                    noiseVector[0] = 1;
                }
                if (sensorBundle.getXAcc() < -tapXLimit && noiseVector[0] > -1) {
                    tapNoise[1] ++;
                    noiseVector[0] = -1;
                }

                if (tapNoise[1] > 1  || tapNoise[0] > 1) {
                    taptaptap[0] = 0;
                    taptaptap[1] = 0;

                    tapNoise[1] = 0;
                    tapNoise[0] = 0;

                    noiseVector[0] = 0;
                } else {
                    if (sensorBundle.getZAcc() > taptapZ && vector[0] < 1) {
                        if (sensorBundle.getTimestamp() - taptaptapTimestamp[0] > timestampLimit*INTERVAL) {
                            taptaptap[0]++;
                        } else {
                            taptaptap[0] = 1;
                        }
                        taptaptapTimestamp[0] = sensorBundle.getTimestamp();
                        vector[0] = 1;
                        Log.i("Log for action", "TAP { " + Arrays.toString(taptaptap) + "}");

                    }
                    if (sensorBundle.getZAcc() < -taptapZ && vector[0] > -1) {

                        if ( sensorBundle.getTimestamp() - taptaptapTimestamp[1] > timestampLimit*INTERVAL) {
                            taptaptap[1]++;
                        } else {
                            taptaptap[1] = 1;
                        }

                        taptaptapTimestamp[1] = sensorBundle.getTimestamp();
                        vector[0] = -1;
                        Log.i("Log for action", "TAP { " + Arrays.toString(taptaptap) + "}");
                    }
                }

                //Check chop chop chop

                if (sensorBundle.getZAcc() > chopZLimit && noiseVector[1] < 1) {
                    chopNoise[0] ++;
                    noiseVector[1] = 1;
                }
                if (sensorBundle.getZAcc() < -chopZLimit && noiseVector[1] > -1) {
                    chopNoise[1] ++;
                    noiseVector[1] = -1;
                }

                if (chopNoise[1] > 1  || chopNoise[0] > 1) {
                    chopchopchop[0] = 0;
                    chopchopchop[1] = 0;
                    chopNoise[1] = 0;
                    chopNoise[0] = 0;
                    noiseVector[1] = 0;
                } else {
                    if (sensorBundle.getXAcc() > chopchopchopX && vector[1] < 1) {

                        if (sensorBundle.getTimestamp() - chopchopchopTimestamp[0]> timestampLimit*INTERVAL) {
                            chopchopchop[0]++;
                        } else {
                            chopchopchop[0] = 1;
                        }

                        chopchopchopTimestamp[0] = sensorBundle.getTimestamp();
                        vector[1] = 1;
                        Log.i("Log for action", "CHOP { " + Arrays.toString(chopchopchop) + "}");
                    }
                    if (sensorBundle.getXAcc()  < -chopchopchopX && vector[1] > -1) {

                        if (sensorBundle.getTimestamp() - chopchopchopTimestamp[1] > timestampLimit*INTERVAL) {
                            chopchopchop[1]++;
                        } else {
                            chopchopchop[1] = 1;
                        }

                        chopchopchopTimestamp[1] = sensorBundle.getTimestamp();
                        vector[1] = -1;
                        Log.i("Log for action", "CHOP { " + Arrays.toString(chopchopchop) + "}");
                    }
                }
            }

            mSensorBundles.clear();

            boolean found = false;
            if (taptaptap[0] >= 3 || taptaptap[1] >= 3) {

                resetTempValues();

                mfliiikListener.OnFliiik(TAP);
            } else if (chopchopchop[0] >= 3 || chopchopchop[1] >= 3) {

                resetTempValues();

                mfliiikListener.OnFliiik(CHOP);

            }
        }
    }

    /**
     * Convenient object used to store the 3 axis accelerations of the device as well as the current
     * captured time.
     */
    private class SensorBundle {
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
}