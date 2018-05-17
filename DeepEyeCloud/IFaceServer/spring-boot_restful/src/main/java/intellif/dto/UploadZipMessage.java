package intellif.dto;

import java.util.ArrayList;
import java.util.List;

public class UploadZipMessage {
	
	//上传疑犯执行成功个数
    private int sucNum;
    //上传疑犯失败个数
    private int failNum;
    
    private int type = 100;//配合前端修改
    
    private long userId;//登录用户ID
    //所有出错消息内容
    private List<String> errorList = new ArrayList<String>();
	public int getSucNum() {
		return sucNum;
	}
	public void setSucNum(int sucNum) {
		this.sucNum = sucNum;
	}
	public int getFailNum() {
		return failNum;
	}
	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	public List<String> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
    
    
    
    
}
