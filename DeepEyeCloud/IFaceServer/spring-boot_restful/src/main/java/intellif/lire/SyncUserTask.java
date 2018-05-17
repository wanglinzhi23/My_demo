package intellif.lire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.dao.PoliceManDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserDao;
import intellif.settings.XinYiSettings;
import intellif.database.entity.PoliceMan;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.UserInfo;
import intellif.database.entity.XinYiUserInfo;

/*
 * 信义数据同步，created by yktang
 */
@Component
public class SyncUserTask {

	private static Logger LOG = LogManager.getLogger(SyncUserTask.class);
	private static boolean lastLoop = false;

	private List<PoliceStation> policeStationList = new ArrayList<>();
	private Map<Long, PoliceStation> policeStationMap = new HashMap<>();

	@Autowired
	UserDao userDao;
	@Autowired
	PoliceManDao policemanDao;
	@Autowired
	PoliceStationDao policeStationDao;

	//@Scheduled(cron = "0 0 0 * * *")
	public void syncUserInfo() {
	    if(!XinYiSettings.isXinyiUserSwitch()){
	        return;
	    }
		int pageIndex = 0;
		int pageSize = 1000;
		policeStationList = (List<PoliceStation>) policeStationDao.findAll();
		policeStationMap = policeStationList.stream()
				.collect(Collectors.toMap(PoliceStation::getId, Function.identity()));
		while (!lastLoop) {
			LOG.info("start read from xin yi API...");
			List<XinYiUserInfo> xinyiuserList = readFromXinYiApi(pageIndex, pageSize);
			LOG.info("read from xin yi API completed...");
			if (xinyiuserList == null || xinyiuserList.size() == 0) {
				return;
			}
			pageIndex++;
			List<String> loginList = xinyiuserList.stream().map(XinYiUserInfo::getUserName)
					.collect(Collectors.toList());
			// fetch t_user and t_policeman_info
			List<UserInfo> userInfoList = fetchUserInfo(loginList);
			List<PoliceMan> policemanList = fetchPoliceMan(loginList);
			// process data
			Map<String, UserInfo> userInfoUpdateMap = new HashMap<>();
			Map<String, PoliceMan> policemanUpdateMap = new HashMap<>();
			process(xinyiuserList, userInfoList, policemanList, userInfoUpdateMap, policemanUpdateMap);

			LOG.info("start write to t_user... total number: " + userInfoUpdateMap.size());
			writeUser(userInfoUpdateMap);
			LOG.info("start write to t_policeman_info... total number: " + policemanUpdateMap.size());
			writePoliceman(policemanUpdateMap);
			LOG.info("end write...");
		}
	}

