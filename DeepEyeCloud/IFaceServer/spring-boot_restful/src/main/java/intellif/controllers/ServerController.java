package intellif.controllers;

import intellif.consts.GlobalConsts;
import intellif.dao.AlgParamDao;
import intellif.dao.IndexFaceRecordDao;
import intellif.dao.OauthAccessTokenDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RoleDao;
import intellif.dao.ServerInfoDao;
import intellif.dao.UserDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dto.JsonObject;
import intellif.lire.UserOnlineThread;
import intellif.settings.ServerSetting;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.AlgParam;
import intellif.database.entity.OnLineUserInfo;
import intellif.database.entity.ServerInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class ServerController.</h1> The ServerController which serves
 * request of the form /server and returns a JSON object representing an
 * instance of ServerInfo.
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
// @RequestMapping("/intellif/server")
@RequestMapping(GlobalConsts.R_ID_SERVER)
public class ServerController {

	private static Logger LOG = LogManager.getLogger(ServerController.class);
	
	// ==============
	// PRIVATE FIELDS
	// ==============
	@Autowired
	private UserDao userRepository;
	@Autowired
	private RoleDao roleRepository;
	@Autowired
	private FaceInfoDaoImpl faceInfoServiceImpl;
	@Autowired
	private OauthAccessTokenDao oauthAccessTokenDao;
	@Autowired
	private PoliceStationDao policestationDao;
	@Autowired
	private IndexFaceRecordDao recordRepository;
	@Autowired
	private AlgParamDao algParamDao;

	SimpleDateFormat bartDateFormat = new SimpleDateFormat(
			"yyyy年MM月dd日 EEE HH:mm:ss");

