package intellif.fk.vo;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_FK_PLACE_CAMERA,schema=GlobalConsts.INTELLIF_BASE)
public class FkPlaceCamera {
	@Id
	private long placeId;
	
	private String cameraIds;

	public FkPlaceCamera(long placeId, String cameraIds) {
		super();
		this.cameraIds = cameraIds;
		this.placeId = placeId;
	}

	public FkPlaceCamera() {
		
	}

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }

	

}
