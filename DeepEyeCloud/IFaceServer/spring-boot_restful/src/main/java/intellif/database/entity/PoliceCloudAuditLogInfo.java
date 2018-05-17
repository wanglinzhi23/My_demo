package intellif.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name = GlobalConsts.T_NAME_POLICE_CLOUD_AUDIT_LOG,schema=GlobalConsts.INTELLIF_BASE)
public class PoliceCloudAuditLogInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 431236999999560563L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String policeId;
	
	 // 日志（消息）标题缩略信息
    private String title;

    // 日志（消息）描述信息
    private String message;

    // 操作CRUD
    private String operation;

    // 操作的对象（表名）
    private String object;

    // 对象变更后的状态（主要针对人的各种分类消息，1：已入库、2：已过期、3：开始布控、4：已删除、5：已更新、6：抓捕、7：取消抓捕、8：已抓捕、9：标记重点；10：取消重点）
    //操作日志记录（11.登录/注销；12.用户信息；13.单位信息；14.库信息；15.（1-10）.黑名单 也就是嫌疑人;）   
    private long object_status;
    
    private int hashCode;
    
    // 操作一级详细信息（搜索：搜索事由）
    private String friDetail = "";
    
    //操作二级详细信息 （搜索：搜索原因）
    private String secDetail = "";

	

	public String getPoliceId() {
		return policeId;
	}

	public void setPoliceId(String policeId) {
		this.policeId = policeId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public long getObject_status() {
		return object_status;
	}

	public void setObject_status(long object_status) {
		this.object_status = object_status;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
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
