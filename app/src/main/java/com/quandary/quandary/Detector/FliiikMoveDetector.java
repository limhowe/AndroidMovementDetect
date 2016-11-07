package com.quandary.quandary.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.quandary.quandary.FliiikConstant;
import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.quandary.quandary.filter.LowPassFilterSmoothing;
import com.quandary.quandary.ui.FliiikHelper;

import java.util.ArrayList;
import java.util.List;

public class FliiikMoveDetector implements SensorEventListener {

    private static final long DEFAULT_BUFFER_LENGTH = (long)(3.5f * (long)Math.pow(10,9));
    private ArrayList<SensorBundle> mSensorBundles;

    private static final float DEFAULT_DISTANCE_THRESHOLD = 2.9f;
    private static final float DEFAULT_THRESHOLD_ACCELERATION = 2.7f;
    private static final int INTERVAL = 200;

    private static SensorManager mSensorManager;
    private static FliiikMoveDetector mSensorEventListener;

    private FliiikMoveDetector.OnFliiikMoveListener mFliiikMoveListener;

    private Object mLock;
    private float mThresholdAcceleration;
    private float mThresholdDistanceThreshold;
    private SensorBundle mLastSensorBundle;

    LowPassFilterSmoothing mLpSmoother;
    protected volatile float[] acceleration = new float[3];

    private Context mContext;

    List<FliiikGesture> mSupportedMoves;

    /**
     * Interface definition for a callback to be invoked when the device has performed one of fliiikmove.
     */
    public static interface OnFliiikMoveListener {
        /**
         * Called when the device has performed one of fliiikmove.
         */
        void OnFliiikMove(FliiikGesture move);
    }

    private FliiikMoveDetector(Context context, FliiikMoveDetector.OnFliiikMoveListener listener) {
        if (listener == null ||context == null ) {
            throw new IllegalArgumentException("FliikMove listener and context must not be null");
        }

        mContext = context;
        mFliiikMoveListener = listener;
        mSensorBundles = new ArrayList<SensorBundle>();
        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;
        mThresholdDistanceThreshold = DEFAULT_DISTANCE_THRESHOLD;
        mLpSmoother = new LowPassFilterSmoothing();
        mLastSensorBundle = null;
        mSupportedMoves = GesturesDatabaseHelper.getInstance(mContext).getAllEnabledGestures();
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
        mSensorEventListener = new FliiikMoveDetector(context, listener);

        return mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }


