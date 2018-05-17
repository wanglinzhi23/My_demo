package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_SEARCH_REASON,schema=GlobalConsts.INTELLIF_BASE)
public class SearchReason extends InfoBase implements Serializable,Cloneable{
	
	private static final long serialVersionUID = -2363436111224977748L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// 事由名称
	private String rName;

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}
	
	
}
