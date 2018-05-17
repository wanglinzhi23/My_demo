package intellif.enums;

/**
 * Created by yangboz on 12/1/15.
 */
public enum IFaceSdkTypes {
    //
    JNI(0), THRIFT(1);
    private int _value;

    IFaceSdkTypes(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
