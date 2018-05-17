package com.intellif.bankmatch.vo;

import java.io.Serializable;
import java.util.List;

import com.intellif.bankmatch.dto.FaceResultPKDto;

public class BankMatchResultTuple implements Serializable {
	private static final long serialVersionUID = -1579677681434305380L;
	private String targetFile;
	private String realName;
	private List<FaceResultPKDto> resultlist;
	
	public BankMatchResultTuple(String targetFile, String realName, List<FaceResultPKDto> resultlist) {
		super();
		this.targetFile = targetFile;
		this.realName = realName;
		this.resultlist = resultlist;
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
	public List<FaceResultPKDto> getResultlist() {
		return resultlist;
	}
	public void setResultlist(List<FaceResultPKDto> resultlist) {
		this.resultlist = resultlist;
	}	
}
