package intellif.facecollision.request;

/**
 * @author Zheng xiaodong
 */
public class FaceCollisionResultRequest {
    private Long taskId;
    private Long personFaceId;
    private String faceIds;
    private int listType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFaceIds() {
        return faceIds;
    }

    public void setFaceIds(String faceIds) {
        this.faceIds = faceIds;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public Long getPersonFaceId() {
        return personFaceId;
    }

    public void setPersonFaceId(Long personFaceId) {
        this.personFaceId = personFaceId;
    }

    @Override
    public String toString() {
        return "FaceCollisionResultRequest{" +
                "taskId=" + taskId +
                ", personFaceId=" + personFaceId +
                ", faceIds='" + faceIds + '\'' +
                ", listType=" + listType +
                '}';
    }
}
