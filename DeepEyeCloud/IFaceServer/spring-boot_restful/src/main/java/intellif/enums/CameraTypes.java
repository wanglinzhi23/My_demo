package intellif.enums;

/**
 * Created by yangboz on 11/5/15.
 */
public enum CameraTypes {
    /**
     * //0:不采集,1:采集
     */
    NONE(0), SNAPER(1);
    private int _value;

    CameraTypes(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
