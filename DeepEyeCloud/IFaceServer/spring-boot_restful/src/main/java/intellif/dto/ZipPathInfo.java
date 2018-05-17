package intellif.dto;

public class ZipPathInfo {
	private String zipFilePath;
	private long finishedTime;
	public ZipPathInfo(String zipFilePath, long finishedTime) {
		super();
		this.zipFilePath = zipFilePath;
		this.finishedTime = finishedTime;
	}
	public String getZipFilePath() {
		return zipFilePath;
	}
	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}
	public long getFinishedTime() {
		return finishedTime;
	}
	public void setFinishedTime(long finishedTime) {
		this.finishedTime = finishedTime;
	}
	
}
