package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.CameraInfoDao;
import intellif.dao.TableRecordDao;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.TableRecord;
import intellif.dto.PersonStatisticCount;
import intellif.service.PersonQueryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonQueryServiceImpl.java
 * @Package intellif.service.impl
 * @Description
 * @date 2018 05-03 16:43.
 */
@Service
public class PersonQueryServiceImpl implements PersonQueryService {

	private static Logger logger = LogManager.getLogger(PersonQueryServiceImpl.class);

	@Autowired
	private TableRecordDao tableRecordDao;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	CameraInfoDao cameraInfoDao;
	@Override
	public List<PersonStatisticCount> getTodayPersonCount(int type,List<CameraInfo> areaId) {
		Date nowDate = new Date();
		DateFormat dffull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dff = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String today = dff.format(nowDate);
		String tmday = dffull.format(nowDate);
		String tableName = "t_face";
		TableRecord tableRecord = tableRecordDao.getCurTable(today, tableName);
		List<Long> sourceIds = getSourceIds(areaId);
		final StringBuilder sqlString = new StringBuilder(
				"select max(id)*:type as id" +
						", HOUR(time) as time" +
						",COUNT(1) as count ");
		switch (type) {
			case 10:
				sqlString.append(",0 as age").append(", 0 as gender");
				break;
			case 20:
				sqlString.append(",gender").append(",0 as age");
				break;
			case 30:
				sqlString.append(",age").append(", 0 as gender");
				break;
			default:
				throw new RuntimeException("value of type is illegal");
		}
		sqlString.append(" from ")
				.append(GlobalConsts.INTELLIF_FACE + "." + tableRecord.getTableName())
				.append(" where source_id in (:sourceIds)")
				.append(" and time between :startTime and :endTime")
				.append(" group by HOUR(time)");
		switch (type) {
			case 10:
				break;
			case 20:
				sqlString.append(",gender");
				break;
			case 30:
				sqlString.append(",age");
				break;
			default:
				throw new RuntimeException("value of type is illegal");
		}
		sqlString.append(" order by  HOUR(time)");
		List<PersonStatisticCount> faceRespObject;
		try {
			Query query = entityManager.createNativeQuery(sqlString.toString(), PersonStatisticCount.class);
			query.setParameter("type", type/10);
			query.setParameter("startTime", today);
			query.setParameter("endTime", tmday);
			query.setParameter("sourceIds",sourceIds);
			faceRespObject = query.getResultList();
		} catch (Exception e) {
			logger.error("fail to getTodayPersonCount, message: " + e.getMessage());
			throw new RuntimeException("sql执行错误");
		}
		return faceRespObject;
	}
	@Override
	public List<PersonStatisticCount> getHistoryPersonCount(int timeType, int personType, Date startTime, Date endTime,List<CameraInfo>areaId) {
		List<Long> sourceIds = getSourceIds(areaId);
		DateFormat pdf= new SimpleDateFormat("yyyy-MM-dd");
		String startTimeStr=pdf.format(startTime);
		String endTimeStr=pdf.format(endTime);
		final StringBuilder sqlString = new StringBuilder(
				"select id,age,gender,count,time from " +
						"" + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_PERSON_STATISTIC
						+ " where 1=1")
		         .append(" and source_id in (:sourceIds)")
		         .append(" and time_type = :timeType")
		         .append(" and person_type = :personType")
		         .append(" and time between :startTime and :endTime")
				 .append(" order by time");
		List<PersonStatisticCount> faceRespObject;
		try {
			Query query = entityManager.createNativeQuery(sqlString.toString(), PersonStatisticCount.class);
			query.setParameter("timeType", timeType);
			query.setParameter("personType", personType);
			query.setParameter("startTime", startTimeStr);
			query.setParameter("endTime", endTimeStr);
			query.setParameter("sourceIds", sourceIds);
			faceRespObject = query.getResultList();
		} catch (Exception e) {
			logger.error("fail to getHistoryPersonCount, message: " + e.getMessage());
			throw new RuntimeException("sql执行错误");
		}
		List<PersonStatisticCount> lists = new ArrayList<>();
		if(timeType==20){
			for(PersonStatisticCount psc:faceRespObject){
				psc.setTime(psc.getTime().substring(0, 7));
				lists.add(psc);
			}
		}else if(timeType==30){
			for(PersonStatisticCount psc:faceRespObject){
				psc.setTime(psc.getTime().substring(0, 4));
				lists.add(psc);
			}
		}else{
			lists=faceRespObject;
		}
		return lists;

	}
	public List<Long> getSourceIds(List<CameraInfo> cameraInfos) {
		List<Long> sourceIds = new ArrayList<>();
		for(CameraInfo cameraInfo:cameraInfos){
			sourceIds.add(cameraInfo.getId());
		}
		return sourceIds;
	}
}
