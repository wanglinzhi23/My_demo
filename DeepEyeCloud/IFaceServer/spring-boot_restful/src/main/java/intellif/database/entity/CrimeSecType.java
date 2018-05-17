package intellif.database.entity;

import java.io.Serializable;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_CRIME_SEC_TYPE,schema=GlobalConsts.INTELLIF_BASE)
public class CrimeSecType implements Serializable{
	
	private static final long serialVersionUID = -6553983105116453822L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long friId;
	
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFriId() {
		return friId;
	}

	public void setFriId(long friId) {
		this.friId = friId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
