package com.quandary.quandary.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.quandary.quandary.detector.move.FliiikMove;
import com.quandary.quandary.detector.move.FliiikMoveTap;
import com.quandary.quandary.filter.LowPassFilterSmoothing;
import com.quandary.quandary.filter.MeanFilterSmoothing;
import com.quandary.quandary.filter.MedianFilterSmoothing;

import java.util.ArrayList;

public class FliiikMoveDetector implements SensorEventListener {

    private static final float DEFAULT_THRESHOLD_ACCELERATION = 2.0f;
    private static final int INTERVAL = 200;

    private static SensorManager mSensorManager;
    private static FliiikMoveDetector mSensorEventListener;

    private FliiikMoveDetector.OnFliiikMoveListener mFliiikMoveListener;
    private ArrayList<SensorBundle> mSensorBundles;
    private Object mLock;
    private float mThresholdAcceleration;

    private SensorBundle mLastSensorBundle;

    protected MeanFilterSmoothing meanFilterAccelSmoothing;
    protected MedianFilterSmoothing medianFilterAccelSmoothing;
    protected LowPassFilterSmoothing lpfAccelSmoothing;

    protected boolean meanFilterSmoothingEnabled;
    protected boolean medianFilterSmoothingEnabled;
    protected boolean lpfSmoothingEnabled;

    private String[] axises = {"X", "Y" , "Z" };
    protected volatile float[] acceleration = new float[3];

    private ArrayList<FliiikMove> mSupportedMoves;

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
        mSensorBundles = new ArrayList<SensorBundle>();
        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;


        meanFilterAccelSmoothing = new MeanFilterSmoothing();
        medianFilterAccelSmoothing = new MedianFilterSmoothing();
        lpfAccelSmoothing = new LowPassFilterSmoothing();

        mLastSensorBundle = null;
        mSupportedMoves = new ArrayList<FliiikMove>();
        mSupportedMoves.add(new FliiikMoveTap()); // Add support FliiikMoveTap
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

        SensorBundle sensorBundle = new SensorBundle(acceleration[0], acceleration[1], acceleration[2], sensorEvent.timestamp);


        boolean checked = false;

        synchronized (mLock) {
            if (mLastSensorBundle == null) {
                mSensorBundles.add(sensorBundle);
                checked = true;
            } else if (sensorBundle.getTimestamp() - mLastSensorBundle.getTimestamp() > INTERVAL) {
                mSensorBundles.add(sensorBundle);
                checked = true;
            }
        }

        if (checked) {
            performCheck();
        }
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
            for (int i=0; i<mSensorBundles.size(); i++) {

                SensorBundle sensorBundle = mSensorBundles.get(i);

                if (mLastSensorBundle == null) {
                    mLastSensorBundle =  sensorBundle;
                    continue;
                }

                SensorBundle changeBundle = new SensorBundle(sensorBundle.getXAcc() - mLastSensorBundle.getXAcc(),
                        sensorBundle.getYAcc() - mLastSensorBundle.getYAcc(),
                        sensorBundle.getZAcc() - mLastSensorBundle.getZAcc(),
                        sensorBundle.getTimestamp());

                for (int j=0; j<mSupportedMoves.size(); j++) {
                    FliiikMove fliiikMove = mSupportedMoves.get(j);
                    FliiikMove.FliiikMoveStatus resultStatus = fliiikMove.performCheck(changeBundle);
                    if (resultStatus == FliiikMove.FliiikMoveStatus.COMPLETE) {
                        mFliiikMoveListener.OnFliiikMove(fliiikMove);
                        resetSupportedMoves(j);
                        break;
                    } else if (resultStatus == FliiikMove.FliiikMoveStatus.PROGRESS) {
                        Log.i("MOVE-TAP :", fliiikMove.toString());
                    }
                }

                mLastSensorBundle = sensorBundle;
            }

            mSensorBundles.clear();
        }
    }

    private void resetSupportedMoves() {
        for (FliiikMove fliiikMove: mSupportedMoves) {
            fliiikMove.resetMove();
        }
    }

    private void resetSupportedMoves(int idExcpetion) {
        for (int i=0; i<mSupportedMoves.size(); i++) {
            if (i == idExcpetion) continue;;
            FliiikMove fliiikMove = mSupportedMoves.get(i);
            fliiikMove.resetMove();
        }
    }

}
