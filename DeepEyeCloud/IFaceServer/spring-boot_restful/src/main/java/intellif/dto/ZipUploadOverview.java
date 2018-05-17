package intellif.dto;

import java.util.ArrayList;
import java.util.List;

public class ZipUploadOverview {
	//Overview
	private int numOfSucc=0;
	private int numOfFail=0;
	private int totalNum = 0;
	
	private List<ZipUploadDetail> details = new ArrayList<ZipUploadDetail>();

	public int getNumOfSucc() {
		return numOfSucc;
	}

	public void setNumOfSucc(int numOfSucc) {
		this.numOfSucc = numOfSucc;
	}

	public int getNumOfFail() {
		return numOfFail;
	}

	public void setNumOfFail(int numOfFail) {
		this.numOfFail = numOfFail;
	}

	public List<ZipUploadDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ZipUploadDetail> details) {
		this.details = details;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	
}