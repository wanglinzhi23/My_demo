package intellif.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 摄像头查询
 * 
 * @author mmm
 *
 */
public class CameraQueryDto extends CommonQueryDto {

    // 节点ID，格式为 1,2,3
    private String id;

    // 节点类型
    private String nodeType;

    // 查询输入关键字
    private String query = "";


    // 是否需要geo_string不为空
    private boolean needGeoString = false;

    // 模糊匹配哪个字段
    private String queryBy = "displayName";

    // 是否查询侯问室
    private Long inStation;

    private String areaIds;
    
    
    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean getNeedGeoString() {
        return needGeoString;
    }

    public void setNeedGeoString(boolean needGeoString) {
        this.needGeoString = needGeoString;
    }

    public String getQueryBy() {
        return queryBy;
    }

    public void setQueryBy(String queryBy) {
        this.queryBy = queryBy;
    }

    public Long getInStation() {
        return inStation;
    }

    public void setInStation(Long inStation) {
        this.inStation = inStation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Long> idSet() {
        Set<Long> idSet = new HashSet<>();
        if (StringUtils.isBlank(id)) {
            return idSet;
        }
        String[] array = id.trim().split(",");
        for (String temp : array) {
            try {
                temp = temp.trim();
                if (StringUtils.isNotBlank(temp) && !"0".equals(temp)) {
                    idSet.add(Long.valueOf(temp));
                }
            } catch (Exception e) {
                continue;
            }
        }
        return idSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CameraQueryDto [id=");
        builder.append(id);
        builder.append(", nodeType=");
        builder.append(nodeType);
        builder.append(", query=");
        builder.append(query);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", needGeoString=");
        builder.append(needGeoString);
        builder.append(", queryBy=");
        builder.append(queryBy);
        builder.append(", inStation=");
        builder.append(inStation);
        builder.append("]");
        return builder.toString();
    }
}
