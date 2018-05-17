package intellif.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import intellif.audit.AuditServiceImpl;
import intellif.consts.GlobalConsts;
import intellif.dto.SearchIPDto;
import intellif.service.AllowipServiceItf;
import intellif.database.entity.AllowIpRange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Service;




@Service
public class AllowipServiceImpl implements AllowipServiceItf {
	
	  private static Logger LOG = LogManager.getLogger(AuditServiceImpl.class);	
	  @PersistenceContext
		EntityManager entityManager;
	  public static BigInteger hisopmaxpage;

	  
    	@Override
    	public ArrayList findByPage(int page,int pagesize,SearchIPDto searchDto) {
		
    	
    	 	ArrayList<AllowIpRange> resp = null;
			
		
			String sqlString = "select *"   + " FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALLOW_IPS+ " where 1 = 1 "; 
		   String name = searchDto.getName();
		   if(StringUtil.isNotBlank(name)){
		       String  fSql = " and (ip_rang_name like '%" + name + "%' ";
		       fSql += " or start_ip like '%" + name + "%' ";
		       fSql += " or end_ip like '%" + name + "%' ";
		       fSql += " or user like '%" + name + "%')";
		       sqlString = sqlString + fSql;
		   }
		   
			hisopmaxpage=getMaxPage(sqlString);

		    sqlString += " LIMIT " + (page - 1) * pagesize + "," + pagesize + "";   

			try {
				Query query = this.entityManager.createNativeQuery(sqlString, AllowIpRange.class);
				resp =  (ArrayList<AllowIpRange>) query.getResultList();
			} catch (Exception e) {
				LOG.error("", e);
			} finally {
				entityManager.close();
			}
			
			return resp;
    		
    		
		
	}
	
    	
           public BigInteger getMaxPage(String sql){
			
			BigInteger maxpage=null;
			String getmaxpagesql="";
			
			getmaxpagesql=sql.replace("*","count(*)");
			

			System.out.println("此处获取最大页数的sql语句是："+getmaxpagesql);
			ArrayList resp = null;
			
			try { 
				Query query = this.entityManager.createNativeQuery(getmaxpagesql);
				
				resp =(ArrayList) query.getResultList();
			
			} catch (Exception e) {
				LOG.error("", e);
				return null; 
			} finally {
				entityManager.close();
			}
			if(resp.size()!=0){
			maxpage=(BigInteger) resp.get(0);}
			
			return maxpage;
			
			
		}
	


	}

  
	 
		
		
              
			
				
	
	
		
	

    
   
