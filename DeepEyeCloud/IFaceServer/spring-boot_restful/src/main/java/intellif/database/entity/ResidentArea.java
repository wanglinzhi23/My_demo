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
@Table(name = GlobalConsts.T_NAME_RESIDENT_AREA,schema=GlobalConsts.INTELLIF_BASE)
public class ResidentArea implements Serializable {
	private static final long serialVersionUID = 5604832709863275668L;
	private static Logger LOG = LogManager.getLogger(ResidentArea.class);
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String cameraIds;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String cameraIds) {
		this.cameraIds = cameraIds;
	}

}
