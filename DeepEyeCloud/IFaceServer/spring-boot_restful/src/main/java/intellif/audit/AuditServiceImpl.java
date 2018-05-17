package intellif.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserDao;
import intellif.dto.AuditLogInfoDto;
import intellif.dto.HistoryOperationDto;
import intellif.dto.HistorySearchOperationDetailDto;
import intellif.dto.HistorySearchOperationDto;
import intellif.dto.JsonObject;
import intellif.dto.ProcessInfo;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FileUtil;
import intellif.database.entity.PoliceCloudAuditLogInfo;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.UserInfo;

/**
 * Created by yangboz on 12/2/15.
 *
 * @see http://www.alexecollins.com/spring-mvc-and-mongodb-auditing-actions/
 */
@Service
public class AuditServiceImpl implements AuditServiceItf {

	private static Logger LOG = LogManager.getLogger(AuditServiceImpl.class);

	SimpleDateFormat bartDateFormat = new SimpleDateFormat(
			"yyyy年MM月dd日  HH:mm:ss");
	SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat loginfDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static BigInteger hisopmaxpage = null; // 用于日志操作返回接口的
	public static BigInteger logusermaxpage = null; // 用于登录信息返回接口的
	public static LinkedMap loginmap2; // 今日登陆用户列表 含ip

	@Autowired
	AuditLogDao auditLogRepository;
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	private PropertiesBean propertiesBean;

    @Autowired
  	private PoliceStationDao policeStationDao;
    @Autowired
    private UserDao userDao;


	@Override
	public void audit(String message, AuditableItf target) {

		LOG.info("Audit: with" + getCurrentUser() + " - " + message);
		if (null != target) {
			AuditLogInfo auditLogInfo = new AuditLogInfo(target.getTitle(),
					target.getMessage(), target.getOperation(),
					target.getObject(), target.getObjectId(),
					target.getObject_status(), target.getOwner());
			LOG.info("Saved audit log :" + auditLogInfo.toString());
			auditLogRepository.save(auditLogInfo);
		}
	}

