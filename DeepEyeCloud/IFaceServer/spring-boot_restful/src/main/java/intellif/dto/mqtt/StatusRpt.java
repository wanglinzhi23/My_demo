package intellif.dto.mqtt;

/**
 * Created by yangboz on 10/12/15.
 */
public class StatusRpt {
    private long taskid;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTaskid() {
        return taskid;
    }

    public void setTaskid(long taskid) {
        this.taskid = taskid;
    }

    @Override
    public String toString() {
        return "taskid:" + getTaskid() + ",status:" + getStatus();
    }
}
