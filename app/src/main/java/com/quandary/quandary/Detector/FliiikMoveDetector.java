package com.quandary.quandary.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.quandary.quandary.filter.LowPassFilterSmoothing;
import com.quandary.quandary.filter.MeanFilterSmoothing;
import com.quandary.quandary.filter.MedianFilterSmoothing;

import java.util.ArrayList;
import java.util.Arrays;

public class FliiikMoveDetector implements SensorEventListener {

    protected volatile float[] acceleration = new float[3];

    private static final float DEFAULT_THRESHOLD_ACCELERATION = 2.0f;
    private static final int INTERVAL = 200;

    private static SensorManager mSensorManager;
    private static FliiikMoveDetector mSensorEventListener;

    private FliiikMoveDetector.OnFliiikMoveListener mFliiikMoveListener;
    private ArrayList<FliiikMoveDetector.SensorBundle> mSensorBundles;
    private Object mLock;
    private float mThresholdAcceleration;


    protected MeanFilterSmoothing meanFilterAccelSmoothing;
    protected MedianFilterSmoothing medianFilterAccelSmoothing;
    protected LowPassFilterSmoothing lpfAccelSmoothing;

    protected boolean meanFilterSmoothingEnabled;
    protected boolean medianFilterSmoothingEnabled;
    protected boolean lpfSmoothingEnabled;


    private String[] axises = {"X", "Y" , "Z" };

    /**
     * Interface definition for a callback to be invoked when the device has performed one of fliiikmove.
     */
    public static interface OnFliiikMoveListener {
        /**
         * Called when the device has performed one of fliiikmove.
         */
        public void OnFliiikMove(FliiikMove move);
    }

    private FliiikMoveDetector(FliiikMoveDetector.OnFliiikMoveListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("FliikMove listener must not be null");
        }
        mFliiikMoveListener = listener;
        mSensorBundles = new ArrayList<FliiikMoveDetector.SensorBundle>();
        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;


        meanFilterAccelSmoothing = new MeanFilterSmoothing();
        medianFilterAccelSmoothing = new MedianFilterSmoothing();
        lpfAccelSmoothing = new LowPassFilterSmoothing();

    }

    /**
     * Creates a shake detector and starts listening for device shakes. Neither {@code context} nor
     * {@code listener} can be null. In that case, a {@link IllegalArgumentException} will be thrown.
     *
     * @param context The current Android context.
     * @param listener The callback triggered when the device is shaken.
     * @return true if the shake detector has been created and started correctly, false otherwise.
     */
    public static boolean create(Context context, OnFliiikMoveListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        mSensorEventListener = new FliiikMoveDetector(listener);

        return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }


    public static boolean start() {
        if (mSensorManager != null && mSensorEventListener != null) {
            return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
        return false;
    }

    /**
     * Stops a previously created FliiikMove detector. If no detector has been created before, the method
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
     * You can update the configuration of the Fliiikmove detector based on your usage but the default settings
     * should work for the majority of cases. It uses {@link FliiikMoveDetector#DEFAULT_THRESHOLD_ACCELERATION}
     * for the sensibility
     *
     * @param sensibility The sensibility, in G, is the minimum acceleration need to be considered
     *                    as a fliiikmove. The higher number you go, the harder you have to fliiikmove your
     *                    device to trigger a move.
     */
    public static void updateConfiguration(float sensibility, boolean meanFilterSmoothingEnabled, boolean medianFilterSmoothingEnabled, boolean lpfSmoothingEnabled) {
        mSensorEventListener.setConfiguration(sensibility,meanFilterSmoothingEnabled,medianFilterSmoothingEnabled,lpfSmoothingEnabled);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        System.arraycopy(sensorEvent.values, 0, acceleration, 0, sensorEvent.values.length);

        if (meanFilterSmoothingEnabled)
        {
            acceleration = meanFilterAccelSmoothing
                    .addSamples(acceleration);
        }

        if (medianFilterSmoothingEnabled)
        {
            acceleration = medianFilterAccelSmoothing
                    .addSamples(acceleration);
        }

        if (lpfSmoothingEnabled)
        {
            acceleration = lpfAccelSmoothing.addSamples(acceleration);
        }

        FliiikMoveDetector.SensorBundle sensorBundle = new FliiikMoveDetector.SensorBundle(acceleration[0], acceleration[1], acceleration[2], sensorEvent.timestamp);

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

    private void setConfiguration(float sensibility, boolean meanFilterSmoothingEnabled, boolean medianFilterSmoothingEnabled, boolean lpfSmoothingEnabled) {
        mThresholdAcceleration = sensibility;

        this.meanFilterSmoothingEnabled = meanFilterSmoothingEnabled;
        this.medianFilterSmoothingEnabled = medianFilterSmoothingEnabled;
        this.lpfSmoothingEnabled = lpfSmoothingEnabled;

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

            for (FliiikMoveDetector.SensorBundle sensorBundle : mSensorBundles) {
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

            StringBuilder buidler = new StringBuilder();

            boolean found = false ;

            for (int i=0; i<3; i++) {
                int[] axis = matrix[i];
                buidler.append(axises[i]);

                for (int direction: axis) {

                    if (direction > 0) {
                        found = true;
                    }
                    //Check which action
                }

                buidler.append(Arrays.toString(axis));
                buidler.append("  ");
            }

            if (found) {
                mFliiikMoveListener.OnFliiikMove(new FliiikMove(buidler.toString()));
            }

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
