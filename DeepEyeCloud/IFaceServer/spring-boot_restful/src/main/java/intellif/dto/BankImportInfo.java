package intellif.dto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import intellif.database.entity.UserInfo;
import intellif.settings.BankImportSetting;

public class BankImportInfo {
	private ConcurrentHashMap<Long,List<String>> baseDirNameMap = new ConcurrentHashMap<Long,List<String>>();
	private ProcessInfo process = new ProcessInfo(); //个人导入进度
	ThreadPoolExecutor blackThreadPool = new ThreadPoolExecutor(BankImportSetting.getCorePoolSize(),
			24, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(25000));
	private UserInfo user;
	private int importType;//导入表信息，0 black_detail, 1 cid_detail,2 juzhu_detail,3 other_detail
	private boolean importState = true;//导入开关
	private boolean findState = true;//是否继续扫描判断是否存在图片
	private int bankId;
	
	
	public BankImportInfo(UserInfo user,int type,int bankId){
		this.user = user;
		this.importType = type;
		this.bankId = bankId;
	}
	
	
	public ProcessInfo getProcess() {
		return process;
	}
	public void setProcess(ProcessInfo process) {
		this.process = process;
	}
	public int getImportType() {
		return importType;
	}
	public void setImportType(int importType) {
		this.importType = importType;
	}
	public boolean isImportState() {
		return importState;
	}
	public void setImportState(boolean importState) {
		this.importState = importState;
	}
	public boolean isFindState() {
		return findState;
	}
	public void setFindState(boolean findState) {
		this.findState = findState;
	}
	public UserInfo getUser() {
		return user;
	}
	public void setUser(UserInfo user) {
		this.user = user;
	}


	public int getBankId() {
		return bankId;
	}


	public void setBankId(int bankId) {
		this.bankId = bankId;
	}


	public ConcurrentHashMap<Long, List<String>> getBaseDirNameMap() {
		return baseDirNameMap;
	}


	public void setBaseDirNameMap(
			ConcurrentHashMap<Long, List<String>> baseDirNameMap) {
		this.baseDirNameMap = baseDirNameMap;
	}


	public ThreadPoolExecutor getBlackThreadPool() {
		return blackThreadPool;
	}


	public void setBlackThreadPool(ThreadPoolExecutor blackThreadPool) {
		this.blackThreadPool = blackThreadPool;
	}
	
	

}
