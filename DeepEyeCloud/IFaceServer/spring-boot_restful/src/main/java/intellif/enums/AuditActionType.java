package intellif.enums;

/**
 * Created by yangboz on 12/2/15.
 */
public enum AuditActionType {

    IMAGE("image"), PERSON("person"), ALARM("alarm");

    private String _value;


    AuditActionType(String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}
