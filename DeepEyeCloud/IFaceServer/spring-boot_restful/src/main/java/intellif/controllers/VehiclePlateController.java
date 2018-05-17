package intellif.controllers;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
import intellif.dto.FaceStreamRequest;
import intellif.dto.JsonObject;
import intellif.dto.VehiclePlateQueryDto;
import intellif.exception.MsgException;
import intellif.service.DataQueryServiceItf;
import intellif.service.FaceStreamServiceItf;
import intellif.service.VehiclePlateServiceItf;
import intellif.service.WifiAccessInfoServiceItf;
import net.sf.json.JSONObject;

/*
 * created by 大运项目, yktangint
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_VEHICLE)
public class VehiclePlateController {

	private static Logger LOG = LogManager.getLogger(VehiclePlateController.class);
			
	@Autowired
	private VehiclePlateServiceItf vehiclePlateServiceItf;

    @Autowired
    private WifiAccessInfoServiceItf wifiAccessInfoServiceItf;

    @Autowired
    private FaceStreamServiceItf faceStreamService;

	@RequestMapping(value = "/gatherNumberByTime", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "调用信义接口gatherNumberByTime.")
	public String gatherNumberByTime(@RequestBody @Valid VehiclePlateQueryDto requestDto) {
		LOG.info("/intellif/vehicle/gatherNumberByTime request: " + requestDto.toString());
		return vehiclePlateServiceItf.gatherNumberByTime(requestDto);
	}

	@RequestMapping(value = "/gatherCountByCrossingId", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "调用信义接口,查询当天指定镜头的车牌数量.")
	public String gatherCountByCrossing_Id(@RequestBody @Valid VehiclePlateQueryDto requestDto) {
		LOG.info("/intellif/vehicle/gatherCountByCrossingId request: " + requestDto.toString());
		return vehiclePlateServiceItf.gatherCountByCrossing_Id(requestDto);
	}
	
	@RequestMapping(value = "/getTotalCountByEveMinute", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "调用信义接口，查询每分钟的车流量")
	public String getTotalCountByEveMinute(@RequestBody @Valid VehiclePlateQueryDto requestDto) {
		LOG.info("/intellif/vehicle/getTotalCountByEveMinute request: " + requestDto.toString());
		return vehiclePlateServiceItf.getTotalCountByEveMinute(requestDto);
	}
	
	@RequestMapping(value = "/dayun/starttime/{starttime}/endtime/{endtime}/venueid/{venueid}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "给信义调用大运场馆查询车辆人数据接口，venueid 1：足球场；2：篮球馆")
    public JsonObject test(@PathVariable("starttime") Long starttime, @PathVariable("endtime") Long endtime, @PathVariable("venueid") Long venueid) {
        Map<String,Object> map = new HashMap<String,Object>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String starttimeStr = formatter.format(starttime);

        String endtimeStr = formatter.format(endtime);

        //车辆
        VehiclePlateQueryDto dto = new VehiclePlateQueryDto();
        dto.setCrossingId("515,516,517,518,519,520");
        dto.setStartTime(starttime);
        dto.setEndTime(System.currentTimeMillis());
        String json = vehiclePlateServiceItf.gatherCountByCrossing_Id(dto);
        JSONObject js = JSONObject.fromObject(json.substring(1, json.length()-1));

        //人数
       long person = 0L;
       FaceStreamRequest request = new FaceStreamRequest();
       request.setStartTime(starttimeStr);
       request.setEndTime(endtimeStr);
       if(venueid!=null && venueid.longValue()!=1L && venueid.longValue()!=2L){
           venueid = 2L;
       }
       request.setVenueId(venueid);
       try {
           person = faceStreamService.getRealTimeCountByStartTimeAndEndTime(request);
       } catch (Exception e) {
           LOG.error("faceStreamService.getRealTimeCount error", e);
       }

       //wifi
       Long wifi = wifiAccessInfoServiceItf.countTotalAccessDevice(starttime.toString(), System.currentTimeMillis()+"");

       map.put("car", Long.parseLong(js.getString("count")));
       map.put("person", person);
       map.put("wifi", wifi);

       return new JsonObject(map);
    }

}
