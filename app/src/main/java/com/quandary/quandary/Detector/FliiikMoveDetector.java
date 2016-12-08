package com.quandary.quandary.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Switch;

import com.quandary.quandary.FliiikConstant;
import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.quandary.quandary.filter.LowPassFilterSmoothing;
import com.quandary.quandary.filter.MeanFilterSmoothing;
import com.quandary.quandary.filter.MedianFilterSmoothing;
import com.quandary.quandary.utils.FliiikHelper;
import com.quandary.quandary.utils.match.FliiikMatcher;

import java.util.ArrayList;
import java.util.List;

public class FliiikMoveDetector implements SensorEventListener {

    private static final long DEFAULT_BUFFER_LENGTH = (long)(3.5f * (long)Math.pow(10,9));

    private ArrayList<SensorFilterBundle> mAllFilterBundles;

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
    MeanFilterSmoothing mMfSmoother;
    MedianFilterSmoothing mMediaSmoother;
    protected volatile float[] acceleration = new float[3];

    private Context mContext;

    List<FliiikGesture> mSupportedMoves;

    double mXDistance = 0;
    double mYDistance = 0;
    double mZDistance = 0;

    int mXDirection =0;
    int mYDirection =0;
    int mZDirection =0;




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

        mAllFilterBundles = new ArrayList<SensorFilterBundle>();

        mLock = new Object();
        mThresholdAcceleration = DEFAULT_THRESHOLD_ACCELERATION;
        mThresholdDistanceThreshold = DEFAULT_DISTANCE_THRESHOLD;
        mLpSmoother = new LowPassFilterSmoothing();
        mMfSmoother = new MeanFilterSmoothing();
        mMediaSmoother = new MedianFilterSmoothing();

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
            mSensorEventListener.resetVariables();
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
//        acceleration = mMfSmoother.addSamples(acceleration);
//        acceleration = mMediaSmoother.addSamples(acceleration);


        SensorBundle sensorBundle = new SensorBundle(acceleration[0], acceleration[1], acceleration[2], sensorEvent.timestamp);

        boolean checked = false;

        synchronized (mLock) {
            if (mLastSensorBundle == null) {
                mAllFilterBundles.clear();
                mLastSensorBundle = sensorBundle;
            } else if (sensorBundle.getTimestamp() - mLastSensorBundle.getTimestamp() > INTERVAL) {
                checked = makeBuffer(sensorBundle, mLastSensorBundle);
                mLastSensorBundle = sensorBundle;
                reduceBuffer(sensorBundle.getTimestamp());
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
            resetVariables();
        }
    }

    private void resetVariables() {

        mLastSensorBundle = null;
        mAllFilterBundles.clear();

        mXDistance = 0;
        mYDistance = 0;
        mZDistance = 0;

        mXDirection =0;
        mYDirection =0;
        mZDirection =0;

        mLpSmoother.reset();
        mMediaSmoother.reset();
        mMfSmoother.reset();
    }

    private void reduceBuffer(long currentTimeStamp) {
        while (true) {
            if (mAllFilterBundles.size() == 0) break;
            SensorFilterBundle sensorBundle = mAllFilterBundles.get(0);
            if (currentTimeStamp - sensorBundle.getTimestamp() > DEFAULT_BUFFER_LENGTH) {
                mAllFilterBundles.remove(0);
            } else {
                break;
            }
        }
    }

    private boolean makeBuffer(SensorBundle currentBundle, SensorBundle baseBundle) {

        int xDirection = (currentBundle.getXAcc() - baseBundle.getXAcc()) > 0 ? 1 : -1;
        int yDirection = (currentBundle.getYAcc() - baseBundle.getYAcc()) > 0 ? 1 : -1;
        int zDirection = (currentBundle.getZAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;

        boolean addedResult = false;

        if (mXDirection != xDirection) {
            if (Math.abs(mXDistance) > mThresholdDistanceThreshold) {
                if (xDirection < 0) {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.X_POSITIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                } else {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.X_NEGATIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                }

                addedResult = true;
                mXDistance = 0;
            }
            mXDirection = xDirection;
        }

        if (mYDirection != yDirection) {
            if (Math.abs(mYDistance) > mThresholdDistanceThreshold) {
                if (yDirection < 0) {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.Y_POSITIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                } else {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.Y_NEGATIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                }

                addedResult = true;
                mYDistance = 0;
            }
            mYDirection = yDirection;
        }

        if (mZDirection != zDirection) {
            if (Math.abs(mZDistance) > mThresholdDistanceThreshold) {
                if (zDirection < 0) {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                } else {
                    mAllFilterBundles.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE, mLastSensorBundle.getTimestamp(), currentBundle.getXAcc(), currentBundle.getYAcc(), currentBundle.getZAcc()));
                }

                addedResult = true;
                mZDistance = 0;
            }
            mZDirection = zDirection;
        }


        mXDistance += currentBundle.getXAcc() - baseBundle.getXAcc();
        mYDistance += currentBundle.getYAcc() - baseBundle.getYAcc();
        mZDistance += currentBundle.getZAcc() - baseBundle.getZAcc();

        return addedResult;
    }

    private void performCheck() {
        synchronized (mLock) {

            if (mSupportedMoves == null || mSupportedMoves.size() == 0 || mAllFilterBundles.size() < 12) {
                return;
            }

            FliiikGesture detectGesture = null;
            int currDetectResult = FliiikMatcher.FLIIIK_NOT_FOUND;

            for (FliiikGesture gesture : mSupportedMoves) {

                int result = FliiikMatcher.match(gesture.action, mAllFilterBundles);
                if (result == FliiikMatcher.FLIIIK_NOT_FOUND) continue;

                if (result == 0)
                {
                    mFliiikMoveListener.OnFliiikMove(gesture);
                    resetVariables();
                    return;
                }

                if (currDetectResult == FliiikMatcher.FLIIIK_NOT_FOUND) {
                    detectGesture = gesture;
                    currDetectResult = result;
                } else if (currDetectResult > result) {
                    detectGesture = gesture;
                    currDetectResult = result;
                }
            }

            if (detectGesture != null) {
                mFliiikMoveListener.OnFliiikMove(detectGesture);
                resetVariables();
                return;
            }
        }
    }

    private void resetSupportedMoves() {
        synchronized (mLock) {
            mSupportedMoves = GesturesDatabaseHelper.getInstance(mContext).getAllEnabledGestures();
        }
    }

    public ArrayList<SensorFilterBundle> getAllBundleHistory() {
        return mAllFilterBundles;
    }
}
