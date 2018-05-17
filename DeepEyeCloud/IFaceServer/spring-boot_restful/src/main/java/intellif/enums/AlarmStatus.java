package intellif.enums;

/**
 * Created by yangboz on 10/8/15.
 */
public enum AlarmStatus {
    /**
     * //0:未查看,1:已查看/已处理,“正确”（即确为同一人）还是“错误”（非同一人）)
     */
    IGNORE(0), VALID(1), INVALID(2);
    private int _value;

    AlarmStatus(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
