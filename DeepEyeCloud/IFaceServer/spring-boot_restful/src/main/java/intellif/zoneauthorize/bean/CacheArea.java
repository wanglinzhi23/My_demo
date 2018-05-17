package intellif.zoneauthorize.bean;

import java.io.Serializable;

import intellif.core.tree.itf.PathTreeNode;

public class CacheArea implements Serializable, PathTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ID
	private long id;
	// 路径
	private String path = pathSeparator();
	// 是否为叶子节点
	private boolean leaf = true;
	// 是否为叶子节点
	private long districtId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (leaf ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		CacheArea other = (CacheArea) obj;
		if (id != other.id)
			return false;
		if (leaf != other.leaf)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CacheArea [id=");
		builder.append(id);
		builder.append(", path=");
		builder.append(path);
		builder.append(", leaf=");
		builder.append(leaf);
		builder.append(", districtId=");
		builder.append(districtId);
		builder.append("]");
		return builder.toString();
	}
}
