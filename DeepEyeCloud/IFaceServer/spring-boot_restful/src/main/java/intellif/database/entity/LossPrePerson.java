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
@Table(name = GlobalConsts.T_NAME_LOSS_PRE,schema=GlobalConsts.INTELLIF_BASE)
public class LossPrePerson implements Serializable{
	
	/**
	 * 防损人员信息
	 */
	private static final long serialVersionUID = 2837355462565530358L;
	private static Logger LOG = LogManager.getLogger(LossPrePerson.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String weixinId;
	
	private long stationId;

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


	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
}