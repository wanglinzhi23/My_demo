package intellif.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 人脸过滤批量更新Dto
 * @author Zheng Xiaodong
 *
 */

public class BatchUpdateFilteredFaceDto implements Serializable {
    private String faceIds;
    private Long typeId;

    public String getFaceIds() {
        return faceIds;
    }

    public void setFaceIds(String faceIds) {
        this.faceIds = faceIds;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}
