package intellif.enums;

/**
 * Created by yangboz on 10/9/15.
 */
public enum TaskTypes {
    /**
     * 1>>0普通任务,1>>1 计划任务1>>2 采集小图
     */
    NORMAL(0), SCHEDULE(1);
    private int _value;

    TaskTypes(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
