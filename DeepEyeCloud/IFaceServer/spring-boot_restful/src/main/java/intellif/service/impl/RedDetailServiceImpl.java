package intellif.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.ImageInfoDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.RedDetailDao;
import intellif.database.entity.Area;
import intellif.dto.AlarmStatisticDto;
import intellif.dto.JsonObject;
import intellif.dto.RedDto;
import intellif.dto.RedParamDto;
import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.AreaServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.RedDetailServiceItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CommonUtil;
import intellif.utils.DateUtil;
import intellif.utils.FileUtil;
import intellif.utils.IFaceSDKUtil;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.RedCheckRecord;
import intellif.database.entity.RedDetail;
@Service
public class RedDetailServiceImpl extends AbstractCommonServiceImpl<RedDetail> implements RedDetailServiceItf<RedDetail>{
	 private static Logger LOG = LogManager.getLogger(RedDetailServiceImpl.class);

	    /*@Autowired
	    private ImageInfoDao _imageInfoDao;*/
	    @Autowired
	    private ImageServiceItf _imageServiceItf;
	    
	    @Autowired
	    private RedDetailDao redDetailDao;

	    @Autowired
	    private IFaceSdkServiceItf iFaceSdkServiceItf;

	    @Autowired
	    private PropertiesBean propertiesBean;
	    
	    @Autowired
        private JdbcTemplate jdbcTemplate;
	    
	    @PersistenceContext
		EntityManager entityManager;
	    
	@Override
	public boolean updateFaceFeature(RedDetail redDetail) throws Exception {
        long imageId = redDetail.getFromImageId();
       // ImageInfo imageInfo = _imageInfoDao.findOne(imageId);
        ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(imageId);
        String imageUrl = imageInfo.getUri();
        String faceUrl = imageInfo.getFaceUri();
        // Get image face feature
        IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
        Boolean imageDataIsNotNull = (null != redDetail.getFaceUrl() && !redDetail.getFaceUrl().equals(""));
        LOG.info("BlackDetail's imageDataIsNotNull?" + imageDataIsNotNull);
        List<T_IF_FACERECT> faceRectFeatures = ifaceSdkTarget.processFaceDetectExtract(imageDataIsNotNull ? imageUrl : redDetail.getFaceUrl(), redDetail.getId(), E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_REDDETAIL.getValue());
        if (faceRectFeatures.size() >= 1) {
            LOG.info("faceRectFeatures.get(0).FaceRect.Rect:" + faceRectFeatures.get(0).Rect.toString());
            String json = IFaceSDKUtil.cutImage(FileUtil.getStoreImageUri(imageUrl, propertiesBean.getIsJar()).oriImageUri, FileUtil.getStoreImageUri(faceUrl, propertiesBean.getIsJar()).oriImageUri, faceRectFeatures.get(0).Rect, propertiesBean.getIsJar());
            redDetailDao.jdbcBatchUpdate("json = "+json, "id = "+redDetail.getId());
        } else {
            LOG.warn("No faceFeature found!");
        }
        return faceRectFeatures.size() >= 1;
    }

