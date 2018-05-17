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
@Table(name = GlobalConsts.T_NAME_MAINTENANCE,schema=GlobalConsts.INTELLIF_BASE)
public class MaintenancePerson implements Serializable{
	
	/**
	 * 运维人员信息
	 */
	private static final long serialVersionUID = 2837355462565530358L;
	private static Logger LOG = LogManager.getLogger(MaintenancePerson.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String weixinId;
	
	private String name;

	private String stationIds;//默认创建运维人员，stationIds为所有station

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWeixinId() {
		return weixinId;
	}

	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStationIds() {
		return stationIds;
	}

	public void setStationIds(String stationIds) {
		this.stationIds = stationIds;
	}
	
	
	
	
}
