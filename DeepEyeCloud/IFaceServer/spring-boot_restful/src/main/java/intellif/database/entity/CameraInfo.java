package intellif.database.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import intellif.consts.GlobalConsts;
import intellif.core.tree.annotation.NodeType;
import intellif.core.tree.annotation.PreviousClass;
import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.itf.Zone;

/**
 * The Class CameraInfo.
 */
@Entity
@PreviousClass(Area.class)
@NodeType(value = ZoneConstant.NODE_TYPE_CAMERA, sort = 60)
@Table(name = GlobalConsts.T_NAME_CAMERA_INFO, schema = GlobalConsts.INTELLIF_BASE)
public class CameraInfo extends GeometryInfoBase implements Serializable, TreeNode, Cloneable, Zone {

    

    /**
     * 
     */
    private static final long serialVersionUID = -80425507833164964L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // 编号id
    private long id;

    // 城市
    private String city;
    // 区县
    private String county;
    // 详细地址
    private String addr;
    // 摄像头rtsp流url
    private String rtspuri;
    // 摄像头实时链接url
    private String uri;// Uniform OauthResource Identifier
    // 摄像头状态
    private int status;// off,on
    // 端口
    private int port = 8000;
    // 缩写
    private String shortName;
    // 名字
    private String name;
    // 管理用户名
    @JsonIgnore
    private String username;
    // 管理密码
    @JsonIgnore
    private String password;
    // 摄像头能力
    private int capability;// 0: 抓拍模式 1： 取ipc流软解码 2：取IPC流硬解码 3：取rtsp流软解码
                           // 4：取rtsp流硬解码
    // 0/1:是否采集;
    private int type = 0;
    // 封面
    private String cover;
    // 所属派出所
    private Long stationId;
    // 0/1:是否在警局内
    private long inStation = 0;
    // 编码
    private String code = "";
    //1 2 3 类高清
    private int cType = 1;
    
    private int snap = 0;//0 正常 -1正常
    

    // 父节点Id
    @Transient
    private Long parentId = 0L;
    // 孩子列表
    @Transient
    private List<TreeNode> childList = new ArrayList<>();
    // 下一张表节点列表
    @Transient
    private List<TreeNode> nextList = new ArrayList<>();
    // 是否有孩子
    @Transient
    private boolean hasChild = false;
    // 是否有下一代
    @Transient
    private boolean hasNext = false;
    // 选择状态
    @Transient
    private String choice = ZoneConstant.ZONE_CHOICE_NONE;
    // 叶子数量
    @Transient
    private int countLeaf = 0;
    //角色类型名
    @Transient
    private String areaName;
    
    @Override
    public int getCountLeaf() {
        return countLeaf;
    }

    @Override
    public void setCountLeaf(int countLeaf) {
        this.countLeaf = countLeaf;
    }

    @Override
    public String getChoice() {
        return this.choice;
    }

    @Override
    public void setChoice(String choice) {
        this.choice = choice;
    }
    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public Long getPreviousId() {
        return stationId;
    }

    @Override
    public List<TreeNode> getChildList() {
        return childList;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    @Override
    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    @Override
    public List<TreeNode> getNextList() {
        return nextList;
    }

    @Override
    public void setNextList(List<TreeNode> nextList) {
        this.nextList = nextList;
    }

    @Override
    public boolean getHasChild() {
        return hasChild;
    }

    @Override
    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    @Override
    public boolean getHasNext() {
        return hasNext;
    }

    @Override
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public TreeNode copyWithoutTreeInfo() {
        try {
            CameraInfo treeNode = (CameraInfo) super.clone();
            treeNode.childList = new ArrayList<>();
            treeNode.nextList = new ArrayList<>();
            return treeNode;
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public CameraInfo() {
    }

    public CameraInfo(long i) {
        super();
        this.id = i;
    }

    public CameraInfo(OtherCameraInfo oci) {
        super();
        this.id = oci.getId();
        this.addr = oci.getAddr();
        this.capability = oci.getCapability();
        this.city = oci.getCity();
        this.code = oci.getCode();
        this.county = oci.getCounty();
        this.cover = oci.getCover();
        this.geometry = oci.getGeometry();
        this.geoString = oci.getGeoString();
        this.inStation = oci.getInStation();
        this.name = oci.getName();
        this.password = oci.getPassword();
        this.port = oci.getPort();
        this.rtspuri = oci.getRtspuri();
        this.shortName = oci.getShortName();
        this.stationId = oci.getStationId();
        this.status = oci.getStatus();
        this.type = oci.getType();
        this.uri = oci.getUri();
        this.username = oci.getUsername();
        this.cType = oci.getcType();
    }
    
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CameraInfo [id=");
        builder.append(id);
        builder.append(", city=");
        builder.append(city);
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
        builder.append(", code=");
        builder.append(code);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
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
    
    @Autowired
    public Long zoneId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getSnap() {
        return snap;
    }

    public void setSnap(int snap) {
        this.snap = snap;
    }
    
}
