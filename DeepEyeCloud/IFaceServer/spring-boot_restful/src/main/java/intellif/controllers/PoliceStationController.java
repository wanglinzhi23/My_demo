package intellif.controllers;

import com.mysql.fabric.xmlrpc.base.Array;
import com.wordnik.swagger.annotations.ApiOperation;

import intellif.audit.EntityAuditListener;
import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.PoliceManDao;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.dto.PoliceStationDto;
import intellif.exception.MsgException;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.PoliceStationCacheItf;
import intellif.service.PoliceStationServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.Pageable;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.UserInfo;
import intellif.database.entity.WeixinAlarmInfo;
import intellif.zoneauthorize.common.ZoneConstant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1>The Class PoliceStationController.</h1>
 * The PoliceStationController which serves request of the form /police/station and returns a JSON object representing an instance of PoliceStation.
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
 * @author <a href="mailto:peng.cheng@intellif.com">PengCheng</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/policestation")
@RequestMapping(GlobalConsts.R_ID_POLICE_STATION)
public class PoliceStationController {

    private static Logger LOG = LogManager.getLogger(PoliceStationController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    @Autowired
    private PoliceStationDao policeStationDao;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
	  @Autowired
    private PoliceStationServiceItf _policeService;
	@Autowired
	private PoliceManDao _policeManDao;
	 @Autowired
    private UserDao userDao; 
	@Autowired
    private PoliceStationAuthorityDao policeStationAuthorityRepository;
    @Autowired
    private PoliceStationCacheItf policeStationCache;
    
	 @Autowired
     private PoliceStationServiceItf policeStationService;
	 @Autowired PoliceStationCacheItf policeCache;
	 
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the police station info is successfully created or not.")
    public JsonObject create(HttpServletResponse response, @RequestBody @Valid PoliceStation policeStation) throws IOException {
        PoliceStation station = null;
        try {
            policeStation.init();
            station = policeStationService.saveStation(policeStation);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(station);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of police station info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this.policeStationDao.findAll());
    }

    @RequestMapping(value = "/tree", method= RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询当前用户有权限的单位列表")
    public JsonObject queryCurrentUserStations() {
        return new JsonObject(this.policeStationService.queryCurrentUserStations());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the police station info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject( this.policeStationDao.findOne(id));
    }
    
    @RequestMapping(value = "/taskid/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the police station info id is successfully get or not.")
    public JsonObject getByTaskId(@PathVariable("id") long id) {
        List<Object[]> pList = policeStationDao.findPoliceByTaskId(id);
    	
    	WeixinAlarmInfo wai = new WeixinAlarmInfo();
        if(pList != null && !pList.isEmpty()){
        	Object[] objArray = pList.get(0);
        	BigInteger bi = (BigInteger) objArray[0];
        	wai.setStationId(bi.longValue());
        	wai.setStationName((String)objArray[1]);
        	wai.setName((String)objArray[2]);
        	return new JsonObject(wai);
        }else{
        	return new JsonObject(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  police station info is successfully updated or not.")
    public JsonObject update(HttpServletResponse response, @PathVariable("id") long id,
                             @RequestBody @Valid PoliceStation policeStation) throws Exception {
    	PoliceStation find = this.policeStationDao.findOne(id);
    	PoliceStation oldStation = find.clone();
        int sTNum = policeStation.getSpecialTotalNum();
        if(sTNum >= 0){
           int offset = sTNum -find.getSpecialTotalNum();
           if(offset < 0){
               if(sTNum < find.getSpecialUseNum()){
                   return new JsonObject("全区域搜索账号分配总数不能小于已使用个数",1001); 
               }
           }
           policeCache.updatePoliceStationTreeValues("specialTotalNum", find.getId(), offset);
        }
        find.update(policeStation);
        PoliceStation station = null;
        try {
            station = this.policeStationService.saveStation(find);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        policeStationService.addAuditLogForUpdate(station, oldStation);
        return new JsonObject(station);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the police station info is successfully delete or not.")
    public JsonObject delete(HttpServletResponse response, @PathVariable("id") long id) throws IOException {
    	
        try {
            this.policeStationService.deleteStation(id);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }

        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "/threshold/{threshold}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  police station info is successfully updated or not.")
    public JsonObject update(@PathVariable("threshold") long threshold) {
    	this.policeStationDao.updateThreshold(threshold);
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }
    
    @RequestMapping(value="/display",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST",value="分页显示单位")
    public JsonObject displayStation(@RequestBody @Valid PoliceStationDto policeStationDto){
    	List<PoliceStation> resultList = new ArrayList<PoliceStation>();
    	String name = policeStationDto.getName();
    	int page = policeStationDto.getPage();
    	int pagesize = policeStationDto.getPageSize();
    	Pageable<PoliceStation> pageable = null;
    	if ("".equals(name)) {
			resultList = (List<PoliceStation>) policeStationDao.findAll();
		} else {
			resultList = (List<PoliceStation>)policeStationDao.findByLikeName(name);
		}
    	pageable = new Pageable<PoliceStation>(resultList);
    	pageable.setPageSize(pagesize);
    	pageable.setPage(page);
    	
    	return new JsonObject(pageable.getListForPage(),0,pageable.getMaxPages());
    	
    }
    
    @RequestMapping(value="/user/nextlevel",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value="查看超级管理员单位下一级单位列表")
    public JsonObject getUserNextlevelStation(){
        List<PoliceStation> list = null;
        //超级管理员才有权限
        long sId = (((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
        PoliceStation ps = policeStationDao.findOne(sId);
        if(TreeUtil.hasParent(ps.getParentId())){
            return new JsonObject("超级管理员才有权限访问该接口！", 1001);
        }else{
            list = policeStationDao.findByParentId(sId);
        }
        return new JsonObject(list);
        
    }
    
    @RequestMapping(value="/longgang/paichusuo",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value="查看龙岗派出所列表")
    public JsonObject getUserNextlevelStations(){
        List<PoliceStation> list = null;   
        list = policeStationDao.findLongGangPCS();
        return new JsonObject(list);
        
    }

    @RequestMapping(value="/queryByIds", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value="根据id集合查询单位")
    public JsonObject queryNamesByIds(@RequestBody Map<String, String> stationIdsDto) {
        List<PoliceStation> list = null;

        try {
            list = policeStationService.queryNamesByIds(stationIdsDto.get("policeIds"));
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(list);
    }
    
    //查询创建该bank的单位的下属单位(包括自己)里面，有哪些对该bank有type权限，若有，将标志位choice置为60_all,若无，置为40_none
    //请求参数：type, bankId
    //不带自己的id
    @RequestMapping(value="/batch/type/{type}/bankId/{bankId}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value="根据type和bankId批量查询")
    public JsonObject getByTypeAndBankId(@PathVariable("type") int type, @PathVariable("bankId") long bankId){
        List<Long> stationIdList = new ArrayList<Long>();
        TreeNode treeNode = null;
        try {
            Tree tree = policeStationCache.tree();
            treeNode = tree.getRootList().get(0);
            List<PoliceStationAuthority> policeStationAuthorityList = policeStationAuthorityRepository.findByBankIdAndType(bankId, type);
            for (PoliceStationAuthority policeStationAuthority : policeStationAuthorityList) {
                stationIdList.add(policeStationAuthority.getStationId());
            }
            ergodicJudgeAuthority(treeNode, stationIdList);
        } catch (Exception e) {
            LOG.error("", e);
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(treeNode);
        
    }
    
    public void ergodicJudgeAuthority(TreeNode treeNode, List<Long> stationIdList) {
        if (stationIdList.contains(treeNode.getId())) {
            treeNode.setChoice(ZoneConstant.ZONE_CHOICE_ALL);
        }
        List<TreeNode> children = treeNode.getChildList();
        if (null != children) {
            for (TreeNode childNode : children) {
                ergodicJudgeAuthority(childNode, stationIdList);
            }
        }
    }
}