    public static boolean start() {
        if (mSensorManager != null && mSensorEventListener != null) {
            mSensorEventListener.resetSupportedMoves();
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

    public static FliiikMoveDetector getInstance () {
        return mSensorEventListener;
    }

    public static void updateConfiguration(float motionSensibility, float distanceSensibility) {
        mSensorEventListener.setConfiguration(motionSensibility,distanceSensibility);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        System.arraycopy(sensorEvent.values, 0, acceleration, 0, sensorEvent.values.length);
        acceleration = mLpSmoother.addSamples(acceleration);

        SensorBundle sensorBundle = new SensorBundle(acceleration[0], acceleration[1], acceleration[2], sensorEvent.timestamp);

        boolean checked = false;

        synchronized (mLock) {
            if (mLastSensorBundle == null) {
                mSensorBundles.clear();
                mSensorBundles.add(sensorBundle);
                mLastSensorBundle = sensorBundle;
            } else if (sensorBundle.getTimestamp() - mLastSensorBundle.getTimestamp() > INTERVAL) {

                double xForce = (sensorBundle.getXAcc() - mLastSensorBundle.getXAcc());
                double yForce = (sensorBundle.getYAcc() - mLastSensorBundle.getYAcc());
                double zForce = (sensorBundle.getZAcc() - mLastSensorBundle.getZAcc());

                double changeLength = xForce * xForce +
                        yForce * yForce +
                        zForce * zForce;
                double gForce = Math.sqrt(changeLength);
                if (gForce > mThresholdAcceleration) {
                    reduceBuffer(sensorBundle.getTimestamp());
                    mSensorBundles.add(sensorBundle);
                    mLastSensorBundle = sensorBundle;
                    checked = true;
                }
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

    private void setConfiguration(float motionSensibility, float distanceSensibility) {
        mThresholdAcceleration = motionSensibility;
        mThresholdDistanceThreshold = distanceSensibility;

        synchronized (mLock) {
            mLastSensorBundle = null;
            mSensorBundles.clear();
        }
    }

    private void reduceBuffer(long currentTimeStamp) {
        while (true) {
            if (mSensorBundles.size() == 0) return;
            SensorBundle sensorBundle = mSensorBundles.get(0);
            if (currentTimeStamp - sensorBundle.getTimestamp() > DEFAULT_BUFFER_LENGTH) {
                mSensorBundles.remove(0);
            } else {
                return;
            }
        }
    }

    private void performCheck() {
        synchronized (mLock) {

            if (mSupportedMoves == null || mSupportedMoves.size() == 0 || mSensorBundles.size() < 11) {
                return;
            }

            SensorBundle baseBundle = mSensorBundles.get(0);
            SensorBundle currentBundle = mSensorBundles.get(1);

            int xDirection = (currentBundle.getXAcc() - baseBundle.getXAcc()) > 0 ? 1 : -1;
            int yDirection = (currentBundle.getYAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
            int zDirection = (currentBundle.getZAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;

            double xDistance = currentBundle.getXAcc() - baseBundle.getXAcc();
            double yDistance = currentBundle.getYAcc() - baseBundle.getYAcc();
            double zDistance = currentBundle.getZAcc() - baseBundle.getZAcc();

            baseBundle = currentBundle;

            String sBody = "";

            for (int i=2; i<mSensorBundles.size(); i++) {
                currentBundle = mSensorBundles.get(i);

                int curXDirection = (currentBundle.getXAcc() - baseBundle.getXAcc()) > 0 ? 1 : -1;
                int curYDirection = (currentBundle.getYAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
                int curZDirection = (currentBundle.getZAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;

                if (curXDirection != xDirection) {
                    if (Math.abs(xDistance) > mThresholdDistanceThreshold) {
                        if (xDirection < 0) {
                            sBody = sBody + FliiikConstant.X_NEGATIVE;
                        } else {
                            sBody = sBody + FliiikConstant.X_POSITIVE;
                        }
                    }
                    xDistance = 0;
                }

                if (curYDirection != yDirection) {
                    if (Math.abs(yDistance) > mThresholdDistanceThreshold) {
                        if (yDirection < 0) {
                            sBody = sBody + FliiikConstant.Y_NEGATIVE;
                        } else {
                            sBody = sBody + FliiikConstant.Y_POSITIVE;
                        }
                    }

                    yDistance = 0;
                }

                if (curZDirection != zDirection) {
                    if (Math.abs(zDistance) > mThresholdDistanceThreshold) {
                        if (zDirection < 0) {
                            sBody = sBody + FliiikConstant.Z_NEGATIVE;
                        } else {
                            sBody = sBody + FliiikConstant.Z_POSITIVE;
                        }
                    }

                    zDistance = 0;
                }

                xDistance += currentBundle.getXAcc() - baseBundle.getXAcc();
                yDistance += currentBundle.getYAcc() - baseBundle.getYAcc();
                zDistance += currentBundle.getZAcc() - baseBundle.getZAcc();

                xDirection = curXDirection;
                yDirection = curYDirection;
                zDirection = curZDirection;

                baseBundle = currentBundle;
            }

            if (sBody == "") {
                return;
            }

            for (FliiikGesture gesture : mSupportedMoves) {
                if (FliiikHelper.compareActionString(sBody, gesture.action)) {
                    mFliiikMoveListener.OnFliiikMove(gesture);

                    mSensorBundles.clear();

                    return;
                }
            }
        }
    }

    private void resetSupportedMoves() {
        synchronized (mLock) {
            mSupportedMoves = GesturesDatabaseHelper.getInstance(mContext).getAllEnabledGestures();
        }
    }

    public List<SensorBundle> getBundleHistory() {
        return mSensorBundles;
    }
}
