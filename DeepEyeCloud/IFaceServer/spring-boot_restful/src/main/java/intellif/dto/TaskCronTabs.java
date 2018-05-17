package intellif.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangboz on 10/19/15.
 *
 * @see https://quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson06
 */
public class TaskCronTabs implements Serializable {

    private long taskId;
    private List<String> cronTabs;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public List<String> getCronTabs() {
        return cronTabs;
    }

    public void setCronTabs(List<String> cronTabs) {
        this.cronTabs = cronTabs;
    }
}
