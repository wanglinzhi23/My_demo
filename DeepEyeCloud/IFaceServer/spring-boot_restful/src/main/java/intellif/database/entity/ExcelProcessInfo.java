package intellif.database.entity;

import intellif.dto.ProcessInfo;

public final class ExcelProcessInfo extends ProcessInfo{

	private volatile long imageTotal;
	private  long imageSuc;
	private  long imageFail;
	private boolean importState = true;
	public ExcelProcessInfo(int i) {
		super(i);
	}
	public long getImageTotal() {
		return imageTotal;
	}
	public void setImageTotal(long imageTotal) {
		this.imageTotal = imageTotal;
	}
	public long getImageSuc() {
		return imageSuc;
	}
	public void setImageSuc(long imageSuc) {
		this.imageSuc = imageSuc;
	}
	public long getImageFail() {
		return imageFail;
	}
	public void setImageFail(long imageFail) {
		this.imageFail = imageFail;
	}
	public boolean isImportState() {
		return importState;
	}
	public void setImportState(boolean importState) {
		this.importState = importState;
	}
	public synchronized void incrementSuccessImgNumWithLock() {
		imageSuc++;
	}
	
	public synchronized void incrementFailedImgNumWithLock() {
		imageFail++;
	}
	
	
}
