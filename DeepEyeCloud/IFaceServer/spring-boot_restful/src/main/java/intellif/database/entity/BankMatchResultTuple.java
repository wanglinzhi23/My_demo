package intellif.database.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import intellif.dto.FaceResultPKDto;

/**
 * 
 * @author yktangint
 *
 */
public class BankMatchResultTuple implements Serializable {
	private static final long serialVersionUID = 7083131396316110038L;
	//目标库人脸图片url
	private String targetFile;
	//该人脸对应的人名
	private String realName;
	//与该人脸最匹配的静态库人脸列表
	private List<FaceResultPKDto> resultList;
	
	public BankMatchResultTuple(String targetFile, String realName, List<FaceResultPKDto> resultList) {
		super();
		this.targetFile = targetFile;
		this.realName = realName;
		this.resultList = resultList;
	}
	public String getTargetFile() {
		return targetFile;
	}
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public List<FaceResultPKDto> getResultList() {
		return resultList;
	}
	public void setResultList(List<FaceResultPKDto> resultList) {
		this.resultList = resultList;
	}
	
}
