package intellif.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 人脸过滤查询DTO
 * @author Zheng Xiaodong
 *
 */

public class FilteredFaceQueryDto implements Serializable {
    private Long typeId;
    private Integer page;
    private Integer pageSize;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "FilteredFaceQueryDto{" +
                "typeId=" + typeId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
