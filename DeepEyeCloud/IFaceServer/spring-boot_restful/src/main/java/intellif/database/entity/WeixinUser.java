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
@Table(name = GlobalConsts.T_NAME_WEIXIN_USER,schema=GlobalConsts.INTELLIF_BASE)
public class WeixinUser implements Serializable{
	/**
	 * 微信用户信息
	 */
	private static final long serialVersionUID = 2770716246479967453L;
	private static Logger LOG = LogManager.getLogger(WeixinUser.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String userName;
	
	private String openId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	
}