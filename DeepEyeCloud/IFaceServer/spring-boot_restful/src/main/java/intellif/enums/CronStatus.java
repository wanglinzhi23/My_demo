package intellif.enums;

/**
 * Created by yangboz on 10/22/15.
 */
public enum CronStatus {
    /**
     * 0 无计划
     * 1 已执行
     * 2 未执行
     */
    NONE(0), EXECUTED(1), STAND_BY(2);
    private int _value;

    CronStatus(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
