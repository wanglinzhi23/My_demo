package intellif.fk.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import intellif.database.entity.CameraInfo;
import intellif.zoneauthorize.itf.Zone;

public class FKFaceResultByCameraDto implements Serializable, Zone {

	private static final long serialVersionUID = -3465504280744251100L;

	private CameraInfo camera;
	
	private List<FkPersonResultDto> faceResult;

	public FKFaceResultByCameraDto(CameraInfo camera, List<FkPersonResultDto> faceResult) {
		super();
		this.camera = camera;
		this.faceResult = faceResult;
	}

	public CameraInfo getCamera() {
		if(camera == null) return new CameraInfo(-1);
		return camera;
	}

	public void setCamera(CameraInfo camera) {
		this.camera = camera;
	}

	public List<FkPersonResultDto> getFaceResult() {
		return faceResult;
	}

	public void setFaceResult(List<FkPersonResultDto> faceResult) {
		this.faceResult = faceResult;
	}
	
    @Autowired
    public Long zoneId() {
        return null == camera ? null : camera.getId();
    }
}
