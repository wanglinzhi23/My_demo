package intellif.core.tree.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import intellif.core.tree.itf.PathTreeNode;

public class PathTree<T extends PathTreeNode> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Long, T> idToValueMap = new HashMap<>();

	private Map<String, Set<T>> pathToValueSetMap = new HashMap<>();

	public PathTree(Collection<T> treeNodeLists) {
		if (CollectionUtils.isNotEmpty(treeNodeLists)) {
			for (T treeNode : treeNodeLists) {
				if (null != treeNode) {
					idToValueMap.put(treeNode.id(), treeNode);
					Set<T> valueSet = pathToValueSetMap.get(treeNode.path());
					if (null == valueSet) {
						valueSet = new HashSet<>();
						pathToValueSetMap.put(treeNode.path(), valueSet);
					}
					valueSet.add(treeNode);
				}
			}
		}
	}

	public T treeNode(Long id) {
		return idToValueMap.get(id);
	}

	public Set<T> treeNodeSet(String path) {
		return pathToValueSetMap.get(path);
	}

	public List<Long> forefather(Long id) {
		T treeNode = treeNode(id);
		if (null == treeNode) {
			return new ArrayList<>();
		}
		return treeNode.forefather();
	}

	public Long father(Long id) {
		T treeNode = treeNode(id);
		if (null == treeNode) {
			return null;
		}
		return treeNode.father();
	}

	public Set<T> child(Long id) {
		T treeNode = treeNode(id);
		if (null == treeNode) {
			return new HashSet<>();
		}
		return treeNodeSet(treeNode.path());
	}

	public Set<Long> offspring(Long id) {
		T treeNode = treeNode(id);
		if (null == treeNode) {
			return new HashSet<>();
		}
		Set<T> set = treeNodeSet(treeNode.path());
		Set<Long> offspringSet = new HashSet<>();
		if (CollectionUtils.isEmpty(set)) {
			return offspringSet;
		}
		for (T t : set) {
			if (!t.leaf()) {
				offspringSet.addAll(offspring(t.id()));
			}
		}
		return offspringSet;
	}
	
	public Collection<T> all() {
		return idToValueMap.values();
	}
	
	public Set<Long> allIds() {
		return idToValueMap.keySet();
	}
	
	/**
	 * 获取所有的叶子节点
	 * @param ids
	 * @return
	 */
	public Set<Long> leaf(Long... ids) {
		if (null == ids) {
			return new HashSet<>();
		}
		return leaf(Arrays.asList(ids));
	}

	/**
	 * 获取所有的叶子节点
	 * @param ids
	 * @return
	 */
	public Set<Long> leaf(Collection<Long> ids) {
		Collection<T> all = all();
		Set<Long> set = new HashSet<>();
		if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(ids)) {
			return set;
		}
		for (T treeNode : all) {
			if (treeNode.leaf()) {
				if (ids.contains(treeNode.id())) {
					set.add(treeNode.id());
					continue;
				}
				for (Long id : ids) {
					if (treeNode.path().contains(treeNode.pathSeparator() + id + treeNode.pathSeparator())) {
						set.add(treeNode.id());
						break;
					}
				}
			}
		}
		return set;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idToValueMap == null) ? 0 : idToValueMap.values().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathTree<?> other = (PathTree<?>) obj;
		if (idToValueMap == null) {
			if (other.idToValueMap != null)
				return false;
		} else if (!idToValueMap.values().equals(other.idToValueMap.values()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PathTree [idToValueMap=");
		builder.append(idToValueMap);
		builder.append(", pathToValueSetMap=");
		builder.append(pathToValueSetMap);
		builder.append("]");
		return builder.toString();
	}
}
