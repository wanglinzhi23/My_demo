package intellif.enums;

/**
 * Created by yangboz on 10/9/15.
 */
public enum MqttTopicNames {
    /**
     * 0 报警频道 1 引擎状态 2申请一键布控
     */
    AlarmInfo("0/0"), EngineReport("IFaceEnginRpt"), ApplyTask("IFApplyTask"), Message("IFMessage");
    private String _value;

    MqttTopicNames(final String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}
