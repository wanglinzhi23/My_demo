package intellif.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.dao.CrimeAlarmDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.dto.AlarmQueryDto;
import intellif.service.CrimeAlarmServiceItf;
import intellif.utils.SqlUtil;
import intellif.database.entity.CountInfo;
import intellif.database.entity.EventInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.StatisticDataQuery;

@Service
public class CrimeAlarmServiceImpl implements CrimeAlarmServiceItf {

	private static Logger LOG = LogManager.getLogger(CrimeAlarmServiceImpl.class);

	@PersistenceContext
	EntityManager entityManager;
	//
	@Autowired
	CrimeAlarmDao crimeAlarmDao;

	@Autowired
	FaceInfoDaoImpl faceInfoDaoImpl;
	
	@Autowired
	ImageInfoDaoImpl imageInfoDaoImpl;
	
	@Override
	public List<EventInfo> findEventsByPersonId(AlarmQueryDto alarmQueryDto) {
	    List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("addr");
        cameraFields.add("geo_string");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "c");
		List<EventInfo> resp = new ArrayList<EventInfo>();
		List<Long> faceIdList = new ArrayList<Long>();
		String sqlString = "SELECT b.*, c.name as camera_name, c.geo_string,c.addr as address, '0' AS area_id from ( SELECT a.id,0 as send, a.face_id, a.crime_person_id as person_id, a.confidence, "
				+ "a.face_url as image_data, 0 as scene, a.camera_id, a.time as time FROM "
		        + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO + " a "
		        + "where a.confidence >= " + alarmQueryDto.getThreshold() + " and a.crime_person_id = "+alarmQueryDto.getId()+" ";
		
//		String sqlString = "SELECT a.id, a.crime_person_id person_id, a.confidence, "
//				+ "a.face_url image_data, b.uri scene, a.camera_id, c.name camera_name, c.geo_string , a.time time FROM "
//		        + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO + " a, "
//				+ GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_IMAGE_INFO + " b, "
//		        + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO + " c , " 
//				+ GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_INFO + " d " 
//		        + "where a.confidence >= " + alarmQueryDto.getThreshold() + " and a.camera_id = c.id and"
//		        		+ " a.face_id = d.id and d.from_image_id = b.id and a.crime_person_id = "+alarmQueryDto.getId()+" ";

		
		if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
			sqlString+="and a.camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
		}
		if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
			sqlString+="and a.time >= '" +alarmQueryDto.getStartTime() + "' " ;
		}
		if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
			sqlString+="and a.time <= '" +alarmQueryDto.getEndTime() + "' " ;
		}
		
		sqlString+= "order by time desc LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + "," + alarmQueryDto.getPageSize() + ") b, ";
		
		sqlString+= cameraSql+ " where b.camera_id = c.id" ;

		try {
			Query query = this.entityManager.createNativeQuery(sqlString, EventInfo.class);
			for(EventInfo eventInfo : (ArrayList<EventInfo>)query.getResultList()){
				if (!faceIdList.contains(eventInfo.getFaceId())) {
					faceIdList.add(eventInfo.getFaceId());
					resp.add(eventInfo);
				}
			}
			updateInfo(resp);
			
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}

	
	private void updateInfo(List<EventInfo> eventList){
		if (null == eventList || eventList.isEmpty()) {
			return;
		}
		Map<Long, EventInfo> eventMap = new HashMap<Long,EventInfo>();
		Map<Long, FaceInfo> faceMap = new HashMap<Long,FaceInfo>();
		List<Long> faceIdList = new ArrayList<Long>();
		List<Long> imageIdList = new ArrayList<Long>();
		for(EventInfo eventInfo : eventList){
			faceIdList.add(eventInfo.getFaceId());
			eventMap.put(eventInfo.getFaceId(), eventInfo);
		}

		List<FaceInfo> faceList = faceInfoDaoImpl.findByIds(faceIdList);
		if (null != faceList && !faceList.isEmpty()) {
			for(FaceInfo faceInfo : faceList){
				faceMap.put(faceInfo.getFromImageId(), faceInfo);
				imageIdList.add(faceInfo.getFromImageId());
			}
			List<ImageInfo> imageList = imageInfoDaoImpl.findByIds(imageIdList);
			if (null != imageList && !imageList.isEmpty()) {
				for(ImageInfo imageInfo : imageList){
					long imageId = imageInfo.getId();
					FaceInfo faceInfo = faceMap.get(imageId);
					long faceId = faceInfo.getId();
					eventMap.get(faceId).setScene(imageInfo.getUri());
				}
			}
		}
	}

	@Override
	public List<OtherInfo> findAlarmPerson(AlarmQueryDto alarmQueryDto) {
		List<OtherInfo> resp = null;
		String sqlString = "SELECT a.crime_person_id, b.* FROM " 
		+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO + " a, " 
		+ GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO + " b "
		+ "where a.confidence >= " + alarmQueryDto.getThreshold() + " and a.crime_person_id = b.id " ;
				
		if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
			sqlString+="and a.camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
		}
		if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
			sqlString+="and a.time >= '" +alarmQueryDto.getStartTime() + "' " ;
		}
		if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
			sqlString+="and a.time <= '" +alarmQueryDto.getEndTime() + "' " ;
		}
		if(null!=alarmQueryDto.getText()&&!"".equals(alarmQueryDto.getText())) {
			sqlString+="and b.xm like '%" +alarmQueryDto.getText() + "%' " ;
		}
		
		sqlString+= "group by a.crime_person_id order by max(a.time) desc LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + "," + (alarmQueryDto.getPageSize() * 3) + "";

		try {
			Query query = this.entityManager.createNativeQuery(sqlString, OtherInfo.class);
			resp = (ArrayList<OtherInfo>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}
	
	@Override
    public List<OtherInfo> findAlarmPersonForOffline(AlarmQueryDto alarmQueryDto) {
        List<OtherInfo> resp = null;
        String sqlString = "";
        if(null!=alarmQueryDto.getText()&&!"".equals(alarmQueryDto.getText())) {
            /*SELECT b.* FROM  
                intellif_base.`t_crime_alarm_info` a,
                intellif_static.`t_other_info` b
                WHERE a.confidence >= 0.92 AND a.crime_person_id = b.id 
                AND b.xm LIKE '%��%';*/
            sqlString = "SELECT b.* FROM " 
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO + " a, " 
                + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO + " b "
                + "where a.confidence >= " + alarmQueryDto.getThreshold() + " and a.crime_person_id = b.id " ;
                        
            if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
                sqlString+="and a.camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
            }
            if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
                sqlString+="and a.time >= '" +alarmQueryDto.getStartTime() + "' " ;
            }
            if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
                sqlString+="and a.time <= '" +alarmQueryDto.getEndTime() + "' " ;
            }
            if(null!=alarmQueryDto.getText()&&!"".equals(alarmQueryDto.getText())) {
                sqlString+="and b.xm like '%" +alarmQueryDto.getText() + "%' " ;
            }
                
            sqlString+= "group by a.crime_person_id order by max(a.time) desc LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + "," + alarmQueryDto.getPageSize() + "";
        } else {
            /*SELECT * FROM intellif_static.t_other_info c WHERE 
                id IN(
                    SELECT b.crime_person_id FROM (
                        SELECT DISTINCT crime_person_id FROM
                            intellif_base.t_crime_alarm_info a
                            WHERE confidence > 0.92
                            ORDER BY TIME DESC LIMIT 0,40
                    ) b
                ) */
            sqlString = "SELECT * FROM "
                    + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO + " c "
                    + " WHERE id IN (SELECT b.crime_person_id FROM (SELECT DISTINCT crime_person_id FROM " 
                    + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO + " a "
                    + " where confidence >= " + alarmQueryDto.getThreshold();
            
            if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
                sqlString+=" and a.camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
            }
            if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
                sqlString+="and a.time >= '" +alarmQueryDto.getStartTime() + "' " ;
            }
            if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
                sqlString+="and a.time <= '" +alarmQueryDto.getEndTime() + "' " ;
            }
            
            sqlString+= "ORDER BY time DESC LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + "," + alarmQueryDto.getPageSize() + ") b)";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, OtherInfo.class);
            resp = (ArrayList<OtherInfo>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }
	
	@Override
    public int countAlarmPersonForOffline(AlarmQueryDto alarmQueryDto) {
	    int resp = 0;
	    String sqlString = "";
        if(null!=alarmQueryDto.getText()&&!"".equals(alarmQueryDto.getText())) {
            /*SELECT COUNT(DISTINCT crime_person_id) FROM (
                    SELECT crime_person_id FROM intellif_base.t_crime_alarm_info WHERE crime_person_id IN(
                            SELECT id FROM intellif_static.t_other_info WHERE xm LIKE '%��%'
                    ) AND confidence > 0.92
              )a*/
            sqlString += "SELECT COUNT(DISTINCT crime_person_id) FROM ( SELECT crime_person_id FROM "
                    + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO 
                    + " WHERE crime_person_id IN( SELECT id FROM "
                    + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO
                    + " WHERE xm like '%" +alarmQueryDto.getText() + "%')AND confidence >= " + alarmQueryDto.getThreshold(); 
            
            if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
                sqlString+="and camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
            }
            if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
                sqlString+="and time >= '" +alarmQueryDto.getStartTime() + "' " ;
            }
            if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
                sqlString+="and time <= '" +alarmQueryDto.getEndTime() + "' " ;
            }
            sqlString += ") a";
        } else {
            /*SELECT COUNT(DISTINCT crime_person_id) FROM (
              SELECT crime_person_id FROM intellif_base.t_crime_alarm_info 
              WHERE confidence > 0.92) a*/
            sqlString = "SELECT COUNT(DISTINCT crime_person_id) FROM ( SELECT crime_person_id FROM "
                    + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_ALARM_INFO 
                    + " where confidence >= " + alarmQueryDto.getThreshold();
            
            if(null!=alarmQueryDto.getCameraIds()&&!"".equals(alarmQueryDto.getCameraIds())) {
                sqlString+="and camera_id in (" + alarmQueryDto.getCameraIds() + ") ";
            }
            if(null!=alarmQueryDto.getStartTime()&&!"".equals(alarmQueryDto.getStartTime())) {
                sqlString+="and time >= '" +alarmQueryDto.getStartTime() + "' " ;
            }
            if(null!=alarmQueryDto.getEndTime()&&!"".equals(alarmQueryDto.getEndTime())) {
                sqlString+="and time <= '" +alarmQueryDto.getEndTime() + "' " ;
            }
            sqlString += ") a";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sqlString);
            resp = Integer.parseInt(query.getResultList().get(0).toString());
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }
	
	@Override
	public Map<Long, List<CountInfo>> findAlarmCount(StatisticDataQuery requestBody) {
		Map<Long, List<CountInfo>> resp = new HashMap<Long, List<CountInfo>>();
		List<CountInfo> result = null;
		int timeslot = requestBody.getTimeslot();
		String formate = "";
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
		switch(timeslot) {
		case 0:
			formate = "%Y-%m-%d %H:%i:%s";
			break;
		case 1:
			formate = "%Y-%m-%d %H:%i";
			break;
		case 2:
			formate = "%Y-%m-%d %H";
			break;
		case 3:
			formate = "%Y-%m-%d";
			break;
		}
		String sqlString = "SELECT alarm.id, DATE_FORMAT(alarm.time, '" + formate + "') as time, camera.id as source_id, count(*) as count FROM " 
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " alarm use index (t_alarm_info_time), "
			    + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " task, "
			    + cameraSql + " camera"+" WHERE alarm.confidence >= " + requestBody.getThreshold() 
			    + " and alarm.time >= '" + requestBody.getStarttime() + "' AND alarm.time <= '" + requestBody.getEndtime() + "' AND alarm.task_id = task.id "
			    + "AND camera.id = task.source_id";
		
		if(requestBody.getCameraids()[0] != 0) {
			sqlString += " AND camera.id in (:cameraids)";
		}
		sqlString += " GROUP BY DATE_FORMAT(alarm.time, '" + formate + "'), camera.id ORDER BY alarm.time asc";
		try {
			Query query = this.entityManager.createNativeQuery(sqlString, CountInfo.class);
			if(requestBody.getCameraids()[0] != 0) {
				query.setParameter("cameraids", Arrays.asList(requestBody.getCameraids()));
			}
			result = query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		if (result == null) return null;
		resp = result.stream().collect(Collectors.groupingBy(CountInfo::getSourceId));
		return resp;
	}

}