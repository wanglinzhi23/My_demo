package intellif.dto;

import java.util.ArrayList;
import java.util.List;

public class ExcelUploadOverview {
	// Overview
	private int numOfSucc = 0;
	private int numOfFail = 0;

	private List<ExcelUploadDetail> details = new ArrayList<ExcelUploadDetail>();

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

	public List<ExcelUploadDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ExcelUploadDetail> details) {
		this.details = details;
	}

}
