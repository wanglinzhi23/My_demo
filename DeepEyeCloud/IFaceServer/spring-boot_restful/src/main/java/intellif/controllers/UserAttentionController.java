package intellif.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.PersonDetailDao;
import intellif.dao.UserAttentionDao;
import intellif.dto.JsonObject;
import intellif.database.entity.UserAttention;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.UserInfo;

@RestController
@RequestMapping(GlobalConsts.R_ID_USER_ATTENTION)
public class UserAttentionController {

    // ==============
    // PRIVATE FIELDS
    // ==============

    private static Logger LOG = LogManager.getLogger(UserAttentionController.class);

    @Autowired
    private UserAttentionDao userAttentionRepository;
    @Autowired
    private PersonDetailDao _personDetailDao;
//    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
//    @ApiOperation(httpMethod = "POST", value = "关注嫌疑人")
//    public JsonObject create(@RequestBody @Valid UserAttention userAttention) throws TException {
//    	userAttention.setUserId(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
//    	UserAttention userAttentionResp = userAttentionRepository.save(userAttention);
//        return new JsonObject(userAttentionResp);
//    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取用户关注关系")
    public JsonObject list() {
        return new JsonObject(this.userAttentionRepository.findByUserId(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()));
    }

    @RequestMapping(value = "/person/{ids}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "关注嫌疑人")
    public JsonObject get(@PathVariable("ids") String ids) {
    	long userId = ((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    	String num = "";
    	String failNum = "";
    	for(String id : ids.split(",")) {
    		PersonDetail pd = _personDetailDao.findOne(Long.parseLong(id));
    		if(pd != null && pd.getIsUrgent() == 1){
    			failNum+=","+id;
    			continue;
    		}
    		List<UserAttention> list = userAttentionRepository.findByUserIdAndPersonId(userId, Long.valueOf(id));
    		if(list.size()>0) {
    			num+=","+id;
    			continue;
    		}
    		try {
    			UserAttention userAttention = new UserAttention();
    			userAttention.setUserId(userId);
    			userAttention.setPersonId(Long.valueOf(id));
    			LOG.info(" insert attention,person Id:"+id);
    			userAttentionRepository.save(userAttention);
    			num+=","+id;
    		} catch (Exception e) {
				// TODO: handle exception
			}
    	}
    	if(num.trim().length() > 0){
    		num = num.substring(1);
    	}
        if(failNum.trim().length() > 0){
        	failNum = failNum.substring(1);
        }
        return new JsonObject("success:"+num+";urgentFail:"+failNum);
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  camera info is successfully updated or not.")
//    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid CameraInfo cameraInfo) throws TException {
//        cameraInfo.setId(id);
//        CameraInfo cameraInfoSaved = _cameraInfoDao.save(cameraInfo);
//        return new JsonObject(cameraInfoSaved);
//    }

    @RequestMapping(value = "/person/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "取消关注嫌疑人")
    public JsonObject delete(@PathVariable("id") long id) throws Exception {
    	long userId = ((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        this.userAttentionRepository.delete(id, userId);
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

//    @RequestMapping(value = "/person", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "获取关注的嫌疑人")
//    public JsonObject getCameraByPersonId(@PathVariable("id") long id) {
//        return new JsonObject(userAttentionRepository.getCameraByPersonId(id));
//    }
}
