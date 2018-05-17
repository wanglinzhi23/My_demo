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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;

import intellif.consts.GlobalConsts;
import intellif.core.tree.annotation.NodeType;
import intellif.core.tree.annotation.PreviousClass;
import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.itf.Zone;

@Entity
@PreviousClass(DistrictInfo.class)
@NodeType(value = ZoneConstant.NODE_TYPE_AREA, sort = 50)
@Table(name = GlobalConsts.T_NAME_AREA, schema = GlobalConsts.INTELLIF_BASE)
public class Area extends GeometryInfoBase implements Serializable, TreeNode, Cloneable, Zone {


    /**
     * 
     */
    private static final long serialVersionUID = 4207039665227406668L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 区域编号
    private String areaNo;
    // 区域名称
    private String areaName;
    // 区域人数限制阈值
    private long personThreshold;
    // 行政区ID
    private Long districtId = 1L;//默认为1
    // 父节点Id
    private Long parentId = 0L;
    //备注
    private String remark;
    //创建用户ID
    private long userId;
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

    @Override
    public Long getPreviousId() {
        return districtId;
    }

    @Override
    public String getName() {
        return areaName;
    }

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
    public List<TreeNode> getChildList() {
        return childList;
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
            Area treeNode = (Area) super.clone();
            treeNode.childList = new ArrayList<>();
            treeNode.nextList = new ArrayList<>();
            return treeNode;
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public Area() {
    }

    public Area(OtherArea oa) {
        this.areaName = oa.getAreaName();
        this.areaNo = oa.getAreaNo();
        this.districtId = oa.getDistrictId();
        this.geometry = oa.getGeometry();
        this.geoString = oa.getGeoString();
        this.id = oa.getId();
        this.personThreshold = oa.getPersonThreshold();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public long getPersonThreshold() {
        return personThreshold;
    }

    public void setPersonThreshold(long personThreshold) {
        this.personThreshold = personThreshold;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Autowired
    public Long zoneId() {
        return id;
    }

    @Override
    public String toString() {
        return "Area [id=" + id + ", areaNo=" + areaNo + ", areaName=" + areaName + ", personThreshold=" + personThreshold + "]" + ", districtId=" + districtId
                + "]";
    }
}
