package intellif.facecollision.request;

/**
 * @author Zheng Xiaodong
 */
public class FaceCollisionTaskRequest {
    private Long userId;
    private String ids;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "FaceCollisionTaskStatusRequest{" +
                "userId=" + userId +
                ", ids='" + ids + '\'' +
                '}';
    }
}
