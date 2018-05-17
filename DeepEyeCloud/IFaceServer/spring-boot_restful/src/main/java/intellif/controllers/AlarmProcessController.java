/**
 *
 */
package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.LossPreDao;
import intellif.database.dao.AlarmProcessDao;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.AlarmProcess;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.LossPrePerson;
import intellif.database.entity.TaskInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.AlarmProcessDetail;
import intellif.dto.AlarmProcessDto;
import intellif.dto.EventsByStationIdKey;
import intellif.dto.JsonObject;
import intellif.dto.QueryInfoDto;
import intellif.exception.MsgException;
import intellif.service.AlarmProcessServiceItf;
import intellif.service.AlarmServiceItf;
import intellif.service.TaskServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.validate.AnnotationValidator;
import intellif.validate.ValidateResult;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping(GlobalConsts.R_ID_ALARM_PROCESS)
public class AlarmProcessController {

    private static Logger LOG = LogManager.getLogger(AlarmProcessController.class);
  
    @Autowired
    private AlarmProcessServiceItf<AlarmProcess> alarmProcessService;
    @Autowired
    private AlarmServiceItf<AlarmInfo> alarmService;
    @Autowired
    private TaskServiceItf<TaskInfo> taskService;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;

    /**
     * Posting anew alarmInfo
     *
     * @param alarmInfo
     * @return Response a string describing if the alarm info is successfully created or not.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the alarm info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid AlarmProcessDto alarmProcessDto) {
    	UserInfo ui = CurUserInfoUtil.getUserInfo();
    	alarmProcessDto.setUserId(ui.getId());
    	try{
    	    
    	    AlarmInfo ai = alarmService.findById(Long.parseLong(alarmProcessDto.getAlarmId()));
    	    if(ai == null){
    	        return new JsonObject("该报警不存在或已删除！", RequestConsts.response_dataresult_error); 
    	    }else if(ai.getStatus() != GlobalConsts.ALARM_NOPROCESS){
    	        return new JsonObject("该报警已经处理过!", RequestConsts.response_dataresult_error);  
    	    }
            long taskid = ai.getTaskId();
            TaskInfo taskinfo = taskService.findById(taskid);
            long cameraid = taskinfo.getSourceId();
            zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
    	    
    	    String filterSql = "alarm_id = "+ alarmProcessDto.getAlarmId();
    	    List<AlarmProcess> pList = alarmProcessService.findByFilter(filterSql);
    	    if(!CollectionUtils.isEmpty(pList)){
    	        return new JsonObject("该报警已经处理过!", RequestConsts.response_dataresult_error);
    	    }
    	    AlarmProcess ap = new AlarmProcess(alarmProcessDto);
            ai.setStatus(ap.getType());
            alarmService.save(ai);
            alarmProcessService.save(ap);
            return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}catch(MsgException e){
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch(Exception e ){
    	    LOG.error("process alarm error,alarmId:"+alarmProcessDto.getAlarmId()+",e:",e);
    	    return new JsonObject("系统错误!", RequestConsts.response_system_error);  
    	}
    }
 
    @RequestMapping(value = "/query", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "条件查询报警记录.")
    public JsonObject listProcessed(@RequestBody @Valid QueryInfoDto queryInfoDto) {
        try{
            List<AlarmProcessDetail>  detailList = alarmProcessService.findProcessedAlarmByParams(queryInfoDto);
            return new JsonObject(detailList);
        }catch(Exception e){
            LOG.error("get processed alarm error,e:",e);
            return new JsonObject("系统错误!", RequestConsts.response_system_error);  
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the alarm info is successfully delete or not.")
    public JsonObject delete(@PathVariable("id") long id) {
        try {
           AlarmProcess process = alarmProcessService.findById(id);
            AlarmInfo alarmInfo = alarmService.findById(process.getAlarmId());
            long taskid = alarmInfo.getTaskId();
            TaskInfo taskinfo = taskService.findOne(taskid);
            long cameraid = taskinfo.getSourceId();
            zoneAuthorizeService.checkIds(CameraInfo.class, cameraid);
            alarmProcessService.delete(id);
        }catch (MsgException e) {
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch (Exception e) {
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    
    
}