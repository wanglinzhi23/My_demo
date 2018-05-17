package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name = GlobalConsts.T_NAME_USER_BUSINESS, schema = GlobalConsts.INTELLIF_BASE)
public class MarkInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8513476939129402250L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long userId;
	
	private String info;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	

}
