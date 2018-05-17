/**
 *
 */
package intellif.controllers;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CrimeFriTypeDao;
import intellif.dao.MarkInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.PersonDetail;
import intellif.dto.AlarmInfoDto;
import intellif.dto.AlarmStatisticByStationDto;
import intellif.dto.AlarmStatisticDto;
import intellif.dto.EventDto;
import intellif.dto.JsonObject;
import intellif.dto.QueryInfoDto;
import intellif.service.AlarmServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.UrgentAlarmServiceItf;
import intellif.service.UserServiceItf;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.CrimeFriType;
import intellif.database.entity.EventInfo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class AlarmController.</h1>
 * The AlarmController which serves request of the form /alarm and returns a JSON object representing an instance of AlarmInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc.
 * (see <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and static data storages),
 * while REST is a very-high-level API style (mostly for webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/alarm")
@RequestMapping(GlobalConsts.R_ID_URGENT_ALARM)
public class UrgentAlarmController {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 40;
    // ==============
    // PRIVATE FIELDS
    // ==============
    /*
     * Autowire objects to this controller.
    */
    @Autowired
    private AlarmInfoDao _alarmInfoDao;
    @Autowired
    private UrgentAlarmServiceItf _urgentAlarmService;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private CrimeFriTypeDao crimeFriTypeRepository;
    @Autowired
    private PersonDetailServiceItf personDetailService;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private MarkInfoDao markInfoDao;
    @Autowired
	private PropertiesBean propertiesBean;
    
    @ApiOperation(httpMethod = "POST", value = "Response a string describing the bank alarm info.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/bank/query/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findAlarmsByBanks(@RequestBody @Valid QueryInfoDto queryInfoDto,@PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	authority = ","+authority+",";
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> alarmPersonList = new ArrayList<PersonDetail>();
        String ids = queryInfoDto.getIds();
        if (!ids.isEmpty()) {
        	for(String idStr : ids.split(",")){
        		long id = Long.parseLong(idStr);
        		
            	if(authority.indexOf(","+id+",")>=0) {
            		List<PersonDetail> pList = _urgentAlarmService.findUrgentAlarmPersonByBankId(id, page, pagesize);
            		if(null != pList){
            			alarmPersonList.addAll(pList);
            		}
            	}
        	}
		}
    	for (PersonDetail alarmPerson : alarmPersonList) {
    		List<EventInfo> eventList = _urgentAlarmService.findEventsByPersonId(alarmPerson.getId(),0, DEFAULT_PAGE_NO, pagesize);
    		EventDto eventDto = new EventDto(alarmPerson);
    		CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
    		if(null==crimeFriType) {
    			eventDto.setCrimeName("未知");
    		} else {
    			eventDto.setCrimeName(crimeFriType.getFullName());
    		}
    		eventDto.setEvents(eventList);
    		respEventDtoList.add(eventDto);
    	}
        return new JsonObject(respEventDtoList);
    }
    @RequestMapping(value = "/banks/type/{type}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询用户授权下所有存在紧急布控人员的库信息列表")
    public JsonObject getBanksOfUrgentPersons(@PathVariable("type") int type) {
    	String authority = _userService.getAuthorityIds(type);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    	List<BlackBank> bankList = _urgentAlarmService.findBanksOfUrgentPersons(authority);
    	return new JsonObject(bankList);
    }
    
    @RequestMapping(value = "/black/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定紧急布控重点人脸ID下的事件情况")
    public JsonObject findEventsByBlackId(@PathVariable("id") long id) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        List<BlackDetail> blackList = _blackDetailDao.findOne(id, authority.split(","));
        if(blackList.size() <=0) {
            return new JsonObject("对不起，您没有查看权限！", 1001);
        }
        BlackDetail black = blackList.get(0);
		List<EventInfo> eventList = _urgentAlarmService.findEventsByPersonId(black.getFromPersonId(), 0, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
        PersonDetail person = (PersonDetail) personDetailService.findById(black.getFromPersonId());
        EventDto eventDto = new EventDto(person);
        eventDto.setEvents(eventList);
        CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(person.getCrimeType());
        if (crimeFriType != null) {
        	eventDto.setCrimeName(crimeFriType.getFullName());
        }
        
        return new JsonObject(eventDto);
    }
}