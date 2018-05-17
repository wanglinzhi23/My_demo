package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.RoleDao;
import intellif.database.dao.AlarmInfoDao;
import intellif.database.dao.AreaAndBlackDetailDao;
import intellif.database.dao.AreaDao;
import intellif.database.dao.BlackBankDao;
import intellif.database.dao.BlackDetailDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.PersonDetailDao;
import intellif.database.dao.UserAreaDao;
import intellif.database.dao.UserDao;
import intellif.database.dao.impl.AreaDaoImpl;
import intellif.database.dao.impl.UserDaoImpl;
import intellif.database.entity.Area;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.UserArea;
import intellif.database.entity.UserInfo;
import intellif.dto.BankDisplayInfo;
import intellif.dto.JsonObject;
import intellif.dto.MonitorAreaInfo;
import intellif.dto.PersonFullDto;
import intellif.dto.PersonQueryDto;
import intellif.enums.SourceTypes;
import intellif.exception.MsgException;
import intellif.ifaas.EBListIoctrlType;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.BlackFeatureServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.database.entity.AlarmPersonDetail;
import intellif.database.entity.BlackDetailRealName;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * The Class PersonDetailServiceImpl.
 */
@Service
public class PersonDetailServiceImpl extends AbstractCommonServiceImpl<PersonDetail> implements PersonDetailServiceItf<PersonDetail> {

    private static Logger LOG = LogManager.getLogger(PersonDetailServiceImpl.class);

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private PersonDetailDao personDetailDao;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    AreaAndBlackDetailDao areaAndBlackDao;
    @Autowired
    CameraAndBlackDetailDao cameraAndBlackDao;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BlackBankDao bankDao;
    @Autowired
    private AreaDaoImpl areaDao;
    @Autowired
    private AlarmInfoDao alarmDao;
    @Autowired
    private BlackFeatureServiceItf featureService;
    @Autowired
    private UserAreaDao<UserArea> userAreaDao;
    
