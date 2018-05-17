package intellif.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.wordnik.swagger.annotations.ApiOperation;

import intellif.audit.EntityAuditListener;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.itf.TreeNode;
import intellif.dao.AlarmInfoDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CrimeFriTypeDao;
import intellif.dao.MarkInfoDao;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.TaskInfoDao;
import intellif.database.dao.PersonDetailDao;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.UserInfo;
import intellif.dto.AlarmFaceZipDto;
import intellif.dto.AlarmInfoDto;
import intellif.dto.AlarmQueryDto;
import intellif.dto.AlarmStatisticByStationDto;
import intellif.dto.AlarmStatisticDto;
import intellif.dto.EventDto;
import intellif.dto.EventsByStationIdKey;
import intellif.dto.JsonObject;
import intellif.dto.MarkDto;
import intellif.dto.PersonQueryDto;
import intellif.dto.ProcessInfo;
import intellif.dto.QueryInfoDto;
import intellif.dto.ZipPathInfo;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.vo.FkInstitutionCode;
import intellif.service.AlarmServiceItf;
import intellif.service.BlackDetailServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.UrgentAlarmServiceItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.AlarmServiceImpl;
import intellif.service.impl.PersonDetailServiceImpl;
import intellif.utils.ApplicationResource;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FileUtil;
import intellif.utils.FunctionUtil;
import intellif.utils.PersonEventDtoComparable;
import intellif.database.entity.AlarmFaceZipTuple;
import intellif.database.entity.AlarmImageInfo;
import intellif.database.entity.AlarmPersonDetail;
import intellif.database.entity.CrimeFriType;
import intellif.database.entity.EventInfo;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.TaskInfo;
import intellif.exception.MsgException;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

