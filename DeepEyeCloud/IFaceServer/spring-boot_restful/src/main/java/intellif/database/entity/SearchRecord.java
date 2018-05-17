package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Entity
@Table(name = GlobalConsts.T_SEARCH_RECORD,schema=GlobalConsts.INTELLIF_BASE)
public class SearchRecord extends InfoBase implements Serializable {

	/**
	 * 检索操作记录
	 */
	private static final long serialVersionUID = -4848885062115021248L;
	private static Logger LOG = LogManager.getLogger(SearchRecord.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 所属图片路径
	private String faceUrl;
	
	//客户端IP
	private String ip;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
