package intellif.zoneauthorize.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.zoneauthorize.common.ZoneConstant;

public class ZoneAuthorizeUtil {

	/**
	 * 将ids转换成List&lt;Long&gt;类型
	 * @param ids
	 * @return
	 */
	public static List<Long> convertList(Object ids) {
		List<Long> idList = new ArrayList<>();
		// 如果为null，则返回空
		if (null == ids) {
			return idList;
		}

		// 处理ids是数组的情况
		if (ids.getClass().isArray()) {
			Object[] idsTemp = (Object[]) ids;
			for (Object id : idsTemp) {
				if (null != id) {
					idList.add(Long.valueOf(id.toString().trim()));
				}
			}
		}
		// 处理ids是Iterable的情况
		else if (ids instanceof Iterable) {
			Iterable<?> idsTemp = (Iterable<?>) ids;
			for (Object id : idsTemp) {
				if (null != id) {
					idList.add(Long.valueOf(id.toString().trim()));
				}
			}
		}
		// 处理ids是String，Long或Number的情况
		else {
			String idsTemp = ids.toString().trim();
			if (StringUtils.isNotBlank(idsTemp)) {
				if (idsTemp.contains(",")) {
					String[] idsTemps = idsTemp.split(",");
					for (String id : idsTemps) {
						if (StringUtils.isNotBlank(id)) {
							idList.add(Long.valueOf(id.trim()));
						}
					}
				} else {
					idList.add(Long.valueOf(idsTemp.trim()));
				}
			}
		}
		return idList;
	}
	

	
	public static Map<Class<? extends TreeNode>, Map<Long, String>> choiceMap(Tree tree, Map<Class<? extends TreeNode>, Set<Long>> classToIdSetMap) {
	    Map<Class<? extends TreeNode>, Map<Long, String>> choiceMap = new HashMap<>();
        for (Map.Entry<Class<? extends TreeNode>, Set<Long>> entry : classToIdSetMap.entrySet()) {
            for (Long id : entry.getValue()) {
                TreeNode treeNode = tree.treeNode(entry.getKey(), id);
                childChoice(choiceMap, tree, treeNode);
                parentChoice(choiceMap, tree, treeNode);
            }
        }
        return choiceMap;
	}
	
    protected static void childChoice(Map<Class<? extends TreeNode>, Map<Long, String>> choiceMap, Tree tree, TreeNode treeNode) {
        if (null == treeNode || null == tree) {
            return;
        }
        Map<Long, String> map = choiceMap.get(treeNode.getClass());
        if (null == map) {
            map = new HashMap<>();
            choiceMap.put(treeNode.getClass(), map);
        }
        map.put(treeNode.getId(), ZoneConstant.ZONE_CHOICE_ALL);
        if (treeNode.getHasChild()) {
            for (TreeNode childTreeNode : treeNode.getChildList()) {
                childChoice(choiceMap, tree, childTreeNode);
            }
        }
        if (treeNode.getHasNext()) {
            for (TreeNode nextTreeNode : treeNode.getNextList()) {
                childChoice(choiceMap, tree, nextTreeNode);
            }
        }
    }
    
    
    
    protected static void parentChoice(Map<Class<? extends TreeNode>, Map<Long, String>> choiceMap, Tree tree, TreeNode treeNode) {
        if (null == treeNode || null == tree) {
            return;
        }
        if (TreeUtil.hasParent(treeNode.getParentId())) {
            TreeNode parent = tree.treeNode(treeNode.getClass(), treeNode.getParentId());
            if (null != parent) {
                Map<Long, String> map = choiceMap.get(parent.getClass());
                if (null == map) {
                    map = new HashMap<>();
                    choiceMap.put(parent.getClass(), map);
                }
                if (null == map.get(parent.getId()) || map.get(parent.getId()).compareTo(ZoneConstant.ZONE_CHOICE_PART) < 0) {
                    map.put(parent.getId(), ZoneConstant.ZONE_CHOICE_PART);
                }
            }
            parentChoice(choiceMap, tree, parent);
        } else if (TreeUtil.hasPrevious(treeNode.getPreviousId())) {
            TreeNode previous = tree.treeNode(TreeUtil.previousClass(treeNode.getClass()), treeNode.getPreviousId());
            if (null != previous) {
                Map<Long, String> map = choiceMap.get(previous.getClass());
                if (null == map) {
                    map = new HashMap<>();
                    choiceMap.put(previous.getClass(), map);
                }
                if (null == map.get(previous.getId()) || map.get(previous.getId()).compareTo(ZoneConstant.ZONE_CHOICE_PART) < 0) {
                    map.put(previous.getId(), ZoneConstant.ZONE_CHOICE_PART);
                }
            }
            parentChoice(choiceMap, tree, previous);
        }
    }
}
