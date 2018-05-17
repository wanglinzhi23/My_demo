package intellif.audit;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;

import intellif.dto.AuditLogInfoDto;
import intellif.dto.HistoryOperationDto;
import intellif.dto.HistorySearchOperationDetailDto;
import intellif.dto.HistorySearchOperationDto;
import intellif.dto.JsonObject;

/**
 * Created by yangboz on 12/2/15.
 *
 * @see http://www.alexecollins.com/spring-mvc-and-mongodb-auditing-actions/
 */
public interface AuditServiceItf {
    public void audit(String message, AuditableItf target);

    public String getCurrentUser();
 
    public ArrayList<HistoryOperationDto> findByCombinedConditions(AuditLogInfoDto auditloginfodto,int page,int pageSize);
    
    public LinkedMap findLoginInformation(AuditLogInfoDto auditloginfodto,int page,int pageSize);
    
    public ArrayList<HistoryOperationDto> findAllByCombinedConditions(AuditLogInfoDto auditloginfodto);
    
    public  String  exportAuditExcel(AuditLogInfoDto auditloginfodto,int key);
  
  	public List<HistoryOperationDto> findLogByPoliceCloud(AuditLogInfoDto auditLogInfoDto,int page,int pageSize);
  	
  	public List<HistorySearchOperationDto> findSearchAudit(int page,int pageSize);
  	
  	public HistorySearchOperationDetailDto findSearchAuditDeatil(long auditId);
   
    
    
}
