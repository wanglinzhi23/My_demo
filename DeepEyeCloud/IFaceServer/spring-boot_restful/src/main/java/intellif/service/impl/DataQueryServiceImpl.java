package intellif.service.impl;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import intellif.annotation.MapDTO;
import intellif.annotation.MapDTOSplitStr;
import intellif.consts.GlobalConsts;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dto.AreaStatisticCameraDataDto;
import intellif.dto.CameraGeometryInfoDto;
import intellif.dto.CameraStatisticInfoDto;
import intellif.dto.StoredCrimeDataDto;
import intellif.service.CameraServiceItf;
import intellif.service.DataQueryServiceItf;
import intellif.service.FaceQualityCameraCountItf;
import intellif.utils.SqlUtil;
import intellif.database.entity.AreaCameraStatistic;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.CameraStatisticInfo;
import intellif.database.entity.FaceStatisticCount;
import intellif.database.entity.StatisticDataQuery;
import intellif.database.entity.StoredCrimeQuery;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

/**
 * 
 * @author yktang, by V1.1.2
 *
 */

@Service
public class DataQueryServiceImpl implements DataQueryServiceItf {
	private static Logger LOG = LogManager.getLogger(DataQueryServiceImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private FaceInfoDaoImpl faceInfoDaoImpl;
	@Autowired
	private CameraServiceItf cameraServiceItf;
	@Autowired
	private FaceQualityCameraCountItf faceQualityCameraCountItf;
	@Autowired
	ZoneAuthorizeServiceItf zoneAuthorizeService;
	
	@Override
	public Map<Long, List<FaceStatisticCount>> statisticByCameraByTimeslot(StatisticDataQuery queryobject) {
		if (queryobject.getTimeslot() == 3) {
			String startTime = queryobject.getStarttime();
			String endTime = queryobject.getEndtime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat dffull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Map<Long, List<FaceStatisticCount>> faceCountFirstDay = null;
				Map<Long, List<FaceStatisticCount>> faceCountLastDay = null;
				Date startDate = df.parse(startTime);
				Date startDateFull = dffull.parse(startTime);
				Date endDate = df.parse(endTime);
				Date endDateFull = dffull.parse(endTime);
				String start = df.format(startDate);
				String end = df.format(endDate);
				Date daylyStartDate = startDate;
				Date daylyEndDate = endDate;
				if (!startDate.equals(startDateFull)) {
					//query count table from next day, step 1
					daylyStartDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24));
					start = df.format(daylyStartDate);
					//query face table for first day
					String endOfFirstDay = dffull.format(daylyStartDate.getTime() - (1000 * 60));
					StatisticDataQuery queryFirstDay = new StatisticDataQuery(queryobject.getCameraids(), queryobject.getStarttime(), 
							endOfFirstDay, queryobject.getTimeslot(), queryobject.getQuality(), queryobject.getThreshold());
					faceCountFirstDay = faceInfoDaoImpl.findByTimeASC(queryFirstDay);
				}
				if (!endDate.equals(endDateFull)) {
					//query count table to day before last day, step 2
					daylyEndDate = new Date(endDate.getTime() - (1000 * 60 * 60 * 24));
					end = df.format(daylyEndDate);
					//query face table for last day
					String beginOfLastDay = dffull.format(endDate.getTime());
					StatisticDataQuery queryLastDay = new StatisticDataQuery(queryobject.getCameraids(), beginOfLastDay, 
							queryobject.getEndtime(), queryobject.getTimeslot(),queryobject.getQuality(), queryobject.getThreshold());
					faceCountLastDay = faceInfoDaoImpl.findByTimeASC(queryLastDay);
				}
				Map<Long, List<FaceStatisticCount>> respMap = new HashMap<Long, List<FaceStatisticCount>>();
				if (faceCountFirstDay != null) {
					respMap = faceCountFirstDay;
				}
				if (!daylyStartDate.before(daylyEndDate)) {
					if (faceCountLastDay != null) {
						mergeMap(respMap, faceCountLastDay);
					}
					return respMap;
//					return faceInfoDaoImpl.findByTimeASC(queryobject);
				}
				
				Map<Long, List<FaceStatisticCount>> daylyCountMap = new HashMap<>();
				LOG.info("face data collect find quality count table start");
				if (queryobject.getCameraids()[0] == 0) {
					daylyCountMap = faceQualityCameraCountItf.findByPeriod(start, end,queryobject.getQuality());
				} else {
					daylyCountMap = faceQualityCameraCountItf.findBySourceIdByPeriod(queryobject.getCameraids(), start, end,queryobject.getQuality());
				}
				LOG.info("face data collect find quality count table end");
				mergeMap(respMap, daylyCountMap);
				
				if (faceCountLastDay != null) {
					mergeMap(respMap, faceCountLastDay);
				}
				return respMap;
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return faceInfoDaoImpl.findByTimeASC(queryobject);
	}
	
