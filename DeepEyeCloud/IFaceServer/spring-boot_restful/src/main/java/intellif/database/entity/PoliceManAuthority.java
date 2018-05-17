package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY, schema = GlobalConsts.INTELLIF_BASE)
public class PoliceManAuthority implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 警号
	private String policeNo;
	// 对应的权限type值
	private int type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPoliceNo() {
		return policeNo;
	}

	public void setPoliceNo(String policeNo) {
		this.policeNo = policeNo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
