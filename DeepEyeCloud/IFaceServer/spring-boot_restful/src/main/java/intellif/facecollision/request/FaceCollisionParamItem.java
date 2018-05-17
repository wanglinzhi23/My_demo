package intellif.facecollision.request;

/**
 * 人脸碰撞任务的每一项, 如一个视频文件或一个压缩包
 * @author Zheng Xiaodong
 */
public class FaceCollisionParamItem {
    // 类型（0：视频，1：压缩包）
    private Integer type;
    // 项的id
    private Long id;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FaceCollisionItem{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }
}
