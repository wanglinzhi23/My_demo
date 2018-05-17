package intellif.dto;

import intellif.consts.GlobalConsts;

import java.util.HashMap;
import java.util.Map;

public class ProcessInfo {
	private JsonObject jo;//若为非0值，则无需再请求进度，业务中断
	private volatile long totalSize;
	
	private long successNum;
	
	private long failedNum;
	
	private Map detailMap;//详细统计
	
	private Object info;

	public ProcessInfo(){
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ProcessInfo(int type){ 
		//type 1 布控
		if(GlobalConsts.process_Black == type){
			Map errorMap = new HashMap();
			errorMap.put(1, 0); // 图片没有人脸记录
			errorMap.put(2, 0); // 图片多于一个人脸记录
			errorMap.put(3, 0); // 图片在红名单内记录
			errorMap.put(4, 0); //其它原因
			this.detailMap = errorMap;
		}else if(GlobalConsts.process_red == type){
			Map errorMap = new HashMap();
			errorMap.put(1, 0); // 图片没有人脸记录
			errorMap.put(2, 0); // 图片多于一个人脸记录
			errorMap.put(4, 0); //其它原因
			this.detailMap = errorMap;
		}
		
	}
	
	public synchronized void incrementSuccessNumWithLock() {
		successNum++;
	}
	
	public synchronized void incrementFailedNumWithLock() {
		failedNum++;
	}
	
	public synchronized void addToTotalSize(int num) {
		totalSize += num;
	}
	
	public Map getDetailMap() {
		return detailMap;
	}
	public void setDetailMap(Map detailMap) {
		this.detailMap = detailMap;
	}
	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(long successNum) {
		this.successNum = successNum;
	}

	public long getFailedNum() {
		return failedNum;
	}

	public void setFailedNum(long failedNum) {
		this.failedNum = failedNum;
	}

	public JsonObject getJo() {
		return jo;
	}

	public void setJo(JsonObject jo) {
		this.jo = jo;
	}
	public Object getInfo() {
		return info;
	}
	public void setInfo(Object info) {
		this.info = info;
	}
	
}
