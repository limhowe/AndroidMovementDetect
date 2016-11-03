package com.quandary.quandary.detector.move;

import com.quandary.quandary.detector.SensorBundle;

/**
 * Created by lim on 10/21/16.
 */

public class FliiikMove {
    FliiikMoveEnum movementCode;
    protected float xDistance;
    protected float yDistance;
    protected float zDistance;

    public enum FliiikMoveEnum {
        TAP(0), CHOP(1), ROLL(2);

        private int value;
        FliiikMoveEnum(int value) {
            this.value = value;
        }
    }

    public enum FliiikMoveStatus {
        COMPLETE(0), PROGRESS(1), BADREQUEST(2);

        private int value;
        FliiikMoveStatus(int value) {
            this.value = value;
        }
    }

    public FliiikMove(FliiikMoveEnum eventNum) {
        this.movementCode = eventNum;
        xDistance = 0;
        yDistance = 0;
        zDistance = 0;
    }

    public FliiikMoveEnum getEventEnum() {
        return movementCode;
    }

    public void resetMove() {}

    public boolean isCompleted() {
        return false;
    }

    public FliiikMoveStatus performCheck(SensorBundle bundle) {
        return FliiikMoveStatus.COMPLETE;
    }

    @Override
    public String toString() {
        switch (movementCode) {
            case TAP:
                return "TAP";
            case CHOP:
                return "CHOP";
            case ROLL:
                return "ROLL";
            default:
                return "None";
        }
    }

}
