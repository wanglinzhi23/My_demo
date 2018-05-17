package intellif.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.database.entity.BlackBank;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.service.UrgentAlarmServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.SqlUtil;
import intellif.database.entity.EventInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@Service
public class UrgentAlarmServiceImpl implements UrgentAlarmServiceItf {
	private static Logger LOG = LogManager.getLogger(UrgentAlarmServiceImpl.class);
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	UserServiceItf _userService;
	@Autowired
	FaceInfoDaoImpl faceInfoDao;
	@Autowired
	ImageInfoDaoImpl imageInfoDao;
	@Autowired
	ZoneAuthorizeServiceItf zoneAuthorizeService;

	/**
	 * 查询指定库下紧急布控人员
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDetail> findUrgentAlarmPersonByBankId(long id, int page, int pageSize) {
		List<PersonDetail> resp = null;
		String sqlString = "SELECT b.from_person_id, c.* FROM " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_URGENT_ALARM_INFO + " a, " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_PERSON_DETAIL
				+ " c where a.black_id = b.id and b.from_person_id = c.id and c.bank_id = " + id
				+ " and c.is_urgent = 1" + " group by b.from_person_id order by max(a.time) desc LIMIT "
				+ (page - 1) * pageSize + "," + (pageSize * 3) + "";

		// sqlString = _userService.processAuthority(sqlString);

		try {
			Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
			resp = (ArrayList<PersonDetail>) query.getResultList();
		} catch (Exception e) {
			LOG.error("findUrgentAlarmPersonByBankId method error:", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}

	/**
	 * 根据紧急布控人员ID查询报警事件流
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventInfo> findEventsByPersonId(long id, float threshold, int page, int pageSize) {
		List<EventInfo> resp = null;
		  List<String> cameraFields = new ArrayList<String>();
	        cameraFields.add("id");
	        cameraFields.add("name");
	        cameraFields.add("station_id");
	        cameraFields.add("geo_string");
	        cameraFields.add("addr");
	        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "f");
		String cameraIdStr = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and f.id in");
		String sql1 = "SELECT id,send,face_id, person_id,confidence,image_data,scene,camera_id,camera_name,area_id,geo_string,address,time FROM ("
				+ "SELECT a.id, a.send,a.face_id,b.from_person_id person_id, a.confidence confidence, 0 as image_data, 0 as scene,f.id camera_id,"
				+ " f.name camera_name,f.station_id area_id, f.geo_string ,f.addr as address, a.time time FROM " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_URGENT_ALARM_INFO + " a, " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_TASK_INFO + " e, " + cameraSql
				+ " where  a.black_id = b.id and a.task_id = e.id and e.source_id = f.id " + cameraIdStr
				+ " and b.from_person_id = " + id + " union ";

		String sql2 = "SELECT a.id+10000000000, 0,0,a.object_id person_id, -1 confidence, a.title image_data, \"\" scene, -1 camera_id, "
				+ "a.object_status camera_name, \"\" area_id, \"\" geo_string, " + "a.created time FROM " + GlobalConsts.INTELLIF_BASE
				+ "." + GlobalConsts.T_NAME_AUDIT_LOG + " a where a.object = '" + GlobalConsts.T_NAME_PERSON_DETAIL
				+ "' and a.object_id = " + id + ") a order by time desc LIMIT " + (page - 1) * pageSize + "," + pageSize
				+ "";
		
		sql1 = _userService.processAuthority(sql1);
		// sql2= _userService.processAuthority(sql2);

		String sqlString = sql1 + sql2;

		try {
			Query query = this.entityManager.createNativeQuery(sqlString, EventInfo.class);
			resp = (ArrayList<EventInfo>) query.getResultList();
			updateFaceInfoToEventInfo(resp);
		} catch (Exception e) {
			LOG.error("findEventByPersonId  method error:", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}

	/**
	 * 查询用户授权下所有存在紧急布控人员的库信息列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlackBank> findBanksOfUrgentPersons(String ids) {
		/**
		 * 
		 * select * from intellif_base.t_black_bank b WHERE EXISTS(select p.id
		 * from intellif_base.t_person_detail p WHERE p.bank_id = b.id and
		 * p.is_urgent = 1) and b.id in (1,26,41,25) ORDER BY b.created desc
		 */
		List<BlackBank> resp = null;

		String sql = " select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " b "
				+ " WHERE EXISTS(select p.id from " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_PERSON_DETAIL + " p where p.bank_id = b.id and p.is_urgent = 1) and b.id in("
				+ ids + ") order by b.created desc";
		try {
			Query query = this.entityManager.createNativeQuery(sql, BlackBank.class);
			resp = (ArrayList<BlackBank>) query.getResultList();
		} catch (Exception e) {
			LOG.error("findBanksOfUrgentPersons  method error:", e);
		} finally {
			entityManager.close();
		}
		return resp;
	}

	/**
	 * 获取event对应图片信息
	 * 
	 * @param alarmList
	 */
	private void updateFaceInfoToEventInfo(List<EventInfo> eventList) {

		Map<Long, List<EventInfo>> eventMap = new HashMap<Long, List<EventInfo>>();
		Map<Long, FaceInfo> faceMap = new HashMap<Long, FaceInfo>();
		List<Long> faceIdList = new ArrayList<Long>();
		List<Long> imageIdList = new ArrayList<Long>();
		for (EventInfo event : eventList) {
			long Id = event.getFaceId();
			if (0 != Id) {
				List<EventInfo> fEventList = eventMap.get(Id);
				if (null == fEventList) {
					fEventList = new ArrayList<EventInfo>();
					eventMap.put(Id, fEventList);
				}
				fEventList.add(event);
				faceIdList.add(Id);
			}
		}

		List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
		if (null != faceList && !faceList.isEmpty()) {
			for (FaceInfo face : faceList) {
				long faceId = face.getId();
				List<EventInfo> fEventList = eventMap.get(faceId);
				for (EventInfo item : fEventList) {
					item.setImageData(face.getImageData());
				}
				faceMap.put(face.getFromImageId(), face);
				imageIdList.add(face.getFromImageId());
			}
			List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
			if (null != imageList && !imageList.isEmpty()) {
				for (ImageInfo image : imageList) {
					long imageId = image.getId();
					FaceInfo face = faceMap.get(imageId);
					long faceId = face.getId();
					List<EventInfo> fEventList = eventMap.get(faceId);
					for (EventInfo item : fEventList) {
						item.setScene(image.getUri());
					}
				}
			}
		}
	}
}
