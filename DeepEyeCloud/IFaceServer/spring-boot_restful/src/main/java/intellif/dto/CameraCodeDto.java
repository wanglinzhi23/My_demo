package intellif.dto;

import java.util.List;

import intellif.database.entity.FaceInfo;

public class CameraCodeDto {
	private String sourceCode;
	private List<FaceInfo> faceInfo;
	
	public CameraCodeDto(String sourceCode, List<FaceInfo> faceInfo) {
		super();
		this.sourceCode = sourceCode;
		this.faceInfo = faceInfo;
	}

	public List<FaceInfo> getFaceInfo() {
		return faceInfo;
	}

	public void setFaceInfo(List<FaceInfo> faceInfo) {
		this.faceInfo = faceInfo;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
	

}
