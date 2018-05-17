package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.CameraInfoDao;
import intellif.database.entity.CameraInfo;
import intellif.dto.ErrorDto;
import intellif.dto.PersonFlowCollectDto;
import intellif.dto.PersonStatisticCount;
import intellif.excel.ExcelView;
import intellif.excel.PersonStatisticsXLS;
import intellif.service.PersonQueryService;
import intellif.utils.IntellifUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonQueryController.java
 * @Package intellif.controllers
 * @Description 人流查询
 * @date 2018 05-03 11:55.
 */
@RestController
public class PersonQueryController {
	@Autowired
	private PersonQueryService personQueryService;
	private static final Logger logger = LogManager.getLogger(PersonQueryController.class);
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}


	@Autowired
	CameraInfoDao cameraInfoDao;
	//将对象转化处理
	/**
	 * 人流量统计的接口
	 * url: /intellif/person/statistics
	 * @param type 1:当日实时, 2:历史统计
	 * @param timeType 10:日, 20:月,30:年
	 * @param personType 10:总榜,20:性别榜,30:年龄榜
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return PersonFlowCollectDto对象
	 */
	@RequestMapping(value = GlobalConsts.R_ID_PERSION_STATISTICS+"/statistics", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "人流量数据查询(当日或历史)")
	public Object personStatistics(@RequestParam(value = "type") String type ,@RequestParam(value = "startTime",required =false)Date startTime ,@RequestParam(value = "endTime",required =false)Date endTime ,
	                               @RequestParam(value = "timeType",required =false)Integer timeType,@RequestParam(value = "personType")Integer personType,@RequestParam(value = "areaIdString")String areaIdString) {
		List<PersonStatisticCount> list;
		List<String> areaIds;
		List<Integer> areaIdList = new ArrayList<>();
		if(areaIdString != null && !areaIdString.equals("")) {
			String st[]=areaIdString.split(",");
			areaIds=Arrays.asList(st);
			for(String str: areaIds){
				areaIdList.add(Integer.valueOf(str));
			}
			if(personType==null ||!(personType.equals(10)||personType.equals(20)||personType.equals(30))) {
				return new ErrorDto("1002", "传入的查询类型personType为空或者错误");
			}
			List<CameraInfo> cameraInfos=cameraInfoDao.findByStationIdList(areaIdList);
			if(cameraInfos==null||cameraInfos.size()==0){
				return new ErrorDto("1009", "传入的areaId不存在数据");
			}
			if ("1".equals(type)) {
				list = personQueryService.getTodayPersonCount(personType, cameraInfos);
			} else if ("2".equals(type)) {
				if (startTime == null || endTime == null || endTime.before(startTime)) {
					return new ErrorDto("1004", "查询历史人流量统计时时间必须不为空,且开始时间必须早于结束时间");
				}
				if(timeType==null ||!(timeType.equals(10)||timeType.equals(20)||timeType.equals(30))){
					return new ErrorDto("1005", "按时间维度查询timeType传参为空或错误,参数传入错误");
				}
				list = personQueryService.getHistoryPersonCount(timeType, personType, startTime, endTime, cameraInfos);
			} else {
				return new ErrorDto("1003", "查询类型type传入为空或错误,实时和历史区分");
			}
			PersonFlowCollectDto pfcDto;
			if (personType.equals(10)) {
				pfcDto = getPersonFlowCollectDto(list);
			} else if (personType.equals(20)) {
				pfcDto = getPersonFlowCollectDtoByGender(list);
			} else {
				pfcDto = getPersonFlowCollectDtoByAge(list);
			}
			Map<Object, Object> jsonMap = new HashMap<>();
			jsonMap.put("PersonFlowCollectDto",pfcDto);
			return jsonMap;
		}else {
			return new ErrorDto("1001", "商铺areaIdString应不为空");
		}
	}

	interface ConversionHandler{
		void handle(PersonStatisticsXLS psx, PersonStatisticCount psc);
	}

	private void fillListData2Map(Map<String,PersonStatisticsXLS> map, List<PersonStatisticCount> all, ConversionHandler myHandler) {
		if(all==null) return;
		PersonStatisticsXLS psx;
		for(PersonStatisticCount psc : all){
			psx = map.get(psc.getTime());
			if(psx==null){
				psx = new PersonStatisticsXLS();
				psx.setTime(psc.getTime());
				map.put(psc.getTime(), psx);
			}
			myHandler.handle(psx, psc);
		}
	}

	@RequestMapping(value = GlobalConsts.R_ID_PERSION_STATISTICS+"/statistics/export",method = RequestMethod.POST)
	@ApiOperation(httpMethod = "GET", value = "人流量数据导出加密")
	@ResponseBody
	public  Map<String,String> getExportUrl(@PathVariable String type, @PathVariable Integer timeType,
	                                                     @PathVariable Date startTime, @PathVariable Date endTime,
	                                                     @PathVariable String areaIdString) {
		if(areaIdString == null || areaIdString.equals("")) {
			throw new RuntimeException("商铺必须存在");
		}
		List<String> areaIds;
		List<Integer> areaIdList = new ArrayList<>();
		String st[] = areaIdString.split(",");
		areaIds = Arrays.asList(st);
		for (String str : areaIds) {
			areaIdList.add(Integer.valueOf(str));
		}
		List<CameraInfo> cameraInfoList=cameraInfoDao.findByStationIdList(areaIdList);
		if(cameraInfoList==null || cameraInfoList.size()==0){
			logger.warn("找不到商户的摄像头,商户id:{0}",areaIdString);
			throw new RuntimeException("找不到商户的摄像头,商户id: "+areaIdString);
		}
		if(timeType==null ||!(timeType.equals(10)||timeType.equals(20)||timeType.equals(30))){
			throw new RuntimeException("按时间维度查询timeType传参为空或错误,参数传入错误");
		}
		//校验用户的areaID是否有权限
		String startTimeStr;
		String endTimeStr;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			startTimeStr = df.format(startTime);
			endTimeStr = df.format(endTime);
		} catch (Exception e) {
			throw new RuntimeException("startTime或endTime日期格式错误");
		}
		Map<String,Object> map = new HashMap<>();
		map.put("type",type);
		map.put("timeType",timeType);
		map.put("startTime",startTimeStr);
		map.put("endTime",endTimeStr);
		map.put("areaIdString",areaIdString);
		map.put("time",new Date().getTime()+10*60*1000);
		JSONObject jsonObject = JSONObject.fromObject(map);
		String jsonStr = jsonObject.toString();

		BASE64Encoder en=new BASE64Encoder();
		String enStr;
		try {
			enStr = en.encode(jsonStr.getBytes("utf-8")).replaceAll("[\\s*\t\n\r]", "");
		} catch (UnsupportedEncodingException e) {
			logger.error("人流量导出功能的加密过程出现异常: {0}", e.getMessage());
			throw new RuntimeException("服务异常,请刷新页面再试...");
		}
		Map<String,String> mapSn =new HashMap();
		mapSn.put("sn",enStr );
		return mapSn;
	}

	// url: http://127.0.0.1:8083/api/intellif/dataExport/person/statistics?sn=xxxxxxx
	@RequestMapping(value = GlobalConsts.R_ID_DataExport+"/person/statistics", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "人流量数据导出")
	public ModelAndView exportExcel3(@RequestParam(value = "sn")String sn) {
		BASE64Decoder den=new BASE64Decoder();
		String jsonStr;
		try {
			jsonStr = new String(den.decodeBuffer(sn));
		} catch (IOException e) {
			throw new RuntimeException("下载请求验证失败,请刷新页面再试...");
		}
		JSONObject jsonObject =JSONObject.fromObject(jsonStr);
		if(IntellifUtil.obj2long(jsonObject.get("time")) < new Date().getTime() ){
			throw new RuntimeException("已经超时，不允许下载...");
		}
		String type = IntellifUtil.obj2str(jsonObject.get("type"));
		Integer timeType = IntellifUtil.obj2int(jsonObject.get("timeType"));
		String startTimeStr = IntellifUtil.obj2str(jsonObject.get("startTime"));
		String endTimeStr = IntellifUtil.obj2str(jsonObject.get("endTime"));
		String areaIdString = IntellifUtil.obj2str(jsonObject.get("areaIdString"));
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date startTime = df.parse(startTimeStr);
			Date endTime = df.parse(endTimeStr);
			return exportExcel2(type, timeType, startTime, endTime, areaIdString);
		}catch (Exception e ){
			throw new RuntimeException("日期错误");
 		}

	}
	private ModelAndView exportExcel2( String type, Integer timeType,Date startTime,Date endTime,String areaIdString) {
		List<String> areaIds;
		List<Integer> areaIdList = new ArrayList<>();
		String st[] = areaIdString.split(",");
		areaIds = Arrays.asList(st);
		for (String str : areaIds) {
			areaIdList.add(Integer.valueOf(str));
		}
		List<CameraInfo> cameraInfoList=cameraInfoDao.findByStationIdList(areaIdList);
		List<PersonStatisticsXLS> list;
		PersonFlowCollectDto pfcDtoAll,pfcDtoSex,pfcDtoAge;
		try {
			if (type.equals("1")) {
				List<PersonStatisticCount>  listAll = personQueryService.getTodayPersonCount(10, cameraInfoList);
				List<PersonStatisticCount>  listSex = personQueryService.getTodayPersonCount(20, cameraInfoList);
				List<PersonStatisticCount>  listAge = personQueryService.getTodayPersonCount(30, cameraInfoList);
				pfcDtoAll = getPersonFlowCollectDto(listAll);
				pfcDtoSex = getPersonFlowCollectDtoByGender(listSex);
				pfcDtoAge = getPersonFlowCollectDtoByAge(listAge);
			}else{
				List<PersonStatisticCount>  listAll = personQueryService.getHistoryPersonCount(timeType,10,startTime,endTime, cameraInfoList);
				List<PersonStatisticCount>  listSex = personQueryService.getHistoryPersonCount(timeType,20,startTime,endTime, cameraInfoList);
				List<PersonStatisticCount>  listAage = personQueryService.getHistoryPersonCount(timeType,30,startTime,endTime, cameraInfoList);
				pfcDtoAll = getPersonFlowCollectDto(listAll);
				pfcDtoSex = getPersonFlowCollectDtoByGender(listSex);
				pfcDtoAge = getPersonFlowCollectDtoByAge(listAage);
			}
			list = combinedStatistics(pfcDtoAll, pfcDtoSex, pfcDtoAge);

			String[] showName = new String[]{"时间", "男性", "女性"/*, "性别未知"*/,"儿童", "少年","青年","中年","老年"/*,"年龄未知"*/,"总人数"};
			String[] fieldName = new String[]{"time", "male", "female"/*, "unknownSexual"*/,"child", "teens","young","middleAge","oldAge"/*,"unknownAge"*/,"total"};

			return new ModelAndView(new ExcelView<>("商铺人流统计数据", list, showName, fieldName), new HashMap<>());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	//合并三个集合数据
	private List<PersonStatisticsXLS> combinedStatistics(PersonFlowCollectDto pfcDtoAll, PersonFlowCollectDto pfcDtoSex, PersonFlowCollectDto pfcDtoAge) {
		Map<String ,PersonStatisticsXLS> map = new HashMap<>();
		fillListData2Map(map, pfcDtoAll.getAll(), (psx, psc) -> psx.setTotal(psc.getCount()));
		fillListData2Map(map, pfcDtoSex.getMale(), (psx, psc) -> psx.setMale(psc.getCount()));
		fillListData2Map(map, pfcDtoSex.getFemale(), (psx, psc) -> psx.setFemale(psc.getCount()));
		fillListData2Map(map, pfcDtoSex.getUnknownSexual(), (psx, psc) -> psx.setUnknownSexual(psc.getCount()));

		fillListData2Map(map, pfcDtoAge.getChild(), (psx, psc) -> psx.setChild(psc.getCount()));
		fillListData2Map(map, pfcDtoAge.getTeens(), (psx, psc) -> psx.setTeens(psc.getCount()));
		fillListData2Map(map, pfcDtoAge.getYoung(), (psx, psc) -> psx.setYoung(psc.getCount()));
		fillListData2Map(map, pfcDtoAge.getMiddleAge(), (psx, psc) -> psx.setMiddleAge(psc.getCount()));
		fillListData2Map(map, pfcDtoAge.getOldAge(), (psx, psc) -> psx.setOldAge(psc.getCount()));
		fillListData2Map(map, pfcDtoAge.getUnknownAge(), (psx, psc) -> psx.setUnknownAge(psc.getCount()));

		List<PersonStatisticsXLS> list = new ArrayList<>(map.values());
		list.sort(Comparator.comparingInt(o -> Integer.valueOf(o.getTime().replaceAll("-", ""))));
		return list;
	}
	//按年龄段分开集合
	private PersonFlowCollectDto getPersonFlowCollectDtoByAge(List<PersonStatisticCount> list) {
		PersonFlowCollectDto pfcDto;
		List<PersonStatisticCount> unknown = new ArrayList<>();
		List<PersonStatisticCount> child = new ArrayList<>();
		List<PersonStatisticCount> teens = new ArrayList<>();
		List<PersonStatisticCount> young = new ArrayList<>();
		List<PersonStatisticCount> middleAge = new ArrayList<>();
		List<PersonStatisticCount> oldAge = new ArrayList<>();
		Map<String, PersonStatisticCount> time2CountMapChild = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapTeens = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapYoung = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapMiddleAge = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapOldAge = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapUnKnownAge = new HashMap<>();
		long count=0;
		for (PersonStatisticCount psc : list) {

			count=count+psc.getCount();
			if (psc.getAge() == 0) {
				add2ListMergeTime(unknown, time2CountMapUnKnownAge, psc);
			} else if (psc.getAge() >= 1 && psc.getAge() <= 2) {
				add2ListMergeTime(child, time2CountMapChild, psc);
			} else if (psc.getAge() == 3) {
				add2ListMergeTime(teens, time2CountMapTeens, psc);
			} else if (psc.getAge() >= 4 && psc.getAge() <= 6) {
				add2ListMergeTime(young, time2CountMapYoung, psc);
			} else if (psc.getAge() >= 7 && psc.getAge() <= 8) {
				add2ListMergeTime(middleAge, time2CountMapMiddleAge, psc);
			} else if (psc.getAge() >= 9) {
				add2ListMergeTime(oldAge, time2CountMapOldAge, psc);
			}
		}
		pfcDto = new PersonFlowCollectDto(child, teens, young, middleAge, oldAge);
		boolean juntan = true;//如果前端展示逻辑有改动，这里就可以直接更改这里了
		if(juntan){
			juntanMap(time2CountMapUnKnownAge,time2CountMapChild,time2CountMapTeens,time2CountMapYoung,time2CountMapMiddleAge,time2CountMapOldAge);
		}else{
			pfcDto.setUnknownAge(unknown);
		}
		pfcDto.setCount(count);
		return pfcDto;
	}
    //按性别分开集合
	private PersonFlowCollectDto getPersonFlowCollectDtoByGender(List<PersonStatisticCount> list) {
		PersonFlowCollectDto pfcDto;
		long count=0;
		List<PersonStatisticCount> unknown = new ArrayList<>();
		List<PersonStatisticCount> male = new ArrayList<>();
		List<PersonStatisticCount> female = new ArrayList<>();
		Map<String, PersonStatisticCount> time2CountMapMale = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapFemale = new HashMap<>();
		Map<String, PersonStatisticCount> time2CountMapUnknownSexual = new HashMap<>();
		for (PersonStatisticCount psc : list) {
			count=count+psc.getCount();
			if (psc.getGender() == 1) {
				add2ListMergeTime(male, time2CountMapMale, psc);
			} else if (psc.getGender() == 2) {
				add2ListMergeTime(female, time2CountMapFemale, psc);
			} else {
				add2ListMergeTime(unknown, time2CountMapUnknownSexual, psc);
			}
		}
		pfcDto = new PersonFlowCollectDto(male, female);
		boolean juntan = true;//如果前端展示逻辑有改动，这里就可以直接更改这里了
		if(juntan){
			juntanMap(time2CountMapUnknownSexual,time2CountMapMale, time2CountMapFemale);
		}else{
			pfcDto.setUnknownSexual(unknown);
		}
		pfcDto.setCount(count);
		return pfcDto;
	}

	private void juntanMap(Map<String,PersonStatisticCount> time2UnknownCount, Map<String,PersonStatisticCount>... mapArr) {
		Map<String,Long> time2KnownCount = new HashMap<>();
		Map<String,Long> time2tmpSum = new HashMap<>();
		for(Map<String,PersonStatisticCount> sexTimeMap : mapArr){//男性的 time:count的map
			for (String time : sexTimeMap.keySet()) {
				if (time2KnownCount.containsKey(time)) {
						time2KnownCount.put(time, time2KnownCount.get(time) + sexTimeMap.get(time).getCount());
					} else {
						time2KnownCount.put(time, sexTimeMap.get(time).getCount());
				}
			}

		}
		for(int j = 0;j<mapArr.length;j++){
			Map<String,PersonStatisticCount> sexTimeMap = mapArr[j];
			long tmp = 0;//增量
			//long tmpSum = 0;//增量
			long unknownCount;//未知的数量
			long knownCount;//男女总数数量
			List<PersonStatisticCount> list = new ArrayList<>(sexTimeMap.values());
			list.sort(Comparator.comparingInt(o -> Integer.valueOf(o.getTime().replaceAll("-", ""))));
			for(int i = 0; i<list.size()-1;i++){
				PersonStatisticCount psc = list.get(i);
				long count = IntellifUtil.obj2long(psc.getCount());
				if(time2UnknownCount.get(psc.getTime())!=null){
					unknownCount = IntellifUtil.obj2long(time2UnknownCount.get(psc.getTime()).getCount());
				}else {
					unknownCount = 0;
				}
				knownCount = IntellifUtil.obj2long(time2KnownCount.get(psc.getTime()));
				if(knownCount!=0){
					if(j==mapArr.length-1){
						//tmp = unknownCount-tmpSum;
						tmp = unknownCount - IntellifUtil.obj2long(time2tmpSum.get(psc.getTime()));
					}else{
						tmp = count*unknownCount/knownCount;
						//tmpSum+=tmp;
						time2tmpSum.put(psc.getTime(), IntellifUtil.obj2long(time2tmpSum.get(psc.getTime()))+tmp);
					}
				}
				psc.setCount(tmp+count);
				tmp=0;
			}
		}

	}



	//除去时间重复的叠加
	private PersonFlowCollectDto getPersonFlowCollectDto(List<PersonStatisticCount> list) {
		PersonFlowCollectDto pfcDto;
		long count=0;
		Map<String, PersonStatisticCount> time2CountMap = new HashMap<>();
		List<PersonStatisticCount> all = new ArrayList<>();
		for(PersonStatisticCount psc : list) {
			count=count+psc.getCount();
			add2ListMergeTime(all, time2CountMap, psc);
		}
		pfcDto = new PersonFlowCollectDto(all);
		pfcDto.setCount(count);
		return pfcDto;
	}

	/**
	 * 将psc添加进list中去,对于psc.time相同的的会对psc.count进行合并
	 */
	private void add2ListMergeTime(List<PersonStatisticCount> list, Map<String, PersonStatisticCount> time2CountMap, PersonStatisticCount psc) {
		if(time2CountMap.containsKey(psc.getTime())){//对于按照年龄划分的:针对age=1和age=2的应该进行合并
			PersonStatisticCount tmp = time2CountMap.get(psc.getTime());
			tmp.setCount(tmp.getCount()+psc.getCount());
		}else{//对于按照性别划分的其实time不会重复也就不会合并
			list.add(psc);
			time2CountMap.put(psc.getTime(),psc);
		}
	}
}
