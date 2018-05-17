package intellif.database.entity;

import java.util.Date;
import intellif.consts.GlobalConsts;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_SEARCH_LOG, schema = GlobalConsts.INTELLIF_BASE)
public class SearchLogInfo{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Date created;

	// 请求时延 秒
	private long timeDelay;

	// 结果码
	private int resultCode;

	// 详情信息 类似图片 url
	private String message = "";

	// 发起人（无则为system）
	private String owner;

	public SearchLogInfo() {
	}

	public SearchLogInfo(Date created, int timeDelay, int resultCode,
			String message, String owner) {
		this.created = created;
		this.timeDelay = timeDelay;
		this.resultCode = resultCode;
		this.message = message;
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "AuditLogInfo{" + "id=" + id + ", created='" + created + '\''
				+ ", timeDelay='" + timeDelay + '\'' + ", failType='"
				+ resultCode + '\'' + ", message='" + message + '\''
				+ ", owner='" + owner + '\'' + '}';
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(long timeDelay) {
		this.timeDelay = timeDelay;
	}

	

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
