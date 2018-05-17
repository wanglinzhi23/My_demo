package intellif.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.AreaDao;
import intellif.dao.CameraInfoDao;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.dto.JsonObject;
import intellif.dto.AreaStatisticCameraDataDto;
import intellif.dto.StatisticCameraCountDto;
import intellif.service.CrimeAlarmServiceItf;
import intellif.service.DataQueryServiceItf;
import intellif.database.entity.AreaCameraStatistic;
import intellif.database.entity.CountInfo;
import intellif.database.entity.FaceStatisticCount;
import intellif.database.entity.StatisticDataQuery;
import intellif.database.entity.StoredCrimeQuery;

/**
 * 
 * @author yktang
 * Created by V1.1.2
 *
 */

@RestController
@RequestMapping(GlobalConsts.R_ID_STATISTIC)
public class DataQueryController {

	private static Logger LOG = LogManager.getLogger(DataQueryController.class);

	@Autowired
    private CrimeAlarmServiceItf crimeAlarmServiceItf;
	@Autowired
    private DataQueryServiceItf dataQueryServiceItf;
	@Autowired
	private CameraInfoDao cameraInfoDao;
	@Autowired
	private AreaDao areaDao;

	// Get statistic data that collected in a fixed period with fixed camera.
    @RequestMapping(value = "/collected", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "摄像头流量数据查询")
    public JsonObject getcollectedheadcountbytime(@RequestBody @Valid StatisticDataQuery requestBody) { 
    	
    	Map<Long, List<FaceStatisticCount>> respMap = new HashMap<Long, List<FaceStatisticCount>>();
    	
    	//获取按时间统计的结果
    	Map<Long, List<FaceStatisticCount>> resultMap = getSawCollectedData(requestBody);
    	LOG.info("data convert start");
    	List<AreaCameraStatistic> aList = dataConvert(resultMap);
    	LOG.info("data convert end");
    	//获取按摄像头统计的结果
    	Map<Long, AreaStatisticCameraDataDto> cameraDetailMap = this.dataQueryServiceItf.statisticStationCamera(requestBody,aList);
    	
    	//如果有某些时间段没有统计到结果，则用0来填充
    	LOG.info("fillCollectedBlankTimePieces start");
		fillCollectedBlankTimePieces(resultMap, respMap, requestBody);
		
		return new JsonObject(new StatisticCameraCountDto(respMap, cameraDetailMap));
    }
    
   private List<AreaCameraStatistic> dataConvert(Map<Long, List<FaceStatisticCount>> faceMap){
	   List<AreaCameraStatistic> aList = new ArrayList<AreaCameraStatistic>();
	   Iterator<Long> iterator = faceMap.keySet().iterator();
	   while(iterator.hasNext()){
		   try{
			   long sourceId = iterator.next();
			   CameraInfo ci = cameraInfoDao.findOne(sourceId);
			   Area area = areaDao.findOne(ci.getStationId());
			   AreaCameraStatistic  acs = new AreaCameraStatistic(sourceId,area.getId(),area.getAreaName(),ci.getName());
			   List<FaceStatisticCount> fList = faceMap.get(sourceId);
			   long count = 0;
			   for(FaceStatisticCount item : fList){
				   count += item.getCount();
			   }
			   acs.setCount(count);
			   aList.add(acs);
		   }catch(Exception e){
			   LOG.error("dataConvert error,e:",e);
		   }
	   }
	   
	   return aList;
	   
   }
    
    private Map<Long, List<FaceStatisticCount>> getSawCollectedData(StatisticDataQuery requestBody) {
		Map<Long, List<FaceStatisticCount>> resultMap = this.dataQueryServiceItf.statisticByCameraByTimeslot(requestBody);
		Long[] cameraIds = requestBody.getCameraids();
		if (cameraIds[0] != 0) {
			for (int i = 0; i < cameraIds.length; i++) {
				if (resultMap.get(cameraIds[i]) == null) {
					resultMap.put(cameraIds[i], new ArrayList<FaceStatisticCount>());
				}
			}
		} else {
			Iterable<CameraInfo> cameraInfoIter = cameraInfoDao.findAll();
			for (CameraInfo camera : cameraInfoIter) {
				if (resultMap.get(camera.getId()) == null) {
					resultMap.put(camera.getId(), new ArrayList<FaceStatisticCount>());
				}
			}
		}
		return resultMap;
    }
    