    @Override
    public List<PersonDetail> query(PersonQueryDto personQueryDto, int page, int pageSize) {
         Tree tree = LocalCache.tree;
        String cameraIds = personQueryDto.getCameraIds();
        List<PersonDetail> resp = null;
        String bankSql = null;
        String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
        if(StringUtils.isNotBlank(authority)){
            bankSql = "b.bank_id in("+authority+")";
        }else{
            bankSql = "1!=1";
        }
        String sqlString = "SELECT distinct a.* FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " a left join " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " b on a.crime_type = b.id left join " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_FRI_TYPE + " c on b.fri_id = c.id ";
        if (StringUtil.isNotBlank(cameraIds)) {
            Set<Long> areaSet = new HashSet<Long>();
            String[] cIdList = cameraIds.split(",");
            for(String item : cIdList){
                CameraInfo ci = (CameraInfo) tree.treeNodeWithOutTreeInfo(CameraInfo.class, Long.valueOf(item));
                areaSet.add(ci.getStationId());
            }
            StringBuffer sb = new StringBuffer();
            for(Long areaId : areaSet){
                sb.append(",");
                sb.append(areaId);
            }
            String areaIds = sb.toString().substring(1);
            
            String cameraSql = "select b.id,b.from_person_id from "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL
                    +" b LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_BLACKDETAIL
                    +" cb on cb.blackdetail_id = b.id WHERE "+bankSql+" and cb.camera_id in("+cameraIds+")";
            String areaSql = "select b.id,b.from_person_id from "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL
                    +" b LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_AREA_BLACKDETAIL
                    +" ab on ab.blackdetail_id = b.id WHERE "+bankSql+" and ab.area_id in("+areaIds+")";
            String cameraAreaSql = "("+cameraSql+" union "+areaSql+")f ";
            
            sqlString += ", " + cameraAreaSql;
        }

        sqlString += " WHERE  a.is_urgent = 0 ";
        if (StringUtil.isNotBlank(cameraIds)) {
            sqlString += " and f.from_person_id = a.id ";
        }

        /************** begin V1.1.0 版本修改，修复BUG: 277（http://192.168.2.150:81/zentao/bug-view-277.html） by pengqirong @ 2016-10-09  *******/
        if (null != personQueryDto.getBankId()) {
        	sqlString += " and a.bank_id = " + personQueryDto.getBankId() + " ";
        }
        /************** begin V1.1.0 版本修改，修复BUG: 277（http://192.168.2.150:81/zentao/bug-view-277.html） by pengqirong @ 2016-10-09  *******/

        if (null != personQueryDto.getCrimeAddr() && !"".equals(personQueryDto.getCrimeAddr())) {
            sqlString += " AND (";
//            for (String addr : personQueryDto.getCrimeAddr().split(" ")) {
//                sqlString += "a.crime_address like '%:addr%' or ";
//            }
            for (int i = 0, length = personQueryDto.getCrimeAddr().split(" ").length; i < length; i++) {
                sqlString += "a.crime_address like :addr" + i + " or ";
            }
            sqlString = sqlString.substring(0, sqlString.lastIndexOf("or"));
            sqlString += ") ";
        }
        if (null != personQueryDto.getCid() && !"".equals(personQueryDto.getCid())) {
            sqlString += "AND a.cid like :cid ";
        }
        if (personQueryDto.getGender() >= 1 && personQueryDto.getGender() <= 2) {
            sqlString += "AND a.real_gender =" + personQueryDto.getGender() + " ";
        }
        if (null != personQueryDto.getAddress() && !"".equals(personQueryDto.getAddress())) {
            sqlString += "AND a.address like :address ";
        }
        if (personQueryDto.getCrimeSecType() > 0) {
            sqlString += "AND a.crime_type =" + personQueryDto.getCrimeSecType() + " ";
        }
        if (personQueryDto.getCrimeFriType() > 0) {
            sqlString += "AND b.fri_id =" + personQueryDto.getCrimeFriType() + " ";
        }
        if (null != personQueryDto.getQueryText() && !"".equals(personQueryDto.getQueryText())) {
            sqlString += "AND (a.address like :baddress " +
                    "or a.cid like :bcid " +
                    "or a.crime_address like :bcrimeAddress " +
                    "or a.description like :bdescription " +
                    "or a.real_name like :realName " +
                    "or b.name like :name " +
                    "or c.short_name like :shortName " +
                    "or c.full_name like :fullName) ";
//					"or f.short_name like '%" + personQueryDto.getQueryText() + "%' " +
//					"or f.name like '%" + personQueryDto.getQueryText() + "%' " +
//					"or f.addr like '%" + personQueryDto.getQueryText() + "%') ";
        }
       /* if (null != personQueryDto.getStarttime() && !"".equals(personQueryDto.getStarttime()) && null != personQueryDto.getEndtime() && !"".equals(personQueryDto.getEndtime())) {
            sqlString += "AND a.created BETWEEN str_to_date('" + personQueryDto.getStarttime() + "','%Y-%m-%d %T') AND str_to_date('" + personQueryDto.getEndtime() + "','%Y-%m-%d %T') ";
        }*/
        if (null != personQueryDto.getStarttime() && !"".equals(personQueryDto.getStarttime())) {
            sqlString += " AND a.created >='" + personQueryDto.getStarttime() + "'";
        }
        if (null != personQueryDto.getEndtime() && !"".equals(personQueryDto.getEndtime())) {
            sqlString += " AND a.created <='" + personQueryDto.getEndtime() + "' ";
        }
        sqlString += "order by a.created desc LIMIT " + (page - 1) * pageSize + "," + (pageSize * 3) + "";

		sqlString= _userService.processAuthority(sqlString);
        try {
            Query query = this.em.createNativeQuery(sqlString, PersonDetail.class);
//            for (String addr : personQueryDto.getCrimeAddr().split(" ")) {
//                query.setParameter("addr", addr);
//            }
            if (null != personQueryDto.getCrimeAddr() && !"".equals(personQueryDto.getCrimeAddr())) {
            	String[] crimeaddrs = personQueryDto.getCrimeAddr().split(" ");
            	for (int i = 0, length = crimeaddrs.length; i < length; i++) {
            		String paramkey = "addr" + i;
            		query.setParameter(paramkey, "%" + crimeaddrs[i] + "%");
            	}
            	
            }
            
            if (null != personQueryDto.getCid() && !"".equals(personQueryDto.getCid())){
            	query.setParameter("cid", "%" + personQueryDto.getCid() + "%");
            }
            if (null != personQueryDto.getAddress() && !"".equals(personQueryDto.getAddress())) {
            	query.setParameter("address", "%" + personQueryDto.getAddress() + "%");
            }
            String baddress = personQueryDto.getQueryText();
            String bcid = personQueryDto.getQueryText();
            String bcrimeAddress = personQueryDto.getQueryText();
            String bdescription = personQueryDto.getQueryText();
            String realName = personQueryDto.getQueryText();
            String name = personQueryDto.getQueryText();
            String shortName = personQueryDto.getQueryText();
            String fullName = personQueryDto.getQueryText();
            if (null != personQueryDto.getQueryText() && !"".equals(personQueryDto.getQueryText())) {
            	query.setParameter("baddress", "%" + baddress + "%");
            	query.setParameter("bcid", "%" + bcid + "%");
            	query.setParameter("bcrimeAddress", "%" + bcrimeAddress + "%");
            	query.setParameter("bdescription", "%" + bdescription + "%");
            	query.setParameter("realName", "%" + realName + "%");
            	query.setParameter("name", "%" + name + "%");
            	query.setParameter("shortName","%" + shortName + "%");
            	query.setParameter("fullName", "%" + fullName + "%");
            }
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }
        return resp;
    }


