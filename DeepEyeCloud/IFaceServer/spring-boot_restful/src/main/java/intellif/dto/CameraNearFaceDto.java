package intellif.dto;

import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;

import java.util.List;

public class CameraNearFaceDto {

	private CameraInfo camera;
	
	private List<FaceInfo> faceList;

	public CameraNearFaceDto(CameraInfo camera, List<FaceInfo> faceList) {
		super();
		this.camera = camera;
		this.faceList = faceList;
	}

	public CameraInfo getCamera() {
		return camera;
	}

	public void setCamera(CameraInfo camera) {
		this.camera = camera;
	}

	public List<FaceInfo> getFaceList() {
		return faceList;
	}

	public void setFaceList(List<FaceInfo> faceList) {
		this.faceList = faceList;
	}
	
}
