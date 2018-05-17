package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.dao.PoliceManAuthorityDao;
import intellif.dao.PoliceManAuthorityTypeDao;
import intellif.dao.PoliceManDao;
import intellif.dto.JsonObject;
import intellif.dto.PoliceManAuthSwitchDto;
import intellif.service.impl.PoliceManAuthorityServiceImpl;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.PoliceManAuthority;
import intellif.database.entity.PoliceManAuthorityType;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_POLICE_MAN_AUTHORITY)
public class PoliceManAuthorityController {

	@Autowired
	private PoliceManDao _policemanDao;
	@Autowired
	private PoliceManAuthorityDao _policemanAuthorityDao;
	@Autowired
	private PoliceManAuthorityTypeDao _policemanAuthorityTypeDao;
	@Autowired
	private PoliceManAuthorityServiceImpl _policemanAuthorityService;

	// 返回给前端有权限的警号序列
	@RequestMapping(value = "/typename", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a list describing all of authority name.")
	public JsonObject getAuthorityTypeName() {

		List<PoliceManAuthorityType> authorityName = (List<PoliceManAuthorityType>) this._policemanAuthorityTypeDao
				.findTypeName();
		return new JsonObject(authorityName);

	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the policeman's authority is successfully created or not.")
	public JsonObject create(
			@RequestBody @Valid PoliceManAuthority policemanAuthority) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN")) {
			return new JsonObject(
					_policemanAuthorityDao.save(policemanAuthority));
		} else {

			return new JsonObject("对不起，您没有给警员增加权限的权限！", 1001);
		}

	}

	// 返回给前端有权限的警号序列
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a list describing all of policeman's authority that is successfully get or not.")
	public JsonObject list() {

		List<PoliceManAuthority> authoritylist = (List<PoliceManAuthority>) this._policemanAuthorityDao
				.findAll();
		// 因为权限还没有分类 所以先按约定返回给前端所有有权限的警号序列
		List<String> resultList = new ArrayList<String>();
		for (PoliceManAuthority key : authoritylist) {
			resultList.add(key.getPoliceNo() + "-" + key.getType()); //
		}

		return new JsonObject(resultList);

	}

