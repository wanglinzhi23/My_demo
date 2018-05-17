package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.CidDetailDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.PoliceCloudAuditLogDao;
import intellif.dao.RoleDao;
import intellif.dao.SearchReasonDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.dto.SearchReasonDto;
import intellif.service.FaceServiceItf;
import intellif.database.entity.SearchReason;
import intellif.database.entity.UserInfo;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;
/**
 * 事由Controller 搜索时需要填写
 * @author shixiaohua
 *
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_SEARCH_REASON)
public class SearchReasonController {
	
	 private static Logger LOG = LogManager.getLogger(BlackBankController.class);
    @Autowired
    private SearchReasonDao reasonDao;

    // Autowire an object of type AuditLogRepository
    @Autowired
    private AuditLogDao auditLogRepository;
    
    @Autowired
    private PoliceCloudAuditLogDao policeCloudAuditLogDao;
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
	private FaceServiceItf faceService;
    @Autowired
    private CidDetailDao cidDetailRepository;
    @Autowired
    private JuZhuDetailDao juzhuDetailRepository;
    @Autowired
    private OtherDetailDao otherDetailRepository;
    

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if searchReason info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid SearchReason searchReason) {
    	String name = searchReason.getrName();
    	if(null == name || "".equals(name)){
    		return new JsonObject("事由名称不能为空!",1001);
    	}
    	List<SearchReason> srList = reasonDao.findByRName(name.trim());
    	if(null != srList && !srList.isEmpty()){
    		return new JsonObject("事由名称已存在!",1001);
    	}
        return new JsonObject(reasonDao.save(searchReason));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if searchReason info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.reasonDao.findOne(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if searchReason info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid SearchReason reason) {
    	reason.setId(id);

    	SearchReason oSReason = reasonDao.findOne(id);
    	if(reason.getrName().equals(oSReason.getrName())){
    		return new JsonObject(this.reasonDao.save(reason));
    	}
    	List<SearchReason> srList = reasonDao.findByRName(reason.getrName().trim());
    	if(null != srList && !srList.isEmpty()){
    		return new JsonObject("事由名称已存在!",1001);
    	}

        return new JsonObject(this.reasonDao.save(reason));

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if searchReason info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this.reasonDao.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }
  
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有事由")
    public JsonObject list() {
        return new JsonObject(this.reasonDao.findAll());
    }
    
    @RequestMapping(value = "/record", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if search reason detail if or not save for every search.")
    public JsonObject addSearchReason(@RequestBody @Valid SearchReasonDto reason) {
    	UserInfo ui = null;
    	try{
    		 ui = (UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		GlobalConsts.searchReasonMap.put(ui.getId(), reason);
    		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    		
    	}catch(Exception e){

    		if(ui!=null){                                        
    			LOG.error("add search reason error, userId:"+ui.getId()+" error:",e);	
    		}else{
    			LOG.error("add search reason error, userinfo is null error:",e);    
    		}
    		//LOG.error("add search reason error, userId:"+ui.getId()+" error:",e);  //find bugs possible null pointer dereference in method on exception path 
    		return new JsonObject("添加事由失败！", 1001);	
    	}
    }
}