	@SuppressWarnings("unchecked")
	@Override
	public JsonObject findRedListByPage(RedDto rd)// group in a day
	{
		List<RedDto> resp = null;
		//
		String sqlString = "SELECT * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_PERSON+" where 1 = 1 ";
		   if (StringUtil.isNotBlank(rd.getKeywords())) {
	            sqlString += " and ( name like \"%" + rd.getKeywords() + "%\" or police_phone like \"%"+ rd.getKeywords()+"%\")";
	        }
	       Long max = getCountBySql(sqlString).longValue();
	        Long totalPage = CommonUtil.calculatePages(rd.getPageSize(), max);
	        sqlString += " order by created desc limit "+(rd.getPage()-1)*rd.getPageSize() +","+rd.getPageSize()+" ";
		try {
			Query query = this.entityManager.createNativeQuery(sqlString, RedDto.class);
			resp = (ArrayList<RedDto>) query.getResultList();
		} catch (Exception e) {
			LOG.error("find red person list method error:",e);
		} finally {
			entityManager.close();
		}
		  return new JsonObject(resp,0,totalPage,max.intValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JsonObject findRedCheckByPage(RedParamDto rpd)// group in a day
	{
		List<RedCheckRecord> resp = null;
		//
		String sqlString = "SELECT * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_CHECK+" WHERE created >= '"+rpd.getStartTime()+"' and created <= '"+rpd.getEndTime()+"'";
	    if(null != rpd.getUserName() && rpd.getUserName().trim().length() > 0){
	    	sqlString += " and apply_person ='"+rpd.getUserName()+"'";
	    }
	    if (rpd.getKeywords() != null && rpd.getKeywords().trim().length()>0) {

            sqlString += " and ( message like \"%" + rpd.getKeywords() + "%\" or apply_person like \"%"
                    + rpd.getKeywords() + "%\" or check_person like \"%" + rpd.getKeywords()
                    + "%\" or cmp_person like \"%" + rpd.getKeywords()
                    + "%\" or result like \"%" + rpd.getKeywords()
                    + "%\" or station like \"%" + rpd.getKeywords()
                    + "%\")";

        }
	   Long max = getCountBySql(sqlString).longValue();
	    Long totalPage = CommonUtil.calculatePages(rpd.getPageSize(), max);
	    
	    sqlString +=" order by created desc limit "+(rpd.getPage()-1)*rpd.getPageSize()+","+rpd.getPageSize()+" ";
		try {
			Query query = this.entityManager.createNativeQuery(sqlString, RedCheckRecord.class);
			resp = (ArrayList<RedCheckRecord>) query.getResultList();
		} catch (Exception e) {
			LOG.error("find red check list method error:",e);
		} finally {
			entityManager.close();
		}
		return new JsonObject(resp,0,totalPage,max.intValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JsonObject findUserCheckRecords(RedParamDto rpd)// group in a day
	{
		List<RedCheckRecord> resp = null;
		String sql = "select r.* from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_CHECK+" r LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_DETAIL+" t on r.r_id = t.id "+
		"LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_PERSON+" p on  t.from_person_id = p.id WHERE p.police_phone = '"+rpd.getPolicePhone() 
				+"' and r.created >= '"+rpd.getStartTime()+"' and r.created <= '"+rpd.getEndTime()+"'"; 
		
		 if (rpd.getKeywords() != null && rpd.getKeywords().trim().length()>0) {

	            sql += " and ( r.message like \"%" + rpd.getKeywords() + "%\" or r.apply_person like \"%"
	                    + rpd.getKeywords() + "%\" or r.check_person like \"%" + rpd.getKeywords()
	                    + "%\" or r.cmp_person like \"%" + rpd.getKeywords()
	                    + "%\" or r.result like \"%" + rpd.getKeywords()
	                    + "%\" or r.station like \"%" + rpd.getKeywords()
	                    + "%\")";

	        }
		
		
		
		 Long max = getCountBySql(sql).longValue();
	     Long totalPage = CommonUtil.calculatePages(rpd.getPageSize(), max);
		sql +=" order by r.created desc limit "+(rpd.getPage()-1)*rpd.getPageSize()+","+rpd.getPageSize()+" ";
	
		   try {
			Query query = this.entityManager.createNativeQuery(sql, RedCheckRecord.class);
			resp = (ArrayList<RedCheckRecord>) query.getResultList();
		} catch (Exception e) {
			LOG.error("find red check list method error:",e);
		} finally {
			entityManager.close();
		}
		   return new JsonObject(resp,0,totalPage,max.intValue());
	}
	
    public BigInteger getCountBySql(String sql){
        
        BigInteger count = new BigInteger("0");
        String countSql="";
        if(sql.indexOf("r.*")>0){
            countSql=sql.replace("r.*","count(1)");
        }else{
            countSql=sql.replace("*","count(1)");
        }
       LOG.info("count sql :"+countSql);
        ArrayList resp = null;
        
        try { 
            Query query = this.entityManager.createNativeQuery(countSql);
            
            resp =(ArrayList) query.getResultList();
        
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        if(resp != null && resp.size() != 0) {
            count=(BigInteger) resp.get(0);
        }
        
        return count;
    }

    /**
     * 警员通过警信更新红名单强制记录审核信息
     */
    @Override
    public void updateRedCheckFromJinxin(String id, String name, String result,String dateStr) {
         if("1".equals(result)){
             result = "违规操作";
         }else if("2".equals(result)){
             result = "误报";
         }else{
             result = "未审核";
         }
      
        String sql = "update "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_RED_CHECK+
                " set check_person = '"+name+"',result = '"+result+"',updated = '"+dateStr+"' WHERE id = "+id +" and result='"+GlobalConsts.RED_CHECK_RESULT_WAIT+"';";
        
        jdbcTemplate.execute(sql);
        
    }

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return redDetailDao;
    }
}
