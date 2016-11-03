package com.quandary.quandary.detector.move;

import android.util.Log;

import com.quandary.quandary.detector.SensorBundle;

import java.util.Arrays;

/**
 * Created by lim on 10/22/16.
 */

public class FliiikMoveTap extends FliiikMove {

    protected float mThreashHoldXrangeMax = 2.0f; //x should not change
    protected float mThreashHoldYrangeMax = 12.0f; // y should change half of z
    protected float mThreashHoldZrangeMax = 12.0f; // z should change

    protected float mThreashHoldYrangeMin = 2.0f; //x should not change
    protected float mThreashHoldZrangeMin = 2.0f; // y should change half of z

    protected float mThreashHoldXAcceleration = 0.15f; //indicate max change
    protected float mThreashHoldYAcceleration = 0.1f;
    protected float mThreashHoldZAcceleration = 0.2f;

    private Object mLock;

    /*
       array of directions
       each element can only be
        0 : indicate start - next direction can be 1 or -1
        1 : next direction can be -1
        -1 : next direction can be 1
     */

    private int[] mDirections = { 0 , 0};
    private int[][] mDistances = {
            { 0 , 0 , 0 },
            { 0 , 0 , 0 }
    };
    protected int mCursor = -1;

    public FliiikMoveTap() {
        super(FliiikMoveEnum.TAP);

        mLock = new Object();

        resetMove();
    }

    @Override
    public void resetMove() {
        mCursor = -1;

        mDirections[0] = 0;
        mDirections[1] = 0;
        mDistances[0][0] = 0; mDistances[0][1] = 0; mDistances[0][2] = 0;
        mDistances[1][0] = 0; mDistances[1][1] = 0; mDistances[1][2] = 0;

        xDistance = 0;
        yDistance = 0;
        zDistance = 0;
    }

    public void resetTurn() {
        xDistance = 0;
        yDistance = 0;
        zDistance = 0;
    }

    @Override
    public boolean isCompleted() {
        return mDirections[1] != 0;
    }

    private boolean checkMaxSanity(SensorBundle sensorBundle) {
        Log.i("MOVE-TAP :", "Check MaxSanity");

        int yFlag = (sensorBundle.getYAcc() > 0) ? 1 : -1;
        int zFlag = (sensorBundle.getZAcc() > 0) ? 1 : -1;

        xDistance = xDistance + sensorBundle.getXAcc();
        yDistance = yDistance + sensorBundle.getYAcc();
        zDistance = zDistance + sensorBundle.getZAcc();

        if (xDistance > mThreashHoldXrangeMax || xDistance < -mThreashHoldXrangeMax) {
            Log.i("MOVE-TAP :", "MAX Sanity false - X MAX");
            return false;
        }

        if ((yFlag == 1 && yDistance > mThreashHoldYrangeMax) || (yFlag == -1 && yDistance < -mThreashHoldYrangeMax)) {
            Log.i("MOVE-TAP :", "MAX Sanity false - Y MAX");
            return false;
        }

        if ((zFlag == -1 && zDistance > mThreashHoldZrangeMax) || (zFlag == -1 && zDistance < -mThreashHoldZrangeMax)) {
            Log.i("MOVE-TAP :", "MAX Sanity false - Z MAX");
            return false;
        }

        Log.i("MOVE-TAP :", "MAX Sanity true");

        return true;
    }

    private boolean checkMinSanity() {

        Log.i("MOVE-TAP :", "Check MinSanity");

        if (mCursor == -1) return false;

        int zFlag = mDirections[mCursor];

        if ((zFlag == 1 && yDistance < mThreashHoldYrangeMin) || ( zFlag == -1 && yDistance > -mThreashHoldYrangeMin)) {
            Log.i("MOVE-TAP :", "Min Sanity false - Y MIN");
            return false;
        }

        if ( (zFlag == 1 && zDistance < mThreashHoldZrangeMin) || (zFlag == -1 && yDistance > -mThreashHoldZrangeMin)) {
            Log.i("MOVE-TAP :", "Min Sanity false - Z MIN");
            return false;
        }

        return true;
    }

    private boolean checkVelocitySanity(SensorBundle sensorBundle) {

        int yFlag = (sensorBundle.getYAcc() > 0) ? 1 : -1;
        int zFlag = (sensorBundle.getZAcc() > 0) ? 1 : -1;

        if ( ((yFlag == 1 && sensorBundle.getYAcc() > mThreashHoldYAcceleration) || (yFlag == -1 && sensorBundle.getYAcc() < -mThreashHoldYAcceleration)) &&
           ((zFlag == 1 && sensorBundle.getZAcc() > mThreashHoldZAcceleration) || (zFlag == -1 && sensorBundle.getZAcc() < -mThreashHoldZAcceleration)) &&
            (sensorBundle.getXAcc() < mThreashHoldXAcceleration && sensorBundle.getXAcc() > -mThreashHoldXAcceleration)) {
            return true;
        }

        return false;
    }

    @Override
    public FliiikMoveStatus performCheck(SensorBundle sensorBundle) {

        synchronized (mLock) {
            if (!checkVelocitySanity(sensorBundle)) {
                return FliiikMoveStatus.BADREQUEST;
            }

            int zFlag = (sensorBundle.getZAcc() > 0) ? 1 : -1;
            int yFlag = (sensorBundle.getYAcc() > 0) ? 1 : -1;

            if (zFlag != yFlag) {

                Log.i("MOVE-TAP :", "Reject for direction mismatch" + sensorBundle.toString());

                resetMove();
                return FliiikMoveStatus.BADREQUEST;
            }

            Log.i("MOVE-TAP :", sensorBundle.toString());


            if (mCursor == -1) {
                mCursor = 0;

                if (checkMaxSanity(sensorBundle)) {
                    mDirections[mCursor] = zFlag;
                    return FliiikMoveStatus.PROGRESS;
                } else {
                    resetMove();
                    return FliiikMoveStatus.BADREQUEST;
                }
            }

            if (zFlag != mDirections[mCursor]) {

                Log.i("MOVE-TAP :", "Checking direction change !!!!" + this.toString());

                if (!checkMinSanity()) {
                    resetMove();
                    return FliiikMoveStatus.BADREQUEST;
                }

                mCursor++;
                if (mCursor > 1) {
                    resetMove(); //need to verify
                    return FliiikMoveStatus.COMPLETE;
                } else {
                    resetTurn();
                    if (checkMaxSanity(sensorBundle)) {
                        mDirections[mCursor] = zFlag;
                        return FliiikMoveStatus.PROGRESS;
                    } else {
                        resetMove();
                        return FliiikMoveStatus.BADREQUEST;
                    }
                }
            } else {
                if (checkMaxSanity(sensorBundle)) {
                    return FliiikMoveStatus.PROGRESS;
                } else {
                    resetMove();
                    return FliiikMoveStatus.BADREQUEST;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "FliiikTap{" +
                "xDistance=" + xDistance +
                ", yDistance=" + yDistance +
                ", zDistance=" + zDistance +
                ", mCursor=" + mCursor +
                ", Direction =" + Arrays.toString(mDirections) +
                '}';
    }
}
