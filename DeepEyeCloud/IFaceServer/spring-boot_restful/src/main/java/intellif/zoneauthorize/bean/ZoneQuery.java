package intellif.zoneauthorize.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 区域查询
 * 
 * @author pengqirong
 */
public class ZoneQuery {
    // 类型
    private String nodeType = "district";
    // 区域ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id = 0L;
    // 用户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId = null;
    // 需要统计个数的节电类型
    private String countNodeType = null;
    // 展开nodeType
    private String spreadNodeType = null;
    //模糊查询
    private String name = null;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSpreadNodeType() {
        return spreadNodeType;
    }

    public void setSpreadNodeType(String spreadNodeType) {
        this.spreadNodeType = spreadNodeType;
    }

    public String getCountNodeType() {
        return countNodeType;
    }

    public void setCountNodeType(String countNodeType) {
        this.countNodeType = countNodeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ZoneQuery [nodeType=");
        builder.append(nodeType);
        builder.append(", id=");
        builder.append(id);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", countNodeType=");
        builder.append(countNodeType);
        builder.append(", spreadNodeType=");
        builder.append(spreadNodeType);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((countNodeType == null) ? 0 : countNodeType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((spreadNodeType == null) ? 0 : spreadNodeType.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        ZoneQuery other = (ZoneQuery) obj;
        if (countNodeType == null) {
            if (other.countNodeType != null)
                return false;
        } else if (!countNodeType.equals(other.countNodeType))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nodeType == null) {
            if (other.nodeType != null)
                return false;
        } else if (!nodeType.equals(other.nodeType))
            return false;
        if (spreadNodeType == null) {
            if (other.spreadNodeType != null)
                return false;
        } else if (!spreadNodeType.equals(other.spreadNodeType))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }


}
