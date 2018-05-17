/**
 *
 */
package intellif.service.impl;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.ImageInfoDao;
import intellif.database.dao.BlackDetailDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.PersonDetailDao;
import intellif.database.dao.UserDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.dto.CameraDto;
import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.BlackDetailServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.UserServiceItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.FileUtil;
import intellif.utils.IFaceSDKUtil;
import intellif.database.entity.EventInfo;
import intellif.database.entity.FaceStatisticCount;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.UserInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * The Class BlackDetailServiceImpl.
 *
 * @author yangboz
 */
@Service
public class BlackDetailServiceImpl extends AbstractCommonServiceImpl<BlackDetail> implements BlackDetailServiceItf<BlackDetail> {

    private static Logger LOG = LogManager.getLogger(BlackDetailServiceImpl.class);

   /* @Autowired
    private ImageInfoDao _imageInfoDao;*/
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private ImageInfoDao _imageInfoDao;
    @Autowired
    private BlackDetailDao blackDetailDao;
    @Autowired
    private PersonDetailDao<PersonDetail> personDetailDao;
    @Autowired
    private UserServiceItf<UserInfo> userService;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @Autowired
    private PropertiesBean propertiesBean;
    
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean updateFaceFeature(BlackDetail blackDetail,String copyUrl) throws Exception {
        long imageId = blackDetail.getFromImageId();
        LOG.info("imageId:"+imageId);
        //ImageInfo imageInfo = _imageInfoDao.findOne(imageId);
        ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(imageId);                         //////////////////////////// v1.1.0
        if(null == copyUrl){
            copyUrl = imageInfo.getUri();
        }
        String faceUrl = imageInfo.getFaceUri();
        // Get image face feature
        IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getCenterServer();
        List<T_IF_FACERECT> faceRectFeatures = ifaceSdkTarget.processFaceDetectExtract(copyUrl, blackDetail.getId(), E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_BLACKDETAIL.getValue());
        if (faceRectFeatures.size() >= 1) {
            LOG.info("faceRectFeatures.get(0).FaceRect.Rect:" + faceRectFeatures.get(0).Rect.toString());
            String json = IFaceSDKUtil.cutImageFromURL(copyUrl, FileUtil.getStoreImageUri(faceUrl, propertiesBean.getIsJar()).oriImageUri, faceRectFeatures.get(0).Rect, propertiesBean.getIsJar());
            //SET json = :json WHERE id = :id
            String uSql = "json = \""+json+"\"";
            String fSql = " id = "+blackDetail.getId();
            super.jdbcBatchUpdate(fSql, uSql);
        } else {
            LOG.warn("No faceFeature found!");
        }
        return faceRectFeatures.size() >= 1;
    }

    /**
     * 根据库类型分类获取重点人员信息
     */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<BlackDetail>> getBlackByBanksPage(List<BlackBank> bankList,
			int size) {
		int a = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		for(BlackBank item : bankList){
			long bankId = item.getId();
			String sqlitem = "(select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_BLACK_DETAIL+" where bank_id=### order by created desc limit 0,"+size+")";
			sqlitem = sqlitem.replace("###", String.valueOf(bankId));
			if(a!=0){
				sb.append(" union ");
			}
			sb.append(sqlitem);
			a++;
		}
		sb.append(")t");
		
		List<BlackDetail> resp = null;

		try {
			Query query = this.entityManager.createNativeQuery(sb.toString(), BlackDetail.class);
			resp =  (ArrayList<BlackDetail>)query.getResultList();
		} catch (Exception e) {
			LOG.error("getBlackByBanksPage,e:",e);
			return null;  
		} finally {
			entityManager.close();
		}
		//Collections.sort(resp, (m, n) -> n.getCreated().toString().compareTo(m.getCreated().toString()));
		Map<Long, List<BlackDetail>> detailMap = resp.stream().collect(Collectors.groupingBy(BlackDetail::getBankId));
		return detailMap;
	}

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return blackDetailDao;
    }

    @Override
    public List<BlackDetail> getBlackDetailsByPerson(long personId) {
        PersonDetail pd = personDetailDao.findById(personId);
        String owner = pd.getOwner();
        String uFilterSql = " login = '"+owner+"'";
        List<UserInfo> uList = userService.findByFilter(uFilterSql);
        if(!CollectionUtils.isEmpty(uList)){
            long uId = uList.get(0).getId();
            userService.isUserOperationAccess(uId);
        }
       String bFilterSql = " from_person_id = "+personId;
       List<BlackDetail> bList = blackDetailDao.findByFilter(bFilterSql);
       return bList;
    }

  

}
