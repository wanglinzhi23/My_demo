package intellif.core.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;






import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;

public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConcurrentHashMap<Class<? extends TreeNode>, ConcurrentHashMap<Long, TreeNode>> map;

    private List<TreeNode> rootList;

    private List<Class<? extends TreeNode>> classList;

    public <T extends TreeNode> List<T> findAllWithoutTreeInfo(Class<? extends TreeNode> clazz) {
        List<T> retList = new ArrayList<>();
        for (TreeNode node : map.get(clazz).values()) {
            retList.add((T) node.copyWithoutTreeInfo());
        }
        Collections.sort(retList);
        return retList;
    }
  
    protected Tree(ConcurrentHashMap<Class<? extends TreeNode>, ConcurrentHashMap<Long, TreeNode>> map, List<TreeNode> rootList, List<Class<? extends TreeNode>> classList) {
        this.map = map;
        if(null == map.get(Area.class)){
            map.put(Area.class, new ConcurrentHashMap<>());
        }
        if(null == map.get(CameraInfo.class)){
            map.put(CameraInfo.class, new ConcurrentHashMap<>());
        }
        this.rootList = rootList;
        this.classList = classList;
    }

    public List<TreeNode> getRootList() {
        return Lists.newArrayList(rootList);
    }
    
    public List<TreeNode> getTreeNodeList(Class<? extends TreeNode> filterClass) {
        List<TreeNode> retList = new ArrayList<>();
        if (null == filterClass) {
            for (Map<Long, TreeNode> temp : map.values()) {
                retList.addAll(temp.values());
            }
            return retList;
        }
        if (null != map.get(filterClass)) {
            retList.addAll(map.get(filterClass).values());
        }
        Collections.sort(retList);
        return retList;
    }
    
    public List<TreeNode> getTreeNodeListWithoutTreeInfo(Class<? extends TreeNode> filterClass) {
        List<TreeNode> treeNodeList = getTreeNodeList(filterClass);
        List<TreeNode> retList = new ArrayList<>();
        for (TreeNode treeNode : treeNodeList) {
            retList.add(treeNode.copyWithoutTreeInfo());
        }
        Collections.sort(retList);
        return retList;
    }

    public List<Class<? extends TreeNode>> getClassList() {
        return Lists.newArrayList(classList);
    }

    public boolean isOffspring(Class<? extends TreeNode> forefatherClass, Long forefatherId, Class<? extends TreeNode> offspringClass,
            Long offspringForefatherId) {
        if (null == forefatherClass || null == forefatherId || null == offspringClass || null == offspringForefatherId || !classList.contains(forefatherClass)
                || !classList.contains(offspringClass)) {
            return false;
        }

        TreeNode offspringNode = this.treeNode(offspringClass, offspringForefatherId);
        TreeNode forefatherNode = this.treeNode(forefatherClass, forefatherId);
        if (null == offspringNode || null == forefatherNode) {
            return false;
        }
        return isOffspring(forefatherNode, offspringNode, offspringNode);
    }

    private boolean isOffspring(TreeNode forefatherNode, TreeNode offspringNode, TreeNode node) {
        if (null == node) {
            return false;
        }
        if (forefatherNode.getClass().equals(node.getClass())) {
            if (node.getId().equals(forefatherNode.getId())) {
                return true;
            }
            TreeNode parentNode = this.treeNode(node.getClass(), node.getParentId());
            while (null != parentNode) {
                if (parentNode.getId().equals(forefatherNode.getId())) {
                    return true;
                }
                parentNode = this.treeNode(parentNode.getClass(), parentNode.getParentId());
            }
        } else {
            TreeNode previousNode = this.treeNode(TreeUtil.previousClass(node.getClass()), node.getPreviousId());
            return isOffspring(forefatherNode, offspringNode, previousNode);
        }
        return false;
    }

    public <T extends TreeNode> T treeNode(Class<T> clazz, long id) {
        if (isEmpty()) {
            return null;
        }
        Map<Long, TreeNode> treeNode = map.get(clazz);
        if (null == treeNode) {
            return null;
        }
        return (T) treeNode.get(id);
    }

    public <T extends TreeNode> T treeNodeWithOutTreeInfo(Class<T> clazz, long id) {
        T treeNode = treeNode(clazz, id);
        if (null == treeNode) {
            return null;
        }
        return (T) treeNode.copyWithoutTreeInfo();
    }

    public <T extends TreeNode> List<T> child(Class<T> clazz, long id) {
        if (isEmpty()) {
            return new ArrayList<>();
        }
        T t = treeNode(clazz, id);
        if (null == t) {
            return new ArrayList<>();
        }
        List<T> retList = TreeUtil.convert(clazz, t.getChildList());
        Collections.sort(retList);
        return retList;
    }

    public <T extends TreeNode> List<T> childWithOutTreeInfo(Class<T> clazz, long id) {
        List<T> child = child(clazz, id);
        if (CollectionUtils.isEmpty(child)) {
            return child;
        }
        List<T> retList = new ArrayList<>();
        for (T t : child) {
            retList.add((T) t.copyWithoutTreeInfo());
        }
        return retList;
    }

    public <T extends TreeNode> Set<Long> idSet(Class<T> clazz) {
        if (isEmpty()) {
            return new HashSet<>();
        }
        return Sets.newHashSet(map.get(clazz).keySet());
    }

    public boolean isEmpty() {
        return MapUtils.isEmpty(map) || CollectionUtils.isEmpty(rootList) || CollectionUtils.isEmpty(classList);
    }

    public List<TreeNode> next(Class<? extends TreeNode> clazz, long id) {
        if (isEmpty()) {
            return new ArrayList<>();
        }
        TreeNode t = treeNode(clazz, id);
        if (null == t) {
            return new ArrayList<>();
        }
        Collections.sort(t.getNextList());
        return t.getNextList();
    }


    /**
     * 查看下一级数据，可跨表
     * @param clazz
     * @param id
     * @return
     */
    public List<TreeNode> nextWithoutCache(Class<? extends TreeNode> clazz, long id) {
        List<TreeNode> next = next(clazz, id);
        if (null == next) {
            return next;
        }
        List<TreeNode> retList = new ArrayList<>();
        for (TreeNode t : next) {
            retList.add(t.copyWithoutTreeInfo());
        }
        return retList;
    }

    public List<Long> nextIds(Class<? extends TreeNode> clazz, long id) {
        List<TreeNode> next = next(clazz, id);
        if (null == next) {
            return null;
        }
        List<Long> idList = next.stream().map(s -> s.getId()).collect(Collectors.toList());
        return idList;
    }



    public List<TreeNode> forefatherList(Class<? extends TreeNode> nodeClass, long id, final Class<? extends TreeNode> filterClass, boolean containSelf) {
        TreeNode treeNode = treeNode(nodeClass, id);
        final List<TreeNode> retList = new ArrayList<>();
        ergodicForefather(treeNode, (temp) -> {
            if (null == filterClass || filterClass.equals(temp.getClass())) {
                retList.add(temp.copyWithoutTreeInfo());
            }
        });
        if (retList.isEmpty()) {
            return retList;
        }
        if (!containSelf && retList.get(0).getId().equals(treeNode.getId()) && retList.get(0).getClass().equals(treeNode.getClass())) {
            retList.remove(0);
        }
        return retList;
    }


    /**
     * 可按类型查看子节点数据，不限下一级
     * @param nodeClass
     * @param id
     * @param filterClass
     * @param containSelf
     * @return
     */
    public List<TreeNode> offspringList(Class<? extends TreeNode> nodeClass, long id, final Class<? extends TreeNode> filterClass, boolean containSelf) {
        TreeNode treeNode = treeNode(nodeClass, id);
        final List<TreeNode> retList = new ArrayList<>();
        ergodicOffspring(treeNode, (temp) -> {
            if (null == filterClass || filterClass.equals(temp.getClass())) {
                retList.add(temp.copyWithoutTreeInfo());
            }
        });
        if (retList.isEmpty()) {
            return retList;
        }
        if (!containSelf && retList.get(0).getId().equals(treeNode.getId()) && retList.get(0).getClass().equals(treeNode.getClass())) {
            retList.remove(0);
        }
        Collections.sort(retList);
        return retList;
    }
    
    public void ergodicOffspring(TreeNode treeNode, Consumer<TreeNode> consumer) {
        if (null == treeNode) {
            return;
        }
        consumer.accept(treeNode);
        if (treeNode.getHasChild()) {
            for (TreeNode temp : treeNode.getChildList()) {
                ergodicOffspring(temp, consumer);
            }
        }
        if (treeNode.getHasNext()) {
            for (TreeNode temp : treeNode.getNextList()) {
                ergodicOffspring(temp, consumer);
            }
        }
    }
    
    
    public void ergodicForefather(TreeNode treeNode, Consumer<TreeNode> consumer) {
        if (null == treeNode) {
            return;
        }
        consumer.accept(treeNode);
        if (TreeUtil.hasParent(treeNode.getParentId())) {
            TreeNode temp = treeNode(treeNode.getClass(), treeNode.getParentId());
            ergodicForefather(temp, consumer);
        } else if (TreeUtil.hasPrevious(treeNode.getPreviousId())) {
            TreeNode temp = treeNode(TreeUtil.previousClass(treeNode.getClass()), treeNode.getPreviousId());
            ergodicForefather(temp, consumer);
        }
    }

    public static class TreeBuilder {

        private Map<Class<? extends TreeNode>, Map<Long, TreeNode>> map = new HashMap<>();

        private List<Class<? extends TreeNode>> classList = new ArrayList<>();

        protected TreeBuilder() {

        }

        public static TreeBuilder newInstance() {
            return new TreeBuilder();
        }

        public TreeBuilder add(Class<? extends TreeNode> clazz, Collection<? extends TreeNode> treeNodes) {
            Validate.notNull(clazz, "param clazz can not be null");
            Validate.notEmpty(treeNodes, "param treeNodes can not be empty");
            if (!classList.isEmpty()) {
                Class<? extends TreeNode> lastClass = classList.get(classList.size() - 1);
                Class<? extends TreeNode> previousClass = TreeUtil.previousClass(clazz);
                if (!lastClass.equals(clazz) && !previousClass.equals(lastClass)) {
                    throw new IllegalArgumentException("param clazz not expect");
                }
            }
            classList.add(clazz);
            Map<Long, TreeNode> treeNodeMap = map.get(clazz);
            if (null == treeNodeMap) {
                treeNodeMap = new HashMap<>();
                map.put(clazz, treeNodeMap);
            }
            for (TreeNode treeNode : treeNodes) {
                treeNodeMap.put(treeNode.getId(), treeNode);
            }
            return this;
        }

        public Tree build() {
            List<TreeNode> rootList = new ArrayList<>();
            for (int i = 0; i < classList.size(); i++) {
                Map<Long, TreeNode> treeNodeMap = map.get(classList.get(i));
                Collection<TreeNode> values = treeNodeMap.values();
                List<TreeNode> treeNodeList = Lists.newArrayList(values);
                Collections.sort(treeNodeList);

                for (TreeNode treeNode : treeNodeList) {
                    Long getParentId = treeNode.getParentId();
                    TreeNode parentTreeNode = TreeUtil.hasParent(getParentId) ? treeNodeMap.get(getParentId) : null;
                    Long getPreviousId = treeNode.getPreviousId();
                    Class<? extends TreeNode> previousClass = TreeUtil.previousClass(treeNode.getClass());
                    Map<Long, TreeNode> previousTreeNodeMap = null == previousClass ? null : map.get(previousClass);
                    TreeNode previousTreeNode = (TreeUtil.hasPrevious(getPreviousId) && null != previousTreeNodeMap) ? previousTreeNodeMap.get(getPreviousId) : null;

                    /*
                     * // 如果父ID对应节点不存在，则删除掉 boolean needRemove =
                     * removeIdSet.contains(getParentId) ||
                     * (TreeUtil.hasParent(getParentId) && null ==
                     * treeNodeMap.get(getParentId)) ||
                     * (TreeUtil.hasPrevious(getPreviousId) && null !=
                     * previousClass && (null == previousTreeNodeMap || null ==
                     * previousTreeNodeMap.get(getPreviousId))); if (needRemove) {
                     * removeIdSet.add(treeNode.id()); if
                     * (CollectionUtils.isNotEmpty(treeNode.getChildList())) {
                     * for (Object child : treeNode.getChildList()) {
                     * removeIdSet.add(((TreeNode) child).id()); } } continue; }
                     */

                    if (TreeUtil.hasParent(getParentId) && null != parentTreeNode) {
                        List<TreeNode> childList = parentTreeNode.getChildList();
                        if (null == childList) {
                            childList = new ArrayList<>();
                            parentTreeNode.setChildList(childList);
                        }
                        childList.add(treeNode);
                        parentTreeNode.setHasChild(true);
                    } else {
                        if (i == 0) {
                            rootList.add(treeNode);
                        } else {
                            if (null != previousTreeNode) {
                                List<TreeNode> nextList = previousTreeNode.getNextList();
                                if (null == nextList) {
                                    nextList = new ArrayList<>();
                                    previousTreeNode.setNextList(nextList);
                                }
                                nextList.add(treeNode);
                                previousTreeNode.setHasNext(true);
                            }
                        }
                    }
                }
            }

            ConcurrentHashMap<Class<? extends TreeNode>, ConcurrentHashMap<Long, TreeNode>> map1 = new ConcurrentHashMap<>();
            put(rootList, map1);
            Collections.sort(rootList);
            return new Tree(map1, rootList, classList);
        }

        private void put(List<TreeNode> treeNodeList, ConcurrentHashMap<Class<? extends TreeNode>, ConcurrentHashMap<Long, TreeNode>> map1) {
            if (CollectionUtils.isNotEmpty(treeNodeList)) {
                for (TreeNode treeNode : treeNodeList) {
                    ConcurrentHashMap<Long, TreeNode> tempMap = map1.get(treeNode.getClass());
                    if (null == tempMap) {
                        tempMap = new ConcurrentHashMap<>();
                        map1.put(treeNode.getClass(), tempMap);
                    }
                    tempMap.put(treeNode.getId(), treeNode);
                    if (treeNode.getHasChild()) {
                        put(treeNode.getChildList(), map1);
                    }
                    if (treeNode.getHasNext()) {
                        put(treeNode.getNextList(), map1);
                    }
                }
            }
        }
    }
    
 public void addTreeNode(Class<? extends TreeNode> clazz,TreeNode tn){
     this.map.get(clazz).put(tn.getId(), tn);
 }
 public void deleteTreeNode(Class<? extends TreeNode> clazz,Long id){
     this.map.get(clazz).remove(id);
 }
}
