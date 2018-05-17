/**
 *
 */
package intellif.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 任务查询DTO
 *
 * @author Peng Cheng
 */
@Entity
public class TaskInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 任务名称
    private String taskName;

    // 服务器名称
    private String serverName;

    // 数据源ID
    /**
     * 0 摄像头
     * 1 视频
     * 2 图片
     */
    private int sourceType;

    // 数据源
    private String source;

    // 规则名称
    private String ruleName;

    // 解码方式
    /**
     * 0 摄像头抓拍方式
     * 1 硬件解码方式
     */
    private int decodeType;

    // 创建时间
    private Date created;

    // 任务状态
    /**
     * 0 未启动
     * 1 启动
     * 2 停止
     */
    private int status;

    /**
     * 定时任务序列
     */
    private String cronTabs = null;

    /**
     * 定时状态
     * 0 无计划
     * 1 已执行
     * 2 未执行
     */
    private int cronStatus;

    @Transient
    private String startTime;

    @Transient
    private String endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(int decodeType) {
        this.decodeType = decodeType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "id:" + this.getId() + ",startTime:" + this.getStartTime() + ",endTime:" + this.getEndTime()
                + ",ruleName:" + this.getRuleName()
                + ",decodeType:" + this.getDecodeType() + ",source:" + this.getSource()
                + ",sourceType:" + this.getSourceType() + ",taskName:" + this.getTaskName() + ",serverName:"
                + this.getServerName() + ",status:" + this.getStatus();
    }

}
