package intellif.facecollision.request;

/**
 * @author Zheng Xiaodong
 */
public class TargetFaceRequest {
    private Long taskId;
    private Long personId;
    private Integer listType;
    private Integer page;
    private Integer pageSize;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Integer getListType() {
        return listType;
    }

    public void setListType(Integer listType) {
        this.listType = listType;
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
        return "TargetFaceRequest{" +
                "taskId=" + taskId +
                ", personId=" + personId +
                ", listType=" + listType +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