	// 这是根据t_police_man_authority表中的id进行的删除 不是根据警号
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the policeman's authority is successfully delete or not.")
	public JsonObject delete(@PathVariable("id") long id) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN")) {
			PoliceManAuthority policemanAuthority = _policemanAuthorityDao
					.findOne(id);
			this._policemanAuthorityDao.delete(id);
		} else {

			return new JsonObject("对不起，您没有删除警员的权限！", 1001);
		}

		return new JsonObject(Boolean.TRUE);
	}

	// 这是根据警号 对权限进行删除
	@RequestMapping(value = "/policeno/{policeno}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the policeman's authority is successfully delete or not.")
	public JsonObject deleteByPoliceNo(@PathVariable("policeno") String policeno) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN")) {
			_policemanAuthorityDao.deleteByPoliceNo(policeno);
		} else {

			return new JsonObject("对不起，您没有删除警员的权限！", 1001);
		}
		return new JsonObject(Boolean.TRUE);

	}

	// 这是根据警号和权限typeid进行指定删除
	@RequestMapping(value = "/policeno/{policeno}/type/{type}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the policeman's authority is successfully delete or not.")
	public JsonObject deleteByPoliceNoAndType(
			@PathVariable("policeno") String policeno,
			@PathVariable("type") int type) {

		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN")) {
			_policemanAuthorityDao.deleteByPoliceNoAndType(policeno, type);
		} else {

			return new JsonObject("对不起，您没有删除警员的权限！", 1001);
		}
		return new JsonObject(Boolean.TRUE);

	}

	/*
	 * // 批量控制警员权限开关 map里面为警号 和对应的要改变权限typeid switch为1表示删除 0表示添加
	 * 
	 * @RequestMapping(value = "/{switchOnOrOff}", method = RequestMethod.POST)
	 * 
	 * @ApiOperation(httpMethod = "POST", value =
	 * "Response a string describing if the policeman's authority is successfully delete or not."
	 * ) public JsonObject batchAddAuth(@PathVariable("switchOnOrOff") int
	 * switchOnOrOff,@RequestBody @Valid Map<String,ArrayList<Integer>>
	 * authTypeMap) {
	 * 
	 * Map<String,ArrayList<Integer>> authMap = authTypeMap;
	 * 
	 * if(switchOnOrOff==0){
	 * 
	 * for (Map.Entry<String, ArrayList<Integer>> entry : authMap.entrySet()) {
	 * 
	 * for(int i=0;i<entry.getValue().size();i++){ PoliceManAuthority
	 * policemanAuthority = new PoliceManAuthority();
	 * policemanAuthority.setPoliceNo(entry.getKey());
	 * policemanAuthority.setType(entry.getValue().get(i));
	 * _policemanAuthorityDao.save(policemanAuthority); } }
	 * 
	 * }else if(switchOnOrOff==1){
	 * 
	 * for (Map.Entry<String, ArrayList<Integer>> entry : authMap.entrySet()) {
	 * 
	 * for(int i=0;i<entry.getValue().size();i++){ try{
	 * _policemanAuthorityDao.deleteByPoliceNoAndType
	 * (entry.getKey(),entry.getValue().get(i)); }catch(Exception e){
	 * System.err.println("权限开发的批量关闭  前端是不是传了本来就没有权限的数据哇 删除报错了哦"); } } }
	 * 
	 * }else{ return new JsonObject("开关状态只能为0和1。0表示开，1表示关",1001); }
	 * 
	 * return new JsonObject(Boolean.TRUE); }
	 */

	/*
	 * // 批量控制警员权限开关 map里面为警号 和对应的要改变权限typeid switch为1表示删除 0表示添加
	 * 
	 * @RequestMapping(value =
	 * "/switchOnOrOff/{switchOnOrOff}/authType/{authType}/policeNoList/{}",
	 * method = RequestMethod.POST)
	 * 
	 * @ApiOperation(httpMethod = "POST", value =
	 * "Response a string describing if the policeman's authority is successfully delete or not."
	 * ) public JsonObject batchAddAuth(@PathVariable("switchOnOrOff") int
	 * switchOnOrOff,@RequestBody @Valid Map<Integer,ArrayList<String>>
	 * authTypeMap) {
	 * 
	 * Map<Integer,ArrayList<String>> authMap = authTypeMap;
	 * 
	 * if(switchOnOrOff==0){
	 * 
	 * for (Map.Entry<Integer,ArrayList<String>> entry : authMap.entrySet()) {
	 * 
	 * //先校验 准备添加的权限type为key的警号 数据库是不是真的都不存在 List alreadyExistPoliceno =
	 * _policemanAuthorityDao.findByType(entry.getKey());
	 * //已有该权限的警员列表和前端传来加权限的警员列表交集
	 * alreadyExistPoliceno.retainAll(entry.getValue());
	 * if(alreadyExistPoliceno.size()!=0){ //则在需要开启权限的警员列表中去掉这些已存在的 求差集
	 * entry.getValue().removeAll(alreadyExistPoliceno);
	 * System.out.println("权限开关去重啦~"+alreadyExistPoliceno.size());
	 * 
	 * }
	 * 
	 * for(int i=0;i<entry.getValue().size();i++){ PoliceManAuthority
	 * policemanAuthority = new PoliceManAuthority();
	 * policemanAuthority.setPoliceNo(entry.getValue().get(i));
	 * policemanAuthority.setType(entry.getKey());
	 * _policemanAuthorityDao.save(policemanAuthority); }
	 * 
	 * }
	 * 
	 * }else if(switchOnOrOff==1){
	 * 
	 * for (Map.Entry<Integer,ArrayList<String>> entry : authMap.entrySet()) {
	 * 
	 * String policeNoList = StringUtils.join(entry.getValue().toArray(),",");
	 * _policemanAuthorityDao.batchDelete(entry.getKey(),policeNoList);
	 * 
	 * }
	 * 
	 * }else{ return new JsonObject("开关状态只能为0和1。0表示开，1表示关",1001); }
	 * 
	 * return new JsonObject(Boolean.TRUE); }
	 */

	// 批量控制警员权限开关 map里面为警号 和对应的要改变权限typeid switch为1表示添加  0表示删除
	@RequestMapping(value = "/switchOnOrOff", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the policeman's authority is successfully delete or not.")
	public JsonObject batchAddAuth(
			@RequestBody @Valid PoliceManAuthSwitchDto policeManAuthSwitchDto) {

		int onOrOff = policeManAuthSwitchDto.getSwitchOnOrOff();
		int switchType = policeManAuthSwitchDto.getAuthType();
		List<String> policeNoList = policeManAuthSwitchDto.getPoliceNoList();
		List<Integer> allSwitchTypes = _policemanAuthorityTypeDao.findAllType();
		List<String> allPoliceNoLists = _policemanDao.findAllPoliceno();
		if (!allSwitchTypes.contains(switchType) && switchType != 0) {
			return new JsonObject("type值对应的权限不存在 ", 1001);
		}
		List<String> policeNoCheck = new ArrayList<String>();
		policeNoCheck.addAll(policeNoList);
		policeNoCheck.removeAll(allPoliceNoLists); // 求交集
		if (policeNoCheck.size() != 0) {
			return new JsonObject("参数中存在无效警号，请检查 ", 1001);
		}

		if (onOrOff == 1) {
			if (switchType != 0) {
				List<String> alreadyExistPoliceno = _policemanAuthorityDao
						.findByType(switchType);
				alreadyExistPoliceno.retainAll(policeNoList);
				if (alreadyExistPoliceno.size() != 0) {
					policeNoList.removeAll(alreadyExistPoliceno);
					System.out
							.println("权限开关去重啦~" + alreadyExistPoliceno.size());
				}
				for (int i = 0; i < policeNoList.size(); i++) {
					PoliceManAuthority policemanAuthority = new PoliceManAuthority();
					policemanAuthority.setPoliceNo(policeNoList.get(i));
					policemanAuthority.setType(switchType);
					_policemanAuthorityDao.save(policemanAuthority);
				}
			} else {
				for (int j = 0; j < allSwitchTypes.size(); j++) {
					List<String> policeCopyList = new ArrayList<String>();
					policeCopyList.addAll(policeNoList);				
					List<String> alreadyExistPoliceno = _policemanAuthorityDao
							.findByType(allSwitchTypes.get(j));
					System.err.println(j+".."+allSwitchTypes.get(j)+",..."+alreadyExistPoliceno.size());
					alreadyExistPoliceno.retainAll(policeNoList);
					System.err.println(alreadyExistPoliceno.size());
					if (alreadyExistPoliceno.size() != 0) {
						policeCopyList.removeAll(alreadyExistPoliceno);
						System.out.println("权限开关去重啦~"
								+ alreadyExistPoliceno.size());
					}
					for (int i = 0; i < policeCopyList.size(); i++) {
						PoliceManAuthority policemanAuthority = new PoliceManAuthority();
						policemanAuthority.setPoliceNo(policeCopyList.get(i));
						policemanAuthority.setType(allSwitchTypes.get(j));
						_policemanAuthorityDao.save(policemanAuthority);
					}
				}
			}
		} else if (onOrOff == 0) {
			List<String> policeNoListAsString = new ArrayList<String>();
			for(int i=0;i< policeManAuthSwitchDto.getPoliceNoList().size();i++){
				String policeNo = "'"+policeManAuthSwitchDto.getPoliceNoList().get(i)+"'";
				policeNoListAsString.add(policeNo);
			}
			if (switchType != 0) {
				String policeNoLine = "( "
						+ StringUtils.join(policeNoListAsString.toArray(), ",") + " )";
				System.out.println("policeNoLine:" + policeNoLine);
				_policemanAuthorityService
						.batchDelete(switchType, policeNoLine);
			} else {
				for (int j = 0; j < allSwitchTypes.size(); j++) {
					String policeNoLine = "( "
							+ StringUtils.join(policeNoListAsString.toArray(), ",")
							+ " )";
					System.out.println("policeNoLine:" + policeNoLine);
					_policemanAuthorityService.batchDelete(allSwitchTypes.get(j),
							policeNoLine);
				}

			}

		} else {
			return new JsonObject("开关状态只能为0和1。0表示关，1表示开", 1001);
		}
		return new JsonObject(Boolean.TRUE);
	}

}
