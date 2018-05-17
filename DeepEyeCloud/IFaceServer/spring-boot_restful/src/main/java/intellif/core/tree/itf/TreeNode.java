package intellif.core.tree.itf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.core.tree.util.TreeUtil;
import intellif.zoneauthorize.common.ZoneConstant;

public interface TreeNode extends Serializable, Comparable<TreeNode> {

    /**
     * ID
     * 
     * @return
     */
    @JsonSerialize(using = ToStringSerializer.class)
    Long getId();

    /**
     * 获取父节点ID
     * 
     * @return
     */
    @JsonSerialize(using = ToStringSerializer.class)
    Long getParentId();

    /**
     * 获取上一级表ID
     * 
     * @return
     */
    @JsonSerialize(using = ToStringSerializer.class)
    default Long getPreviousId() {
        return 0L;
    }

    /**
     * 获取名字
     * 
     * @return
     */
    String getName();

    /**
     * 获取排序
     * 
     * @return
     */
    default int getSort() {
        return 0;
    }


    /**
     * 节点类型, 外键一般为nodeType() + "_id"
     * 
     * @return
     */
    @Transient
    default String getNodeType() {
        return TreeUtil.nodeType(this.getClass());
    }

    /**
     * 设置下一个表的列表
     * 
     * @return
     */
    default void setNextList(List<TreeNode> nextList) {

    }

    /**
     * 获取下一个表的列表
     * 
     * @return
     */
    @Transient
    default List<TreeNode> getNextList() {
        return new ArrayList<>();
    }

    /**
     * 设置孩子节点
     * 
     * @return
     */
    default void setChildList(List<TreeNode> childList) {
        
    }

    /**
     * 获取孩子节点
     * 
     * @return
     */
    @Transient
    List<TreeNode> getChildList();

    /**
     * 复制节点并忽略其孩子列表
     * 
     * @return
     */
    TreeNode copyWithoutTreeInfo();

    /**
     * 是否有下一张表节点
     * 
     * @return
     */
    @Transient
    default boolean getHasNext() {
        return false;
    }

    /**
     * 设置是否有下一张表的节点
     */
    default void setHasNext(boolean hasNext) {

    }

    /**
     * 获取选择状态
     * 
     * @return
     */
    @Transient
    default String getChoice() {
        return ZoneConstant.ZONE_CHOICE_NONE;
    }

    /**
     * 设置选择状态
     */
    default void setChoice(String choice) {

    }

    /**
     * 是否有孩子节点
     * 
     * @return
     */
    @Transient
    boolean getHasChild();

    /**
     * 设置是否有孩子节点
     */
    void setHasChild(boolean hasChild);

    /**
     * 获取叶子个数
     * 
     * @param count
     */
    @Transient
    default int getCountLeaf() {
        return 0;
    }

    /**
     * 设置叶子个数
     * 
     * @param count
     */
    default void setCountLeaf(int count) {
    }

    /**
     * 实现比较接口
     */
    default int compareTo(TreeNode o) {
        if (null == o) {
            return Integer.MIN_VALUE;
        }
        int nodeTypeSort = TreeUtil.nodeTypeSort(this.getClass()) - TreeUtil.nodeTypeSort(o.getClass());
        if (0 != nodeTypeSort) {
            return nodeTypeSort;
        }
        return this.getSort() - o.getSort();
    }
}
