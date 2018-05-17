/**
 *
 */
package intellif.controllers;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.WeixinUserDao;
import intellif.dto.JsonObject;
import intellif.database.entity.WeixinUser;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class ImageController.</h1>
 * The ImageController which serves request of the form /image and returns a JSON object representing an instance of ImageInfo.
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
 * @author shixiaohua
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/image")
@RequestMapping(GlobalConsts.R_ID_WEIXIN_USER)
public class WeixinUserController {

	 private static Logger LOG = LogManager.getLogger(WeixinUserController.class);
    @Autowired
    private WeixinUserDao _weixinDao;
   

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the weixin user info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid WeixinUser user) {
    	List<WeixinUser> wUserList = this._weixinDao.findByUserName(user.getUserName());
    	List<WeixinUser> wUserList1 = this._weixinDao.findByOpenId(user.getOpenId());
    	if(null != wUserList && !wUserList.isEmpty()){
    		 return new JsonObject("该名称已经被注册！",1001);
    	}else if(null != wUserList1 && !wUserList1.isEmpty()){
    		 return new JsonObject("你已注册过！",1002);
    	}
    	else{
    		return new JsonObject(_weixinDao.save(user));
    	}
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of weixin user  info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._weixinDao.findAll());
    }

   
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the weixin user  info id is successfully get or not.")
    public JsonObject get(@PathVariable("name") String name) {
        return new JsonObject(this._weixinDao.findByUserName(name));
    }
    
  
    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the weixin user  info id is successfully delete or not.")
	public JsonObject delete(@PathVariable("name") String name) {
	
			List<WeixinUser> wUserList = this._weixinDao.findByUserName(name);
			if(null != wUserList && !wUserList.isEmpty()){
				for(WeixinUser item : wUserList){
					_weixinDao.delete(item.getId());
				}
	    	}else{
	    		return new JsonObject("不存在该微信用户！",1001);
	    	}
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
					HttpStatus.OK));
	}
    
    @RequestMapping(value = "/openid/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the weixin user  info id is successfully delete or not.")
	public JsonObject deleteByOpenId(@PathVariable("id") String id) {
			List<WeixinUser> wUserList = this._weixinDao.findByOpenId(id);
			if(null != wUserList && !wUserList.isEmpty()){
				for(WeixinUser item : wUserList){
					_weixinDao.delete(item.getId());
				}
	    	}else{
	    		return new JsonObject("不存在该微信用户！",1001);
	    	}
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
					HttpStatus.OK));
	}
}
