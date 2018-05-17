package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.service.FaceServiceItf;
import intellif.service.ResidentServiceItf;
import intellif.settings.ResidentSetting;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ResidentInfo;
import intellif.database.entity.ResidentPerson;
import intellif.database.entity.ResidentTotal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResidentServiceImpl implements ResidentServiceItf {
	private static Logger LOG = LogManager.getLogger(ResidentServiceImpl.class);
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
    private FaceServiceItf faceService;

	@SuppressWarnings("unchecked")
	@Override
	public ResidentInfo getResidentInfo(long areaId, String startTime,
			String endTime) {
		ResidentInfo ri = new ResidentInfo();
		
	
		List<ResidentTotal> resp = null;
		String sqlString = "SELECT * FROM "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_RESIDENT_TOTAL
				+ " WHERE   ana_area_id = " + areaId
				+ " AND created BETWEEN STR_TO_DATE('" + startTime
				+ "','%Y-%m-%d') and STR_TO_DATE('" + endTime + "','%Y-%m-%d')";
		try {

			Query query = this.entityManager.createNativeQuery(sqlString,
					ResidentTotal.class);
			resp = (ArrayList<ResidentTotal>) query.getResultList();

			if (null != resp && !resp.isEmpty()) {
				ResidentTotal rt = resp.get(0);
				long residentNum = rt.getResidentNum();
				if (0 == residentNum) {
					// ��һ�β�ѯ��residentNumΪ0,��Ҫ����ȥt_resident_person���ѯ
					String sqlString1 = "SELECT count(1) FROM "
							+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_RESIDENT_PERSON
							+ " WHERE  ana_area_id = " + areaId+ " AND created BETWEEN STR_TO_DATE('"
							+ startTime + "','%Y-%m-%d') and STR_TO_DATE('"
							+ endTime + "','%Y-%m-%d')"+" and rate >=" + ResidentSetting.getRate();
					Query query1 = this.entityManager
							.createNativeQuery(sqlString1);
					BigInteger result = (BigInteger) query1.getSingleResult();
					rt.setResidentNum(result.longValue());
				//	this.entityManager.merge(rt);
					residentNum = result.longValue();
				}
				ri.setTotalNum(rt.getTotalNum());
				ri.setCreated(rt.getCreated());
				ri.setResidentNum(residentNum);
				ri.setRate((float) residentNum / (float) rt.getTotalNum());
				ri.setArea_id(rt.getAnaAreaId());
			}
		} catch (Exception e) {
			LOG.error("error", e);
		} finally {
			entityManager.close();
		}

		return ri;
	}
	
	/*@SuppressWarnings("unchecked")
	@Override
	public ResidentInfo getResidentInfos(long areaId, String startTime,
			String endTime) {
		ResidentInfo ri = new ResidentInfo();
		if(GlobalConsts.resident_debug_State){
			areaId = 1;
			startTime = "2016-04-30";
			endTime = "2016-05-01";
		}
	
		List<ResidentTotal> resp = null;
		String sqlString = "SELECT * FROM "
				+ GlobalConsts.T_NAME_RESIDENT_TOTAL
				+ " WHERE   ana_area_id = " + areaId
				+ " AND created BETWEEN STR_TO_DATE('" + startTime
				+ "','%Y-%m-%d') and STR_TO_DATE('" + endTime + "','%Y-%m-%d')";
		try {

			Query query = this.entityManager.createNativeQuery(sqlString,
					ResidentTotal.class);
			resp = (ArrayList<ResidentTotal>) query.getResultList();

			if (null != resp && !resp.isEmpty()) {
				ResidentTotal rt = resp.get(0);
				long residentNum = rt.getResidentNum();
				if (0 == residentNum) {
					// ��һ�β�ѯ��residentNumΪ0,��Ҫ����ȥt_resident_person���ѯ
					String sqlString1 = "SELECT count(1) FROM "
							+ GlobalConsts.T_NAME_RESIDENT_PERSON
							+ " WHERE  ana_area_id = " + areaId+ "AND created BETWEEN STR_TO_DATE('"
							+ startTime + "','%Y-%m-%d') and STR_TO_DATE('"
							+ endTime + "','%Y-%m-%d')"
					        + "ORDER BY created desc";
					Query query1 = this.entityManager
							.createNativeQuery(sqlString1);
					BigInteger result = (BigInteger) query1.getSingleResult();
					rt.setResidentNum(result.longValue());
					this.entityManager.merge(rt);
					residentNum = result.longValue();
				}
				ri.setTotalNum(rt.getTotalNum());
				ri.setResidentNum(residentNum);
				ri.setRate((float) residentNum / (float) rt.getTotalNum());
				ri.setArea_id(rt.getAnaAreaId());
			}
		} catch (Exception e) {
			LOG.error("error", e);
		} finally {
			entityManager.close();
		}

		return ri;
	}*/

	
	@SuppressWarnings("unchecked")
	@Override
	public List<ResidentPerson> getResidentIndexsByAreaId(long areaId,
			String startTime, String endTime) {
		List<ResidentPerson> resp = null;
		
		/**
		 * SELECT t.id,t.person_id, f.created,f.face_id as image_id from 
(select  rp.id,rp.person_id from t_resident_person rp WHERE rp.ana_area_id =1 AND rp.created >'2016-04-14' and rp.created <'2016-04-15' 
and rate >1 GROUP BY rp.person_id)t 
LEFT JOIN 
(SELECT rf.person_id,rf.created,rf.face_id from t_resident_face rf 
WHERE rf.ana_area_id=1 AND rf.created >'2016-04-14' and rf.created <'2016-04-15' GROUP BY rf.person_id)f
on f.person_id =t.person_id;
		 */
		String sqlString =  "SELECT t.id,t.person_id ,f.created, f.face_id as image_id from " +
				"(select  rp.id,rp.person_id from t_resident_person rp"+ 
				 " WHERE rp.ana_area_id ="
					+ areaId
				 +" AND rp.created >'" +
				 startTime +
				 "' and rp.created <'" +
				 endTime +
				 "' and rate >= " +
				 ResidentSetting.getRate() +
				 " GROUP BY rp.person_id"+
				 ")t "+
				" LEFT JOIN"+
				"(SELECT rf.person_id,rf.created,rf.face_id from t_resident_face rf"+ 
				 " WHERE rf.ana_area_id ="
					+ areaId
				 +" AND rf.created >'" +
				 startTime +
				 "' and rf.created <'" +
				 endTime +
				 "' GROUP BY rf.person_id)f on f.person_id=t.person_id";
		
		
	
		

		try {

			Query query = this.entityManager.createNativeQuery(sqlString,
					ResidentPerson.class);
			 resp = (ArrayList<ResidentPerson>) query.getResultList();
			if(null != resp){
				for(ResidentPerson item : resp){
					long faceId = item.getImageId();
					FaceInfo fi = this.faceService.findOne(faceId);
					if(null != fi){
						item.setAge(fi.getAge());
						item.setGender(fi.getGender());
						item.setImageData(fi.getImageData());
					}
				}
			}
		} catch (Exception e) {
			LOG.error("get resident person list by areaId error", e);
		} finally {
			entityManager.close();
		}

		return resp;
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ResidentPerson> getResidentPersonByAreaId(long areaId,
			String startTime, String endTime, int start, int pageSize) {
		List<ResidentPerson> resp = null;
	 
		/**
		 * SELECT t.id,t.person_id ,f.faceNum, f.created,f.face_id as image_id from 
(select  rp.id,rp.person_id from t_resident_person rp WHERE rp.ana_area_id =1 AND rp.created >'2016-04-14' and rp.created <'2016-04-15' 
and rate >1 GROUP BY rp.person_id limit 0,40)t 
LEFT JOIN 
(SELECT rf.person_id,count(rf.id) as faceNum,rf.created,rf.face_id from t_resident_face rf 
WHERE rf.ana_area_id=1 AND rf.created >'2016-04-14' and rf.created <'2016-04-15' GROUP BY rf.person_id)f
on f.person_id =t.person_id ORDER BY f.faceNum desc;;
		 */
		String sqlString =  "SELECT t.id,t.person_id ,f.faceNum, f.created,f.face_id as image_id from " +
				"(select  rp.id,rp.person_id from t_resident_person rp"+ 
				 " WHERE rp.ana_area_id ="
					+ areaId
				 +" AND rp.created >'" +
				 startTime +
				 "' and rp.created <'" +
				 endTime +
				 "' and rate >= " +
				 ResidentSetting.getRate() +
				 " GROUP BY rp.person_id LIMIT " +
				 start +
				 "," +
				 pageSize +
				 ")t "+
				" LEFT JOIN " +
				"(SELECT rf.person_id,count(rf.id) as faceNum,rf.created,rf.face_id from t_resident_face rf"+ 
				 " WHERE rf.ana_area_id ="
					+ areaId
				 +" AND rf.created >'" +
				 startTime +
				 "' and rf.created <'" +
				 endTime +
				 "' GROUP BY rf.person_id)f on f.person_id=t.person_id order by f.faceNum desc";
		
		

		try {

			Query query = this.entityManager.createNativeQuery(sqlString,
					ResidentPerson.class);
			resp = (ArrayList<ResidentPerson>) query.getResultList();
			if(null != resp){
				for(ResidentPerson item : resp){
					long faceId = item.getImageId();
					FaceInfo fi = this.faceService.findOne(faceId);
					if(null != fi){
						item.setAge(fi.getAge());
						item.setGender(fi.getGender());
						item.setImageData(fi.getImageData());
					}
				}
			}

		} catch (Exception e) {
			LOG.error("get resident person list by areaId error", e);
		} finally {
			entityManager.close();
		}

		return resp;
	}
}
