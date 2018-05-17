package intellif.database.entity;

import java.io.Serializable;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY)
public class PoliceStationAuthority extends InfoBase implements Serializable {

	private static final long serialVersionUID = -3461476926931499424L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// 编号id
	private long id;

	// 派出所Id
	private long stationId;
	
	// 库Id
	private long bankId;
	
	// 权限类型（0：查看权限；1：编辑与查看权限；2：分配与编辑与查看权限）
	private int type;

	public PoliceStationAuthority() {
	}

	public PoliceStationAuthority(long stationId, long bankId, int type) {
		super();
		this.stationId = stationId;
		this.bankId = bankId;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
