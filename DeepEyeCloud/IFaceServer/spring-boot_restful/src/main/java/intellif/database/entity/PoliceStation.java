package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.core.tree.itf.PathTreeNode;
import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.common.ZoneConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = GlobalConsts.T_NAME_POLICE_STATION,schema=GlobalConsts.INTELLIF_BASE)
public class PoliceStation extends GeometryInfoBase implements TreeNode, Serializable,Cloneable {

    private static final long serialVersionUID = -6881932137544888832L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 派出所编号
    private String stationNo;
    // 派出所名称
    private String stationName;
    // 区域人数限制阈值
    private long personThreshold;
    // 父单位id
    private Long parentId;
    //可配置全区域搜索用户个数
    private int specialTotalNum = -1;
    //已使用全区域搜索用户个数
    private int specialUseNum = -1;
    //用户个数
    private int userCount = -1;
    @Transient
    private List<TreeNode> childList;
    // 是否有孩子
    @Transient
    private boolean hasChild = false;
    //获取选择状态
    @Transient
    private String choice = ZoneConstant.ZONE_CHOICE_NONE;

    public PoliceStation() {
        
    }
    
    public void init(){
        this.specialTotalNum = 0;
        this.specialUseNum = 0;
        this.userCount = 0;
    }

    public void update(PoliceStation ps){
       if(null != ps.getGeometry()){
           this.geometry = ps.getGeometry();
       }
       if(null != ps.getGeoString() && ps.getGeoString().trim().length() > 0){
           this.geoString = ps.getGeoString();
       }
       if(null != ps.getStationName() && ps.getStationName().trim().length() > 0){
           this.stationName = ps.getStationName();
       }
       if(null != ps.getStationNo() && ps.getStationNo().trim().length() > 0){
           this.stationNo = ps.getStationNo();
       }
       if(0 <= ps.getPersonThreshold()){
           this.personThreshold = ps.getPersonThreshold();
       }
       if(0 <= ps.getSpecialTotalNum()){
           this.specialTotalNum = ps.getSpecialTotalNum();
       }
       if (null != ps.getParentId())
           this.parentId = ps.getParentId();

    }
    
    
    @Override
	public String toString() {
		return "PoliceStation [id=" + id + ", stationNo=" + stationNo
				+ ", stationName=" + stationName + ", personThreshold="
				+ personThreshold +", specialTotalNum="
		                + specialTotalNum+", specialUseNum="
		                        + specialUseNum+ "]";
	}

    @Override
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

	public long getPersonThreshold() {
		return personThreshold;
	}

	public void setPersonThreshold(long personThreshold) {
		this.personThreshold = personThreshold;
	}

	@Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

  
    public int getSpecialTotalNum() {
        return specialTotalNum;
    }

    public void setSpecialTotalNum(int specialTotalNum) {
        this.specialTotalNum = specialTotalNum;
    }

    public int getSpecialUseNum() {
        return specialUseNum;
    }

    public void setSpecialUseNum(int specialUseNum) {
        this.specialUseNum = specialUseNum;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    @Override
	public PoliceStation clone() {   
        try {   
            return (PoliceStation) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }

    @Override
    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    @Override
    public List<TreeNode> getChildList() {
        return this.childList;
    }

    @Override
    public TreeNode copyWithoutTreeInfo() {
        PoliceStation treeNode = (PoliceStation) clone();
        treeNode.childList = new ArrayList<>();
        return treeNode;
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
    public String getName() {
        return stationName;
    }
    
    @Override
    public String getChoice() {
        return choice;
    }

    @Override
    public void setChoice(String choice) {
        this.choice = choice;
    }

}