	private List<XinYiUserInfo> readFromXinYiApi(int pageIndex, int pageSize) {
		List<XinYiUserInfo> xinyiuserList = new ArrayList<>();
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(XinYiSettings.getUserApiUrl());
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
		urlParameters.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jsonObject = (JSONObject) (new JSONParser().parse(result));
			if (jsonObject == null) {
				return xinyiuserList;
			}

			JSONArray xinyiuserArray = (JSONArray) jsonObject.get("data");
			if (xinyiuserArray == null) {
				return xinyiuserList;
			}

			int length = xinyiuserArray.size();
			if (length < pageSize) {
				lastLoop = true;
				LOG.info("last loop!!!!!!!!!!!!!!!!!!");
			}

			for (int i = 0; i < length; i++) {
				JSONObject element = (JSONObject) xinyiuserArray.get(i);
				XinYiUserInfo xinyiuserinfo = new XinYiUserInfo();
				xinyiuserinfo.setId((Long) element.get("id"));
				xinyiuserinfo.setCurrentMainJob((String) element.get("currentMainJob"));
				xinyiuserinfo.setOrganName((String) element.get("organName"));
				xinyiuserinfo.setDept((String) element.get("dept"));
				xinyiuserinfo.setPhoneNumber((String) element.get("phoneNumber"));
				xinyiuserinfo.setRealName((String) element.get("realName"));
				xinyiuserinfo.setUserName((String) element.get("userName"));
				xinyiuserList.add(xinyiuserinfo);
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return xinyiuserList;
	}

	private List<UserInfo> fetchUserInfo(List<String> loginList) {
		return userDao.findByLoginList(loginList.toArray(new String[0]));
	}

	private List<PoliceMan> fetchPoliceMan(List<String> loginList) {
		return policemanDao.findByPoliceNoArray(loginList.toArray(new String[0]));
	}

	private void process(List<XinYiUserInfo> xinyiuserList, List<UserInfo> userInfoList, List<PoliceMan> policemanList,
			Map<String, UserInfo> userInfoUpdateMap, Map<String, PoliceMan> policemanUpdateMap) {
		Map<String, UserInfo> userInfoMap = userInfoList.stream()
				.collect(Collectors.toMap(UserInfo::getLogin, Function.identity()));
		Map<String, PoliceMan> policemanMap = policemanList.stream()
				.collect(Collectors.toMap(PoliceMan::getPoliceNo, Function.identity()));
		for (XinYiUserInfo xyuserInfo : xinyiuserList) {
			String username = xyuserInfo.getUserName();
			UserInfo userInfo = userInfoMap.get(username);
			PoliceMan policeman = policemanMap.get(username);
			String xyRealName = xyuserInfo.getRealName();
			String organName = xyuserInfo.getOrganName();

			long newStationId = 0L;

			if (userInfo != null) {
				boolean changedUserInfo = false;
				if (xyRealName != null && !xyRealName.equals(userInfo.getName())) {
					userInfo.setName(xyRealName);
					changedUserInfo = true;
				}
				String position = xyuserInfo.getCurrentMainJob();
				if (position != null && !position.equals(userInfo.getPost())) {
					userInfo.setPost(position);
					changedUserInfo = true;
				}
				newStationId = userInfo.getPoliceStationId();
				if (organName != null) {
					if (policeStationMap.get(newStationId) != null && !organName.equals(policeStationMap.get(newStationId).getStationName())) {
						List<PoliceStation> policeStations = policeStationList.stream()
								.filter(x -> x.getStationName().equals(organName)).collect(Collectors.toList());
						if (policeStations != null && policeStations.size() > 0) {
							PoliceStation policeStation = policeStations.get(0);
							newStationId = policeStation.getId();
							userInfo.setPoliceStationId(newStationId);
							changedUserInfo = true;
						}
					}
				}

				if (changedUserInfo) {
					userInfoUpdateMap.put(username, userInfo);
				}
			}

			if (policeman != null) {
				boolean changedPoliceman = false;
				if (xyRealName != null && !xyRealName.equals(policeman.getName())) {
					policeman.setName(xyRealName);
					changedPoliceman = true;
				}
				String phone = xyuserInfo.getPhoneNumber();
				if (phone != null && !phone.equals(policeman.getPhone())) {
					policeman.setPhone(phone);
					changedPoliceman = true;
				}
				if (newStationId != 0 && newStationId != policeman.getStationId()) {
					policeman.setStationId(newStationId);
					changedPoliceman = true;
				}
				if (changedPoliceman) {
					policemanUpdateMap.put(username, policeman);
				}
			}
		}
	}

	private void writeUser(Map<String, UserInfo> userInfoMap) {
		for (Map.Entry<String, UserInfo> entry : userInfoMap.entrySet()) {
			UserInfo userInfo = entry.getValue();
			LOG.info(userInfo.getLogin() + ", " + userInfo.getName() + ", " + userInfo.getPost() + ", " + userInfo.getPoliceStationId());
			userDao.updateUserInfo(userInfo.getLogin(), userInfo.getName(), userInfo.getPost(),
					userInfo.getPoliceStationId());
		}
	}

	private void writePoliceman(Map<String, PoliceMan> policemanMap) {
		for (Map.Entry<String, PoliceMan> entry : policemanMap.entrySet()) {
			PoliceMan policeman = entry.getValue();
			LOG.info(policeman.getPoliceNo() + ", " + policeman.getName() + ", " + policeman.getPhone() + ", " + policeman.getStationId());
			policemanDao.updatePoliceman(policeman.getPoliceNo(), policeman.getName(), policeman.getPhone(),
					policeman.getStationId());
		}
	}
}
