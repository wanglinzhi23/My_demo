/**
 *
 */
package intellif.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CrimeFriTypeDao;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.UserInfo;
import intellif.dto.EventDto;
import intellif.dto.JsonObject;
import intellif.service.AlarmServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.G20Service;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.CrimeFriType;
import intellif.database.entity.EventInfo;
import intellif.database.entity.G20Statistic;

/**
 * use to handle G20 business 
 * 
 * @author simon_zhang
 *
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_G20)
public class G20Controller {

    private static Logger LOG = LogManager.getLogger(G20Controller.class);

    @Autowired
    private G20Service g20Service;

    /*
     * Autowire objects to this controller.
    */
    @Autowired
    private AlarmInfoDao _alarmInfoDao;
    @Autowired
    private AlarmServiceItf _alarmService;
    @Autowired
    private CameraServiceItf cameraService;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private CrimeFriTypeDao crimeFriTypeRepository;


    @RequestMapping(value = "/statistic/{starttime}/{endtime}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "取得当天会场进出人数统计")
    public JsonObject list(@PathVariable("starttime") String startTime, @PathVariable("endtime") String endTime)  {
    	List<G20Statistic> statistics = g20Service.findG20Statistic(startTime, endTime);
        return new JsonObject(statistics);
    }
    

    @RequestMapping(value = "/station/{id}/threshold/{threshold}/page/{page}/important/{important}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定派出所ID下的实时报警情况")
    public JsonObject findEventsByStationId(@PathVariable("id") long id, @PathVariable("threshold") double threshold, 
    		@PathVariable("page") int page, @PathVariable("important") int important) {
    	long start = System.currentTimeMillis();
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        Iterable<CameraInfo> cameraList;
        if(id>0) {
        	cameraList = this.cameraService.findByStationId(id);
        } else {
        	cameraList = this.cameraService.findAll();
        }
        String ids = "";
        for (CameraInfo camera : cameraList) {
            ids += "," + camera.getId();
        }
        if (ids.length() > 0) {
            ids = ids.substring(1);
            List<PersonDetail> alarmPersonList = _alarmService.findAlarmPersonByCameraId(ids, 0, threshold, page, 40);
            String personIds = new String();
            for (PersonDetail alarmPerson : alarmPersonList) {
            	personIds += "," + alarmPerson.getId();
            }
            
            long stationId = (((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
            List<EventInfo> eventList = g20Service.findAllPersonEvents(stationId, 40, personIds, threshold, important);
            Map<Long, List<EventInfo>> eventMap = new HashMap<Long, List<EventInfo>>();
            for (EventInfo event : eventList) {
            	Long personId = event.getPersonId();
            	List<EventInfo> personEvents = eventMap.get(personId);
            	if (personEvents == null) {
            		personEvents = new ArrayList<EventInfo>();
            		eventMap.put(personId, personEvents);
            	}
            	personEvents.add(event);

            }
            
            for (PersonDetail alarmPerson : alarmPersonList) {
              List<EventInfo> personEvents = eventMap.get(alarmPerson.getId());
              EventDto eventDto = new EventDto(alarmPerson);
              CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
              if(null==crimeFriType) {
              	eventDto.setCrimeName("未知");
              } else {
              	eventDto.setCrimeName(crimeFriType.getFullName());
              }
              eventDto.setEvents(personEvents);
              respEventDtoList.add(eventDto);
            } 
            LOG.info("event time: "+(System.currentTimeMillis() - start));
        }
        return new JsonObject(respEventDtoList);
    }

}
