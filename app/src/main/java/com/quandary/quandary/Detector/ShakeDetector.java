package com.quandary.quandary.Detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by lim on 9/21/16.
 */

public class ShakeDetector implements SensorEventListener {

    private static final float DEFAULT_THRESHOLD_ACCELERATION = 2.0f;
    private static final int DEFAULT_THRESHOLD_SHAKE_NUMBER = 3;
    private static final int INTERVAL = 200;

    private static SensorManager mSensorManager;
    private static ShakeDetector mSensorEventListener;

    private OnShakeListener mShakeListener;
    private ArrayList<SensorBundle> mSensorBundles;
    private Object mLock;
    private float mThresholdAcceleration;
    private int mThresholdShakeNumber;

    /**
     * Interface definition for a callback to be invoked when the device has been shaken.
     */
    public static interface OnShakeListener {
        /**
         * Called when the device has been shaken.
         */
        public void OnShake();
    }

    /**
     * Creates a shake detector and starts listening for device shakes. Neither {@code context} nor
     * {@code listener} can be null. In that case, a {@link IllegalArgumentException} will be thrown.
     *
     * @param context The current Android context.
     * @param listener The callback triggered when the device is shaken.
     * @return true if the shake detector has been created and started correctly, false otherwise.
     */
    public static boolean create(Context context, OnShakeListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        mSensorEventListener = new ShakeDetector(listener);

        return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Starts a previously created shake detector. If no detector has been created before, the method
     * won't create one and will return false.
     *
     * @return true if the shake detector has been started correctly, false otherwise.
     */
    public static boolean start() {
        if (mSensorManager != null && mSensorEventListener != null) {
            return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
        return false;
    }

    /**
     * Stops a previously created shake detector. If no detector has been created before, the method
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
     * You can update the configuration of the shake detector based on your usage but the default settings
     * should work for the majority of cases. It uses {@link ShakeDetector#DEFAULT_THRESHOLD_ACCELERATION}
     * for the sensibility and {@link ShakeDetector#DEFAULT_THRESHOLD_SHAKE_NUMBER} for the number
     * of shake required.
     *
     * @param sensibility The sensibility, in G, is the minimum acceleration need to be considered
     *                    as a shake. The higher number you go, the harder you have to shake your
     *                    device to trigger a shake.
     * @param shakeNumber The number of shake (roughly) required to trigger a shake.
     */
    public static void updateConfiguration(float sensibility, int shakeNumber) {
        mSensorEventListener.setConfiguration(sensibility, shakeNumber);
    }

    private ShakeDetector(OnShakeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Shake listener must not be null");
        }
        mShakeListener = listener;
        mSensorBundles = new ArrayList<SensorBundle>();
        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;
        mThresholdShakeNumber = DEFAULT_THRESHOLD_SHAKE_NUMBER;
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

    private void setConfiguration(float sensibility, int shakeNumber) {
        mThresholdAcceleration = sensibility;
        mThresholdShakeNumber = shakeNumber;
        synchronized (mLock) {
            mSensorBundles.clear();
        }
    }

    private void performCheck() {
        synchronized (mLock) {
            int[] vector = {0, 0, 0};
            int[][] matrix = {
                    {0, 0}, // Represents X axis, positive and negative direction.
                    {0, 0}, // Represents Y axis, positive and negative direction.
                    {0, 0}  // Represents Z axis, positive and negative direction.
            };

            for (SensorBundle sensorBundle : mSensorBundles) {
                if (sensorBundle.getXAcc() > mThresholdAcceleration && vector[0] < 1) {
                    vector[0] = 1;
                    matrix[0][0]++;
                }
                if (sensorBundle.getXAcc() < -mThresholdAcceleration && vector[0] > -1) {
                    vector[0] = -1;
                    matrix[0][1]++;
                }
                if (sensorBundle.getYAcc() > mThresholdAcceleration && vector[1] < 1) {
                    vector[1] = 1;
                    matrix[1][0]++;
                }
                if (sensorBundle.getYAcc() < -mThresholdAcceleration && vector[1] > -1) {
                    vector[1] = -1;
                    matrix[1][1]++;
                }
                if (sensorBundle.getZAcc() > mThresholdAcceleration && vector[2] < 1) {
                    vector[2] = 1;
                    matrix[2][0]++;
                }
                if (sensorBundle.getZAcc() < -mThresholdAcceleration && vector[2] > -1) {
                    vector[2] = -1;
                    matrix[2][1]++;
                }
            }

            for (int[] axis: matrix) {
                for (int direction: axis) {
                    if (direction < mThresholdShakeNumber) {
                        return;
                    }
                }
            }

            mShakeListener.OnShake();
            mSensorBundles.clear();
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