	private void mergeMap(Map<Long, List<FaceStatisticCount>> mainMap, Map<Long, List<FaceStatisticCount>> addMap) {
		for (Map.Entry<Long, List<FaceStatisticCount>> entry : addMap.entrySet()) {
		    Long key = entry.getKey();
		    List<FaceStatisticCount> value = entry.getValue();
		    List<FaceStatisticCount> mainList = mainMap.get(key);
		    if (mainList == null) {
		    	mainMap.put(key, value);
		    } else {
		    	mainList.addAll(value);
		    }
		}
	}
	
	@Override
	public Map<Long, AreaStatisticCameraDataDto> statisticStationCamera(StatisticDataQuery queryobject,List<AreaCameraStatistic> sawresult) {
		//List<AreaCameraStatistic> sawresult = faceInfoDaoImpl.findStatisticCameraData(queryobject);
		Map<Long, AreaStatisticCameraDataDto> result = new HashMap<>();
		Map<Long, AreaCameraStatistic> sawmap = new HashMap<>();
		for(int index = 0; index < sawresult.size(); index++) {
			AreaCameraStatistic sawCameraData = sawresult.get(index);
			sawmap.put(sawCameraData.getId(), sawCameraData);
			if(result.get(sawCameraData.getStationId()) == null) {
				AreaStatisticCameraDataDto dto = new AreaStatisticCameraDataDto();
				dto.setAreaName(sawCameraData.getAreaName());
				dto.setCount(sawCameraData.getCount());
				List<AreaCameraStatistic> cameralist = new ArrayList<>();
				cameralist.add(sawCameraData);
				dto.setCameraList(cameralist);
				result.put(sawCameraData.getStationId(), dto);
			} else {
				AreaStatisticCameraDataDto dto = result.get(sawCameraData.getStationId());
				dto.setCount(dto.getCount() + sawCameraData.getCount());
				List<AreaCameraStatistic> cameralist = dto.getCameraList();
				int i = 0;
				for(i = 0; i < cameralist.size(); i++) {
					if(cameralist.get(i).getId() == sawCameraData.getId()) {
						cameralist.get(i).setCount(cameralist.get(i).getCount() + sawCameraData.getCount());
						break;
					}
				}
				if(i == cameralist.size()) {
					cameralist.add(sawCameraData);
				}
			}
		}
		 LOG.info("statisticStationCamera fill zero start");
		// fill zero
		List<AreaCameraStatistic> cameraList = cameraServiceItf.findCameraStatistic(queryobject.getCameraids());
		cameraList.forEach(camera -> {
			if (sawmap.get(camera.getId()) == null) {
				if (result.get(camera.getStationId()) == null) {
					AreaStatisticCameraDataDto dto = new AreaStatisticCameraDataDto();
					dto.setAreaName(camera.getAreaName());
					dto.setCount(0);
					List<AreaCameraStatistic> cameralist = new ArrayList<>();
					cameralist.add(camera);
					dto.setCameraList(cameralist);
					result.put(camera.getStationId(), dto);
				} else {
					AreaStatisticCameraDataDto dto = result.get(camera.getStationId());
					List<AreaCameraStatistic> cameralist = dto.getCameraList();
					cameralist.add(camera);
				}
			}
		});
		
		return result;
	}
	
