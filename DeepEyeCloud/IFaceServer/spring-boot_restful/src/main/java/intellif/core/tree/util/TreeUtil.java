package intellif.core.tree.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import intellif.core.tree.annotation.NodeType;
import intellif.core.tree.annotation.PreviousClass;
import intellif.core.tree.itf.TreeNode;
import intellif.utils.StringUtil;

public class TreeUtil {
    
    private static Logger LOG = LogManager.getLogger(TreeUtil.class);

    public static String nodeType(Class<?> class1) {
        if (null == class1) {
            return null;
        }
        NodeType nodeType = class1.getAnnotation(NodeType.class);
        if (null == nodeType) {
            return StringUtil.toUnderlineName(class1.getSimpleName());
        }
        return nodeType.value();
    }
    
    public static int nodeTypeSort(Class<?> class1) {
        if (null == class1) {
            return 0;
        }
        NodeType nodeType = class1.getAnnotation(NodeType.class);
        if (null == nodeType) {
            return 0;
        }
        return nodeType.sort();
    }

    public static Class<? extends TreeNode> previousClass(Class<? extends TreeNode> class1) {
        if (null == class1) {
            return null;
        }
        PreviousClass previouse = class1.getAnnotation(PreviousClass.class);
        return null == previouse ? null : previouse.value();
    }
    
    public static <T> List<T> convert(Class<T> clazz, List list) {
        if (null == list || null == clazz) {
            return null;
        }
        List<T> retList = new ArrayList<>();
        for (Object o : list) {
            try {
                retList.add((T) o);
            } catch (Exception e) {
                LOG.warn("Cannot convert " + o + " to " + clazz + ", catch exception: ", e);
            }
        }
        return retList;
    }
    
    public static Map<Class<? extends TreeNode>, Set<Long>> idMap(Collection<TreeNode> treeNodes) {
        Map<Class<? extends TreeNode>, Set<Long>> retMap = new HashMap<>();
        if (CollectionUtils.isEmpty(treeNodes)) {
            return retMap;
        }
        for (TreeNode treeNode : treeNodes) {
            Set<Long> set = retMap.get(treeNode.getClass());
            if (null == set) {
                set = new HashSet<>();
                retMap.put(treeNode.getClass(), set);
            }
            set.add(treeNode.getId());
        }
        return retMap;
    }
    
    public static boolean hasParent(Long parentId) {
        return null != parentId && !parentId.equals(0L);
    }
    
    public static boolean hasPrevious(Long previousId) {
        return null != previousId && !previousId.equals(0L);
    }
}
