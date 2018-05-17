package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.dao.PoliceManAuthorityDao;
import intellif.dao.PoliceManAuthorityTypeDao;
import intellif.dao.PoliceManDao;
import intellif.dao.PushAlarmInfoDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.dto.PoliceManDto;
import intellif.dto.PushAlarmInfoDto;
import intellif.dto.ShowAlarmInfoDto;
import intellif.service.PoliceManServiceItf;
import intellif.service.impl.PoliceManAuthorityServiceImpl;
import intellif.service.impl.PoliceManServiceImpl;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.HttpUtil;
import intellif.database.entity.PoliceMan;
import intellif.database.entity.PoliceManAuthority;
import intellif.database.entity.PushAlarmInfo;
import intellif.database.entity.UserInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_POLICE_MAN)
public class PoliceManController {

	private static Logger LOG = LogManager.getLogger(PoliceManController.class);
	public static String uri = "";
	public static String param = "";
	public static String successtarget = "";

	@Autowired
	private PoliceManDao _policemanDao;
	@Autowired
	private PushAlarmInfoDao _pushalarminfoDao;
	@Autowired
	private UserDao userRepository;
	@Autowired
	private RoleDao roleRepository;
	@Autowired
	private PoliceManServiceItf _policemanService;
	@Autowired
	private PoliceManAuthorityDao _policemanAuthorityDao;
	@Autowired
	private PoliceManAuthorityServiceImpl _policemanAuthorityService;
	@Autowired
	private PoliceManAuthorityTypeDao _policemanAuthorityTyprDao;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the policeman is successfully created or not.")
	public JsonObject create(@RequestBody @Valid PoliceManDto policeManDto) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		PoliceMan p = new PoliceMan();
		PoliceMan pm = null;
		if (roleName.equals("SUPER_ADMIN")) {

			p.setId(policeManDto.getId());
			p.setName(policeManDto.getName());
			p.setPhone(policeManDto.getPhone());
			p.setPoliceNo(policeManDto.getPoliceNo());
			p.setSex(policeManDto.getSex());
			p.setStationId(policeManDto.getStationId());

			// 警号不能重复
			if (_policemanDao.findByPoliceNo(policeManDto.getPoliceNo()).size() != 0) {

				return new JsonObject("对不起，该警号警员已经存在，不能重复添加！", 1001);
			}

			pm = _policemanDao.save(p);
			if (pm == null) {
				return new JsonObject("对不起，数据库添加失败！", 1002);
			}

			List<Integer> authStatusList = policeManDto.getAuthStatusList();
			for (int i = 0; i < authStatusList.size(); i++) {
				int status = authStatusList.get(i);
				if (status == 1) {
					_policemanAuthorityService.addIfNotExsit(
							policeManDto.getPoliceNo(), i+1);
				}
			}

		} else {

			return new JsonObject("对不起，您没有添加警员或者给警员增加权限的权限！", 1001);
		}

