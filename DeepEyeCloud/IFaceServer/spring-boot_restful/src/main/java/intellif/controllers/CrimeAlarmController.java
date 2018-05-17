package intellif.controllers;

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
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.CrimeAlarmDao;
import intellif.dao.OtherInfoDao;
import intellif.dto.AlarmQueryDto;
import intellif.dto.EventDto;
import intellif.dto.JsonObject;
import intellif.service.CrimeAlarmServiceItf;
import intellif.database.entity.EventInfo;
import intellif.database.entity.OtherInfo;

@RestController
@RequestMapping(GlobalConsts.R_ID_CRIME_ALARM)
public class CrimeAlarmController {
    // ==============
    // PRIVATE FIELDS
    // ==============

    @Autowired
    private CrimeAlarmDao crimeAlarmDao;
    @Autowired
    private OtherInfoDao otherInfoRepository;
    @Autowired
    private CrimeAlarmServiceItf crimeAlarmServiceItf;

/*    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有罪犯告警")
    public JsonObject list() {
        return new JsonObject(this.crimeAlarmDao.findAll());
    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id罪犯告警")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.crimeAlarmDao.findOne(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除指定id罪犯告警")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this.crimeAlarmDao.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/person/cameras", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询指定离线布控人ID下的历史事件情况")
    public JsonObject findEventsByPersonId(@RequestBody @Valid AlarmQueryDto alarmQueryDto) {
        OtherInfo person = otherInfoRepository.findOne(alarmQueryDto.getId());
        if(null == person) return new JsonObject(null);
        List<EventInfo> eventList = crimeAlarmServiceItf.findEventsByPersonId(alarmQueryDto);
		EventDto eventDto = new EventDto(person);
        eventDto.setEvents(eventList);
        return new JsonObject(eventDto);
    }

    @RequestMapping(value = "/cameras", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询离线布控库下的实时报警情况")
    public JsonObject findEventsByBankId(@RequestBody @Valid AlarmQueryDto alarmQueryDto) {
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
    	List<OtherInfo> alarmPersonList = crimeAlarmServiceItf.findAlarmPersonForOffline(alarmQueryDto);
    	if(alarmPersonList.size()==0) return new JsonObject(respEventDtoList);
    	for (OtherInfo alarmPerson : alarmPersonList) {
    		alarmQueryDto.setId(alarmPerson.getId());
    		alarmQueryDto.setPage(1);
    		alarmQueryDto.setPageSize(20);
    		List<EventInfo> eventList = crimeAlarmServiceItf.findEventsByPersonId(alarmQueryDto);
    		EventDto eventDto = new EventDto(alarmPerson);
    		eventDto.setEvents(eventList);
    		respEventDtoList.add(eventDto);
    	}
    	int count = (int) crimeAlarmServiceItf.countAlarmPersonForOffline(alarmQueryDto);
        return new JsonObject(respEventDtoList,0, 0, count);
    }
    
}
