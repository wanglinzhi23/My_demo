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

import intellif.consts.GlobalConsts;
import intellif.core.tree.annotation.NodeType;
import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.itf.Zone;

@Entity
@NodeType(value = ZoneConstant.NODE_TYPE_DISTRICT, sort = 40)
@Table(name = GlobalConsts.T_NAME_DISTRICT, schema = GlobalConsts.INTELLIF_BASE)
public class DistrictInfo extends GeometryInfoBase implements Serializable, TreeNode, Cloneable, Zone {

    private static final long serialVersionUID = 4207039665227406668L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 区域编号
    private String districtNo;
    // 区域名称
    private String districtName;
    // 是否本地区域
    private Integer local;
    // 父节点Id
    private Long parentId = 0L;
    // 排序字段，按从小到大顺序
    private int sort = 0;
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
            DistrictInfo treeNode = clone();
            treeNode.childList = new ArrayList<>();
            treeNode.nextList = new ArrayList<>();
            return treeNode;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public DistrictInfo() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDistrictNo() {
        return districtNo;
    }

    public void setDistrictNo(String districtNo) {
        this.districtNo = districtNo;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getLocal() {
        return local;
    }

    public void setLocal(Integer local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return "DistrictInfo [id=" + id + ", districtNo=" + districtNo + ", districtName=" + districtName + "]";
    }

    @Override
    public Long zoneId() {
        return id;
    }

    @Override
    public String getName() {
        return districtName;
    }

    @Override
    public DistrictInfo clone() {
        try {
            return (DistrictInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