/**
 * <h1>The Class AlarmController.</h1> The AlarmController which serves request
 * of the form /alarm and returns a JSON object representing an instance of
 * AlarmInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc. (see <a
 * href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and
 * static data storages), while REST is a very-high-level API style (mostly for
 * webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
// @RequestMapping("/intellif/alarm")
@RequestMapping(GlobalConsts.R_ID_ALARM)
public class AlarmController {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 40;
    // ==============
    // PRIVATE FIELDS
    // ==============
    /*
     * Autowire objects to this controller.
     */
    @Autowired
    private AlarmServiceImpl alarmService;
    @Autowired
    private CameraServiceItf cameraService;
    @Autowired
    private BlackDetailServiceItf blackDetailService;
    @Autowired
    private CrimeFriTypeDao crimeFriTypeRepository;
    @Autowired
    private PersonDetailServiceImpl personDetailService;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private MarkInfoDao markInfoDao;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private TaskInfoDao taskInfoDao;
    @Autowired
    private UrgentAlarmServiceItf _urgentAlarmService;
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
    @Autowired
    PoliceStationAuthorityDao policeStationAuthorityRepository;
    @Autowired
    private BlackBankDao blackBankDao;
    private static Logger LOG = LogManager.getLogger(AlarmController.class);
    /**
     * Posting anew alarmInfo
     *
     * @param alarmInfo
     * @return Response a string describing if the alarm info is successfully
     *         created or not.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the alarm info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid AlarmInfo alarmInfo) {
        long taskid = alarmInfo.getTaskId();
        TaskInfo taskinfo = taskInfoDao.findOne(taskid);
        long cameraid = taskinfo.getSourceId();
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
        return new JsonObject(alarmService.save(alarmInfo));
    }

    /*
     * @RequestMapping(method = RequestMethod.GET)
     * 
     * @ApiOperation(httpMethod = "GET", value =
     * "Response a list describing all of alarm info that is successfully get or not."
     * ) public JsonObject list() { // return new
     * JsonObject(this._alarmInfoDao.findAll()); return new
     * JsonObject(this._alarmInfoDao.findAllByOrderByTimeAsc()); }
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the alarm info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        
        AlarmInfo alarmInfo = alarmService.findById(id);
        long taskid = alarmInfo.getTaskId();
        TaskInfo taskinfo = taskInfoDao.findOne(taskid);
        long cameraid = taskinfo.getSourceId();
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
        return new JsonObject(alarmService.findById(id));
    }

    @RequestMapping(value = "/blackdetail/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the alarm info id is successfully get or not.")
    public JsonObject getByBlackDetailId(@PathVariable("id") long id) {
        List<AlarmInfoDto> respAlarmInfoDto = alarmService.findByBlackDetailId(id);
        return new JsonObject(respAlarmInfoDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  alarm info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid AlarmInfo alarmInfo) {
       try{
           AlarmInfo ai = alarmService.findById(id);
           long taskid = ai.getTaskId();
           TaskInfo taskinfo = taskInfoDao.findOne(taskid);
           long cameraid = taskinfo.getSourceId();
           zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
           if(ai.getStatus() > 0){
               return new JsonObject("该报警已处理!", RequestConsts.response_dataresult_error);
           }
           int status = alarmInfo.getStatus();
           if(0 != status){
               ai.setStatus(status);
           }
           return new JsonObject(alarmService.save(ai));
       }catch(MsgException e){
           return new JsonObject(e.getMessage(), e.getErrorCode());
       }catch(Exception e){
           return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
       }
      
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the alarm info is successfully delete or not.")
    public JsonObject delete(@PathVariable("id") long id) {
        try {
            AlarmInfo alarmInfo = (AlarmInfo) alarmService.findById(id);
            long taskid = alarmInfo.getTaskId();
            TaskInfo taskinfo = taskInfoDao.findOne(taskid);
            long cameraid = taskinfo.getSourceId();
            zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
            alarmService.delete(id);
        }catch (MsgException e) {
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch (Exception e) {
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the alarm is successfully searched or not.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findByCombinedConditions(@RequestBody @Valid AlarmInfoDto alarmInfoDto) {
        List<AlarmInfoDto> respAlarmInfoDto = alarmService.findByCombinedConditions(alarmInfoDto);
        return new JsonObject(respAlarmInfoDto);
    }

    @ApiOperation(httpMethod = "GET", value = "按天统计最近七天不同报警等级的报警次数", notes = "Current week based.")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    public JsonObject findByCountAndBetweenWeek() {
        List<AlarmStatisticDto> respAlarmStatisticDto = alarmService.findByCountAndBetweenWeek();
        return new JsonObject(respAlarmStatisticDto);
    }

    @ApiOperation(httpMethod = "GET", value = "按派出所统计最近七天报警次数", notes = "Current Policstation based.")
    @RequestMapping(value = "/statistic/station", method = RequestMethod.GET)
    public JsonObject statisticByPoliceStation() {
        List<AlarmStatisticByStationDto> respAlarmStatisticByStationDto = alarmService.statisticByPoliceStation();
        return new JsonObject(respAlarmStatisticByStationDto);
    }

    @RequestMapping(value = "/{id}/status/{value}", method = RequestMethod.PUT)
    //
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the alarm status is successfully patched or not.")
    public JsonObject patch(@PathVariable("id") long id, @PathVariable("value") int value) {
        AlarmInfo find = alarmService.findById(id);
        long taskid = find.getTaskId();
        TaskInfo taskinfo = taskInfoDao.findOne(taskid);
        long cameraid = taskinfo.getSourceId();
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
        find.setStatus(value);
        return new JsonObject(alarmService.save(find));
    }

    @RequestMapping(value = "/{id}/send/{value}", method = RequestMethod.PUT)
    //
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the alarm status is successfully patched or not.")
    public JsonObject send(@PathVariable("id") long id, @PathVariable("value") int value) {
        AlarmInfo find = alarmService.findById(id);
        if (null == find) {
            return new JsonObject("对不起，无该报警！", 1001);
        } else {
            long taskid = find.getTaskId();
            TaskInfo taskinfo = taskInfoDao.findOne(taskid);
            long cameraid = taskinfo.getSourceId();
            zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
            find.setSend(value);
            return new JsonObject(alarmService.save(find));
        }

    }

    // @RequestMapping(value =
    // "/station/{id}/threshold/{threshold}/page/{page}", method =
    // RequestMethod.GET)
    // @ApiOperation(httpMethod = "GET", value = "查询指定派出所ID下的实时报警情况")
    // public JsonObject findEventsByStationId(@PathVariable("id") long id,
    // @PathVariable("threshold") float threshold, @PathVariable("page") int
    // page) {
    // List<EventDto> respEventDtoList = new ArrayList<EventDto>();
    // Iterable<CameraInfo> cameraList;
    // if(id>0) {
    // cameraList = this._cameraInfoDao.findByStationId(id);
    // } else {
    // cameraList = this._cameraInfoDao.findAll();
    // }
    // String ids = "";
    // for (CameraInfo camera : cameraList) {
    // ids += "," + camera.getId();
    // }
    // if (ids.length() > 0) {
    // ids = ids.substring(1);
    // List<PersonDetail> alarmPersonList =
    // _alarmService.findAlarmPersonByCameraId(ids, page, DEFAULT_PAGE_SIZE);
    // for (PersonDetail alarmPerson : alarmPersonList) {
    // List<EventInfo> eventList =
    // _alarmService.findEventsByPersonId(alarmPerson.getId(), threshold,
    // DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
    // EventDto eventDto = new EventDto(alarmPerson);
    // CrimeFriType crimeFriType =
    // crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
    // if(null==crimeFriType) {
    // eventDto.setCrimeName("未知");
    // } else {
    // eventDto.setCrimeName(crimeFriType.getFullName());
    // }
    // eventDto.setEvents(eventList);
    // respEventDtoList.add(eventDto);
    // }
    // }
    // return new JsonObject(respEventDtoList);
    // }


    @RequestMapping(value = "/station/query", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询指定派出所ID下的实时报警情况")
    public JsonObject findEventsByStationId(@RequestBody @Valid QueryInfoDto queryInfoDto) {
        EventsByStationIdKey key = new EventsByStationIdKey(queryInfoDto);
        return new JsonObject(eventsByStationIdPost(key));
    }

    @ReadThroughSingleCache(namespace = "events_by_station_id", expiration = 60)
    private List<EventDto> eventsByStationIdPost(@ParameterValueKeyProvider final EventsByStationIdKey key) {
        String ids = key.getIds();
        float threshold = key.getThreshold();
        int page = key.getPage();
        int pagesize = key.getPagesize();
        int type = key.getType();
        String nodeType = key.getNodeType();
        long userId = key.getUserId();
        if(1 == key.getOnlyFirst()){
            key.setStatus("0,1,2");
        }
        if(0 == userId){
            key.setUserId(CurUserInfoUtil.getUserInfo().getId());
        }
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<TreeNode> cameraList = new ArrayList<TreeNode>();
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                long id = Long.parseLong(idStr);
                List<TreeNode> nodeList = zoneAuthorizeService.offspring(
                ZoneConfig.getNodeTypeMap().get(nodeType), id, CameraInfo.class);       
                cameraList.addAll(nodeList);
            }
        }
        List<Long> cameraIdList = cameraList.stream().map(m -> m.getId()).collect(Collectors.toList());
        String cIds = StringUtils.join(cameraIdList, ",");
        if (cIds.length() > 0) {
            List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByCameraIdConfidence(cIds,key);
           
            if(!CollectionUtils.isEmpty(alarmPersonList)){
                List<Long> personIdList = alarmPersonList.stream().map(s -> s.getId()).collect(Collectors.toList());
                 
                List<Future<?>> tasklist = new ArrayList<>();
                for (PersonDetail person : alarmPersonList) {
                    EventDto eventDto = new EventDto(person);
                    respEventDtoList.add(eventDto);
                    tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                        try {
                           QueryInfoDto dto = new QueryInfoDto();
                           dto.setPage(page);
                           dto.setPageSize(pagesize);
                           dto.setThreshold(threshold);
                           dto.setPersonId(person.getId());
                           dto.setUserId(key.getUserId());                    
                           List<EventInfo> result = alarmService.findEventsByPersonId(dto);
                           if(!CollectionUtils.isEmpty(result) && 1 == key.getOnlyFirst()){
                               if(!"0".equals(result.get(0).getStatus())){
                                   respEventDtoList.remove(eventDto);//onlyFirst条件下，最新一条处理过就不返回任何报警
                               }
                           }else if(CollectionUtils.isEmpty(result)){
                               respEventDtoList.remove(eventDto);
                           }
                           eventDto.setEvents(result);    
                        } catch (Exception e) {
                            LOG.error("findAlarmPersonByCameraId method error, personId：" + person.getId() + " error:", e);
                        } 
                    }));
                }
                tasklist.forEach(FunctionUtil::waitTillThreadFinish);
            }
        }
        
        return respEventDtoList;
    }

    @RequestMapping(value = "/bank/{id}/threshold/{threshold}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定库ID下的实时报警情况")
    public JsonObject findEventsByBankId(@PathVariable("id") long id, @PathVariable("threshold") float threshold, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {/*
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByBankId(id, threshold, page, pagesize);
        for (PersonDetail alarmPerson : alarmPersonList) {
          //  List<EventInfo> eventList = alarmService.findEventsByPersonId(0,alarmPerson.getId(), threshold, DEFAULT_PAGE_NO, pagesize);
            EventDto eventDto = new EventDto(alarmPerson);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
            if (null == crimeFriType) {
                eventDto.setCrimeName("未知");
            } else {
                eventDto.setCrimeName(crimeFriType.getFullName());
            }
            eventDto.setEvents(eventList);
            respEventDtoList.add(eventDto);
        }
        return new JsonObject(respEventDtoList);
    */
        return null;
        }

    @RequestMapping(value = "/bank/query/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询指定库ID下的实时报警情况")
    public JsonObject findEventsByBankId(@RequestBody @Valid QueryInfoDto queryInfoDto, @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
        String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
        authority = "," + authority + ",";
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> alarmPersonList = new ArrayList<PersonDetail>();
        String ids = queryInfoDto.getIds();
        
        //反恐事件流查询
        if((ids==null||ids.trim().equals(""))&&!StringUtils.isEmpty(queryInfoDto.getFkType())){
            ids = getFkBankIds(queryInfoDto.getSubInstitutionId(),queryInfoDto.getLocalInstitutionId());
        }
           
        float threshold = queryInfoDto.getThreshold();
        if (!StringUtils.isEmpty(ids)) {
            for (String idStr : ids.split(",")) {
                long id = Long.parseLong(idStr);
                if (authority.indexOf("," + id + ",") >= 0) {
                    List<PersonDetail> pList = null;
                    // 反恐事件流查询
                    if (queryInfoDto.getFkType()!=null&&!queryInfoDto.getFkType().trim().equals("")) {
                        pList = alarmService.findAlarmPersonByFkType(id, queryInfoDto.getFkType(), threshold, page, pagesize);
                    }else{
                        pList = alarmService.findAlarmPersonByBankId(id, threshold, page, pagesize);
                    }
                    if (null != pList) {
                        alarmPersonList.addAll(pList);
                    }
                }
            }
        }
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        long userId = userInfo.getId();
        List<Future<?>> tasklist = new ArrayList<>();
        int index = 0;
        for (PersonDetail alarmPerson : alarmPersonList) {
            EventDto eventDto = new EventDto(alarmPerson);
            eventDto.setIndex(++index);
            tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                try {
                  //  List<EventInfo> eventList = alarmService.findEventsByPersonId(userId,alarmPerson.getId(), threshold, DEFAULT_PAGE_NO, pagesize);
                    CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
                    if (null == crimeFriType) {
                        eventDto.setCrimeName("未知");
                    } else {
                        eventDto.setCrimeName(crimeFriType.getFullName());
                    }
                   // eventDto.setEvents(eventList);
                    respEventDtoList.add(eventDto);

                } catch (Exception e) {
                    LOG.error("findEventsByPersonId error, alarmPerson：" + alarmPerson.getId() + " error:", e);
                } 
            }));
        }
        tasklist.forEach(FunctionUtil::waitTillThreadFinish);
        Collections.sort(respEventDtoList, new PersonEventDtoComparable("asc"));
        return new JsonObject(respEventDtoList);
    }

    @RequestMapping(value = "/bank/cameras", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询指定库ID指定摄像头下的实时报警情况")
    public JsonObject findEventsByBankIdByCameras(@RequestBody @Valid AlarmQueryDto alarmQueryDto) {
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        int size = 0;
        boolean authority = false;
        long stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
        List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
        long id = alarmQueryDto.getId();
        for (PoliceStationAuthority policeStationAuthority : authorityList) {
            if (policeStationAuthority.getBankId() == id) {
                authority = true;
                break;
            }
        }
        if (authority) {
            List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonForOffline(alarmQueryDto);
            size = alarmService.countFindAlarmPersonForOffline(alarmQueryDto);
            for (PersonDetail alarmPerson : alarmPersonList) {
                alarmQueryDto.setPage(1);
                alarmQueryDto.setPageSize(20);
                alarmQueryDto.setId(alarmPerson.getId());
                List<EventInfo> eventList = alarmService.findEventsByPersonIdAndCameras(alarmQueryDto);
                EventDto eventDto = new EventDto(alarmPerson);
                CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
                if (null == crimeFriType) {
                    eventDto.setCrimeName("未知");
                } else {
                    eventDto.setCrimeName(crimeFriType.getFullName());
                }
                eventDto.setEvents(eventList);
                respEventDtoList.add(eventDto);
            }
        }
        return new JsonObject(respEventDtoList, 0, 0, size);
    }

    @RequestMapping(value = "/zip/person/cameras", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "导出指定人ID条件过滤后的报警情况")
    public JsonObject zipEventsByBankIdByCameras(@RequestBody @Valid AlarmQueryDto alarmQueryDto) {
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByBankIdAndCameras(alarmQueryDto);
        alarmQueryDto.setPage(1);
        alarmQueryDto.setPageSize(Integer.MAX_VALUE);
        List<PersonDetail> all = alarmService.findAlarmPersonByBankIdAndCameras(alarmQueryDto);
        for (PersonDetail alarmPerson : alarmPersonList) {
            alarmQueryDto.setPage(1);
            alarmQueryDto.setPageSize(20);
            alarmQueryDto.setId(alarmPerson.getId());
            List<EventInfo> eventList = alarmService.findEventsByPersonIdAndCameras(alarmQueryDto);
            EventDto eventDto = new EventDto(alarmPerson);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
            if (null == crimeFriType) {
                eventDto.setCrimeName("未知");
            } else {
                eventDto.setCrimeName(crimeFriType.getFullName());
            }
            eventDto.setEvents(eventList);
            respEventDtoList.add(eventDto);
        }
        return new JsonObject(respEventDtoList, 0, 0, all.size());
    }

    /*
     * @RequestMapping(value =
     * "/camera/{ids}/threshold/{threshold}/page/{page}/pagesize/{pagesize}",
     * method = RequestMethod.GET)
     * 
     * @ApiOperation(httpMethod = "GET", value = "查询指定摄像头ID下的实时报警情况") public
     * JsonObject findEventsByCameraId(@PathVariable("ids") String ids,
     * 
     * @PathVariable("threshold") float threshold, @PathVariable("page") int
     * page, @PathVariable("pagesize") int pagesize) { List<EventDto>
     * respEventDtoList = new ArrayList<EventDto>(); List<PersonDetail>
     * alarmPersonList = _alarmService.findAlarmPersonByCameraId(ids, 2,
     * threshold, page, pagesize); // for (PersonDetail alarmPerson :
     * alarmPersonList) { // List<EventInfo> eventList =
     * _alarmService.findEventsByPersonId(alarmPerson.getId(), threshold,
     * DEFAULT_PAGE_NO, pagesize); // EventDto eventDto = new
     * EventDto(alarmPerson); // eventDto.setEvents(eventList); // CrimeFriType
     * crimeFriType =
     * crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType()); // if
     * (crimeFriType != null) { //
     * eventDto.setCrimeName(crimeFriType.getFullName()); // } // //
     * respEventDtoList.add(eventDto); // }
     * 
     * long[] idList = alarmPersonList.stream().mapToLong(m ->
     * m.getId()).toArray(); List<EventInfo> eventList =
     * _alarmService.findEventsByPersonIdList(idList, threshold,
     * DEFAULT_PAGE_NO, pagesize); for (PersonDetail alarmPerson :
     * alarmPersonList) { EventDto eventDto = new EventDto(alarmPerson);
     * List<EventInfo> eventSubList = eventList.stream().filter(m ->
     * m.getPersonId() == alarmPerson.getId()).collect(Collectors.toList());
     * eventDto.setEvents(eventSubList); CrimeFriType crimeFriType =
     * crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType()); if
     * (crimeFriType != null) {
     * eventDto.setCrimeName(crimeFriType.getFullName()); }
     * respEventDtoList.add(eventDto); } return new
     * JsonObject(respEventDtoList); }
     */

    @RequestMapping(value = "/camera/query/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询指定摄像头ID下的实时报警情况")
    public JsonObject findEventsByCameraId(@RequestBody @Valid QueryInfoDto queryInfoDto, @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
        String ids = queryInfoDto.getIds();

        String[] idsArray = ids.split(",");
        Long[] idsLongArray = Arrays.stream(idsArray).map(m -> Long.valueOf(m)).toArray(Long[]::new);
        zoneAuthorizeService.checkIds(CameraInfo.class, idsArray);

        float threshold = queryInfoDto.getThreshold();
        int type = queryInfoDto.getListType();
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        // long time1 = System.currentTimeMillis();
        List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByCameraId(ids, type, threshold, page, pagesize);

        for (PersonDetail alarmPerson : alarmPersonList) {
            EventDto eventDto = new EventDto(alarmPerson);
            List<EventInfo> eventSubList = alarmService.findEventsByPersonIdCameraIds(alarmPerson.getId(), ids, threshold, 1, pagesize);
            // List<EventInfo> eventSubList = eventList.stream().filter(m ->
            // m.getPersonId() ==
            // alarmPerson.getId()).collect(Collectors.toList());
            eventDto.setEvents(eventSubList);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
            if (crimeFriType != null) {
                eventDto.setCrimeName(crimeFriType.getFullName());
            }
            respEventDtoList.add(eventDto);
        }
        // long time3 = System.currentTimeMillis();
        // System.out.println("time2-time1=" + String.valueOf(time2-time1));
        // System.out.println("time21-time2=" + String.valueOf(time21-time1));
        // System.out.println("time3-time21=" + String.valueOf(time3-time21));
        // System.out.println("time3-time2=" + String.valueOf(time3-time2));
        return new JsonObject(respEventDtoList);

    }

    @RequestMapping(value = "/person/query", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询指定人物ID下的历史事件情况")
    public JsonObject findEventsByPersonId(@RequestBody @Valid QueryInfoDto queryInfoDto) {
      
        String filterSql = " id = "+queryInfoDto.getPersonId();
        List<PersonDetail> personList = personDetailService.findByFilter(filterSql);
        if (CollectionUtils.isEmpty(personList)){
            return new JsonObject("布控人员不存在", RequestConsts.response_dataresult_error);
        }
        PersonDetail person = personList.get(0);
        List<EventInfo> eventList = null;   
        eventList = alarmService.findEventsByPersonId(queryInfoDto);
        
        EventDto eventDto = new EventDto(person);
        eventDto.setEvents(eventList);
      /*  CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(person.getCrimeType());
        if (null == crimeFriType) {
            eventDto.setCrimeName("未知");
        } else {
            eventDto.setCrimeName(crimeFriType.getFullName());
        }*/
        return new JsonObject(eventDto);
    }

    @RequestMapping(value = "/threshold/{threshold}/black/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询指定重点人脸ID下的事件情况")
    public JsonObject findEventsByBlackId(@PathVariable("id") long id, @PathVariable("threshold") float threshold) {
        String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
        if (authority.trim().length() == 0) {
            return new JsonObject("本单位没有库授权！", 1001);
        }
        String fSql = " id = "+ id +" AND bank_id in ("+authority+")";
        List<BlackDetail> blackList = blackDetailService.findByFilter(fSql);
        if (blackList.size() <= 0) {
            return new JsonObject("对不起，您没有查看权限！", 1001);
        }
        BlackDetail black = blackList.get(0);
        //List<EventInfo> eventList = alarmService.findEventsByPersonId(0,black.getFromPersonId(), threshold, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
     
        PersonDetail person = personDetailService.findById(black.getFromPersonId());
        EventDto eventDto = new EventDto(person);
        //eventDto.setEvents(eventList);
        CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(person.getCrimeType());
        if(null==crimeFriType) {
          eventDto.setCrimeName("未知");
        } else {
          eventDto.setCrimeName(crimeFriType.getFullName());
        }

        return new JsonObject(eventDto);
    }

    @ApiOperation(httpMethod = "POST", value = "分页获取结构化标签搜索嫌疑人事件")
    @RequestMapping(value = "/query/threshold/{threshold}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject query(@RequestBody @Valid PersonQueryDto personQueryDto, @PathVariable("threshold") float threshold, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> personList = personDetailService.query(personQueryDto, page, pagesize);
        for (PersonDetail person : personList) {
            //List<EventInfo> eventList = alarmService.findEventsByPersonId(0,person.getId(), threshold, DEFAULT_PAGE_NO, pagesize);
            EventDto eventDto = new EventDto(person);
            //eventDto.setEvents(eventList);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(person.getCrimeType());
            if(null==crimeFriType) {
              eventDto.setCrimeName("未知");
            } else {
              eventDto.setCrimeName(crimeFriType.getFullName());
            }
            respEventDtoList.add(eventDto);
        }
        return new JsonObject(respEventDtoList);
    }

    @RequestMapping(value = "/attention/threshold/{threshold}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询关注嫌疑人（我的任务）的实时报警情况")
    public JsonObject findEventsByAttention(@PathVariable("page") int page, @PathVariable("threshold") float threshold, @PathVariable("pagesize") int pagesize) {
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByAttention(((UserInfo) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getId(), page, pagesize, 2);
        for (PersonDetail alarmPerson : alarmPersonList) {
            //List<EventInfo> eventList = alarmService.findEventsByPersonId(0,alarmPerson.getId(), threshold, DEFAULT_PAGE_NO, pagesize);
            EventDto eventDto = new EventDto(alarmPerson);
            //eventDto.setEvents(eventList);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
            if(null==crimeFriType) {
              eventDto.setCrimeName("未知");
            } else {
              eventDto.setCrimeName(crimeFriType.getFullName());
            }
            
            respEventDtoList.add(eventDto);
        }
        return new JsonObject(respEventDtoList);
    }

    @RequestMapping(value = "/attention/query/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询关注嫌疑人（我的任务）的实时报警情况")
    public JsonObject findEventsByAttention(@RequestBody @Valid QueryInfoDto queryInfoDto, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {/*
        List<EventDto> respEventDtoList = new ArrayList<EventDto>();
        int type = queryInfoDto.getListType();
        float threshold = queryInfoDto.getThreshold();
        List<PersonDetail> alarmPersonList = alarmService.findAlarmPersonByAttention(((UserInfo) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getId(), page, pagesize, type);
        for (PersonDetail alarmPerson : alarmPersonList) {
            List<EventInfo> eventList = alarmService.findEventsByPersonId(0,alarmPerson.getId(), threshold, DEFAULT_PAGE_NO, pagesize);
            EventDto eventDto = new EventDto(alarmPerson);
            eventDto.setEvents(eventList);
            CrimeFriType crimeFriType = crimeFriTypeRepository.queryBySecId(alarmPerson.getCrimeType());
            if(null==crimeFriType) {
              eventDto.setCrimeName("未知");
            } else {
              eventDto.setCrimeName(crimeFriType.getFullName());
            }
            respEventDtoList.add(eventDto); 
        }
        return new JsonObject(respEventDtoList);
        
    */
        return null;
        }

    @RequestMapping(value = "/query/mark/info", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "存储选中的相关信息")
    public JsonObject createMarkInfo(@RequestBody @Valid MarkDto markDto) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EntityAuditListener.markInfoMap.put(userInfo.getId(), markDto.getInfo());
        if (EntityAuditListener.markInfoMap.containsKey(userInfo.getId())) {
            EntityAuditListener.markInfoMap.put(userInfo.getId(), markDto.getInfo());
        }
        return new JsonObject(EntityAuditListener.markInfoMap);
    }

    @RequestMapping(value = "/query/mark/info", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取存储的信息")
    public JsonObject getMarkInfo() {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = userInfo.getId();
        String info = EntityAuditListener.markInfoMap.get(userId);
        return new JsonObject(info);
    }

    @RequestMapping(value = "/progress/zip/{key}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出指定的报警图片读取进度")
    public JsonObject zipSelectedPhotoPrograss(@PathVariable("key") int key) {
        return new JsonObject(GlobalConsts.downloadMap.get(key));
    }

    @RequestMapping(value = "/zip/selected/result/{key}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回导出指定报警图片的链接")
    public JsonObject createdSelectedPhotoZipLink(@PathVariable("key") int key) {
        if (GlobalConsts.zipMap.get(key) == null)
            return null;
        return new JsonObject(GlobalConsts.zipMap.get(key).getZipFilePath());
    }

    @RequestMapping(value = "/zip/selected", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "导出指定的报警图片，返回key")
    public JsonObject createSelectedPhotoZip(@RequestBody @Valid AlarmFaceZipDto request) {
        List<AlarmFaceZipTuple> alarmFaceZipDtoList = request.getAlarmFaceZipList();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = ((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        int rand = CommonUtil.getRandomNumber(2);
        String randPath = userName + "_" + Calendar.getInstance().getTime().getTime() + "_" + rand;

        Random random = new Random();
        int randkey = random.nextInt(Integer.MAX_VALUE) + 1;
        while (GlobalConsts.zipMap.get(randkey) != null && GlobalConsts.downloadMap.get(randkey) != null) {
            randkey = random.nextInt(Integer.MAX_VALUE) + 1;
        }

        final int key = randkey;
        ProcessInfo processInfo = new ProcessInfo();
        GlobalConsts.downloadMap.put(key, processInfo);

        Runnable task = () -> {
            String[] personids = alarmFaceZipDtoList.stream().map(item -> String.valueOf(item.getPersonId())).toArray(String[]::new);
            List<AlarmPersonDetail> personDetailList = personDetailService.findAlarmPersonDetail(personids);

            ConcurrentHashMap<Long, AlarmPersonDetail> personDetailMap = new ConcurrentHashMap<>();
            personDetailList.forEach(item -> {
                personDetailMap.put(item.getId(), item);
            });
            if (personDetailList == null || personDetailList.isEmpty()) {
                return;
            }

            Map<Long, List<String>> personAreaListMap = personDetailService.findPersonArea(personids);

            String[] headingList = new String[] { "序号", "嫌疑人名称", "性别", "身份证", "住址", "犯罪地点", "说明", "犯罪类型", "所在库", "布控开始时间", "布控结束时间", "布控区域" };
            try {
                String sourcePath = FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/报警导出结果";
                String destPath = FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath + "/";
                long sum = 0;
                for (AlarmFaceZipTuple tuple : alarmFaceZipDtoList) {
                    List<AlarmImageInfo> alarmImageInfoList = alarmService.findImageByAlarmIds(tuple.getAlarmIdList(),userId);
                    sum += alarmImageInfoList.size() * 2;
                }
                GlobalConsts.downloadMap.get(key).setTotalSize(sum);
                alarmFaceZipDtoList.forEach(tuple -> saveAlarmImageToDir(tuple, key, sourcePath, personDetailMap,userId));
                exportExcel(headingList, personDetailList, personAreaListMap, sourcePath);
                compressZip(sourcePath, destPath);
                String httpPath = FileUtil.getZipHttpUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath + "/alarmFaceData.zip";
                GlobalConsts.zipMap.put(key, new ZipPathInfo(httpPath, System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(task).start();
        return new JsonObject(key);
    }

    @RequestMapping(value = "/update/{id}/status/{status}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "更新报警记录人工标记")
    public JsonObject updateStatus(@PathVariable("id") long id, @PathVariable("status") int status) {
        try {
            alarmService.sendJinxinOnConfirm(id);
        } catch (Exception e) {
            LOG.error("推送警信错误：", e);
        }
        return new JsonObject(alarmService.updateStatusById(id, status));
    }
    
    private void saveAlarmImageToDir(AlarmFaceZipTuple item, int key, String path, ConcurrentHashMap<Long, AlarmPersonDetail> personDetailMap,Long userId) {
        List<AlarmImageInfo> alarmImageInfoList = alarmService.findImageByAlarmIds(item.getAlarmIdList(),userId);
        int j = 0;
        for (AlarmImageInfo alarmImageInfo : alarmImageInfoList) {
            Date time = alarmImageInfo.getTime();
            String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);

            AlarmPersonDetail personDetail = personDetailMap.get(item.getPersonId());
            String bigFolderPath = path + "/" + personDetail.getRealName() + "/" + alarmImageInfo.getStationName() + "/" + alarmImageInfo.getCameraName()
                    + "/报警大图/";
            String smallFolderPath = path + "/" + personDetail.getRealName() + "/" + alarmImageInfo.getStationName() + "/" + alarmImageInfo.getCameraName()
                    + "/报警小图/";
            File fileBig = new File(bigFolderPath);
            File fileSmall = new File(smallFolderPath);

            if (fileBig.exists() || fileBig.isDirectory()) {
                bigFolderPath = path + "/" + personDetail.getRealName() + "(" + personDetail.getId() + ")" + "/" + alarmImageInfo.getStationName() + "/"
                        + alarmImageInfo.getCameraName() + "/报警大图/";
                smallFolderPath = path + "/" + personDetail.getRealName() + "(" + personDetail.getId() + ")" + "/" + alarmImageInfo.getStationName() + "/"
                        + alarmImageInfo.getCameraName() + "/报警小图/";
                fileBig = new File(bigFolderPath);
                fileSmall = new File(smallFolderPath);
            }
            synchronized (this) {
                FileUtil.checkFileExist(fileBig);
                FileUtil.checkFileExist(fileSmall);
            }

            String fullBigFileName = bigFolderPath + j + "_" + timeStr + ".jpg";
            String fullSmallFileName = smallFolderPath + j++ + "_" + timeStr + ".jpg";
            boolean bStatus = FileUtil.copyUrl(alarmImageInfo.getUri(), fullBigFileName);
            boolean sStatus = FileUtil.copyUrl(alarmImageInfo.getImageData(), fullSmallFileName);
            if (bStatus) {
                GlobalConsts.downloadMap.get(key).incrementSuccessNumWithLock();
            } else {
                GlobalConsts.downloadMap.get(key).incrementFailedNumWithLock();
                FileUtil.deleteFile(new File(fullBigFileName), true);
            }
            if (sStatus) {
                GlobalConsts.downloadMap.get(key).incrementSuccessNumWithLock();
            } else {
                GlobalConsts.downloadMap.get(key).incrementFailedNumWithLock();
                FileUtil.deleteFile(new File(fullSmallFileName), true);
            }

        }
    }

    private void exportExcel(String[] headingList, List<AlarmPersonDetail> rowList, Map<Long, List<String>> personAreaListMap, String path) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("导出报警图片列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < headingList.length; i++) {
            row.createCell(i).setCellValue(headingList[i]);
            // row.createCell(i).setCellStyle(style);
        }
        String none = "无";
        for (int i = 0; i < rowList.size(); i++) {
            row = sheet.createRow(i + 1);
            AlarmPersonDetail cel = rowList.get(i);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(StringUtil.isEmpty(cel.getRealName()) ? none : cel.getRealName());
            if (cel.getRealGender() == 1) {
                row.createCell(2).setCellValue("男");
            } else if(cel.getRealGender() == 2) {
                row.createCell(2).setCellValue("女");
            } else {
                row.createCell(2).setCellValue("未知");
            }
            row.createCell(3).setCellValue(none);
            row.createCell(4).setCellValue(StringUtil.isEmpty(cel.getAddress()) ? none : cel.getAddress());
            row.createCell(5).setCellValue(StringUtil.isEmpty(cel.getCrimeAddress()) ? none : cel.getCrimeAddress());
            row.createCell(6).setCellValue(StringUtil.isEmpty(cel.getDescription()) ? none : cel.getDescription());
            row.createCell(7).setCellValue("（" + cel.getCrimeName() + "）" + cel.getSubcrimeName());
            row.createCell(8).setCellValue(StringUtil.isEmpty(cel.getBankName()) ? none : cel.getBankName());
            row.createCell(9).setCellValue(StringUtil.isEmpty(cel.getStarttime()) ? none : cel.getStarttime());
            row.createCell(10).setCellValue(StringUtil.isEmpty(cel.getEndtime()) ? none : cel.getEndtime());
            List<String> areas = personAreaListMap.get(cel.getId());
            StringBuilder areasStrBuilder = new StringBuilder();
            if (areas != null && !areas.isEmpty()) {
                areasStrBuilder.append(areas.toString());
            }
            String areasStr = areasStrBuilder.toString();
            row.createCell(11).setCellValue(StringUtil.isEmpty(areasStr) ? none : areasStr);
        }
        for (int i = 0; i < headingList.length; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            String FilePath = path + "/person.xls";
            FileOutputStream fout = new FileOutputStream(FilePath);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compressZip(String sourcePath, String destPath) throws Exception {
        File file = new File(destPath);
        FileUtil.deleteFile(file, true);
        FileUtil.checkFileExist(file);
        FileUtil.zipCompress(sourcePath, destPath + "alarmFaceData.zip");

        File zipFile = new File(destPath + "alarmFaceData.zip");
        if (!zipFile.exists()) {
            throw new Exception("压缩失败");
        }
    }
    
    
    //fk  按区域过滤告警事件流
    //根据反恐平台区域计算库ids 因为反恐人员就是按区域分库存放的  所以按区域查询告警事件就相当于  按库查询          
    public String getFkBankIds(long subId,long localId){
        
         String bankIds = "";
         List<BlackBank> bankList = new ArrayList<BlackBank>();
         String fkBankName = "";
      
            String subName = "";
            String localName = "";
            FkInstitutionCode subInstitution = fkInstitutionCodeDao.findOne(subId);
            FkInstitutionCode localInstitution = fkInstitutionCodeDao.findOne(localId);
            if(subInstitution!=null){
                subName =  subInstitution.getJGMC();
            }
            if(localInstitution!=null){
                localName = localInstitution.getJGMC();
            }       
            
            if(subName.equals("深圳市公安局")||subId==0){
                if(localId==0){
                    //全部区域 相当于不过滤 返回所有反恐库id列表
                    bankList =  blackBankDao.findFkBankList("fk_");
                }else if(localId!=0&&!StringUtils.isEmpty(localName)){
                    //市局 - 所
                    String localCode = localInstitution.getJGDM();
                    String parentCode = localCode.substring(0,localCode.length()-6)+"000000";
                    List<FkInstitutionCode> parentInstitution = fkInstitutionCodeDao.findInstitionByCode(parentCode);                   
                    fkBankName = "fk_" + parentInstitution.get(0).getJGMC() + "_" + localName;
                    bankList = blackBankDao.findByBankName(fkBankName);                   
                }
            }else if(subId!=0&&!StringUtils.isEmpty(subName)){
                if(localId==0){
                    //分局                 
                    bankList = blackBankDao.findFkBankList("fk_"+subName);
                    bankList.addAll(blackBankDao.findFkBankList("fk_深圳市公安局_"+subName));
                    
                }else if(localId!=0&&!StringUtils.isEmpty(localName)){
                    //分局 -所
                    fkBankName = "fk_" + subName + "_" + localName;
                    bankList = blackBankDao.findByBankName(fkBankName);  
                }
            }
                       
            for(int i=0;i<bankList.size();i++){
                if(StringUtils.isEmpty(bankIds)){
                    bankIds = ""+bankList.get(i).getId();
                }else{
                    bankIds = bankIds+","+bankList.get(i).getId();
                }              
            }
           
        return bankIds;
    }

}