package intellif.enums;

/**
 * Created by yangboz on 10/12/15.
 */
public enum ServerStatus {

    OFFLINE(0), ONLINE_VALID(1), ONLINE_INVALID(2);//未启动,启动切正常,启动且异常
    private int _value;

    ServerStatus(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }

    public int getRandValue() {
        return (int) Math.floor(Math.random() * 90000000) + 10000000;//FIXME:Without differ value also trigger @PreUpdate
        // To trigger JPA @PreUpdate with differ value;
    }
}
