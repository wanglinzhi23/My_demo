package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The Class CameraInfo.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_OTHER_CAMERA_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class OtherCameraInfo extends GeometryInfoBase implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -4098341125587400236L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // 编号id
    private long id;

    // 城市
    private String city;
    // 编码
    private String code;
    // 区县
    private String county;
    // 详细地址
    private String addr;
    // 摄像头rtsp流url
    private String rtspuri;
    // 摄像头实时链接url
    private String uri;//Uniform OauthResource Identifier
    // 摄像头状态
    private int status;//off,on
    // 端口
    private int port;
    // 缩写
    private String shortName;
    // 名字
    private String name;
    // 管理用户名
    private String username;
    // 管理密码
    private String password;
    // 摄像头能力
    private int capability;//0:  抓拍模式 1： 取ipc流软解码 2：取IPC流硬解码 3：取rtsp流软解码 4：取rtsp流硬解码
    // 0/1:是否采集;
    private int type = 0;
    // 封面
    private String cover;
    // 所属派出所
    private long stationId;
    // 0/1:是否在警局内
    private long inStation = 0;
    //1 2 3 类高清
    private int cType;
    public OtherCameraInfo() {
    }

    public OtherCameraInfo(long i) {
		super();
		this.id=i;
	}



    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OtherCameraInfo [id=");
        builder.append(id);
        builder.append(", city=");
        builder.append(city);
        builder.append(", code=");
        builder.append(code);
        builder.append(", county=");
        builder.append(county);
        builder.append(", addr=");
        builder.append(addr);
        builder.append(", rtspuri=");
        builder.append(rtspuri);
        builder.append(", uri=");
        builder.append(uri);
        builder.append(", status=");
        builder.append(status);
        builder.append(", port=");
        builder.append(port);
        builder.append(", shortName=");
        builder.append(shortName);
        builder.append(", name=");
        builder.append(name);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append(", capability=");
        builder.append(capability);
        builder.append(", type=");
        builder.append(type);
        builder.append(", cover=");
        builder.append(cover);
        builder.append(", stationId=");
        builder.append(stationId);
        builder.append(", inStation=");
        builder.append(inStation);
        builder.append("]");
        return builder.toString();
    }

    public String getRtspuri() {
        return rtspuri;
    }

    public void setRtspuri(String rtspuri) {
        this.rtspuri = rtspuri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

	public long getInStation() {
		return inStation;
	}

	public void setInStation(long inStation) {
		this.inStation = inStation;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