	// Autowire an object of type ServerInfoDao
	@Autowired
	private ServerInfoDao _serverInfoDao;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing if the server info is successfully created or not.")
	public JsonObject create(@RequestBody @Valid ServerInfo serverInfo) {
		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int type = serverInfo.getPort();
		List<Long> idList = new ArrayList<Long>();
		StringTokenizer toKenizer = new StringTokenizer(serverInfo.getIp(), ",");
		while (toKenizer.hasMoreElements()) {
			idList.add(Long.valueOf(toKenizer.nextToken()));
		}
		switch (type) {
		case 1: {
			return new JsonObject(faceInfoServiceImpl.findByIds(idList));
		}
		case 2: {
			return new JsonObject(faceInfoServiceImpl.findByIdsFromOther(
					idList, "from_image_id"));
		}
		case 3: {
			return new JsonObject(faceInfoServiceImpl.findByTime(
					serverInfo.getServerName(), serverInfo.getAddress(),
					"time", "", serverInfo.getPeak(), 100,null));
		}
		case 4: {
			return new JsonObject(
					faceInfoServiceImpl.findAll("age>2 and age<7"));
		}
		default:
			break;
		}
		return new JsonObject(_serverInfoDao.save(serverInfo));
	}

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a list describing all of server info that is successfully get or not.")
	public JsonObject list() {
		return new JsonObject(this._serverInfoDao.findAll());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a string describing if the server info id is successfully get or not.")
	public ServerInfo get(@PathVariable("id") long id) {
		return this._serverInfoDao.findOne(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  server info is successfully updated or not.")
	public JsonObject update(@PathVariable("id") long id,
			@RequestBody @Valid ServerInfo serverInfo) {
		// ServerInfo find = this._serverInfoDao.findOne(id);
		serverInfo.setId(id);
		return new JsonObject(this._serverInfoDao.save(serverInfo));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the server info is successfully delete or not.")
	public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
		this._serverInfoDao.delete(id);
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}

	@RequestMapping(value = "/time", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "返回服务器当前时间")
	public JsonObject getTime() {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		String userName = authentication.getName();
		UserOnlineThread.visitedusers.put(userName, (new Date()).getTime()
				+ "," + false);
		UserOnlineThread.onlineusers.put(userName, (new Date()).getTime());

		UserOnlineThread.userOutOfDateState.put(userName, "");

		if (!UserOnlineThread.onlineusersinfo.containsKey(userName)) {
			OnLineUserInfo user = new OnLineUserInfo();
			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			String ip = CurUserInfoUtil.getIP();
			Long uid = userinfo.getId();
			Long policeStationId = userinfo.getPoliceStationId();
			String jobtitle = userinfo.getPost();
			String accounttype = roleinfo.getCnName();
			String owner = userinfo.getLogin();
			user.setId(uid); 
			user.setIp(ip);
			user.setPost(jobtitle);
			user.setPoliceStationId(policeStationId);
			user.setAccounttype(accounttype);
			user.setName(userName);
			user.setTime(bartDateFormat.format(new Date()));
			user.setOwner(owner);
			UserOnlineThread.onlineusersinfo.put(userName, user);

		}

		return new JsonObject(new Date());

	}

	// 注销登录
	@RequestMapping(value = "/logoff", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "注销登录")
	public JsonObject logOff() {
        try{
            String userName = CurUserInfoUtil.getUserInfo().getLogin();
            oauthAccessTokenDao.deleteByName(userName);
        }catch(Exception e){
            LOG.info("log out error,e:",e);
        }
		return new JsonObject("已注销");

	}

	// 返回在线人数 及其详情信息 的接口 (带权限)
	@RequestMapping(value = "/useramount/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "分页返回在线用户情况")
	public JsonObject getonlinevisitednumbers(@PathVariable("page") int page,
			@PathVariable("pagesize") int pageSize) {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		Long userid = Long.parseLong(((authentication.getPrincipal().toString()
				.split(","))[0].split("="))[1]);
		UserInfo userinfo = userRepository.findOne(userid);
		String roleName = roleRepository.findOne(userinfo.getRoleId())
				.getName();

		Map<String, OnLineUserInfo> map = UserOnlineThread.onlineusersinfo;
		ArrayList onlineuserslist = new ArrayList();

		// 遍历map中的值
		for (OnLineUserInfo value : map.values()) {

			onlineuserslist.add(value);

		}

		if (roleName.equals("SUPER_ADMIN") || roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {

			ArrayList OnLineUserList = new ArrayList();

			for (int i = pageSize * (page - 1); i < pageSize * page; i = i + 1) {

				OnLineUserInfo user = new OnLineUserInfo();

				if (i < onlineuserslist.size()) {
					user = (OnLineUserInfo) onlineuserslist.get(i);

					OnLineUserList.add(user);
				}

			}

			int maxpage = 0;
			if ((onlineuserslist.size() % pageSize) != 0) {

				maxpage = onlineuserslist.size() / pageSize + 1;

			} else {

				maxpage = onlineuserslist.size() / pageSize;

			}

			return new JsonObject(OnLineUserList, 0, maxpage);

		} else {

			return new JsonObject("对不起，您没有修改权限！", 1001);
		}

	}

	@RequestMapping(value = "/log", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "前端日志上传接口")
	public JsonObject log(@RequestBody @Valid String str) {
		try {
			appendMethod("logs\\webLog.log", str);
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
					HttpStatus.OK));
		} catch (Exception e) {
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.FALSE,
					HttpStatus.OK));
		}
	}

	@RequestMapping(value = "/syn/alg", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "同步数据库内算法参数与各Solr服务器接口")
	public JsonObject synAlg() {
		Iterable<AlgParam> algParamList = algParamDao.findAll();
		if(!algParamList.iterator().hasNext()) {
			return new JsonObject("数据库未配置Solr算法参数！");
		}
		
		String failServer = "";
		List<Object> solrServerCameras = recordRepository.getSolrServerWithCameras();
		solrServerCameras.add(ServerSetting.getSolrServer()+"otherinfo");
		for (Object object : solrServerCameras) {
			HttpSolrClient server = new HttpSolrClient(object.toString());
			server.setSoTimeout(30000); // socket read timeout
			server.setConnectionTimeout(30000);
			server.setDefaultMaxConnectionsPerHost(100);
			server.setMaxTotalConnections(100);
			server.setFollowRedirects(false); // defaults to false
			server.setAllowCompression(true);
			server.setMaxRetries(1); // defaults to 0. > 1 not recommended.

			for(AlgParam algParam : algParamList) {
				boolean success =false;
				for(int i = 0; i<3 ; i++) {
					try {
						SolrQuery query = new SolrQuery();
						query.setRequestHandler("/alg");
						query.set("alg_version",algParam.getVersion());
						query.set("base",algParam.getBasePoints());
						query.set("point",algParam.getNewPoints());
						System.out.println(query);
						QueryResponse rsp = server.query(query);
						NamedList<Object> namedlist = rsp.getResponse();
						String result = namedlist.get("result").toString();
						if(null!=result && result.equalsIgnoreCase("success")) {
							success = true;
							break;
						}
					} catch (Exception e) {
						LOG.error(object+" 算法同步第"+(i+1)+"次失败：", e);
					}
				}
				if(!success) {
					failServer += ("," + object.toString());
					break;
				}
			}
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(failServer.length() == 0) {
			return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
		} else {
			return new JsonObject(failServer.substring(1)+" 服务器同步算法参数失败！");
		}
	}

	public static synchronized void appendMethod(String fileName, String content) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List getPageList(List list, int page, int pageSize) {
		int start = (page - 1) * pageSize;
		if (start < 0)
			start = 0;
		if (start > list.size() - 1)
			return null;
		int end = page * pageSize;
		if (end < 0)
			return null;
		if (end > list.size())
			end = list.size();
		return list.subList(start, end);
	}

}
