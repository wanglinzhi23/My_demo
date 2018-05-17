package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;
import intellif.enums.CronStatus;

/**
 * The Class TaskInfo.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_TASK_INFO,schema=GlobalConsts.INTELLIF_BASE)
//Notice: RMI object should be Serializable.
public class TaskInfo extends InfoBase implements Serializable {

    private static final long serialVersionUID = 1L;

    public TaskInfo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 任务名称
    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // 所属摄像头ID
//	@Column(name="source_id")
    private long sourceId;

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    // 数据源ID
    /**
     * 0 摄像头
     * 1 视频
     * 2 图片
     */
    private int sourceType;

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    // 数据源类型
    private long serverId;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    // 所配置的人脸库ID
    //@see: http://stackoverflow.com/questions/9420916/onetoone-unidirectional-and-bidirectional
//	@ManyToOne
//	@JoinColumn(name = "id")
//	private BlackBank bankId;

    private long bankId;

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

//	public BlackBank getBankId() {
//		return bankId;
//	}
//
//	public void setBankId(BlackBank bankId) {
//		this.bankId = bankId;
//	}

    // 所配置的规则ID
    private long ruleId;

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    // 任务状态
    /**
     * 0 未启动
     * 1 启动
     * 2 停止
     */
    private int status;

    // 解码方式
    /**
     * 0 摄像头抓拍方式
     * 1 硬件解码方式
     */
    private int decodeType = 0;

    /**
     * 0 实时任务
     * 1 定时任务
     */
    private int type = 0;

    private String cronTabs = "";

    public int getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(int decodeType) {
        this.decodeType = decodeType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 0 无计划
     * 1 已执行
     * 2 未执行
     */
    private int cronStatus = CronStatus.NONE.getValue();

    @Override
    public String toString() {
        return "Id:" + this.getId()
                + ",Type:" + this.getType()
                + ",BankId:" + this.getBankId()
                + ",RuleId:" + this.getRuleId()
                + ",ServerId:" + this.getServerId()
                + ",SourceId:" + this.getSourceId()
                + ",SourceType:" + this.getSourceType()
                + ",Status:" + this.getStatus()
                + ",TaskName:" + this.getTaskName()
                + ",DecodeType:" + this.getDecodeType()
                + ",CronTabs:" + this.getCronTabs()
                + ",CronStatus:" + this.getCronStatus()
                ;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCronTabs() {
        return cronTabs;
    }

    public void setCronTabs(String cronTabs) {
        this.cronTabs = cronTabs;
    }

    public int getCronStatus() {
        return cronStatus;
    }

    public void setCronStatus(int cronStatus) {
        this.cronStatus = cronStatus;
    }
}
