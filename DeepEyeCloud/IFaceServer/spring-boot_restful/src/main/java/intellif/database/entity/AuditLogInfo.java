package intellif.database.entity;

import intellif.audit.AuditableItf;
import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_AUDIT_LOG,schema=GlobalConsts.INTELLIF_BASE)
public class AuditLogInfo extends InfoBase implements AuditableItf {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 日志（消息）标题缩略信息
    private String title;

    // 日志（消息）描述信息
    private String message;
    
    // 操作一级详细信息（搜索：搜索事由）
    private String friDetail = "";
    
    //操作二级详细信息 （搜索：搜索原因）
    private String secDetail = "";

    // 操作CRUD
    private String operation;

    // 操作的对象（表名）
    private String object;

    // 操作的对象id
    private long objectId;

    // 对象变更后的状态（主要针对人的各种分类消息，1：已入库、2：已过期、3：开始布控、4：已删除、5：已更新、6：抓捕、7：取消抓捕、8：已抓捕、9：标记重点；10：取消重点）
    //操作日志记录（11.登录/注销；12.用户信息；13.单位信息；14.库信息；15(批量移库).17(搜索).（1-10）.黑名单 也就是嫌疑人; 1000（批量移库）统指白名单（，1001：已入库、1002：已过期、1003：开始布控、1004：已删除、1005：已更新  ） ;2000统指红名单（2001：已入库、2002：已更新、2003：已删除））   
    private long object_status;

    // 发起人（无则为system）
    private String owner;
   
  

	public AuditLogInfo() {
    }

    public AuditLogInfo(String title, String message, String operation, String object, long object_id, long object_status, String owner) {
        this.title = title;
        this.message = message;
        this.operation = operation;
        this.object = object;
        this.objectId = object_id;
        this.object_status = object_status;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "AuditLogInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", operation='" + operation + '\'' +
                ", object='" + object + '\'' +
                ", object_id=" + objectId +
                ", object_status=" + object_status +
                ", owner='" + owner + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

  
    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public long getObject_status() {
        return object_status;
    }

    public void setObject_status(long object_status) {
        this.object_status = object_status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

	public String getFriDetail() {
		return friDetail;
	}

	public void setFriDetail(String friDetail) {
		this.friDetail = friDetail;
	}

	public String getSecDetail() {
		return secDetail;
	}

	public void setSecDetail(String secDetail) {
		this.secDetail = secDetail;
	}

}
