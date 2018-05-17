/**
 *
 */
package intellif.controllers;

import java.util.List;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.audit.EntityAuditListener;
import intellif.consts.GlobalConsts;
import intellif.dao.RuleInfoDao;
import intellif.database.entity.RuleInfo;
import intellif.dto.JsonObject;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERuleIoctrlType;
import intellif.service.IoContrlServiceItf;
import intellif.database.entity.SearchReason;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

/**
 * <h1>The Class RuleController.</h1>
 * The RuleController which serves request of the form /rule and returns a JSON object representing an instance of RuleInfo.
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
 * @since 2015-09-18
 */
@RestController
//@RequestMapping("/intellif/rule")
@RequestMapping(GlobalConsts.R_ID_RULE)
public class RuleController {

    private static Logger LOG = LogManager.getLogger(RuleController.class);

    // ==============
    // PRIVATE FIELDS
    // ==============

    // Autowire an object of type RuleInfoDao
    @Autowired
    private RuleInfoDao _ruleInfoDao;
    @Autowired
    private RuleInfoDao _ruleDao;

    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the rule info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid RuleInfo ruleInfo) {
    	String name = ruleInfo.getRuleName();
    	if(null == name || "".equals(name)){
    		return new JsonObject("规则名称不能为空!",1001);
    	}
    	List<RuleInfo> rList = _ruleDao.findByRuleName(name.trim());
    	if(null != rList && !rList.isEmpty()){
    		return new JsonObject("规则名称已存在!",1001);
    	}
    	return new JsonObject(_ruleInfoDao.save(ruleInfo));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of rule info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._ruleInfoDao.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the rule info id is successfully get or not.")
    public RuleInfo get(@PathVariable("id") long id) {
        return this._ruleInfoDao.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  rule info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid RuleInfo ruleInfo) throws Exception {
//		RuleInfo find = this._ruleInfoDao.findOne(id);
    	RuleInfo find =_ruleDao.findOne(id); 
    	if(!ruleInfo.getRuleName().equals(find.getRuleName())){
    		List<RuleInfo> rList = _ruleDao.findByRuleName(ruleInfo.getRuleName());
    		if(null != rList && !rList.isEmpty()){
    			return new JsonObject("规则名称已存在!",1001);
    		}
    	}
    		EntityAuditListener.RuleInfoStatusMap.put(id, find.clone());
    		ruleInfo.setId(id);
    		RuleInfo ruleInfoSaved = this._ruleInfoDao.save(ruleInfo);
    		//
    		ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_VERIFYRULE.getValue(), ERuleIoctrlType.RULE_IOCTRL_UPDATE.getValue(), id, 0);
    		//
    		return new JsonObject(ruleInfoSaved);
    	
        
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the rule info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this._ruleInfoDao.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

}
