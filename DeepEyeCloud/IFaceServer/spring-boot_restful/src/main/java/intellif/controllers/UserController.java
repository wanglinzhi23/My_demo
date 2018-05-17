/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellif.controllers;

import intellif.audit.EntityAuditListener;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.dao.PoliceStationDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.database.dao.OauthResourceDao;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OauthResource;
import intellif.database.entity.UserInfo;
import intellif.dto.BatchUpdateSpecialAccountDto;
import intellif.dto.CameraQueryDto;
import intellif.dto.JsonObject;
import intellif.dto.SearchUserDto;
import intellif.dto.UserAccountDto;
import intellif.dto.UserDto;
import intellif.dto.UserRightDto;
import intellif.exception.MsgException;
import intellif.lire.UserOnlineThread;
import intellif.service.PoliceStationCacheItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.UserServiceImpl;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.utils.Pageable;
import intellif.utils.SqlUtil;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class UserController.</h1> The UserController which serves request of
 * the form /user and returns a JSON object representing an instance of
 * UserInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc. (see
 * <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and
 * static data storages), while REST is a very-high-level API style (mostly for
 * webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-09-18
 */
@RestController
// @RequestMapping("/intellif/user")
@RequestMapping(GlobalConsts.R_ID_USER)
public class UserController {


	// ==============
	// PRIVATE FIELDS
	// ==============
	private static Logger LOG = LogManager.getLogger(UserController.class);
	// Autowire an object of type userInfoDao
	@Autowired
	private UserServiceImpl _userService;
	@Autowired
	private RoleDao roleRepository;
	@Autowired
	OauthResourceDao oauthResourceRepository;
	@Autowired
	private ZoneAuthorizeServiceItf zoneAuthorizeService;
	@Autowired
	private PoliceStationDao policeStationDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	PoliceStationCacheItf policeCache;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the userInfo info is successfully created or not.")
	public JsonObject create(@AuthenticationPrincipal @RequestBody @Valid UserInfo userInfo) {
		UserInfo user = null;
		try {
			user = _userService.save(userInfo);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), e.getErrorCode());
		} catch (Exception e) {
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
		return new JsonObject(user);
	}

	@RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of user info that is successfully get or not.")
    public JsonObject list(@AuthenticationPrincipal UserInfo userInfo) {
        String roleName = roleRepository.findOne(userInfo.getRoleId()).getName();
        if (roleName.equals(GlobalConsts.SUPER_ADMIN)) {
            return new JsonObject(this._userService.findAll());
        } else if (roleName.equals(GlobalConsts.ADMIN)) {
              List<String> filterList = new ArrayList<String>();
              filterList.add("police_station_id = "+userInfo.getPoliceStationId());
             return  new JsonObject(this._userService.findUserInfoByFilters(filterList));
        } else {
            return new JsonObject(this._userService.findUserInfoById(userInfo.getId()));
        }
    }
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of user info that is successfully get or not.")
	//推送警信功能中，选择需要推送的目标，需要返回所有user的部分信息
    public JsonObject listForPush() {
	    return new JsonObject(_userService.findFieldByFilter("id, login, NAME, police_station_id , post", null));
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "查询单个用户")
	public JsonObject get(@PathVariable("id") long id) {
	    UserInfo user = null;
		try {
			user = _userService.findById(id);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), e.getErrorCode());
		}catch (Exception e) {
            return new JsonObject(e.getMessage(),RequestConsts.response_system_error);
        }
		if (user == null)
			return new JsonObject("用户不存在", 1001);

		user.setOpened(zoneAuthorizeService.userZoneAuthorizeSwitch(id));
		return new JsonObject(user);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  userInfo info is successfully updated or not.")
	public JsonObject update(@PathVariable("id") long id,
			@RequestBody @Valid UserInfo modifyUserInfo) {
		Validate.notNull(modifyUserInfo, "用户信息不得为空！");
		modifyUserInfo.setId(id);
		UserInfo user = null;
		try {
			user = (UserInfo) _userService.update(modifyUserInfo);
		} catch (Exception e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(user);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the user info is successfully delete or not.")
	public JsonObject delete(@PathVariable("id") long id) {
		try {
		   /*  UserInfo ui = _userService.findUserInfoById(id);
		     if(ui.getSpecialSign() > 0){
		         //回收已分配全区域搜索账号数
		         policeCache.updatePoliceStationTreeValues("specialUseNum", ui.getPoliceStationId(), -1);
		     }*/
			_userService.delete(id);
		} catch (Exception e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}

	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the user is successfully created or not.")
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.OK)
	public JsonObject findByCombinedConditions(@RequestBody @Valid UserDto userDto) {
		LOG.info("POST userDto:" + userDto.toString());
		List<UserDto> respuserDto = _userService.findByCombinedConditions(userDto);
		return new JsonObject(respuserDto);
	}

	@RequestMapping(value = "/change/{password}/newpassword/{newpassword}", method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "修改登陆用户密码")
	public JsonObject modifyPassword(@AuthenticationPrincipal UserInfo userInfo, OAuth2Authentication authentication,
			@PathVariable("password") String password, @PathVariable("newpassword") String newpassword) {
		UserInfo find = this._userService.findUserInfoById(userInfo.getId());
		//EntityAuditListener.UserStatusMap.put(find.getId(), find.clone()); //////// 9.13
		if (find.getPassword().equals(password)) {
			find.setPassword(newpassword);
			this._userService.update(find);
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
		} else {
			return new JsonObject("旧密码输入错误，请重新输入！", 1001);
		}
	}



	@RequestMapping(value = "/display", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "分页显示自己所属单位的账户")
	public JsonObject displaySelfAccout(@AuthenticationPrincipal UserInfo userInfo,
			@RequestBody @Valid UserAccountDto userAccountDto) {
		String roleName = roleRepository.findOne(userInfo.getRoleId()).getName();
		List<UserInfo> listResult = new ArrayList<UserInfo>();
		String name = userAccountDto.getName();
		long id = userAccountDto.getId();
		int page = userAccountDto.getPage();
		int pagesize = userAccountDto.getPageSize();
		Pageable<UserInfo> pageable = null;
	    List<String> fList = new ArrayList<String>();
		 fList.add("police_station_id = "+ id);
		 if(StringUtils.isNotBlank(name)){
		        //(login like %:name% or name like %:name% or post like %:name%) order by created desc
		    fList.add("(login like +'%"+name+"%' or name like '%"+name+"%' or post like '%"+name+"%') order by created desc");
		 }
	    listResult = this._userService.findUserInfoByFilters(fList);
		List<UserInfo> result = new ArrayList<UserInfo>();
		if (!roleName.equals(GlobalConsts.SUPER_ADMIN)) {
			for (UserInfo userInfo2 : listResult) {
				if (userInfo2.getRoleId() < userInfo.getRoleId()) {
					continue;
				}
				result.add(userInfo2);
			}
			pageable = new Pageable<UserInfo>(result);
		} else {
			pageable = new Pageable<UserInfo>(listResult);
		}
		pageable.setPageSize(pagesize);
		pageable.setPage(page);
		return new JsonObject(pageable.getListForPage(), 0, pageable.getMaxPages());
	}
	
	@RequestMapping(value="/query", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "查询单位下的用户")
	public JsonObject queryUsersByStationId(@RequestBody @Valid SearchUserDto searchUserDto) {
	    PageDto<UserInfo> users;
		try {
			users = _userService.queryUsersByStationId(searchUserDto);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(users.getData(), 0, users.getMaxPages(), (int) users.getCount());
	}

	@RequestMapping(value = "/getUsers", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "分页显示自己所属单位的账户")
	public JsonObject getUsers(@RequestBody @Valid UserAccountDto user) {
	    return _userService.getUsers(user);
	}

	@RequestMapping(value = "/right/{name}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a list describing all of user info that is successfully get or not.")
	public JsonObject getUserRight(@PathVariable("name") String name) {
		try {
		    UserRightDto rightDto = _userService.getUserRight(name);			
			return new JsonObject(rightDto);
		} catch (Exception e) {
			LOG.error("get user right error:", e);
			return new JsonObject("获取用户权限信息出错！", 1001);
		}
	}

	// 修改之前的用户登录时限outofdate属性跟着token一起返回给前端 换成直接通过接口
	@RequestMapping(value = "/isOutOfDateorNot", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response the user's login is outofdate or not.")
	public JsonObject isOutOfDateOrNot() {

		UserInfo userInfo = CurUserInfoUtil.getUserInfo();
		String outofdate = UserOnlineThread.userOutOfDateState.get(userInfo.getLogin());
		Date userEndTime = userInfo.getEndTime();
		Date userStartTime = userInfo.getStartTime();
		Date now = new Date();
		if (userEndTime.before(now) || userStartTime.after(now)) {
			outofdate = "true";
			UserOnlineThread.userOutOfDateState.put(userInfo.getLogin(), outofdate);
		} else {
			outofdate = "";
			UserOnlineThread.userOutOfDateState.put(userInfo.getLogin(), outofdate);
		}

		return new JsonObject(outofdate);

	}
	
   /* @RequestMapping(value = "/ctype/batch/change/", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response batch update user response ")
    public JsonObject updateUsersCType(@RequestBody @Valid UserDto user) {
        String[] userIds = user.getUserIds();
        if(null != userIds){

        }
        return new JsonObject(userDao.findSpecialUsersByPoliceStationId(id));

    }

	/**
	 * 解除用户与单位的绑定关系
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/unbinded/{userId}")
	@ApiOperation(httpMethod = "POST", value = "解除用户与单位的绑定")
	public JsonObject unbindUser(@PathVariable("userId") long userId) {
		try {
			_userService.unbindUser(userId);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}

		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}


	@RequestMapping(value = "/unbinded/list")
	@ApiOperation(httpMethod = "POST", value = "查询所有待绑定单位的用户")
	public JsonObject queryUnbindedUsers(@RequestBody SearchUserDto searchUserDto) {
		PageDto<UserInfo> users;
		try {
		    users = _userService.queryUnbindedUsers(searchUserDto);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}

		return new JsonObject(users.getData(), 0, users.getMaxPages(), (int) users.getCount());
	}


	@RequestMapping(value = "/special/{userId}")
	@ApiOperation(httpMethod = "POST", value = "编辑全区域搜索账号属性")
	public JsonObject updateSpecialAccount(@RequestParam Long userId, @RequestParam Integer specialSign) {
		if (specialSign == null)
			return new JsonObject("请指定全区域搜索账号属性", 1001);

		try {
			_userService.setSpecialSign(userId, specialSign);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));
	}


	@RequestMapping(value = "/special/batch")
	@ApiOperation(httpMethod = "POST", value = "批量分配全区域搜索账号")
	public JsonObject batchUpdateSpecialAccount(@RequestBody BatchUpdateSpecialAccountDto updateSpecialAccountDto) {
		if (StringUtils.isBlank(updateSpecialAccountDto.getUserIds()))
			return new JsonObject("请指定要修改的用户", 1001);

		try {
			_userService.batchUpdateSpecialSign(updateSpecialAccountDto.getUserIds(), updateSpecialAccountDto.getSpecialSign());
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));
	}


	/**
	 * 查询账号的联络员
	 * @param userId - 用户id, 如果为空，则查询当前登录用户的联络员
	 * @return
	 */
	@RequestMapping(value = "/special/contact/{userId}")
	@ApiOperation(httpMethod = "GET", value = "查询账号的联络员")
	public JsonObject queryContact(@PathVariable("userId") Long userId) {
	    List<UserInfo> contacts;
		try {
			contacts = _userService.queryContact(userId);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(contacts);
	}

	@RequestMapping(value = "hasAllAreas")
	@ApiOperation(httpMethod = "GET", value = "查询用户是否有全部区域的权限")
	public JsonObject hasAllAreas() {
		boolean hasAllAreas;
		try {
			hasAllAreas = _userService.hasAllAreas();
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(hasAllAreas);
	}

	  @RequestMapping(value = "/ctype/batch/change", method = RequestMethod.POST)
	    @ApiOperation(httpMethod = "POST", value = "Response batch update user response ")
	    public JsonObject updateUsersCType(@RequestBody @Valid UserDto user) {
	        try{
	            String[] userIds = user.getUserIds();
	            String cTypeIds = user.getcTypeIds();
	            if(StringUtils.isNotBlank(cTypeIds) && null != userIds){
	                updateFieldsByIds("c_type_ids",user.getcTypeIds(),userIds);
	            }
	        }catch(Exception e){
	            LOG.error("batch users ctype error:",e);
	            return new JsonObject(e.getMessage(), 1001);
	        }
	        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	        
	    }
	    
	  
	    public void updateFieldsByIds(String fieldName,String fieldValues, String[] idsList) {
	        StringBuffer sb = new StringBuffer();
	        for(String id: idsList){
	            sb.append(",");
	            sb.append(Long.parseLong(id));
	        }
	        String sql = sb.toString().substring(1);
	        String exeSql = "update "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_USER+" set "+fieldName+" = '" + fieldValues + "' where id in("+sql+")";
	        jdbcTemplate.execute(exeSql);
	        
	    }

}
