package intellif.enums;

/**
 * Created by yangboz on 11/17/15.
 */
public enum ResourceScopes {
    READ("read"), WRITE("write");
    private String _value;

    ResourceScopes(final String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}