		return new JsonObject(pm);

	}

	@ApiOperation(httpMethod = "POST", value = "Response a page list describing all of policeman info that is successfully get or not.")
	@RequestMapping(value = "/search/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.OK)
	public JsonObject listByPage(@RequestBody @Valid PoliceManDto policeManDto,
			@PathVariable("page") int page,
			@PathVariable("pagesize") int pageSize) {

		// 把权限开关也顺带返回
		List<PoliceManDto> policelist = _policemanService.findPoliceMan(
				policeManDto, page, pageSize);
		for (int i = 0; i < policelist.size(); i++) {
			List<Integer> authList = _policemanAuthorityDao
					.findAuthTypeByPoliceNo(policelist.get(i).getPoliceNo());
			int typeSize = _policemanAuthorityTyprDao.findTypeCounts()
					.intValue();
			List<Integer> authStatusList = new ArrayList<Integer>();
			if (authList != null && authList.size() != 0) {
				for (int j = 0; j < typeSize; j++) {
					if (authList.contains(j+1)) {
						authStatusList.add(1);
					} else {
						authStatusList.add(0);
					}
				}

			}
			policelist.get(i).setAuthStatusList(authStatusList);
		}
		int maxPage = (PoliceManServiceImpl.policeManTotalNum.intValue())
				/ pageSize;
		if ((PoliceManServiceImpl.policeManTotalNum.intValue()) % pageSize != 0) {
			maxPage++;
		}

		return new JsonObject(policelist, 0, maxPage);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  policeman info is successfully updated or not.")
	public JsonObject update(@PathVariable("id") long id,
			@RequestBody @Valid PoliceManDto policeManDto) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		PoliceMan p = new PoliceMan();
		PoliceMan pm = null;

		if (roleName.equals("SUPER_ADMIN")) {

			// 新的警员信息
			p.setId(policeManDto.getId());
			p.setName(policeManDto.getName());
			p.setPhone(policeManDto.getPhone());
			p.setPoliceNo(policeManDto.getPoliceNo());
			p.setSex(policeManDto.getSex());
			p.setStationId(policeManDto.getStationId());

			p.setId(id);

			// 老的警员信息
			PoliceMan oldP = _policemanDao.findOne(id);
			String oldPoliceNo = oldP.getPoliceNo();

			if (!oldPoliceNo.equals(policeManDto.getPoliceNo())) {

				// 如果警员号也修改了的话 那原来那个警员号的相关权限记录也得删掉
				_policemanAuthorityDao.deleteByPoliceNo(oldPoliceNo);

				// 修改时 警号不能和 其他已存在的警号 重复
				if (_policemanDao.findByPoliceNo(policeManDto.getPoliceNo())
						.size() != 0) {

					return new JsonObject("对不起，修改时 警号不能和 其他已存在的警号 重复！", 1001);
				}
			}
           
			List<Integer> authStatusList = policeManDto.getAuthStatusList();
			for(int i=0;i<authStatusList.size();i++){
				int status = authStatusList.get(i);
				if (status == 1) {
					_policemanAuthorityService.addIfNotExsit(
							policeManDto.getPoliceNo(), i+1);
				}else if(status==0){
					_policemanAuthorityService.deleteIfExsit(
							policeManDto.getPoliceNo(), i+1);
				}
			}
			pm = this._policemanDao.save(p);

		} else {

			return new JsonObject("对不起，您没有添加警员或者给警员增加权限的权限！", 1001);
		}

		return new JsonObject(pm);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the policeman info is successfully delete or not.")
	public JsonObject delete(@PathVariable("id") long id) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN")) {
			PoliceMan policeman = this._policemanDao.findOne(id);
			this._policemanDao.delete(id);
			_policemanAuthorityDao.deleteByPoliceNo(policeman.getPoliceNo());
		} else {

			return new JsonObject("对不起，您没有删除警员的权限！", 1001);
		}
		return new JsonObject(Boolean.TRUE);

	}

	// 向警号推送消息
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the alarm info is successfully pushed or not.")
	@RequestMapping(value = "/push", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	public JsonObject pushAlarmInfo(
			@RequestBody @Valid PushAlarmInfoDto pushalarminfodto) {

		UserInfo userinfo = CurUserInfoUtil.getUserInfo();

		String recieverNos = pushalarminfodto.getReceiverNos();
		long alarmId = pushalarminfodto.getAlarmId();
		String recieverName = pushalarminfodto.getReceiverName();

		String[] policeNoList = recieverNos.split(",");
		String exception = "";
		for (int i = 0; i < policeNoList.length; i++) {
			List<PoliceMan> result = _policemanDao
					.findByPoliceNo(policeNoList[i]);
			if (result.size() == 0) {
				exception = exception + "警号为" + policeNoList[i] + "的警员不存在";
				return new JsonObject(exception, 1003); // 只要选中任意一个不存在的警员 就返回
														// 要求重新刷新列表 重新选中警员推送
			}


		}


		


		// String uri =
		// "url= http://10.42.0.235:9000/services/gaw/message/pushapp?Serverld=SJZY&";
		// String param =
		// "userinfold="+recieverNos+"&userinfoName="+recieverName+"&title=告警推送&content="+alarmid+"&url=modules/sjzy/detail.html&taskid=1";

		/*
		 * uri = "https://www.baidu.com/s"; /////////////////////////////////测试
		 * param = "ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=java";
		 * /////////////////////////测试
		 */
		String alarmParam = param;
		alarmParam = alarmParam.replace("@recieverNos", recieverNos);
		alarmParam = alarmParam.replace("@recieverName", recieverName);
		alarmParam = alarmParam.replace("@alarmid", alarmId + "");

		String result = HttpUtil.sendPost(uri, alarmParam);
		System.out.println("告警推送中 post发送的uri是：" + uri + "param是：" + alarmParam
				+ "返回的结果是result：" + result);

		LOG.info("Autowired PoliceManController.pushAlarmInfo:" + result);

		// if(result!=null||result!=""){ ///////////////////////////////测试
		if (result.equals("success")) {
			// 每推送成功一条告警信息 得存进t_push_alarm_info表中记录
			String recieverno[] = recieverNos.split(",");
			for (int i = 0; i < recieverno.length; i++) {

				PushAlarmInfo pai = new PushAlarmInfo();
				// pai.setId(i);
				pai.setAlarmId(alarmId);
				pai.setChecked(0);
				pai.setSendUserId(userinfo.getId());
				pai.setReceiverNo(recieverno[i]);
				pai.setTime(new Date());
				_pushalarminfoDao.save(pai);

			}
		}

		return new JsonObject(result);

	}

	// 获取 用户未读的 告警消息列表
	@RequestMapping(value = "/push/read/{id}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a string describing if the user's alarm info successfully get or not.")
	public JsonObject get(@PathVariable("id") String id,
			@PathVariable("pagesize") int pagesize) {

		/*
		 * Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); Long userid=
		 * Long
		 * .parseLong(((authentication.getPrincipal().toString().split(","))[
		 * 0].split("="))[1]);
		 */

		ArrayList<ShowAlarmInfoDto> auditDto = (ArrayList<ShowAlarmInfoDto>) _policemanService
				.findByUserId(id, pagesize);

		_pushalarminfoDao.updatechecked(id, pagesize);

		return new JsonObject(auditDto);
	}

	// 获取 用户未读的 告警消息 总数
	@RequestMapping(value = "/push/read/count/id/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a string describing if the amount of alarms successfully get or not.")
	public JsonObject getCount(@PathVariable("id") String id) {

		BigInteger count = null;

		count = _policemanService.findCountByUserId(id);

		return new JsonObject(count);
	}

}