	@Override
	public Map<Long, AreaStatisticCameraDataDto> statisticStationCameraAlarm(StatisticDataQuery queryobject) {
		List<AreaCameraStatistic> sawresult = null;
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("id");
        areaFields.add("area_name");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "area");
		String sqlString = "select camera.id AS id, area.area_name, camera.name, count(*) as count, camera.station_id from "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " alarm use index (t_alarm_info_time), "
				+ cameraSql + ", "
				+ areaSql+ ", "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " task "
				+ "where alarm.confidence >= " + queryobject.getThreshold() + " and alarm.task_id = task.id "
				+ "and task.source_id = camera.id and area.id = camera.station_id and alarm.time >= '" + queryobject.getStarttime() 
				+ "' and alarm.time <= '" + queryobject.getEndtime() + "'";
		
		String cameraStatement = "";
		if (queryobject.getCameraids()[0] == 0) {
			cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and camera.id in");
		} else {
			List<Long> cameraidList = zoneAuthorizeService.filterIds(CameraInfo.class, Arrays.asList(queryobject.getCameraids()), null);
			cameraStatement = " and camera.id in (" + cameraidList.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(",")) + ") ";
		}
		sqlString += cameraStatement;
//		if (queryobject.getCameraids()[0] != 0) {
//			sqlString += " and camera.id in (:cameraids)";
//		}
		sqlString += " group by camera.id order by camera.id";
		try {
			Query query = this.entityManager.createNativeQuery(sqlString, AreaCameraStatistic.class);
//			if (queryobject.getCameraids()[0] != 0) {
//				query.setParameter("cameraids", Arrays.asList(queryobject.getCameraids()));
//			}
			sawresult = query.getResultList();
		} catch(Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		
		if (sawresult == null) {
			sawresult = new ArrayList<>();
		}
		Map<Long, AreaStatisticCameraDataDto> result = new HashMap<>();
		Map<Long, AreaCameraStatistic> sawmap = new HashMap<>();
		for(int index = 0; index < sawresult.size(); index++) {
			AreaCameraStatistic sawCameraData = sawresult.get(index);
			sawmap.put(sawCameraData.getId(), sawCameraData);
			if(result.get(sawCameraData.getStationId()) == null) {
				AreaStatisticCameraDataDto dto = new AreaStatisticCameraDataDto();
				dto.setAreaName(sawCameraData.getAreaName());
				dto.setCount(sawCameraData.getCount());
				List<AreaCameraStatistic> cameralist = new ArrayList<>();
				cameralist.add(sawCameraData);
				dto.setCameraList(cameralist);
				result.put(sawCameraData.getStationId(), dto);
			} else {
				AreaStatisticCameraDataDto dto = result.get(sawCameraData.getStationId());
				dto.setCount(dto.getCount() + sawCameraData.getCount());
				List<AreaCameraStatistic> cameralist = dto.getCameraList();
				int i = 0;
				for(i = 0; i < cameralist.size(); i++) {
					if(cameralist.get(i).getId() == sawCameraData.getId()) {
						cameralist.get(i).setCount(cameralist.get(i).getCount() + sawCameraData.getCount());
						break;
					}
				}
				if(i == cameralist.size()) {
					cameralist.add(sawCameraData);
				}
			}
		}
		
		// fill zero
		List<AreaCameraStatistic> cameraList = cameraServiceItf.findCameraStatistic(queryobject.getCameraids());
		cameraList.forEach(camera -> {
			if (sawmap.get(camera.getId()) == null) {
				if (result.get(camera.getStationId()) == null) {
					AreaStatisticCameraDataDto dto = new AreaStatisticCameraDataDto();
					dto.setAreaName(camera.getAreaName());
					dto.setCount(0);
					List<AreaCameraStatistic> cameralist = new ArrayList<>();
					cameralist.add(camera);
					dto.setCameraList(cameralist);
					result.put(camera.getStationId(), dto);
				} else {
					AreaStatisticCameraDataDto dto = result.get(camera.getStationId());
					List<AreaCameraStatistic> cameralist = dto.getCameraList();
					cameralist.add(camera);
				}
			}
		});
		
		return result;
	}
	
	@Override
	public List<StoredCrimeDataDto> storedCrimeData(StoredCrimeQuery querydata) {
		List<StoredCrimeDataDto> resp = null;
		String sqlString = null;
		if (querydata.getSubcrimeid() == 0 && querydata.getCrimeid() == 0) {
			if (querydata.getGender() == 1 || querydata.getGender() == 2) {
				sqlString = "SELECT * FROM (select blackbank.id, blackbank.bank_name, blackbank.list_type as type, count(*) personcount, sum(f.facecount) as facecount from "
						+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " person, "
						+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " blackbank, " 
						+ "(select ee.bank_id, count(*) as facecount, ee.from_person_id from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " ee "
							+"where ee.from_person_id in (select id from intellif_base.t_person_detail where real_gender =" + querydata.getGender() + ") "
							+"group by ee.from_person_id order by ee.from_person_id) f "
						+ "where blackbank.id = person.bank_id and f.from_person_id = person.id and person.real_gender = " + querydata.getGender() 
						+ " group by blackbank.id "; 
			} else {
				sqlString = "SELECT * FROM (select blackbank.id, blackbank.bank_name, blackbank.list_type as type, count(*) personcount, sum(f.facecount) as facecount from "
						+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " person, "
						+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " blackbank, " 
						+ "(select ee.bank_id, count(*) as facecount,ee.from_person_id from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " ee "
								+ "group by ee.from_person_id order by ee.from_person_id) f "
						+ "where blackbank.id = person.bank_id  and f.from_person_id = person.id group by blackbank.id ";
			}
		} else if (querydata.getSubcrimeid() == 0) {
			sqlString = "SELECT * FROM (SELECT blackbank.id, blackbank.bank_name,blackbank.list_type as type, count(*) as personcount,sum(f.facecount) as facecount FROM "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " person, " 
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " subcrime, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " blackbank, " 
					+ "(SELECT dd.id, count(*) as facecount,ee.from_person_id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " aa, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " bb, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " dd, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " ee WHERE ";
			if (querydata.getGender() == 1 || querydata.getGender() == 2) {
				sqlString += ("aa.real_gender = " + querydata.getGender() + " AND ");
			}
			sqlString += ("aa.crime_type = bb.id AND bb.fri_id = " + querydata.getCrimeid() + " AND ee.bank_id = dd.id AND aa.id = ee.from_person_id GROUP BY ee.from_person_id ORDER BY ee.from_person_id ) f WHERE ");
			if(querydata.getGender() == 1 || querydata.getGender() == 2) {
				sqlString += "person.real_gender = " + querydata.getGender() + " AND ";
			}
			sqlString += ("person.crime_type = subcrime.id AND subcrime.fri_id = " + querydata.getCrimeid() + " AND person.bank_id = blackbank.id AND f.from_person_id = person.id "
				+ "GROUP BY blackbank.id ");
		}else {
			sqlString = "SELECT * FROM (SELECT blackbank.id, blackbank.bank_name,blackbank.list_type as type, count(*) as personcount, sum(f.facecount) as facecount FROM "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " person, " 
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " subcrime, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " blackbank, " 
					+ "(SELECT dd.id, count(*) as facecount,ee.from_person_id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " aa, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CRIME_SEC_TYPE + " bb, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " dd, "
					+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " ee WHERE ";
			if(querydata.getGender() == 0 || querydata.getGender() == 1) {
				sqlString += ("aa.real_gender = " + querydata.getGender() + " AND ");
			}
			sqlString += ("aa.crime_type = bb.id AND bb.fri_id = " + querydata.getCrimeid() + " AND bb.id = " + querydata.getSubcrimeid() 
					+ " AND ee.bank_id = dd.id AND aa.id = ee.from_person_id GROUP BY ee.from_person_id ORDER BY ee.from_person_id ) f WHERE ");
			if(querydata.getGender() == 0 || querydata.getGender() == 1) {
				sqlString += "person.real_gender = " + querydata.getGender() + " AND ";
			}
			sqlString += ("person.crime_type = subcrime.id AND subcrime.fri_id = " + querydata.getCrimeid() 
			+ " AND subcrime.id = " + querydata.getSubcrimeid() + " AND person.bank_id = blackbank.id AND f.from_person_id = person.id "
			+ "GROUP BY blackbank.id ");
		}
		sqlString += " UNION SELECT blackbank.id, blackbank.bank_name, blackbank.list_type as type, 0 personcount, 0 facecount "
				+ "FROM intellif_base.t_black_bank blackbank) tb GROUP BY id ORDER BY id";
		try {
			Query query = this.entityManager.createNativeQuery(sqlString, StoredCrimeDataDto.class);
			resp = (ArrayList<StoredCrimeDataDto>)query.getResultList();
		} catch(Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}

	@Override
	public List<CameraStatisticInfoDto> statisticCameraInfo() {
		List<CameraStatisticInfo> result = null;
		String sqlString = "SELECT station.`id` AS stationid, station.`station_name` AS stationname, count(*) AS quantity, GROUP_CONCAT(CAST(camera.`name` AS CHAR) SEPARATOR 0x1D) AS cameranames FROM " 
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION + " station, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO 
				+ " camera WHERE station.id = camera.station_id GROUP BY station.id ORDER BY station.id";
		try{
			Query query = this.entityManager.createNativeQuery(sqlString, CameraStatisticInfo.class);
			result = (ArrayList<CameraStatisticInfo>)query.getResultList();
		} catch(Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		List<CameraStatisticInfoDto> resp = new ArrayList<>();
		if(result != null) {
			result.forEach(item -> {
				try {
					CameraStatisticInfoDto dto = new CameraStatisticInfoDto();
					for(Field field : item.getClass().getDeclaredFields()) {
						if(field.isAnnotationPresent(MapDTO.class)) {
							MapDTO annotation = field.getAnnotation(MapDTO.class);
							Field fieldDTO = dto.getClass().getDeclaredField(annotation.dtofieldname()); 
							fieldDTO.setAccessible(true);
							field.setAccessible(true);
							fieldDTO.set(dto, field.get(item));
						} else if (field.isAnnotationPresent(MapDTOSplitStr.class)) {
							MapDTOSplitStr annotation = field.getAnnotation(MapDTOSplitStr.class);
							boolean bychar = annotation.bychar();
							boolean bystring = annotation.bystring();
							field.setAccessible(true);
							if(bychar) {
								Field fieldDTO = dto.getClass().getDeclaredField(annotation.dtofieldname());
								fieldDTO.setAccessible(true);
								char separatorchar = annotation.separatorchar();
								fieldDTO.set(dto, ((String)field.get(item)).split(String.valueOf(separatorchar)));
							} else if(bystring) {
								Field fieldDTO = dto.getClass().getDeclaredField(annotation.dtofieldname());
								fieldDTO.setAccessible(true);
								String separator = annotation.separator();
								fieldDTO.set(dto, ((String)field.get(item)).split(separator));
							}
						}
					}
					resp.add(dto);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		return resp;
	}

	@Override
	public List<CameraGeometryInfoDto> cameraGeometryInfo() {
		return cameraServiceItf.findCameraGeometry();
	} 
	
	@Override
	public Long faceCountByStationIdNPeriod(long id, String starttime, String endtime) {
		return faceInfoDaoImpl.findByStationNPeriod(id, starttime, endtime);
	} 

}
