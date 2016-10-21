package com.quandary.quandary.detector;

/**
 * Created by lim on 10/21/16.
 */

public class FliiikMove {
    FliiikMoveEnum movementCode;
    String desc;

    public FliiikMoveEnum getEventEnum() {
        return movementCode;
    }
    public String getDesc() {
        return desc;
    }
    public FliiikMove(FliiikMoveEnum eventNum) {
        this.movementCode = eventNum;
    }
    public FliiikMove(String description) {
        this.movementCode = FliiikMoveEnum.TAP;
        this.desc = description;
    }

    public enum FliiikMoveEnum {
        TAP(0), CHOP(1), ROLL(2);

        private int value;

        FliiikMoveEnum(int value) {
            this.value = value;
        }
    }
}
