package com.quandary.quandary.Detector;

/**
 * Created by lim on 10/21/16.
 */

public class FliiikMove {
    FliiikMoveEnum movementCode;

    public FliiikMoveEnum getEventEnum() {
        return movementCode;
    }
    public FliiikMove(FliiikMoveEnum eventNum) {
        this.movementCode = eventNum;
    }

    public enum FliiikMoveEnum {
        TAP(0), CHOP(1), ROLL(2);

        private int value;

        FliiikMoveEnum(int value) {
            this.value = value;
        }
    }
}
