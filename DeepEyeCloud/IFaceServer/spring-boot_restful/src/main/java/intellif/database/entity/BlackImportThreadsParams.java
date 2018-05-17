package intellif.database.entity;

import intellif.database.entity.UserInfo;
import intellif.excel.PersonBankXLS;

import java.util.List;
import java.util.Map;
/**
 * 批量导入黑名单多线程处理参数
 * @author shixiaohua
 *
 */
public class BlackImportThreadsParams extends ImportThreadsParams {
      private long bankId;
      
      public BlackImportThreadsParams(List<Object> xlsList, Map<String,String> extMap,
    		  UserInfo ui,String dirPath,int key,int count,String excelName,long bankId){
    	 super(xlsList,extMap,ui,dirPath,key,count,excelName);
    	 this.bankId = bankId;
      }

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}
      

	
}
