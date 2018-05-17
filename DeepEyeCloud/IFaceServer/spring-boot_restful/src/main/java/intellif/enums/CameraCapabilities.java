package intellif.enums;

/**
 * Created by yangboz on 11/7/15.
 */
public enum CameraCapabilities {
    //0:  抓拍模式 1： 取ipc流软解码 2：取IPC流硬解码 3：取rtsp流软解码 4：取rtsp流硬解码
    SNAPER(0), IPC_SOFT(1), IPC_HARD(2), RTSP_SOFT(3), RTSP_HARD(4);

    private int _value;

    CameraCapabilities(int value) {
        this._value = value;
    }

    public int getValue() {
        return _value;
    }
}
