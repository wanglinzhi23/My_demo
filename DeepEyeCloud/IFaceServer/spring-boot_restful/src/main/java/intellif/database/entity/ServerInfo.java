package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = GlobalConsts.T_NAME_SERVER_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class ServerInfo extends GeometryInfoBase implements Serializable {

    private static final long serialVersionUID = -7351094243602422397L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 服务器名称
    private String serverName;
    // 服务器IP
    private String ip;
    // 服务器端口
    private int port;
    // 服务器所在地址
    private String address;
    // 服务器任务峰值
    private int peak;
    // 服务器状态
    private int status;//@see:Enum ServerStatus
     // 1 中心管理集群
    private int type;
    
    public ServerInfo() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "id=" + id +
                ", serverName='" + serverName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                ", peak=" + peak +
                ", status=" + status +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPeak() {
        return peak;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
