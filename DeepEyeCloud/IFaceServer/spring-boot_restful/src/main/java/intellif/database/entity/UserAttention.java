package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_USER_ATTENTION,schema=GlobalConsts.INTELLIF_BASE)
public class UserAttention {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// 编号id
	private long id;

	// 用户Id
	private long userId;
	
	// 嫌疑人Id
	private long personId;
	
	public UserAttention() {
		
	}

	public UserAttention(long id, long userId, long persionId) {
		super();
		this.id = id;
		this.userId = userId;
		this.personId = persionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	
	
}