    private void fillCollectedBlankTimePieces(Map<Long, List<FaceStatisticCount>> resultMap, Map<Long, List<FaceStatisticCount>> respMap, StatisticDataQuery requestBody) {
    	int timeslot = requestBody.getTimeslot();
    	String startTimeStr = requestBody.getStarttime();
    	String endTimeStr = requestBody.getEndtime();
    	Date enddate = new Date();
		Date currentdate = new Date();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	if (timeslot == 0) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	} else if (timeslot == 1) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	} else if (timeslot == 2) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH");
    	} else if (timeslot == 3) {
    		df = new SimpleDateFormat("yyyy-MM-dd");
    	}
    	
    	for (Map.Entry<Long, List<FaceStatisticCount>> entry : resultMap.entrySet()) {
    		try {
    			enddate = df.parse(endTimeStr);
    			currentdate = df.parse(startTimeStr);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    		int i = 0;
    		List<FaceStatisticCount> result = entry.getValue();
    		List<FaceStatisticCount> resp = new ArrayList<FaceStatisticCount>();
    		while(!currentdate.after(enddate)) {
    			if (i < result.size()) {
					Calendar cal = Calendar.getInstance();
					Date date;
					try {
						date = df.parse(result.get(i).getTime());
						cal.setTime(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				if (timeslot == 1) {
    					cal.set(Calendar.SECOND, 0);
    				} else if (timeslot == 2) {
    					cal.set(Calendar.MINUTE, 0);
    					cal.set(Calendar.SECOND, 0);
    				} else if (timeslot == 3) {
    					cal.set(Calendar.HOUR_OF_DAY, 0);
    					cal.set(Calendar.MINUTE, 0);
    					cal.set(Calendar.SECOND, 0);
    				}
					if (cal.getTime().after(currentdate)) {
						FaceStatisticCount countInfo = new FaceStatisticCount();
	    				countInfo.setCount(0);
	    				countInfo.setTime(df.format(currentdate));
	    				resp.add(countInfo);
	    			} else {
	    				resp.add(result.get(i++));
	    			}
    			} else {
    				FaceStatisticCount countInfo = new FaceStatisticCount();
    				countInfo.setCount(0);
    				countInfo.setTime(df.format(currentdate));
    				resp.add(countInfo);
    			}
    			if (timeslot == 0) {
    				currentdate = new Date(currentdate.getTime() + 1000);
    			} else if (timeslot == 1) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60));
    			} else if (timeslot == 2) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60 * 60));
    			} else if (timeslot == 3) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60 * 60 * 24));
    			}
    		}
    		respMap.put(entry.getKey(), resp);
		}
    }
    
    // Get statistic alarm data in a fixed period with fixed camera.
    @RequestMapping(value = "/alarm", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "摄像头报警数量查询")
    public JsonObject getalarmheadcount(@RequestBody @Valid StatisticDataQuery requestBody) {
    	Map<Long, List<CountInfo>> resultMap = this.crimeAlarmServiceItf.findAlarmCount(requestBody);
    	if (resultMap == null) {
    		resultMap = new HashMap<Long, List<CountInfo>>();
    	}
    	Map<Long, List<CountInfo>> respMap = new HashMap<Long, List<CountInfo>>();
    	Map<Long, AreaStatisticCameraDataDto> cameraDetailMap = this.dataQueryServiceItf.statisticStationCameraAlarm(requestBody);
    	Long[] cameraIds = requestBody.getCameraids();
    	if (cameraIds[0] != 0) {
			for (int i = 0; i < cameraIds.length; i++) {
				if (resultMap.get(cameraIds[i]) == null) {
					resultMap.put(cameraIds[i], new ArrayList<CountInfo>());
				}
			}
    	}
    	fillAlarmBlankTimePieces(resultMap, respMap, requestBody);
    	return new JsonObject(new StatisticCameraCountDto(respMap, cameraDetailMap));
    }
    
    private void fillAlarmBlankTimePieces(Map<Long, List<CountInfo>> resultMap, Map<Long, List<CountInfo>> respMap, StatisticDataQuery requestBody) {
    	int timeslot = requestBody.getTimeslot();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	if (timeslot == 0) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	} else if (timeslot == 1) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	} else if (timeslot == 2) {
    		df = new SimpleDateFormat("yyyy-MM-dd HH");
    	} else if (timeslot == 3) {
    		df = new SimpleDateFormat("yyyy-MM-dd");
    	}
    	Date enddate = new Date();
		Date currentdate = new Date();
		String startTimeStr = requestBody.getStarttime();
    	String endTimeStr = requestBody.getEndtime();
		for (Map.Entry<Long, List<CountInfo>> entry : resultMap.entrySet()) {
			try {
    			enddate = df.parse(endTimeStr);
    			currentdate = df.parse(startTimeStr);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    		int i = 0;
    		List<CountInfo> resp = new ArrayList<CountInfo>();
			List<CountInfo> result = entry.getValue();
			while(!currentdate.after(enddate)) {
    			if (i < result.size()) {
    				Calendar cal = Calendar.getInstance();
    				Date date;
					try {
						date = df.parse(result.get(i).getTime());
						cal.setTime(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
    				if (timeslot == 1) {
    					cal.set(Calendar.SECOND, 0);
    				} else if (timeslot == 2) {
    					cal.set(Calendar.MINUTE, 0);
        				cal.set(Calendar.SECOND, 0);	
    				} else if (timeslot == 3) {
    					cal.set(Calendar.HOUR_OF_DAY, 0);
    					cal.set(Calendar.MINUTE, 0);
        				cal.set(Calendar.SECOND, 0);
    				}
	    			if (cal.getTime().after(currentdate)) {
	    				CountInfo countInfo = new CountInfo();
	    				countInfo.setCount(0);
	    				countInfo.setTime(df.format(currentdate));
	    				resp.add(countInfo);
	    			} else {
	    				resp.add(result.get(i++));
	    			}
    			} else {
    				CountInfo countInfo = new CountInfo();
    				countInfo.setCount(0);
    				countInfo.setTime(df.format(currentdate));
    				resp.add(countInfo);
    			}
    			if (timeslot == 0) {
    				currentdate = new Date(currentdate.getTime() + 1000);
    			} else if (timeslot == 1) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60));
    			} else if (timeslot == 2) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60 * 60));
    			} else if(timeslot == 3) {
    				currentdate = new Date(currentdate.getTime() + (1000 * 60 * 60 * 24));
    			}
    		}
    		respMap.put(entry.getKey(), resp);
		}
    }
    
    // Search warehouse crime data.
    @RequestMapping(value = "/warehouse/crime", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "入库数据查询")
    public JsonObject getwarehousedata(@RequestBody @Valid StoredCrimeQuery querydata) {
    	return new JsonObject(this.dataQueryServiceItf.storedCrimeData(querydata));
    }
    
    // Search camera quantity information
    // 该接口在真实环境中未用到，前端调用了另一个现有接口
    @RequestMapping(value = "/camerainfo", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "摄像头数量详情")
    public JsonObject getcamerainfo() {
    	return new JsonObject(this.dataQueryServiceItf.statisticCameraInfo());
    }
    
    // Get camera geometry information
    @RequestMapping(value = "/camera/geometry", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回摄像头地理位置")
    public JsonObject getCameraGeometry() {
    	return new JsonObject(this.dataQueryServiceItf.cameraGeometryInfo());
    }
    
    // Count face numbers
    @RequestMapping(value = "/face/station/{id}/starttime/{starttime}/endtime/{endtime}")
    @ApiOperation(httpMethod = "GET", value = "根据时间段统计指定所的人脸抓拍数量")
    public JsonObject faceCountByStataionByPeriod(@PathVariable("id") long id, @PathVariable("starttime") String starttime, @PathVariable("endtime") String endtime) {
    	return new JsonObject(this.dataQueryServiceItf.faceCountByStationIdNPeriod(id, starttime, endtime));
    }
    
}
