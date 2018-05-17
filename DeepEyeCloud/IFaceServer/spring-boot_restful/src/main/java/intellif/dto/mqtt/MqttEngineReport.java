package intellif.dto.mqtt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangboz on 10/9/15.
 */
public class MqttEngineReport {

    private int msgType;
    private String ipaddr;
    private long port;
    private MsgBody msgBody;

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public long getPort() {
        return port;
    }

    public void setPort(long port) {
        this.port = port;
    }

    public MsgBody getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(MsgBody msgBody) {
        this.msgBody = msgBody;
    }

    @Override
    public String toString() {
        return "msgType:" + getMsgType() + ",ipaddr:" + getIpaddr() + ",port:" + getPort() + ",msgBody:" + getMsgBody();
    }
}
