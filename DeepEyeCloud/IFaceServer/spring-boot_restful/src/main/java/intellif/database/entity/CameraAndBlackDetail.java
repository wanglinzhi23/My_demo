package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_CAMERA_BLACKDETAIL,schema=GlobalConsts.INTELLIF_BASE)
public class CameraAndBlackDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// 编号id
	private long id;

	private long cameraId;

	private long blackdetailId;

	public CameraAndBlackDetail(long cameraId, long blackdetailId) {
		super();
		this.cameraId = cameraId;
		this.blackdetailId = blackdetailId;
	}

	public CameraAndBlackDetail() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCameraId() {
		return cameraId;
	}

	public void setCameraId(long cameraId) {
		this.cameraId = cameraId;
	}

	public long getBlackdetailId() {
		return blackdetailId;
	}

	public void setBlackdetailId(long blackdetailId) {
		this.blackdetailId = blackdetailId;
	}

}
