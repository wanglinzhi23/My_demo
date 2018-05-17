package intellif.enums;

public enum TaskStatus {
    /**
     * 0 未启动 1 启动 2 停止 3 异常
     */
    UNSTART(0), START(1), STOP(2), ERROR(3);
    private int _value;

    TaskStatus(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
