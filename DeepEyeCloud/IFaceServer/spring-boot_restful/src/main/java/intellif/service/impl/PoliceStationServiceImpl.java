package intellif.service.impl;

import intellif.common.Constants;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.dao.AuditLogDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.PoliceStationDao;
import intellif.database.entity.Area;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.exception.MsgException;
import intellif.service.AuditLogInfoServiceItf;
import intellif.service.PoliceStationCacheItf;
import intellif.service.PoliceStationServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.AuditLogInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class PoliceStationServiceImpl extends AbstractCommonServiceImpl<PoliceStation> implements PoliceStationServiceItf<PoliceStation> {


	private static Logger LOG = LogManager.getLogger(PoliceStationServiceImpl.class);

	private static final String SUPER_ADMIN_NAME = "SUPER_ADMIN";

	private static final int ERROR_HAS_CHILD = -1;

	private static final int ERROR_HAS_USER = -2;

	private static final int OK = 0;

	private String errMsg = "";

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private PoliceStationDao policeStationDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
    private PoliceStationCacheItf policeStationCache;

	@Autowired
    private UserDao userDao;
	@Autowired
	private PoliceStationDao policestationDao;
	@Autowired
	private AuditLogInfoServiceItf auditService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void init() {
		System.out.println("PoliceStationServiceImpl from @service");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findPoliceByTaskId(long id) {
		List<Object> resp = null;
		String sqlString = "SELECT p.id as stationId,p.station_name as stationName,c.name as name FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" p left join "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" c on p.id=c.station_id left join "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_TASK_INFO+" t on c.id= t.source_id WHERE t.id="+id;

		try {
			Query query = this.entityManager.createNativeQuery(sqlString);
			resp = (ArrayList<Object>) query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		return resp;
	}

	@Override
	public List<PoliceStation> queryCurrentUserStations() {
		UserInfo userInfo = CurUserInfoUtil.getUserInfo();
		Tree tree = policeStationCache.tree();
		long userStationId = userInfo.getPoliceStationId();
		List<PoliceStation> result = new ArrayList<>();
		if (isSuperAdmin(userInfo.getId())) {
			List<TreeNode> rootList = tree.getRootList();
			for (TreeNode node : rootList) {
			    result.add((PoliceStation) node);
			}
		} else {
			PoliceStation node = tree.treeNode(PoliceStation.class, userStationId);
			result.add(node);
		}
		return result;
	}

	public PoliceStation saveStation(PoliceStation station) {
		if (StringUtils.isBlank(station.getStationName()))
			throw new MsgException(Constants.ERROR_STATION_NAME_EMPTY,RequestConsts.response_dataresult_error);
        if (!checkPrivilege(station.getParentId()))
        	throw new MsgException(Constants.ERROR_STATION_PRIVILEGE,RequestConsts.response_right_error);
		Tree tree = policeStationCache.tree();
		PoliceStation stationNode = tree.treeNode(PoliceStation.class, station.getParentId());
		List<TreeNode> children = null;
		if (stationNode != null) {
			children = stationNode.getChildList();
		}
		PoliceStation s;
		if (children != null) {
			for (TreeNode n: children) {
				s = (PoliceStation) n;
				if (s.getStationName().equals(station.getStationName()) && !s.getId().equals(station.getId()))
					throw new MsgException(Constants.ERROR_STATION_NAME_DUPLICATE,RequestConsts.response_dataresult_error);
			}
		}

		return policeStationCache.save(station);
	}

    @Override
    public void deleteStation(long id) {
        if (!checkPrivilege(id)) {
            throw new MsgException(Constants.ERROR_STATION_PRIVILEGE);
        }
        int status = checkDataForDeletion(id);
        if (status == ERROR_HAS_CHILD)
        	throw new MsgException(Constants.ERROR_STATION_HAS_CHILD);
        else if (status == ERROR_HAS_USER)
        	throw new MsgException(Constants.ERROR_STATION_HAS_USER);
        policeStationCache.delete(id);
    }

	private boolean isSuperAdmin(long userId) {
		List<RoleInfo> roles = roleDao.queryRoleInfoByUserId(userId);
		if (roles != null) {
			for (RoleInfo r : roles) {
				if (SUPER_ADMIN_NAME .equals(r.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private long getCurrentUserDefaultStationId() {
        UserInfo currentUser = CurUserInfoUtil.getUserInfo();
        long stationId = currentUser.getPoliceStationId();
        return stationId;
    }

    /*
     * 判断用户有没有操作单位的权限
     */
    public boolean checkPrivilege(long stationId) {
        UserInfo currentUser = CurUserInfoUtil.getUserInfo();
        if (isSuperAdmin(currentUser.getId())) {
        	return true;
		}
		List<PoliceStation> stations = this.queryCurrentUserStations();
        Tree tree = policeStationCache.tree();
		for (PoliceStation s : stations) {
		    if (tree.isOffspring(PoliceStation.class, s.getId(),
                    PoliceStation.class, stationId)) {
                return true;
            }
		}
		return false;
    }

	@Override
	public String getStationIds(long stationId) {
		Tree tree = policeStationCache.tree();
		List<TreeNode> stations = tree.offspringList(PoliceStation.class, stationId, PoliceStation.class, true);
		String idstr = "";
		if (stations != null) {
			for (TreeNode n : stations) {
				PoliceStation s = (PoliceStation) n;
				idstr += s.getId() + ",";
			}
		}
		if (idstr.length() > 0)
			idstr = idstr.substring(0, idstr.length() - 1);
		return idstr;
	}

	/*
	 * 查询单位下是否有子单位及或用户，用于删除单位时判断
	 */
	private int checkDataForDeletion(long stationId) {
        Tree tree = policeStationCache.tree();
        PoliceStation station = tree.treeNode(PoliceStation.class, stationId);
        boolean hasChild = CollectionUtils.isNotEmpty(station.getChildList());
        long userCount = userDao.countByPoliceStationId(stationId);
        if (hasChild)
        	return ERROR_HAS_CHILD;
        else if (userCount > 0)
        	return ERROR_HAS_USER;
        else
        	return OK;
    }

    @Override
	public void addAuditLogForUpdate(PoliceStation newinfo, PoliceStation oldinfo) {

		UserInfo userinfo = CurUserInfoUtil.getUserInfo();
		RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();

		boolean updateFlag = false;

		Long uid = userinfo.getId();
		Long policeStationId = userinfo.getPoliceStationId();
		String stationname = ((PoliceStation) policestationDao.findById(policeStationId)).getStationName();   //单位名称
		String userrealname = userinfo.getName();
		String accounttype = roleinfo.getCnName();
		String user = userinfo.getLogin();

		AuditLogInfo log = new AuditLogInfo();
		log.setOwner(user);
		log.setOperation("Update");

		String changes = "";

		if (null == oldinfo) {
			return;
		}

		if (!newinfo.getStationName().equals(oldinfo.getStationName())) {
			changes = "更新单位名称" + oldinfo.getStationName() + "为" + newinfo.getStationName();
			updateFlag = true;
		}

		if (newinfo.getStationNo() != null && !newinfo.getStationNo().equals(oldinfo.getStationNo())) {
			updateFlag = true;
			changes = changes + " 更新单位编号 " + oldinfo.getStationNo() + "为" + newinfo.getStationNo();

		}


		if (newinfo.getPersonThreshold() != oldinfo.getPersonThreshold()) {
			updateFlag = true;
			changes = changes + " 更新区域人数限制阈值 " + oldinfo.getPersonThreshold() + "为" + newinfo.getPersonThreshold();

		}

		if ((newinfo.getParentId() == null && oldinfo.getParentId() != null) ||
				(newinfo.getParentId() != null && !newinfo.getParentId().equals(oldinfo.getParentId()))) {
			updateFlag = true;
			changes += " 更新父单位 " + oldinfo.getParentId() + "为" + newinfo.getParentId();
		}

		LOG.info("EntityAuditListener->touchForUpdate->Auditable PoliceStation!!!");

		log.setObject(GlobalConsts.T_NAME_POLICE_STATION);
		log.setObjectId(oldinfo.getId());
		log.setObject_status(13);
		log.setTitle(log.getOwner() + "已更新单位," + userrealname + "," + stationname);
		log.setMessage(accounttype + "(" + log.getOwner() + ")" + "已更新单位 :" + changes);
		if (updateFlag)
		    auditService.save(log);

	}

	@Override
	public String searchStationIdsByName(Long stationId, String searchName) {
		Tree tree = policeStationCache.tree();
		List<TreeNode> stations = tree.offspringList(PoliceStation.class, stationId, PoliceStation.class, true);
		String idstr = "";
		if (stations != null) {
			for (TreeNode n : stations) {
				PoliceStation s = (PoliceStation) n;
				if (s.getStationName().contains(searchName))
					idstr += s.getId() + ",";
			}
		}
		if (idstr.length() > 0)
			idstr = idstr.substring(0, idstr.length() - 1);
		return idstr;
	}

	@Override
	public List<PoliceStation> queryNamesByIds(String policeIds) {
	    if (policeIds == null)
			throw new MsgException("policeIds不能为空");
		String[] policeIdArr = policeIds.split(",");

		String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION
				+ " s where s.id in (" + policeIds + ")";

		List<PoliceStation> policeStations = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PoliceStation.class));

		return policeStations;
	}

    @Override
    public List<PoliceStation> getForefathers(long stationId) {
        List<PoliceStation> list = new ArrayList<PoliceStation>();
        long parentId = 0L;
        try {
            parentId = ((TreeNode) policeStationDao.findById(stationId)).getParentId();
            PoliceStation policeStation = null;
            while(parentId != 0L) {
                policeStation = (PoliceStation) policeStationDao.findById(parentId);
                parentId = policeStation.getParentId();
                list.add(policeStation);
            }
        } catch (NullPointerException e) {
            LOG.error("stationId为" + stationId + "的数据不存在");
        }
        return list;
    }

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return policeStationDao;
    }

}
