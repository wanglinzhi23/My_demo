package intellif.enums;

/**
 * Created by yangboz on 11/5/15.
 */
public enum SourceTypes {
    /**
     * 0 摄像头
     * 1 视频
     * 2 图片
     */
    CAMERA(0), VIDEO(1), IMAGE(2);
    private int _value;

    SourceTypes(final int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