	@Override
	public String getCurrentUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	// 历史操作记录的条件查询
	@Override
	public ArrayList<HistoryOperationDto> findByCombinedConditions(
			AuditLogInfoDto auditloginfodto, int page, int pageSize) {

		List<AuditLogInfo> resp = null;
		ArrayList<HistoryOperationDto> result = new ArrayList<HistoryOperationDto>();

		String sqlString = "select a.* FROM " + GlobalConsts.INTELLIF_BASE
				+ "." + GlobalConsts.T_NAME_AUDIT_LOG + " a, "
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_AUDIT_LOG_TYPE
				+ " b where a.object_status=b.type_id";

		if (auditloginfodto.getStarttime() != null
				&& auditloginfodto.getEndtime() != null) {

			// sqlString += " where updated between '"+
			// sqlDateFormat.format(auditloginfo.getStarttime())+"' and
			// '"+sqlDateFormat.format(auditloginfo.getEndtime())+"'";
			// //库中操作时间是updated为主 此处是借助created和updated两个字段 将前台的时间段条件传回来
			sqlString += " and updated between '"
					+ auditloginfodto.getStarttime() + "' and '"
					+ auditloginfodto.getEndtime() + "'";

		}

		if (auditloginfodto.getOwner() != null
				&& !"".equals(auditloginfodto.getOwner())) {

			sqlString += " and owner= '" + auditloginfodto.getOwner() + "'";

		}

		if (auditloginfodto.getObject_status() > 0
				&& auditloginfodto.getObject_status() < 15) {

			sqlString += " and object_status="
					+ auditloginfodto.getObject_status();

		} else if (auditloginfodto.getObject_status() == 15) {// 黑名单
			sqlString += " and ((object_status between 1 and 10) or object_status=15) ";
		}

		else if (auditloginfodto.getObject_status() == 17) {//搜索
				sqlString += " and object_status=17 "; 
	    }
		
		if (auditloginfodto.getObject_status() > 1000 && auditloginfodto.getObject_status() <= 1005) {

			sqlString += " and object_status="
					+ auditloginfodto.getObject_status();

		} else if (auditloginfodto.getObject_status() == 1000) {// 白名单
			sqlString += " and ((object_status between 1000 and 1005) or object_status=1000) ";
		}

		if (auditloginfodto.getObject_status() > 2000
				&& auditloginfodto.getObject_status() <= 2005) {// 红名单

			sqlString += " and object_status="
					+ auditloginfodto.getObject_status();

		} else if (auditloginfodto.getObject_status() == 2000) {
			sqlString += " and ((object_status between 2000 and 2005) or object_status=2000) ";
		}

		// 根据关键字搜索 传来的auditloginfo 中 message作为关键字 关键字里面得type的情况
		// 前台id和type与数据库的存储之前是需要转换的
		if (auditloginfodto.getKeywords() != null
				&& !"".equals(auditloginfodto.getKeywords())) {

			sqlString += " and ( message like \"%"
					+ auditloginfodto.getKeywords() + "%\" or title like \"%"
					+ auditloginfodto.getKeywords()
					+ "%\" or b.type_name like \"%"
					+ auditloginfodto.getKeywords()
					+ "%\" or fri_detail like \"%"
					+ auditloginfodto.getKeywords()
					+ "%\" or sec_detail like \"%"
					+ auditloginfodto.getKeywords() + "%\")";

		}

		hisopmaxpage = getMaxPage(sqlString);
		sqlString += " order by id desc LIMIT " + (page - 1) * pageSize + ","
				+ pageSize + "";

		try {
			Query query = this.entityManager.createNativeQuery(sqlString,
					AuditLogInfo.class);
			resp = (ArrayList<AuditLogInfo>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		// 把结果集封装成前端要的对象返回
		if (resp != null) {
			for (int i = 0; i < resp.size(); i++) {

			    AuditLogInfo ali = resp.get(i);
			    try{
				HistoryOperationDto hod = new HistoryOperationDto();
				hod.setFriDetail(ali.getFriDetail());
				hod.setSecDetail(ali.getSecDetail());
				hod.setDetail(ali.getMessage());
				hod.setId(pageSize * (page - 1) + i + 1);

				if (ali.getObject_status() == 11) {
					hod.setOperationtype("登录/注销");
				} else if (ali.getObject_status() == 12) {
					hod.setOperationtype("用户信息");
				} else if (ali.getObject_status() == 13) {
					hod.setOperationtype("单位信息");
				} else if (ali.getObject_status() == 14) {
					hod.setOperationtype("库信息");

				} else if (ali.getObject_status() == 17) {
                    hod.setOperationtype("人脸检索");
                }else if (ali.getObject_status() == 15 || (ali.getObject_status() > 0 && ali.getObject_status()<=10)) {

					hod.setOperationtype("黑名单");
				} else if (ali.getObject_status() >= 1000
						&& ali.getObject_status() <= 1005) {
					hod.setOperationtype("白名单");
				} else if (ali.getObject_status() >= 2000
						&& ali.getObject_status() <= 2005) {
					hod.setOperationtype("红名单");
				}

				
				UserInfo ui = userDao.findByLogin(ali.getOwner());
				long policeStationId = ui.getPoliceStationId();
				String policeStation =  policeStationDao.findOne(policeStationId).getStationName();
				hod.setOperatorstation(policeStation);

				hod.setOperator(ali.getOwner());
				hod.setOpetime(bartDateFormat.format(ali.getUpdated()));

				result.add(hod);

			    }catch(Exception e){
                    LOG.error("process history log item error id:"+ali.getId()+",error:",e);
                }

			}
		}
		return result;
	}

  	
  	
 // 历史操作记录 条件查询  不分页   --- 用于 导出excel
 	 //历史操作记录的条件查询
 	@Override
 	public ArrayList<HistoryOperationDto> findAllByCombinedConditions(AuditLogInfoDto auditloginfodto) {

 			   	List<AuditLogInfo> resp = null;
 				ArrayList<HistoryOperationDto> result = new ArrayList<HistoryOperationDto>();
 			
 				String sqlString = "select a.* "   + " FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_AUDIT_LOG+" a, "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AUDIT_LOG_TYPE+" b where a.object_status=b.type_id";

 				
 		        if(auditloginfodto.getStarttime()!=null&&auditloginfodto.getEndtime()!=null){

 		    	        sqlString += " and updated between '"+ auditloginfodto.getStarttime()+"' and '"+auditloginfodto.getEndtime()+"'"; 	  
 		    	 
 				  }	
 				
 				
 			  if(auditloginfodto.getOwner() != null && !"".equals(auditloginfodto.getOwner())){
 				  
 				  sqlString += " and owner= '" + auditloginfodto.getOwner()+"'"; 
 			  
 			  }
 			  
 			  if(auditloginfodto.getObject_status()>0&&auditloginfodto.getObject_status()<15){
 				  
 				  sqlString += " and object_status=" + auditloginfodto.getObject_status();
 				  
 			  }else if(auditloginfodto.getObject_status()==15){
 				  //等于15状态 对应所有的黑名单信息日志
 				 sqlString += " and ((object_status between 1 and 10) or object_status=15) ";  
 				  
 			  }else if(auditloginfodto.getObject_status()==1000){
 				  
 				 sqlString += " and ((object_status between 1001 and 1005) or object_status=1000) ";  
 				  
 			  }else if(auditloginfodto.getObject_status()==2000){
 				  
 				 sqlString += " and ((object_status between 2001 and 2003) or object_status=2000) ";  
 				  
 			  }
 			
 			  if(auditloginfodto.getKeywords() != null && !"".equals(auditloginfodto.getKeywords())){	  
 				  
 				  sqlString += " and ( message like \"%"+auditloginfodto.getKeywords()+"%\" or title like \"%"+auditloginfodto.getKeywords()+"%\" or b.type_name like \"%"+auditloginfodto.getKeywords()+"%\")"; 
 				  
 			  }
 			
 			  hisopmaxpage=getMaxPage(sqlString);
 			  sqlString += " order by id desc ";  
		  
 				try {
 					Query query = this.entityManager.createNativeQuery(sqlString, AuditLogInfo.class);
 					resp = (ArrayList<AuditLogInfo>) query.getResultList();
 				} catch (Exception e) {
 					LOG.error("", e);
 				} finally {
 					entityManager.close();
 				}

 				//把结果集封装成前端要的对象返回
 				if (resp != null) {
 					for(int i=0;i<resp.size();i++){
 		 				
 		 				AuditLogInfo ali=resp.get(i);
 		 			   try{
 		 				HistoryOperationDto hod=new HistoryOperationDto();
                        hod.setFriDetail(ali.getFriDetail());
                        hod.setSecDetail(ali.getSecDetail());
 		 				hod.setDetail(ali.getMessage());
 		 				hod.setId(ali.getId());
 		 			
 		 				if(ali.getObject_status()==11){
 		 					hod.setOperationtype("登录/注销");	
 		 				}else if(ali.getObject_status()==12){
 		 					hod.setOperationtype("用户信息");	
 		 				}else if(ali.getObject_status()==13){
 		 					hod.setOperationtype("单位信息");	
 		 				}else if(ali.getObject_status()==14){
 		 					hod.setOperationtype("库信息");	
 		 				}else if(ali.getObject_status()==15||(ali.getObject_status()>0 && ali.getObject_status()<=10)){
 		 					hod.setOperationtype("黑名单");	
 		 				}else if(ali.getObject_status()==1000||(ali.getObject_status()>1000 && ali.getObject_status()<=1005)){
 		 					hod.setOperationtype("白名单");	
 		 				}else if(ali.getObject_status()==2000||(ali.getObject_status()>2000 && ali.getObject_status()<=2003)){
 		 					hod.setOperationtype("红名单");	
 		 				}
 		 				UserInfo ui = userDao.findByLogin(ali.getOwner());
 		                long policeStationId = ui.getPoliceStationId();
 		                String policeStation =  policeStationDao.findOne(policeStationId).getStationName();
 		                hod.setOperatorstation(policeStation);
 		 				hod.setOperator(ali.getOwner());
 		 				hod.setOpetime(bartDateFormat.format(ali.getUpdated()));
 		 				
 		 				result.add(hod);
 		 			  }catch(Exception e){
 		                    LOG.error("process history log item error id:"+ali.getId()+",error:",e);
 		                }
 		 					
 		 			}		

				}
		return result;
	}

	@Override
	public List<HistoryOperationDto> findLogByPoliceCloud(
			AuditLogInfoDto auditLogInfoDto, int page, int pageSize) {
		List<PoliceCloudAuditLogInfo> resp = new ArrayList<PoliceCloudAuditLogInfo>();

		List<HistoryOperationDto> result = new ArrayList<HistoryOperationDto>();

		String sqlString = "select a.* FROM " + GlobalConsts.INTELLIF_BASE
				+ "." + GlobalConsts.T_NAME_POLICE_CLOUD_AUDIT_LOG + " a, "
				+ GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_AUDIT_LOG_TYPE
				+ " b where a.object_status=b.type_id";

		if (auditLogInfoDto.getStarttime() != null
				&& auditLogInfoDto.getEndtime() != null) {
			sqlString += " and updated between '"
					+ auditLogInfoDto.getStarttime() + "' and '"
					+ auditLogInfoDto.getEndtime() + "'";
		}

		if (auditLogInfoDto.getPoliceId() != null
				&& !auditLogInfoDto.getPoliceId().equals("")) {
			sqlString += " and police_id = '" + auditLogInfoDto.getPoliceId()
					+ "'";
		}

		if (auditLogInfoDto.getObject_status() >= 16
				&& auditLogInfoDto.getObject_status() <= 18) {
			sqlString += " and object_status="
					+ auditLogInfoDto.getObject_status();
		}

		if (auditLogInfoDto.getKeywords() != null
				&& !"".equals(auditLogInfoDto.getKeywords())) {
			sqlString += " and ( message like \"%"
					+ auditLogInfoDto.getKeywords() + "%\" or title like \"%"
					+ auditLogInfoDto.getKeywords()
					+ "%\" or b.type_name like \"%"
					+ auditLogInfoDto.getKeywords() + "%\")";
		}

		hisopmaxpage = getMaxPage(sqlString);
		sqlString += " order by id desc LIMIT " + (page - 1) * pageSize + ","
				+ pageSize + "";

		try {
			Query query = this.entityManager.createNativeQuery(sqlString,
					PoliceCloudAuditLogInfo.class);
			resp = (ArrayList<PoliceCloudAuditLogInfo>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		// 把结果集封装成前端要的对象返回
		for (int i = 0; i < resp.size(); i++) {

			PoliceCloudAuditLogInfo ali = resp.get(i);
			HistoryOperationDto hod = new HistoryOperationDto();
			hod.setFriDetail(ali.getFriDetail());
			hod.setSecDetail(ali.getSecDetail());
			hod.setDetail(ali.getMessage());
			hod.setId(pageSize * (page - 1) + i + 1);

			if (ali.getObject_status() == 16) {
				hod.setOperationtype("身份查询");
			} else if (ali.getObject_status() == 17) {
				hod.setOperationtype("人脸检索");
			} else if (ali.getObject_status() == 18) {
				hod.setOperationtype("布控人员");
			}

			hod.setPoliceId(ali.getPoliceId());
			hod.setOpetime(bartDateFormat.format(ali.getUpdated()));
			result.add(hod);
		}
		return result;
	}

	// 登录统计查询
	@Override
	public LinkedMap findLoginInformation(AuditLogInfoDto auditloginfodto,
			int page, int pageSize) {

		// 对象中 只包含starttime和endtime两个字段 如果时间间隔在一天之内 我就返回详细登录信息给前端 如果有时间间隔
		// 就返回每天的登录人数

		Date startdate = new Date();
		Date enddate = new Date();
		int days = 0;

		try {
			enddate = sqlDateFormat.parse(auditloginfodto.getEndtime());
			startdate = sqlDateFormat.parse(auditloginfodto.getStarttime());

			if (((enddate.getTime() - startdate.getTime()) % 86400000) == 0) {

				days = (int) ((enddate.getTime() - startdate.getTime()) / 86400000);
			} else {

				days = (int) ((enddate.getTime() - startdate.getTime()) / 86400000) + 1;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
			System.out.println("格式化日期失败");
		}

		System.out.println("间隔天数：" + days);

		LinkedMap loginmap = new LinkedMap();

		if (days > 1) {

			List<Object[]> resp = null;
			String sqlString;
			sqlString = " select date(a.created) ,count(distinct a.owner) from "
					+ GlobalConsts.INTELLIF_BASE
					+ "."
					+ GlobalConsts.T_NAME_AUDIT_LOG
					+ " a,"
					+ GlobalConsts.INTELLIF_BASE
					+ "."
					+ GlobalConsts.T_NAME_USER
					+ " b where b.login=a.owner and date(a.created) between '"
					+ auditloginfodto.getStarttime().split(" ")[0]
					+ "' and '"
					+ auditloginfodto.getEndtime().split(" ")[0] + "'";

			// 还得加上单位限制
			if (auditloginfodto.getStationid() != ""
					&& auditloginfodto.getStationid() != null) {

				sqlString += " and b.police_station_id= "
						+ auditloginfodto.getStationid();

			}
			sqlString += "  group by date(a.created)  ";

			System.out
					.println("######################################################################################");
			System.out.println(sqlString);

			try {
				Query query = this.entityManager.createNativeQuery(sqlString);
				resp = (ArrayList<Object[]>) query.getResultList();

			} catch (Exception e) {
				LOG.error("", e);
			} finally {
				entityManager.close();
			}
			// 把结果集封装成前端要的对象返回
			if (resp != null) {
				for (int i = 0; i < resp.size(); i++) {
					Object[] o = resp.get(i);
					loginmap.put(loginfDateFormat.format(o[0]), o[1]);

				}
			}
			return loginmap;

		} else {

			loginmap2 = new LinkedMap(); // 封装另一种格式在今日登陆用户 包含ip以及post信息的

			// String sqlString
			// ="SELECT * FROM  "+GlobalConsts.T_NAME_AUDIT_LOG+" where id in (select max(id) from "+GlobalConsts.T_NAME_AUDIT_LOG+" where  date(created)=\""+loginfDateFormat.format(auditloginfo.getStarttime())+"\" and operation=\"log in\" group by owner)";
			String sqlString = "";
			sqlString = "SELECT a.*  FROM  "
					+ GlobalConsts.T_NAME_AUDIT_LOG
					+ " a,"
					+ GlobalConsts.T_NAME_USER
					+ " b where a.id in (select max(id) from "
					+ GlobalConsts.T_NAME_AUDIT_LOG
					+ " where  date(created)=\""
					+ auditloginfodto.getStarttime().split(" ")[0]
					+ "\" and operation=\"log in\" group by owner)  and b.login=a.owner";
			// sqlString =
			// "SELECT a.*  FROM  "+GlobalConsts.T_NAME_AUDIT_LOG+" a where a.id in (select max(id) from "+GlobalConsts.T_NAME_AUDIT_LOG+" where  date(created)=\""+auditloginfodto.getStarttime().split(" ")[0]+"\" and operation=\"log in\" group by owner)";

			// 还得加上单位限制
			if (auditloginfodto.getStationid() != null
					&& !"".equals(auditloginfodto.getStationid())) {

				sqlString += " and b.police_station_id= "
						+ auditloginfodto.getStationid();

			}

			sqlString += " order by date(a.created) desc ";

			System.out
					.println("######################################################################################");
			System.out.println(sqlString);
			List<AuditLogInfo> resp = null;

			try {
				Query query = this.entityManager.createNativeQuery(sqlString,
						AuditLogInfo.class);

				resp = (ArrayList<AuditLogInfo>) query.getResultList();
			} catch (Exception e) {
				LOG.error("", e);
			} finally {
				entityManager.close();
			}

			// 把结果集封装成前端要的对象返回
			if (resp != null) {
				for (int i = 0; i < resp.size(); i++) {

					AuditLogInfo h = resp.get(i);

					if (h.getTitle().split(",").length > 2) {

						loginmap.put(
								h.getOwner(),
								sqlDateFormat.format(h.getCreated()) + ","
										+ h.getMessage() + ","
										+ h.getTitle().split(",")[2]);

						if (h.getTitle().split(",").length > 3) {

							loginmap2.put(
									h.getOwner(),
									h.getTitle().split(",")[1]
											+ ","
											+ h.getTitle().split(",")[3]
											+ ","
											+ h.getTitle().split(",")[2]
											+ ","
											+ h.getTitle().split(",")[4]
											+ ","
											+ h.getTitle().split(",")[5]
											+ ","
											+ sqlDateFormat.format(h
													.getCreated()) + ","
											+ h.getOwner()); // 最后补充上login字段作为警号

						} else {

							loginmap2.put(
									h.getOwner(),
									h.getTitle().split(",")[1]
											+ ", ,"
											+ h.getTitle().split(",")[2]
											+ ", , ,"
											+ sqlDateFormat.format(h
													.getCreated()) + ","
											+ h.getOwner());// 最后补充上login字段作为警号
						}

					} else {

						loginmap.put(
								h.getOwner(),
								sqlDateFormat.format(h.getCreated()) + ","
										+ h.getMessage() + "," + ""); // 针对数据库存在的空数据样本

					}

				}
			}

			// 注意 这里的分页不能加在 sql语句里面 因为我的去重没在sql里做 而是在linkmap里做的
			logusermaxpage = BigInteger.valueOf(loginmap2.size());
			return loginmap;

		}
	}

	// 操作记录导出
	@Override
	public String exportAuditExcel(AuditLogInfoDto auditloginfodto, int key) {

		Boolean exportResult = false;
		// webbook对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 在webbook中添加一个sheet 对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("操作记录表");
		// 在sheet中添加表头第0行
		HSSFRow row = sheet.createRow((int) 0);
		// 创建单元格
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

		@SuppressWarnings("deprecation")
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("类型");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("操作记录");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("检索事由");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("检索原因");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("操作人员");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("单位");
		cell.setCellStyle(style);
		cell = row.createCell((short) 7);
		cell.setCellValue("时间");
		cell.setCellStyle(style);

		// 第五步，写入实体数据 实际应用中这些数据从数据库得到，
		ArrayList<HistoryOperationDto> list = findAllByCombinedConditions(auditloginfodto);

		if (list.size() == 0) {
			return "数据为空";
		}

		ProcessInfo process = GlobalConsts.downloadAuditLogMap.get(key);
		process.setTotalSize(list.size());

		while (GlobalConsts.downloadAuditLogMapStop.get(key)) {
			for (int i = 0; i < list.size(); i++) {

				try {
					row = sheet.createRow((int) i + 1);
					HistoryOperationDto opr = (HistoryOperationDto) list.get(i);
					// 第四步，创建单元格，并设置值
					// row.createCell((short) 0).setCellValue((double)
					// opr.getId());
					row.createCell((short) 0).setCellValue(i + 1);
					row.createCell((short) 1).setCellValue(
							opr.getOperationtype());
					row.createCell((short) 2).setCellValue(opr.getDetail());
					row.createCell((short) 3).setCellValue(opr.getFriDetail());
					row.createCell((short) 4).setCellValue(opr.getSecDetail());
					row.createCell((short) 5).setCellValue(opr.getOperator());
					row.createCell((short) 6).setCellValue(
							opr.getOperatorstation());
					row.createCell((short) 7).setCellValue(opr.getOpetime());
					process.setSuccessNum(process.getSuccessNum() + 1);
				} catch (Exception e) {
					process.setFailedNum(process.getFailedNum() + 1);
				}

			}
			GlobalConsts.downloadAuditLogMapStop.put(key, false);

		}

		// 自适应调整列宽
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);

		// 第六步，将文件存到指定位置
		String FilePath = "";
		String httppath = "";
		try {
			String userName = CurUserInfoUtil.getUserInfo().getName();
			int rand = CommonUtil.getRandomNumber(2);
			String randPath = userName + "_"
					+ Calendar.getInstance().getTime().getTime() + "_" + rand;

			FilePath = FileUtil.getZipUrl(propertiesBean.getIsJar())
					+ "export/image/" + randPath
					+ "/historyOperation/audit.xls";
			File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
					+ "export/image/" + randPath + "/historyOperation/");
			FileUtil.checkFileExist(file);
			// FileOutputStream fout = new
			// FileOutputStream("G:/historyOperation.xls");
			// File filePath = new File(
			// FileUtil.getZipUrl(propertiesBean.getIsJar())+ "export/image/" +
			// randPath + "/historyOperation/audit.xls");
			// FileUtil.checkFileExist(filePath);
			FileOutputStream fout = new FileOutputStream(FilePath);
			wb.write(fout);
			fout.close();
			exportResult = true;
			httppath = FileUtil.getZipHttpUrl(propertiesBean.getIsJar())
					+ "export/image/" + randPath
					+ "/historyOperation/audit.xls";
			return httppath;

		} catch (Exception e) {

			exportResult = false;
			e.printStackTrace();
			return "exporting failed";

		}

	}

	public BigInteger getMaxPage(String sql) {

		BigInteger maxpage = null;
		String getmaxpagesql = "";
		if (sql.indexOf("a.*") > 0) {
			getmaxpagesql = sql.replace("a.*", "count(*)");
		} else {
			getmaxpagesql = sql.replace("*", "count(*)");
		}

		System.out.println("此处获取最大页数的sql语句是：" + getmaxpagesql);
		ArrayList resp = null;

		try {
			Query query = this.entityManager.createNativeQuery(getmaxpagesql);

			resp = (ArrayList) query.getResultList();

		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		if (resp != null && resp.size() != 0) {
			maxpage = (BigInteger) resp.get(0);
		}

		return maxpage;

	}

	@Override
	public List<HistorySearchOperationDto> findSearchAudit(int page,
			int pageSize) {

		List<HistorySearchOperationDto> resp = null;

		/*
		 * String sql =
		 * "select a.owner as operator,a.created as opetime,a.message as faceUrl,a.object as dataType,a.object_id as faceId,a.fri_detail,a.sec_detail,c.cn_name as operatorRole from "
		 * + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_AUDIT_LOG
		 * +" a, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
		 * + " b ," + GlobalConsts.INTELLIF_BASE + "." +
		 * GlobalConsts.T_NAME_ROLE +
		 * " c where a.owner=b.login and b.role_ids=c.id and fri_detail !=''";
		 */

		String sql = "select a.id,a.created as opetime,a.message as face_url,a.object as data_type,a.object_id as face_id from "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_AUDIT_LOG + " a where a.fri_detail !=''";

		sql += " order by a.id desc LIMIT " + (page - 1) * pageSize + ","
				+ pageSize + "";

		try {
			Query query = this.entityManager.createNativeQuery(sql,
					HistorySearchOperationDto.class);
			resp = query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		// 把结果集封装成前端要的对象返回
		if (resp != null) {
			Iterator<HistorySearchOperationDto> audit = resp.iterator();
			while (audit.hasNext()) {

				HistorySearchOperationDto searchAudit = audit.next();
				String faceUrl = searchAudit.getFaceUrl();
				if (faceUrl != "" && faceUrl.split("，").length > 1) {
					faceUrl = faceUrl.split("，")[1];
				}
				searchAudit.setFaceUrl(faceUrl);

				// 过滤旧数据
				String type = searchAudit.getDataType();

				if (type == null || type.equals("")) {
					audit.remove();
				} else {
					int dateType = 0;
					try {
						dateType = Integer.valueOf(searchAudit.getDataType())
								.intValue();
					} catch (NumberFormatException e) {
						audit.remove();
					}
					if (dateType > 10) {
						audit.remove();
					}
				}

			}

		}

		return resp;

	}

	@Override
	public HistorySearchOperationDetailDto findSearchAuditDeatil(long auditId) {

		List<HistorySearchOperationDetailDto> resp = null;
		HistorySearchOperationDetailDto searchAuditDetail = null;

		String sql = "select a.id,a.owner as operator,a.created as opetimestamp,a.message as face_url,a.object as data_type,a.object_id as face_id,a.fri_detail,a.sec_detail,c.cn_name as operator_role from "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_AUDIT_LOG
				+ " a, "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_USER
				+ " b ,"
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_ROLE
				+ " c where a.owner=b.login and b.role_ids=c.id and fri_detail !='' and a.id="
				+ auditId;

		try {
			Query query = this.entityManager.createNativeQuery(sql,
					HistorySearchOperationDetailDto.class);
			resp = query.getResultList();
			if(null == resp || resp.size() == 0) {
				searchAuditDetail = new HistorySearchOperationDetailDto();
				searchAuditDetail.setOperator("");
				return searchAuditDetail;
			}
			searchAuditDetail = resp.get(0);
			String faceUrl = resp.get(0).getFaceUrl();
			if (faceUrl != "" && faceUrl.split("，").length > 1) {
				faceUrl = faceUrl.split("，")[1];
			} 
			searchAuditDetail.setFaceUrl(faceUrl);

			searchAuditDetail.setOpetime(sqlDateFormat
					.format(searchAuditDetail.getOpetimestamp()));

		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		return searchAuditDetail;

	}

}
