package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_CRIME_FRI_TYPE,schema=GlobalConsts.INTELLIF_BASE)
public class CrimeFriType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 简称
	private String shortName;
	
	// 全称
	private String fullName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