    @Override
    public void refreshPerson() throws TException {
      /*  @Modifying
        @Transactional
        @Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" SET status = 0 WHERE endtime < sysdate() OR starttime > sysdate()", nativeQuery = true)
        void refreshStopStatus();
        
        @Modifying
        @Transactional
        @Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" SET status = 1 WHERE endtime >= sysdate() AND starttime <= sysdate()", nativeQuery = true)
        void refreshStartStatus();*/
        String fSql = "(endtime < sysdate() OR starttime > sysdate()) AND status = 1";
        List<PersonDetail> personStopList = personDetailDao.findByFilter(fSql);
        for (PersonDetail person : personStopList) {
            String bSql = "from_person_id = "+person.getId();
            List<BlackDetail> blackList = _blackDetailDao.findByFilter(bSql);
            if (blackList.size() > 0) {
                String aSql = " blackdetail_id = "+blackList.get(0).getId();
                List<AreaAndBlackDetail> list = areaAndBlackDao.findByFilter(aSql);
                if (list.size() > 0) {
                    for (AreaAndBlackDetail areaAndBlackDetail : list) {
                        ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), SourceTypes.CAMERA.getValue(), areaAndBlackDetail.getAreaId(), person.getId());
                    }
                }
            }
        }
        String updateSql = " status = 0 ";
        String filterSql = " endtime < sysdate() OR starttime > sysdate()";
        personDetailDao.jdbcBatchUpdate(filterSql, updateSql);
        String fSql1 = "WHERE  endtime >= sysdate() AND starttime <= sysdate() AND status = 0";
        List<PersonDetail> personStartList = personDetailDao.findByFilter(fSql1);
        for (PersonDetail person : personStartList) {
            String bSql1 = "from_person_id = "+person.getId();
            List<BlackDetail> blackList = areaAndBlackDao.findByFilter(bSql1);
            if (blackList.size() > 0) {
                String aSql1 = " blackdetail_id = "+blackList.get(0).getId();
                List<AreaAndBlackDetail> list = areaAndBlackDao.findByFilter(aSql1);
                if (list.size() > 0) {
                    for (AreaAndBlackDetail areaAndBlackDetail : list) {
                        ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_ADD_PERSON.getValue(), SourceTypes.CAMERA.getValue(), areaAndBlackDetail.getAreaId(), person.getId());
                    }
                }
            }
        }
        String updateSql1 = "status = 1";
        String filterSql1 = "endtime >= sysdate() AND starttime <= sysdate()";
        personDetailDao.jdbcBatchUpdate(filterSql1, updateSql1);
        //TODO:update personDetail related ruleId staff.
       
    }
    
    
    @Override
    public boolean refreshPersonStatus(PersonDetail pd) throws TException {
    	boolean status = false;
    	if(pd.getStarttime().equals(pd.getEndtime())&& pd.getStatus()==1){
    		/*//布控开关为off
    		List<BlackDetail> blackList = this._blackDetailDao.findByFromPersonId(pd.getId());
    		if (blackList.size() > 0) {
    			List<CameraAndBlackDetail> list = this._cameraAndBlackDetailRepository.findByBlackdetailId(blackList.get(0).getId());
    			if (list.size() > 0) {
    				for (CameraAndBlackDetail cameraAndBlackDetail : list) {
    					ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), SourceTypes.CAMERA.getValue(), cameraAndBlackDetail.getCameraId(), pd.getId());
    				}
    			}
    		}*/
    		pd.setStatus(0);
    		personDetailDao.save(pd);
    		status = true;
    	}
    	else if(!pd.getStarttime().equals(pd.getEndtime())&&pd.getStatus()==0){
    		/*List<BlackDetail> blackList1 = this._blackDetailDao.findByFromPersonId(pd.getId());
    		if (blackList1.size() > 0) {
    			List<CameraAndBlackDetail> list = this._cameraAndBlackDetailRepository.findByBlackdetailId(blackList1.get(0).getId());
    			if (list.size() > 0) {
    				for (CameraAndBlackDetail cameraAndBlackDetail : list) {
    					ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_ADD_PERSON.getValue(), SourceTypes.CAMERA.getValue(), cameraAndBlackDetail.getCameraId(), pd.getId());
    				}
    			}
    		}*/
    		
    		pd.setStatus(1);
    		personDetailDao.save(pd);
    		status = true;
    	}
        //TODO:update personDetail related ruleId staff.
      return status;
    }
    
   
    
	@SuppressWarnings("unchecked")
    @Override
	public PageDto<PersonFullDto> findByBankId(BankDisplayInfo bdi) {
		List<PersonFullDto> resp = null;
		String type = bdi.getbType();
		int page = bdi.getPage();
		if(page < 1){
		    page = 1;
		}
		int pageSize = bdi.getPageSize();
		//条件语句
		String fSql = " 1 = 1 and bank_id = "+bdi.getId();
		if(StringUtil.isNotBlank(type)){
		    fSql += " AND type = "+type; 
		}
		if (StringUtil.isNotBlank(bdi.getName())) {
		            fSql += " AND real_name like '%"+bdi.getName()+"%'";
		        }
		 if(StringUtil.isNotBlank(bdi.getBkstartime()) && StringUtil.isNotBlank(bdi.getBkendime())){
		     String stime = bdi.getBkstartime() + " 00:00:00";
		        String etime = bdi.getBkendime() + " 24:00:00";
		            fSql += " AND created >= '"+stime+"' and created <= '"+etime+"'";
		        }
		 
		 //基础查询语句       
		 String bSql = "select * from " + GlobalConsts.INTELLIF_BASE+ "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" where "+fSql+" limit "+(page-1)*pageSize+","+pageSize;      
		 //统计总数语句
		 String countSql = "select count(1) from " + GlobalConsts.INTELLIF_BASE+ "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" where "+fSql;
		 //查询字段语句
		 String selSql =  "SELECT CONCAT(c.full_name,'-',b.name) as crime_name,0 as area_name,a.* FROM "; 
		 //join语句
		String joinSql = " LEFT JOIN "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_BLACK_DETAIL
				+ " e  on a.id = e.from_person_id LEFT JOIN "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_CRIME_SEC_TYPE
				+ " b on a.crime_type = b.id LEFT JOIN "
				+ GlobalConsts.INTELLIF_BASE
				+ "."
				+ GlobalConsts.T_NAME_CRIME_FRI_TYPE
				+ " c on c.id = b.fri_id ";
		
		String exeSql = selSql +" ("+bSql+")a "+joinSql+" group by a.id";
	     LOG.info("exeSql:"+exeSql);
	        try {
	            Query query = this.em.createNativeQuery(exeSql, PersonFullDto.class);
	            resp = (ArrayList<PersonFullDto>) query.getResultList();
	        } catch (Exception e) {
	            LOG.error("", e);
	        } finally {
	            em.close();
	        }
	        Long count = jdbcTemplate.queryForObject(countSql, Long.class);   
	        
	        PageDto<PersonFullDto> pageDto = new PageDto<PersonFullDto>(resp, count, page,pageSize);
	        
		return pageDto;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public PageDto<PersonFullDto> findByQueryParams(PersonQueryDto pqd) {
	    String areaIds = pqd.getAreaIds();
	    if(StringUtils.isNotBlank(areaIds)){
	        String fSql = "area_id in("+areaIds+")";
	        List<UserArea> uaList = userAreaDao.findByFilter(fSql);
	        if(!CollectionUtils.isEmpty(uaList)){
	            for(UserArea ua : uaList){
	                _userService.isUserOperationAccess(ua.getUserId());
	            }
	        }
	    }else{
	      long userId = CurUserInfoUtil.getUserInfo().getId();
	      String fSql1 = "user_id ="+userId;
          List<UserArea> uaList = userAreaDao.findByFilter(fSql1);
          if(!CollectionUtils.isEmpty(uaList)){
              List<Long> curcList = uaList.stream().map(s -> s.getAreaId()).collect(Collectors.toList());
              areaIds = StringUtils.join(curcList, ",");
          }
	    }
	    String aSql = null;
	    if(StringUtils.isNotBlank(areaIds)){
	         aSql = " station_id in("+areaIds+")";
	    }else{
	        aSql = " 1 != 1";
	    }
	    List<BlackBank> bbList = bankDao.findByFilter(aSql);
	    
	  
	    
        List<PersonFullDto> resp = null;
        int page = pqd.getPage();
        if(page < 1){
            page = 1;
        }
        int pageSize = pqd.getPageSize();
       
        String fSql = " 1 = 1 and owner = '"+ CurUserInfoUtil.getUserInfo().getLogin()+"'";
        
        if(!CollectionUtils.isEmpty(bbList)){
            List<Long> bIdList = bbList.stream().map(s -> s.getId()).collect(Collectors.toList());
            String bIds = StringUtils.join(bIdList, ",");
            fSql += " and bank_id in("+bIds+")";
        }else{
            fSql += " and 1 != 1";
        }
        //条件语句
     
        if (StringUtil.isNotBlank(pqd.getQueryText())) {
                    fSql += " AND real_name like '%"+pqd.getQueryText()+"%'";
                }
       
         //基础查询语句       
         String bSql = "select * from " + GlobalConsts.INTELLIF_BASE+ "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" where "+fSql+" order by created desc limit "+(page-1)*pageSize+","+pageSize;      
         //统计总数语句
         String countSql = "select count(1) from " + GlobalConsts.INTELLIF_BASE+ "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" where "+fSql;
         //查询字段语句
         String selSql =  "SELECT CONCAT(c.full_name,'-',b.name) as crime_name,0 as area_name,a.* FROM "; 
         //join语句
        String joinSql = "  LEFT JOIN "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_CRIME_SEC_TYPE
                + " b on a.crime_type = b.id LEFT JOIN "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_CRIME_FRI_TYPE
                + " c on c.id = b.fri_id ";
        
        String exeSql = selSql +" ("+bSql+")a "+joinSql;
         LOG.info("exeSql:"+exeSql);
            try {
                resp = personDetailDao.getPersonsBySql(exeSql);
            } catch (Exception e) {
                LOG.error("personDetailService:findByQueryParams(),e:", e);
            } 
            Long count = super.countBySql(countSql);
            
            PageDto<PersonFullDto> pageDto = new PageDto<PersonFullDto>(resp, count, page,pageSize);
            return pageDto;
    }
	
	@Override
	public List<BlackDetailRealName> findBlackDetailByBankId(long id) {
		List<BlackDetailRealName> resp = null;
		String sqlString = "SELECT e.*, a.real_name FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" a, "
		+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" e "+			
		"WHERE a.bank_id = "+id+" AND e.from_person_id = a.id group by a.id";
		
		try {
			Query query = this.em.createNativeQuery(sqlString, BlackDetailRealName.class);
			resp = (ArrayList<BlackDetailRealName>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			em.close();
		}
		return resp;
	}
	
	@Override
	public List<BlackDetailRealName> findAllGreaterId(long bankid, long blackid) {
		List<BlackDetailRealName> resp = null;
		String sqlString = "SELECT e.*, a.real_name FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" a, "
        + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" e "			
		+ "WHERE a.bank_id = "+bankid+" AND e.from_person_id = a.id AND e.id > " +blackid+ " group by a.id";
		try {
			Query query = this.em.createNativeQuery(sqlString, BlackDetailRealName.class);
			resp = (ArrayList<BlackDetailRealName>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			em.close();
		}
		return resp;
	}
	
	@Override
	public List<BlackDetailRealName> findAllLessId(long bankid, long blackid) {
		List<BlackDetailRealName> resp = null;
		String sqlString = "SELECT e.*, a.real_name FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" a, "
				+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" e "+
		"WHERE a.bank_id = "+bankid+" and e.from_person_id = a.id AND e.id <= " +blackid+ " group by a.id";
		try {
			Query query = this.em.createNativeQuery(sqlString, BlackDetailRealName.class);
			resp = (ArrayList<BlackDetailRealName>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			em.close();
		}
		return resp;
	}

	@Override
	public List<AlarmPersonDetail> findAlarmPersonDetail(String[] personids) {
		List<AlarmPersonDetail> resp = null;
		String sqlString = "SELECT person.id, person.real_name, person.real_gender, person.description, person.address, person.crime_address, crime.full_name as crime_name, subcrime.name as subcrime_name, bank.bank_name, person.starttime, person.endtime FROM " 
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " person, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_FRI_TYPE + " crime, "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " subcrime, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " bank "
				+ "WHERE person.id in (:ids) and subcrime.id = person.crime_type and subcrime.fri_id = crime.id and person.bank_id = bank.id";
		try {
			Query query = this.em.createNativeQuery(sqlString, AlarmPersonDetail.class);
			query.setParameter("ids", Arrays.asList(personids));
			resp = query.getResultList();
		} catch (Exception e)  {
			e.printStackTrace();
		} finally {
			em.close();
		}
		return resp;
	}

	@Override
	public Map<Long, List<String>> findPersonArea(String[] personids) {
		Map<Long, List<String>> resp = new HashMap<>();
		if (personids != null) {
			for (String pid : personids) {
			    try{
			    Long pId = Long.valueOf(pid);
			    Set<String> areaNameSet = new HashSet<String>();
			    List<BigInteger> cList = cameraAndBlackDao.findCameraIdsByPersonId(pId);
		        List<Long> curcList = cList.stream().map(s -> s.longValue()).collect(Collectors.toList());
		        List<BigInteger> aList = areaAndBlackDao.findAreaIdsByPersonId(pId);
		        List<Long> curaList = aList.stream().map(s -> s.longValue()).collect(Collectors.toList());
		        Tree tree = LocalCache.tree;
		        if(!CollectionUtils.isEmpty(curaList)){
		            for(Long item : curaList){
		                Area area = (Area) tree.treeNodeWithOutTreeInfo(Area.class, item);
		                areaNameSet.add(area.getAreaName());
		            }
		        }
		        if(!CollectionUtils.isEmpty(curcList)){
                    for(Long item : curcList){
                        CameraInfo camera = (CameraInfo) tree.treeNodeWithOutTreeInfo(CameraInfo.class, item);
                        Area area = (Area) tree.treeNodeWithOutTreeInfo(Area.class, camera.getStationId());
                        areaNameSet.add(area.getAreaName());
                    }
                }
				resp.put(pId, areaNameSet.stream().distinct().collect(Collectors.toList()));
                }catch(Exception e){
                    LOG.error("findPersonArea error,personId:"+pid+",e:",e);
                }
			 }
		}
		return resp;
	}

	
	@Override
	public void refreshPersonOfUpdate(long personId){
		try{
			ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), personId,0,0);
		}catch(Exception e){
			LOG.error("update person to engine error,id:"+personId+",error:",e);
		}
	}
	
	/**
	 * 根据前端布控范围参数，分析出按区域布控集合和按摄像头布控集合
	 */
	@Override
	public Map<String, List<Long>> processParamDataToMap(List<MonitorAreaInfo> areaList) {
	        Map<String, List<Long>> resultMap = new HashMap<String, List<Long>>();
	        UserInfo ui = CurUserInfoUtil.getUserInfo();
	        String roleName = roleRepository.findOne(ui.getRoleId()).getName();
	        boolean isSpecial = roleName.equals(GlobalConsts.SUPER_ADMIN) || (ui.getSpecialSign() > 0);
	        if (null != areaList && !areaList.isEmpty()) {
	            List<Long> sAreaList = new ArrayList<Long>();
	            Set<Long> sCameraList = new HashSet<Long>();
	            Set<Long> uAreaSet = zoneAuthorizeService.idSet(Area.class, null);
	            areaList.forEach(item -> {
	                try {
	                    int isSelect = item.getAllSelected();
	                    List<Long> cList = item.getCameraIds();
	                    if (RequestConsts.blackdetail_area_allselected == isSelect) {
	                        // 区域授权导致界面区域可能显示不了所有摄像头，所以全选不一定是真正全选，需要检查操作
	                        // 全区域搜索账号和超级管理员可以不用此判断
	                        if (isSpecial) {
	                            sAreaList.add(item.getAreaId());
	                        } else {
	                            Tree tree = LocalCache.tree;
	                            List<Long> cameraList = tree.nextIds(Area.class, item.getAreaId());
	                            if (null != cameraList && cameraList.containsAll(item.getCameraIds())) {

	                                // 正常界面选择的区域摄像头都在实际区域摄像头集合内，除非假造数据
	                                //由于存在某用户对某区域所有摄像头有权限，但对于该区域没有权限情况，故需要将此类情况区域按摄像头布控处理
	                                if (cameraList.size() == cList.size() && uAreaSet.contains(item.getAreaId())) {
	                                    // 确认物理全选
	                                    sAreaList.add(item.getAreaId());
	                                } else {
	                                    // 物理非全选，按摄像头布控处理
	                                    sCameraList.addAll(item.getCameraIds());
	                                }
	                            }
	                        }

	                    } else if (RequestConsts.blackdetail_area_notallselected == isSelect) {
	                        sCameraList.addAll(item.getCameraIds());
	                    }
	                } catch (Exception e) {
	                    LOG.error("processParamDataToMap error,areaId:" + item.getAreaId() + ",allSelect:" + item.getAllSelected() + ",e:", e);
	                }
	            });
	            // 所有摄像头进行区域授权过滤
	            List<Long> caList = zoneAuthorizeService.filterIds(CameraInfo.class, sCameraList, null);
	            resultMap.put("area", sAreaList);
	            resultMap.put("camera", caList);
	        }
	        return resultMap;
	    }
	
	

    /**
     * 按区域布控和摄像头布控逻辑，格式化某用户区域授权数据
     */
    @Override
    public void processUserAreaDataToMap(Long userId) {
       
            UserInfo ui = userService.findById(userId);
            String roleName = roleRepository.findOne(ui.getRoleId()).getName();
            ui.setRoleTypeName(roleName);
        
            ConcurrentHashMap<String, List<Long>> resultMap = new ConcurrentHashMap<String, List<Long>>();
            List<Long> uAreaSet = new ArrayList<Long>();
            List<Long> uCameraSet = new ArrayList<Long>();
            Set<Long> notPassAreaSet = new HashSet<Long>();
            try{
            Tree tree = LocalCache.tree;
            Set<Long> cameraSet = zoneAuthorizeService.idSet(CameraInfo.class, userId);
            Set<Long> cameraNotCheckCtypeSet = zoneAuthorizeService.idSetNotCTypeCheck(CameraInfo.class, userId);
            Set<Long> areaSet = zoneAuthorizeService.idSet(Area.class, userId);
            
            cameraNotCheckCtypeSet.removeAll(cameraSet);
            for(Long caId : cameraNotCheckCtypeSet){
                CameraInfo ci = (CameraInfo) tree.treeNodeWithOutTreeInfo(CameraInfo.class, caId);
                notPassAreaSet.add(ci.getStationId());
            }
            cameraSet.forEach(cId ->{
                CameraInfo ci = (CameraInfo) tree.treeNodeWithOutTreeInfo(CameraInfo.class, cId);
                Long stationId = ci.getStationId();
                if(!uAreaSet.contains(stationId)){
                    //修改用户摄像头1234类属性时，用户某区域权限没变，但实际该区域可查看摄像头有可能变化，所以需要notPassAreaSet处理
                    if(areaSet.contains(stationId) && !notPassAreaSet.contains(stationId)){
                        uAreaSet.add(stationId);//区域全选时，记录区域
                    }else{
                        uCameraSet.add(cId);//该摄像头所在区域不在授权范围内，记录摄像头
                    }
                }
            });
            }catch(Exception e){
                LOG.error("processUserAreaDataToMap error:",e);
            }
            resultMap.put("area", uAreaSet);
            resultMap.put("camera", uCameraSet);
            GlobalConsts.userBukongMap.put(userId, resultMap);
        }
    
    /**
     * 通知引擎更新布控区域和摄像头布控信息
     * @param userId
     */
    @Override
    public void noticeEngineUpdateBlackDatas(Long userId) {
        try {
            Map<String, List<Long>> uAreaDataMap = GlobalConsts.userBukongMap.get(userId);
            if (CollectionUtils.isEmpty(uAreaDataMap)) {
                processUserAreaDataToMap(userId);
                uAreaDataMap = GlobalConsts.userBukongMap.get(userId);
            }
           List<IFaaServiceThriftClient> clientList = new ArrayList<IFaaServiceThriftClient>();
               IFaaServiceThriftClient centerClient = iFaceSdkServiceItf.getCenterServer();
               if(null != centerClient){
                   clientList.add(centerClient);
               }else{
                   LOG.info("center engine server not find ");
                   List<IFaaServiceThriftClient> cList = iFaceSdkServiceItf.getAllTarget();
                   if(!CollectionUtils.isEmpty(cList)){
                       clientList.addAll(cList);
                   }
               }
               for(IFaaServiceThriftClient client : clientList){
                   client.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_BLACKLIST.getValue(),
                           EBListIoctrlType.BLIST_IOCTRL_BLACK_PERSON_UPDATE.getValue(), 0, 0);
                   
               }
            // 摄像头布控
            List<Long> cameraSet = uAreaDataMap.get("camera");
            if (!CollectionUtils.isEmpty(cameraSet)) {
                for (Long cId : cameraSet) {
                    for(IFaaServiceThriftClient client : clientList){
                        client.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_BLACKLIST.getValue(),
                                EBListIoctrlType.BLIST_IOCTRL_CAMERA_UPDATE.getValue(), SourceTypes.CAMERA.getValue(), cId);
                    }
                }
            }
            //区域布控
            List<Long> areaSet = uAreaDataMap.get("area");
            if (!CollectionUtils.isEmpty(areaSet)) {
                for (Long aId : areaSet) {
                    for(IFaaServiceThriftClient client : clientList){
                        client.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_BLACKLIST.getValue(),
                                EBListIoctrlType.BLIST_IOCTRL_AREA_UPDATE.getValue(), aId, 0);
                    }
                }

            }

        } catch (Exception e) {
            LOG.error("notice black bukong area and camera error,userId:" + userId + ",e:", e);
        }
        /*
         * // 统计通知c++更新camera 任务信息 try {
         * LOG.info("notice c++ all camera black datas"); List<CameraInfo>
         * cameraList = (List<CameraInfo>) cameraService.findAll(); if (null !=
         * cameraList && !cameraList.isEmpty()) { for (CameraInfo camera :
         * cameraList) { long cId = camera.getId();
         * ioContrlServiceItf.ioContrlWithBatch
         * (EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(),
         * EParamIoctrlType.PARAM_IOCTRL_BLACKLIST.getValue(),
         * EBListIoctrlType.BLIST_IOCTRL_CAMERA_UPDATE.getValue(),
         * SourceTypes.CAMERA.getValue(), cId);
         * 
         * } } } catch (Throwable e) { LOG.error("notice c++ black info error",
         * e); }
         */
    }


    @Override
    @Transactional
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return personDetailDao;
    }


    @Override
    public void delete(long id) {
        PersonDetail pd = findById(id);
        String owner = pd.getOwner();
        String uFilterSql = " login = '"+owner+"'";
        List<UserInfo> uList = userService.findByFilter(uFilterSql);
        if(!CollectionUtils.isEmpty(uList)){
            long uId = uList.get(0).getId();
            userService.isUserOperationAccess(uId);
        }
        personDetailDao.delete(id);
        
       String fSql = " from_person_id = "+id;
       List<BlackDetail> blackList = _blackDetailDao.findByFilter(fSql);
    
       List<String> delList = new ArrayList<String>();
       for (BlackDetail black : blackList) {
           _blackDetailDao.delete(black.getId());
           //this._cameraAndBlackDetailRepository.deleteByBlackdetailId(black.getId());
           String filter = " blackdetail_id = "+black.getId();
           areaAndBlackDao.deleteByFilter(filter);
           String filter1 = " black_id = "+black.getId();
           alarmDao.deleteByFilter(filter1);
           delList.add("" + black.getId());
       }
       
       List<Long> idList = delList.stream().map(item -> Long.parseLong(item.trim())).collect(Collectors.toList());
       featureService.deleteByFaceIds(idList);
     
    }
    
    
}
