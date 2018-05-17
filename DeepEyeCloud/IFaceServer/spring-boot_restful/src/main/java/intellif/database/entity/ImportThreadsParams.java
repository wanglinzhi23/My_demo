package intellif.database.entity;

import intellif.database.entity.UserInfo;
import intellif.excel.PersonBankXLS;

import java.util.List;
import java.util.Map;

public class ImportThreadsParams {
	  private List<Object> xlsList;
      private UserInfo ui;
      private String dirPath;
      private Map<String,String> extMap;
      private int key;
      private int count;	
      private String excelName;
      
      public ImportThreadsParams(List<Object> xlsList, Map<String,String> extMap,
    		  UserInfo ui,String dirPath,int key,int count,String excelName){
    	  this.xlsList = xlsList;
    	  this.extMap = extMap;
    	  this.ui = ui;
    	  this.dirPath = dirPath;
    	  this.key = key;
    	  this.count = count;
    	  this.excelName = excelName;
      }
      
	public List<Object> getXlsList() {
		return xlsList;
	}
	public void setXlsList(List<Object> xlsList) {
		this.xlsList = xlsList;
	}
	public UserInfo getUi() {
		return ui;
	}
	public void setUi(UserInfo ui) {
		this.ui = ui;
	}
	public String getDirPath() {
		return dirPath;
	}
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}
	public Map<String, String> getExtMap() {
		return extMap;
	}
	public void setExtMap(Map<String, String> extMap) {
		this.extMap = extMap;
	}
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public String getExcelName() {
		return excelName;
	}

	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	
}
