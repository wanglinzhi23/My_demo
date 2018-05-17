package intellif.service.impl;

import intellif.common.Constants;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.PoliceStationDao;
import intellif.database.dao.AreaDao;
import intellif.database.dao.CameraInfoDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.OauthResourceDao;
import intellif.database.dao.RoleDao;
import intellif.database.dao.RoleResourceDao;
import intellif.database.dao.UserDao;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.OauthResource;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.JsonObject;
import intellif.dto.SearchUserDto;
import intellif.dto.UserAccountDto;
import intellif.dto.UserDto;
import intellif.dto.UserRightDto;
import intellif.enums.RoleTypes;
import intellif.exception.MsgException;
import intellif.service.AuditLogInfoServiceItf;
import intellif.service.PoliceStationCacheItf;
import intellif.service.PoliceStationServiceItf;
import intellif.service.ResourceServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.PageDto;
import intellif.utils.Pageable;
import intellif.utils.SqlUtil;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.RoleResourceDto;
import intellif.zoneauthorize.dao.SystemSwitchDao;
import intellif.zoneauthorize.dao.UserCameraDao;
import intellif.zoneauthorize.dao.UserSwitchDao;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class UserServiceImpl  extends AbstractCommonServiceImpl<UserInfo> implements UserServiceItf<UserInfo> {

    private static Logger LOG = LogManager.getLogger(UserServiceImpl.class);

    private static final String ADMIN = "ADMIN";
    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    private static final String SWITCH_LOG_MSG = "用户%s%s了用户%s的区域授权开关";

    private static final String UPDATE_LOG_MSG = "用户%s设置了用户%s的区域授权范围";

    private static final int DEFAULT_PAGE = 1;

    private static final int DEFAULT_PAGE_SIZE = 20;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    BlackBankDao blackBankDao;
    
    @Autowired
    AreaDao areaDao;
    
    @Autowired
    CameraInfoDao cameraInfoDao;

    @Autowired
    BlackDetailDao blackDetailDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private AuditLogInfoServiceItf auditService;
    @Autowired
    private UserCameraDao userCameraDao;
    
    @Autowired
    private UserSwitchDao userSwitchDao;
    @Autowired
    private SystemSwitchDao systemSwitchDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;

    @Autowired
    PoliceStationAuthorityDao policeStationAuthorityRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;
    @Autowired
    private PoliceStationCacheItf policeStationCache;
    @Autowired
    private ResourceServiceItf resourceServiceItf;
    @Autowired
    private PoliceStationServiceItf policeStationService;
    @Autowired
    private ResourceServiceItf resourceService;
    @Autowired
    private RoleResourceDao roleResourceDao;
    @Autowired
    private PoliceStationDao policeStationDao;
    @Autowired
    PoliceStationCacheItf policeCache;
    @Autowired
    private ZoneAuthorizeCacheItf zoneAuthorizeCache;
    @Autowired
    private OauthResourceDao resourceDao;


    @Override
    public List<UserDto> findByCombinedConditions(UserDto userDto) {
        List<UserDto> resp = null;
        //
        String sqlString = "SELECT a.id, a.login, a.password, a.name, a.gender, a.mobile, a.age, a.post, b.station_name police_station_name, a.camera_rights, a.created, a.updated, a.role_ids roles,a.role_ids roleId, a.c_type_ids "
                + " FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " a, " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_POLICE_STATION + " b " // +
                                                             // GlobalConsts.T_NAME_BLACK_BANK
                                                             // + " c"
                + " WHERE a.police_station_id = b.id ";

        if (!"".equals(userDto.getName())) {
            sqlString += "AND a.name LIKE '%" + userDto.getName() + "%' ";
        }
        if (!"".equals(userDto.getLogin())) {
            sqlString += "AND a.login LIKE '%" + userDto.getLogin() + "%' ";
        }
        if (!"".equals(userDto.getMobile())) {
            sqlString += "AND a.mobile LIKE '%" + userDto.getMobile() + "%' ";
        }
        if (!"".equals(userDto.getPost())) {
            sqlString += "AND a.post LIKE '%" + userDto.getPost() + "%' ";
        }
        if (userDto.getGender() < 3 && userDto.getGender()>0) {
            sqlString += "AND a.gender = " + userDto.getGender() + " ";
        }
        if (!"全部".equals(userDto.getPoliceStationName())) {
            sqlString += "AND b.station_name = '" + userDto.getPoliceStationName() + "' ";
        }
        sqlString += "AND a.age >= " + userDto.getStartAge() + " AND a.age <= " + userDto.getEndAge() + " ";
        sqlString += "AND a.created between str_to_date('" + userDto.getStartTime() + "','%Y-%m-%d %T') AND str_to_date('" + userDto.getEndTime()
                + "','%Y-%m-%d %T')";

        LOG.info(sqlString);
        // @see:
        // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
        try {
            Query query = this.em.createNativeQuery(sqlString, UserDto.class);
            resp = (ArrayList<UserDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }
        return resp;
    }

    @Override
    public String processAuthorityByThread(String sql,long userId) {
        long stationId;
        if( 0 == userId){
          stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
        }else{
            UserInfo ui =  (UserInfo) userDao.findById(userId);
            stationId = ui.getPoliceStationId();
        }
        if (sql.indexOf(GlobalConsts.T_NAME_BLACK_DETAIL) > 0) {
            // 截取 T_NAME_BLACK_DETAIL 表名的别名
            String temp = sql.substring(sql.indexOf(GlobalConsts.T_NAME_BLACK_DETAIL) + GlobalConsts.T_NAME_BLACK_DETAIL.length()).trim();

            List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
            String authorityStr = "";
            for (PoliceStationAuthority authority : authorityList) {
                authorityStr += "," + authority.getBankId();
            }

            if (authorityStr.trim().length() == 0) {
                authorityStr = " 1!=1 ";// 查找不到任何库授权
            } else {
                authorityStr = "bank_id in (" + authorityStr.substring(1) + ") ";
                // 判断表名是否存在别名
                if (!temp.toUpperCase().startsWith("WHERE")) {
                    int offset = temp.indexOf(" ") > temp.indexOf(",") ? temp.indexOf(",") : temp.indexOf(" ");
                    String tableName = temp.substring(0, offset);
                    authorityStr = tableName + "." + authorityStr;
                }
            }

            int splitOffset = sql.toUpperCase().indexOf("WHERE") + "WHERE ".length();
            if (splitOffset > 6) {
                sql = sql.substring(0, splitOffset) + authorityStr + " AND " + sql.substring(splitOffset);
            } else {
                sql = sql + " WHERE " + authorityStr;
            }
        } else if (sql.indexOf(GlobalConsts.T_NAME_PERSON_DETAIL) > 0) {
            // 截取 T_NAME_PERSON_DETAIL 表名的别名
            String temp = sql.substring(sql.indexOf(GlobalConsts.T_NAME_PERSON_DETAIL) + GlobalConsts.T_NAME_PERSON_DETAIL.length()).trim();

            List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
            String authorityStr = "";
            for (PoliceStationAuthority authority : authorityList) {
                authorityStr += "," + authority.getBankId();
            }

            if (authorityStr.trim().length() == 0) {
                authorityStr = " 1!=1 ";// 查找不到任何库授权
            } else {
                authorityStr = "bank_id in (" + authorityStr.substring(1) + ") ";
                // 判断表名是否存在别名
                if (!temp.toUpperCase().startsWith("WHERE")) {
                    int offset = temp.indexOf(" ") > temp.indexOf(",") ? temp.indexOf(",") : temp.indexOf(" ");
                    String tableName = temp.substring(0, offset);
                    authorityStr = tableName + "." + authorityStr;
                }
            }

            int splitOffset = sql.toUpperCase().indexOf("WHERE") + "WHERE ".length();
            if (splitOffset > 6) {
                sql = sql.substring(0, splitOffset) + authorityStr + " AND " + sql.substring(splitOffset);
            } else {
                sql = sql + " WHERE " + authorityStr;
            }
        }
        return sql;
    }
    @Override
    public String processAuthority(String sql) {
        long stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
        if (sql.indexOf(GlobalConsts.T_NAME_BLACK_DETAIL) > 0) {
            // 截取 T_NAME_BLACK_DETAIL 表名的别名
            String temp = sql.substring(sql.indexOf(GlobalConsts.T_NAME_BLACK_DETAIL) + GlobalConsts.T_NAME_BLACK_DETAIL.length()).trim();

            List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
            String authorityStr = "";
            for (PoliceStationAuthority authority : authorityList) {
                authorityStr += "," + authority.getBankId();
            }

            if (authorityStr.trim().length() == 0) {
                authorityStr = " 1!=1 ";// 查找不到任何库授权
            } else {
                authorityStr = "bank_id in (" + authorityStr.substring(1) + ") ";
                // 判断表名是否存在别名
                if (!temp.toUpperCase().startsWith("WHERE")) {
                    int offset = temp.indexOf(" ") > temp.indexOf(",") ? temp.indexOf(",") : temp.indexOf(" ");
                    String tableName = temp.substring(0, offset);
                    authorityStr = tableName + "." + authorityStr;
                }
            }

            int splitOffset = sql.toUpperCase().indexOf("WHERE") + "WHERE ".length();
            if (splitOffset > 6) {
                sql = sql.substring(0, splitOffset) + authorityStr + " AND " + sql.substring(splitOffset);
            } else {
                sql = sql + " WHERE " + authorityStr;
            }
        } else if (sql.indexOf(GlobalConsts.T_NAME_PERSON_DETAIL) > 0) {
            // 截取 T_NAME_PERSON_DETAIL 表名的别名
            String temp = sql.substring(sql.indexOf(GlobalConsts.T_NAME_PERSON_DETAIL) + GlobalConsts.T_NAME_PERSON_DETAIL.length()).trim();

            List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
            String authorityStr = "";
            for (PoliceStationAuthority authority : authorityList) {
                authorityStr += "," + authority.getBankId();
            }

            if (authorityStr.trim().length() == 0) {
                authorityStr = " 1!=1 ";// 查找不到任何库授权
            } else {
                authorityStr = "bank_id in (" + authorityStr.substring(1) + ") ";
                // 判断表名是否存在别名
                if (!temp.toUpperCase().startsWith("WHERE")) {
                    int offset = temp.indexOf(" ") > temp.indexOf(",") ? temp.indexOf(",") : temp.indexOf(" ");
                    String tableName = temp.substring(0, offset);
                    authorityStr = tableName + "." + authorityStr;
                }
            }

            int splitOffset = sql.toUpperCase().indexOf("WHERE") + "WHERE ".length();
            if (splitOffset > 6) {
                sql = sql.substring(0, splitOffset) + authorityStr + " AND " + sql.substring(splitOffset);
            } else {
                sql = sql + " WHERE " + authorityStr;
            }
        }
        return sql;
    }

    @Override
    public String getAuthorityIds(int authorityType) {
        long stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
        List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationIdAndType(stationId, authorityType);
        String authorityStr = "";
        for (PoliceStationAuthority authority : authorityList) {
            authorityStr += "," + authority.getBankId();
        }
        if (authorityStr.length() > 0) {
            authorityStr = authorityStr.substring(1);
        }
        return authorityStr;
    }

    @Override
    public String getAuthorityIdsByType(int authorityType) {

        long stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());

        List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationType(stationId, authorityType);

        String authorityStr = "";
        for (PoliceStationAuthority authority : authorityList) {
            authorityStr += "," + authority.getBankId();
        }
        if (authorityStr.length() > 0) {
            authorityStr = authorityStr.substring(1);
        }
        return authorityStr;
    }

    @Override
    public List<PoliceStationAuthority> getAuthorityByOnlyType(int authorityType) {
       return policeStationAuthorityRepository.findByAuthorityType(authorityType);
    }
    
    @Override
    public void createAuthorityOrIgnore(long stationId, long bankId) {
        try {
            policeStationAuthorityRepository.deleteByStationIdAndBankId(stationId, bankId);
            PoliceStationAuthority policeStationAuthority = new PoliceStationAuthority(stationId, bankId, GlobalConsts.UPDATE_AUTORITY_TYPE);
            policeStationAuthorityRepository.save(policeStationAuthority);
        } catch (Exception e) {
            LOG.error("为stationId:" + stationId + ",bankId:" + bankId + "添加权限失败,可能因为没有该bankId对应的库", e);
        }
    }
    
    @Override
    public void delete(long id) {
        UserInfo userInfo = findOneUserWithAdditionalInfo(id);

        if (userInfo == null)
            throw new MsgException("用户不存在");

        if (!checkUserPrivilege(userInfo)) {
            throw new MsgException(Constants.ERROR_USER_PRIVILEGE);
        }
        policeStationService.delete(userInfo.getPoliceStationId());
        userDao.delete(id);
       // policeStationCache.updatePoliceStationTreeValues("userCount", userInfo.getPoliceStationId(), -1);
    }

    @Override
    @Transactional
    public UserInfo save(UserInfo userInfo) {
        Validate.notNull(userInfo, "用户信息为空！");
        UserInfo loginUserInfo = CurUserInfoUtil.getUserInfo();
        Validate.notNull(loginUserInfo, "请先登录！");

        userInfo.setId(0);

        if (StringUtils.isEmpty(userInfo.getResIds()))
            throw new MsgException("功能权限不能为空",RequestConsts.response_dataresult_error);
/*        if (userInfo.getZone() == null || userInfo.getZone().isEmpty())
            throw new IllegalArgumentException("区域权限不能为空");*/
        // 校验是否有修改权限
        if (!checkUserPrivilege(userInfo)) {
            throw new MsgException("对不起，您没有添加该用户的权限！",RequestConsts.response_right_error);
        }

     /*   List<String> invalidRes = new ArrayList<>();
        if (!checkUserResIds(userInfo, invalidRes)) {
            String resIds = "";
            for (String s : invalidRes) {
                resIds += s;
            }
            throw new MsgException("功能权限选择不正确：" + resIds);
        }*/
         List<UserInfo> uList = this.userDao.findByFilter("login = '"+userInfo.getLogin()+"'");
         if(!CollectionUtils.isEmpty(uList)){
             throw new MsgException("账号已存在！",RequestConsts.response_dataresult_error);
         }

        if ((userInfo.getStartTime() != null && userInfo.getEndTime() != null) && (userInfo.getEndTime().before(userInfo.getStartTime()))) {
            throw new MsgException("对不起，用户账号可使用时间结束时间不能早于开始时间!",RequestConsts.response_dataformat_error);
        }

        // 给用户账号设置可用时限的功能不对超级管理员,中级管理员和管理员适用，
        // 所以超级管理员和管理员默认可用时限是不可修改的
        RoleTypes userRoleType = RoleTypes.fromName(userInfo.getRoleTypeName());
        if (userRoleType == RoleTypes.SUPER_ADMIN || userRoleType == RoleTypes.MIDDLE_ADMIN || userRoleType == RoleTypes.ADMIN) {
            try {
                userInfo.setStartTime(DateUtil.getFormatDate("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
                // userInfo.setEndTime(DateFormat.getDateInstance().parse("2050-01-01
                // 00:00:00"));
                userInfo.setEndTime(DateUtil.getFormatDate("2050-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        createOrUpdateRoleForUser(userInfo);

       /* Validate.notNull(userInfo.getOpened(), "请设置区域授权开关！");

        if (StringUtils.isBlank(userInfo.getName())) {
            userInfo.setName("匿名用户");
        }*/

        
        //每个用户分配一个单位
        PoliceStation ps = new PoliceStation();
        ps.init();
        ps.setStationName("station-"+userInfo.getLogin());
        ps.setParentId(loginUserInfo.getPoliceStationId());
        PoliceStation respPs = policeStationService.saveStation(ps);
        userInfo.setPoliceStationId(respPs.getId());
        UserInfo userInfoRet = (UserInfo) userDao.save(userInfo);
/*
        policeStationCache.updatePoliceStationTreeValues("userCount", userInfo.getPoliceStationId(), 1);
        zoneAuthorizeService.save(userInfo.getZone(), userInfo.getOpened(), userInfoRet.getId());

        StringBuilder logMsg = new StringBuilder();
        logMsg.append(String.format(SWITCH_LOG_MSG, loginUserInfo.getLogin(), userInfo.getOpened() ? "打开" : "关闭", userInfo.getLogin()));
        if (userInfo.getOpened()) {
            logMsg.append("， 并且").append(String.format(UPDATE_LOG_MSG, loginUserInfo.getLogin(), userInfo.getLogin()));
        }
        addAuditLog(logMsg.toString(), userInfoRet.getId(), "Create");*/
        return userInfoRet;
    }

    @Override
    @Transactional
    public UserInfo update(UserInfo modifyUserInfo) {
        Validate.notNull(modifyUserInfo, "用户信息不得为空！");
        UserInfo loginUserInfo = CurUserInfoUtil.getUserInfo();
        Validate.notNull(loginUserInfo, "请先登录！");

        // 判断被修改用户是否存在
        UserInfo oldModifyUser = findOneUserWithAdditionalInfo(modifyUserInfo.getId());
        if (null == oldModifyUser) {
            throw new MsgException("被修改的用户不存在！");

        }
        // 如果是修改自己，只能修改头像
        if (modifyUserInfo.getId() == loginUserInfo.getId()) {
            oldModifyUser.setFaceId(modifyUserInfo.getFaceId());
            return (UserInfo) userDao.save(oldModifyUser);
        }

        if (StringUtils.isEmpty(modifyUserInfo.getResIds()))
            throw new MsgException("功能权限不能为空");

		if (oldModifyUser.getPoliceStationId() != null &&
                (!checkUserPrivilege(oldModifyUser) || !checkUserPrivilege(modifyUserInfo))) {
			throw new MsgException("对不起，您没有修改该用户的权限！");
		}

		try {
		    modifyUserInfo.setStartTime(DateUtil.getFormatDate("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));           
		    modifyUserInfo.setEndTime(DateUtil.getFormatDate("2050-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
		modifyUserInfo.setRoleIds(oldModifyUser.getRoleIds());
		modifyUserInfo.setCreated(oldModifyUser.getCreated());
		modifyUserInfo.setUpdated(new Date());
		modifyUserInfo.setPoliceStationId(oldModifyUser.getPoliceStationId());
		createOrUpdateRoleForUser(modifyUserInfo);
		this.userDao.save(modifyUserInfo);
		//只要用户有修改就清除内存用户可布控权限数据
/*		GlobalConsts.userBukongMap.remove(modifyUserInfo.getId());
		if (oldModifyUser.getPoliceStationId() != null)
            policeStationCache.updatePoliceStationTreeValues("userCount",
                    oldModifyUser.getPoliceStationId(), -1);
		if (modifyUserInfo.getPoliceStationId() != null)
            policeStationCache.updatePoliceStationTreeValues("userCount",
                    modifyUserInfo.getPoliceStationId(), 1);
		// null 表示不改变，集合为空表示清空
		if (modifyUserInfo.getZone() != null)
			zoneAuthorizeService.save(modifyUserInfo.getZone(), modifyUserInfo.getOpened(), modifyUserInfo.getId());

		if (oldModifyUser.getOpened() != modifyUserInfo.getOpened()) {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append(String.format(SWITCH_LOG_MSG, loginUserInfo.getLogin(), modifyUserInfo.getOpened() ? "打开" : "关闭", modifyUserInfo.getLogin()));
            if (modifyUserInfo.getOpened()) {
                logMsg.append("， 并且").append(String.format(UPDATE_LOG_MSG, loginUserInfo.getLogin(), modifyUserInfo.getLogin()));
            }
            addAuditLog(logMsg.toString(), modifyUserInfo.getId(), "Update");
        }*/
        return modifyUserInfo;
    }

    @SuppressWarnings("unchecked")
    private boolean isResIdsChanged(String userTypeName, String oldResIds, String newResIds) {
        Set<String> oldResIdSet = new HashSet<>();
        Set<String> newResIdSet = new HashSet<>();
        List<RoleResourceDto> implicitRes = roleResourceDao.queryResourcesByRoleName(userTypeName, false);

        if (!CollectionUtils.isEmpty(implicitRes))
            for (RoleResourceDto r : implicitRes)
                newResIds += "," + r.getResourceId();

        if (oldResIds != null) {
            oldResIdSet.addAll(Arrays.asList(oldResIds.split(",")));
        }

        if (newResIdSet != null) {
            newResIdSet.addAll(Arrays.asList(newResIds.split(",")));
        }
        return !oldResIdSet.equals(newResIdSet);
    }

    // 检查用户是否有修改全区域搜索账号的权限
    @SuppressWarnings("unchecked")
    private boolean checkUserSpecialSignPrivilege(long userId) {
        List<RoleInfo> loginRoles = roleDao.queryRoleInfoByUserId(userId);
        boolean canModifySpecialSign = false;
        for (RoleInfo r : loginRoles) {
            if (checkRoleSpecialSignPrivilege(r.getName())) {
                canModifySpecialSign = true;
                break;
            }
        }
        return canModifySpecialSign;
    }

    // 检查特定角色是否有修改全区域搜索账号的权限
    private boolean checkRoleSpecialSignPrivilege(String roleTypeName) {
        RoleTypes roleType = RoleTypes.fromName(roleTypeName);
        return roleType == RoleTypes.MIDDLE_ADMIN || roleType == RoleTypes.SUPER_ADMIN;
    }

    /* 查询可分配的特殊账号数量 */
    private int queryAvailableSpecialSignCount(long userStationId) {
        PoliceStation policeStation = findSubStation(userStationId);
        if (policeStation == null || policeStation.getParentId() == null) {
            return 0;
        } else if (policeStation.getParentId() == 0) {
            PoliceStation userStation = policeStationCache.tree().treeNode(PoliceStation.class, userStationId);
            if (userStation.getParentId() != 0)
                return 0;
        }
        return policeStation.getSpecialTotalNum() - policeStation.getSpecialUseNum();
    }

    /* 查找第一个全局域搜索账号总数不为0的单位 */
    private PoliceStation findSubStation(long userStationId) {
        Tree tree = policeStationCache.tree();
        List<TreeNode> forfathers = tree.forefatherList(PoliceStation.class, userStationId, PoliceStation.class, true);
        for (TreeNode node : forfathers) {
            PoliceStation station = (PoliceStation) node;
            if (station.getSpecialTotalNum() > 0)
                return station;
        }
        return null;
    }

    private void addAuditLog(String message, long userId, String operation) {
        AuditLogInfo logInfo = new AuditLogInfo();
        logInfo.setCreated(new Date());
        logInfo.setMessage(message);
        logInfo.setObject(GlobalConsts.T_NAME_USER);
        logInfo.setObjectId(userId);
        logInfo.setObject_status(12);
        logInfo.setOperation(operation);
        logInfo.setOwner(CurUserInfoUtil.getUserInfo().getLogin());
        logInfo.setTitle(message);
        logInfo.setFriDetail("");
        logInfo.setSecDetail("");
        auditService.save(logInfo);
    }

    /*
     * 判断当前用户是否有操作指定用户的权限(用户角色及单位)
     */
    private boolean checkUserPrivilege(UserInfo userInfo) {
        UserInfo loginUserInfo = CurUserInfoUtil.getUserInfo();
        List<RoleInfo> roles = roleDao.queryRoleInfoByUserId(loginUserInfo.getId());
        if (roles == null) {
            return false;
        }
        for (RoleInfo role : roles) {
            RoleTypes roleType = RoleTypes.fromName(role.getName());
            if (roleType == RoleTypes.SUPER_ADMIN)
                return true;
        }

        return checkRoleType(roles, userInfo) && policeStationCache.tree().isOffspring(PoliceStation.class, loginUserInfo.getPoliceStationId(),
                PoliceStation.class, userInfo.getPoliceStationId());
    }

    /*
     * 判断新增或编辑时角色类型设置是否正确
     */
    private boolean checkRoleType(List<RoleInfo> creatorRoles, UserInfo userInfo) {
        RoleTypes userRoleType = RoleTypes.fromName(userInfo.getRoleTypeName());
        for (RoleInfo r : creatorRoles) {
            if (canCreateRole(RoleTypes.fromName(r.getName()), userRoleType))
                return true;
        }
        return false;
    }

    private boolean canCreateRole(RoleTypes creatorRole, RoleTypes userRole) {
        if (creatorRole == RoleTypes.SUPER_ADMIN) {
            return true;
        } else if (creatorRole == RoleTypes.MIDDLE_ADMIN) {
            return userRole == RoleTypes.MIDDLE_ADMIN || userRole == RoleTypes.ADMIN || userRole == RoleTypes.USER || userRole == RoleTypes.GUEST;
        } else if (creatorRole == RoleTypes.ADMIN) {
            return userRole == RoleTypes.ADMIN || userRole == RoleTypes.USER || userRole == RoleTypes.GUEST;
        } else {
            return false;
        }
    }

    /*
     * 判断新增或编辑时用户权限设置是否正确
     * @param invalidRes - 不合法的资源列表, 用于返回
     */
    private boolean checkUserResIds(UserInfo userInfo, List<String> invalidRes) {
        List<RoleResourceDto> resources = resourceServiceItf.queryResourcesByCurrentUser(userInfo.getRoleTypeName(), null);
        List<Long> resIds = new ArrayList<>();
        for (RoleResourceDto r : resources) {
            resIds.add(r.getId());
        }
        Set<Long> compatibleRes = resourceService.compatibleResIds(resIds);
        String[] userResArray = userInfo.getResIds().split(",");

        if (userResArray == null) {
            return true;
        }
        boolean allValid = true;
        for (int i = 0; i < userResArray.length; i++) {
            boolean valid = false;
            for (RoleResourceDto res : resources) {
                if (res.getResourceId().longValue() == Long.valueOf(userResArray[i])) {
                    valid = true;
                    break;
                }
            }
            for (Long id : compatibleRes) {
                if (id.longValue() == Long.valueOf(userResArray[i])) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                invalidRes.add(userResArray[i]);
                allValid = false;
            }
        }
        return allValid;
    }

    @SuppressWarnings("unchecked")
    private RoleInfo createOrUpdateRoleForUser(UserInfo userInfo) {
        List<RoleResourceDto> implicitRes = roleResourceDao.queryResourcesByRoleName(userInfo.getRoleTypeName(), false);
        RoleTypes roleType = RoleTypes.fromName(userInfo.getRoleTypeName());
        RoleInfo roleInfo = new RoleInfo();
        Date now = new Date();
        roleInfo.setCnName(roleType.getCnName());
        roleInfo.setName(roleType.getName());
        roleInfo.setCreated(now);
        roleInfo.setUpdated(now);
        String resIds = userInfo.getResIds();
        if (resIds == null)
            resIds = "";
        if (!CollectionUtils.isEmpty(implicitRes))
            for (RoleResourceDto r : implicitRes)
                resIds += "," + r.getResourceId();
        roleInfo.setResIds(resIds);
        if(StringUtils.isNotBlank(userInfo.getRoleIds())){
            roleInfo.setId(Long.parseLong(userInfo.getRoleIds()));
        }
        roleInfo = (RoleInfo) roleDao.save(roleInfo);
        userInfo.setRoleIds(String.valueOf(roleInfo.getId()));
        return roleInfo;
    }

    private RoleInfo queryUserRole(long userId) {
        List<RoleInfo> roles = roleDao.queryRoleInfoByUserId(userId);
        RoleTypes[] typeArray = new RoleTypes[] { RoleTypes.SUPER_ADMIN, RoleTypes.MIDDLE_ADMIN, RoleTypes.ADMIN, RoleTypes.USER, RoleTypes.GUEST };
        if (roles == null)
            return null;
        for (int i = 0; i < typeArray.length; i++) {
            for (RoleInfo role : roles) {
                if (role.getName().equals(typeArray[i].getName()))
                    return role;
            }
        }
        return null;
    }

    private String filterCameraByInput(String str){
        StringBuffer rStr = new StringBuffer();
        if(StringUtil.isNotBlank(str)){
            if(GlobalConsts.chn_camera_1.indexOf(str) >=0){
                rStr.append(" or u.c_type_ids = '1' ");
            }if(GlobalConsts.chn_camera_123.indexOf(str) >=0){
                rStr.append(" or u.c_type_ids = '1,2,3' ");
            }if(GlobalConsts.chn_camera_special.indexOf(str) >=0){
                rStr.append(" or u.c_type_ids = '1,2,3,4' ");
        }
        }
        return rStr.toString();
    }
    @Override
    public PageDto<UserInfo> queryUsersByStationId(SearchUserDto searchUserDto) {
        if (!policeStationService.checkPrivilege(searchUserDto.getStationId()))
            throw new MsgException("没有搜索此单位的权限");
        List<UserInfo> result;
        int page = searchUserDto.getPage();
        int pageSize = searchUserDto.getPageSize();
        if (page <= 0)
            page = DEFAULT_PAGE;
        if (pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;
        String mofuCType = filterCameraByInput(searchUserDto.getName());
		String stationIds = policeStationService.getStationIds(searchUserDto.getStationId());
        String stationIdsByName = "";
		if (StringUtils.isNotBlank(searchUserDto.getName()))
		    stationIdsByName = policeStationService.searchStationIdsByName(searchUserDto.getStationId(), searchUserDto.getName());

		String selSql = "SELECT u.*, r.name as role_type_name, s.opened, r.res_ids FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
				+ " u left join " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_SWITCH + " s on u.id = s.user_id left join "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
				+ " r on r.id = u.role_ids where 1 = 1 ";
		StringBuffer fb = new StringBuffer();
		if (StringUtils.isNotBlank(stationIds))
			fb.append(" and u.police_station_id in (" + stationIds + ") ");
		if (StringUtils.isNotBlank(searchUserDto.getUserTypeName()))
			fb.append(" and r.name = '" + searchUserDto.getUserTypeName() + "'");
        if (StringUtils.isNotBlank(searchUserDto.getName())) {
           fb.append(" and (u.name like '%" + searchUserDto.getName() + "%' ");
            fb.append(" or u.login like '%" + searchUserDto.getName() + "%' ");
            fb.append(" or r.cn_name like '%" + searchUserDto.getName() + "%' ");
            if(StringUtils.isNotBlank(mofuCType)){
                fb.append(mofuCType);
            }
            if (StringUtils.isNotBlank(stationIdsByName))
                fb.append(" or u.police_station_id in (" + stationIdsByName + ")");
            fb.append(")");
        }
        if(StringUtils.isNotBlank(searchUserDto.getSpecialSign())){
            String sSign = searchUserDto.getSpecialSign();
            if(GlobalConsts.normal_user_sign == Integer.valueOf(sSign).intValue()){
                fb.append(" and u.special_sign ="+sSign);
            }else if(GlobalConsts.special_user_sign == Integer.valueOf(sSign).intValue()){
                fb.append(" and u.special_sign >= "+sSign);
            }
        }
        if(StringUtils.isNotBlank(searchUserDto.getcTypeIds())){
            String cTypeIds = searchUserDto.getcTypeIds();
            fb.append(" and (LOCATE('"+cTypeIds+"',u.c_type_ids) >= 1 or u.c_type_ids is null) ");
        }
        
        String limitSql = " order by u.created desc limit " + (page - 1) * pageSize + ", " + pageSize;
		result = userDao.findObjectBySql(selSql+fb.toString()+limitSql);
        if (result == null)
            result = new ArrayList<>();
        updateUserStationName(result);

        String countSql = "SELECT count(1) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                + " u left join " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_SWITCH + " s on u.id = s.user_id left join "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                + " r on r.id = u.role_ids where 1 = 1 ";
        
        Long count = userDao.countBySql(countSql+fb.toString());
        PageDto<UserInfo> pageDto = new PageDto<UserInfo>(result, count, searchUserDto.getPage(), searchUserDto.getPageSize());
        return pageDto;
    }

    @Override
    public UserInfo findById(long id) {
        UserInfo user;

        UserInfo loginUser = CurUserInfoUtil.getUserInfo();
        user = findOneUserWithAdditionalInfo(id);

        if (user == null)
            return null;

        String aField = "id";
        String filterSql = "user_id = "+id;
        List<Long> aList = areaDao.findFieldByFilter(aField, filterSql);
       
        if(!CollectionUtils.isEmpty(aList)){
            user.setAreaCount(aList.size());
            String aIdStr = StringUtils.join(aList, ",");
            String cFilterSql = " station_id in("+aIdStr+")";
           Long cCount = cameraInfoDao.countByFilter(cFilterSql);
           user.setCameraCount(cCount.intValue());
        }
        
        
        if (loginUser.getId() == user.getId())
            return user;

        if (!checkUserPrivilege(user))
            throw new MsgException("没有查询此用户的权限",RequestConsts.response_right_error);
        return user;
    }

    /*
     * 查询单个用户，带上与用户相关的信息(roleTypeName, resIds, opened)
     * 用于内部调用, 不判断权限
     *
     */
    private UserInfo findOneUserWithAdditionalInfo(long id) {
        UserInfo user  = userDao.findOneUserWithAdditionalInfo(id);
        if (user == null)
            return null;

        user.setOpened(zoneAuthorizeService.userZoneAuthorizeSwitch(id));
        return user;
    }

    private void updateUserStationName(List<UserInfo> users) {
        Tree tree = policeStationCache.tree();

        for (UserInfo user : users) {
            PoliceStation station = tree.treeNode(PoliceStation.class, user.getPoliceStationId());
            user.setPoliceStationNames(getStationNames(tree, station));
        }
    }

    @Override
    public boolean isRoleTypeModified(Long oldRoleId, Long newRoleId, Map<String, String> roleNameInfo) {
        RoleInfo oldRole = (RoleInfo) roleDao.findById(oldRoleId);
        RoleInfo newRole = (RoleInfo) roleDao.findById(newRoleId);
        if (StringUtils.equals(oldRole.getName(), newRole.getName()))
            return false;
        else {
            roleNameInfo.put("oldType", oldRole.getCnName());
            roleNameInfo.put("newType", newRole.getCnName());
            return true;
        }
    }

    @Override
    public boolean isResourcesModified(Long oldRoleId, Long newRoleId, Map<String, String> resNameInfo) {
        RoleInfo oldRole = (RoleInfo) roleDao.findById(oldRoleId);
        RoleInfo newRole = (RoleInfo) roleDao.findById(newRoleId);
        if (isResIdsEqual(oldRole.getResIds(), newRole.getResIds()))
            return false;
        else {
            String oldRes = resourceService.queryResourceNames(oldRole.getResIds());
            String newRes = resourceService.queryResourceNames(newRole.getResIds());
            resNameInfo.put("oldRes", oldRes);
            resNameInfo.put("newRes", newRes);
            return true;
        }
    }

    @Override
    public void unbindUser(long userId) {
        UserInfo user = findOneUserWithAdditionalInfo(userId);
        String updateStationSql = "";

        if (user == null)
            throw new MsgException("用户不存在");

        if (!checkUserPrivilege(user))
            throw new MsgException("没有修改此用户的权限");

        if (user.getPoliceStationId() != null)
            policeStationCache.updatePoliceStationTreeValues("userCount",
                    user.getPoliceStationId(), -1);

        if (user.getSpecialSign() != 0) {
            policeCache.updatePoliceStationTreeValues("specialUseNum", user.getPoliceStationId(), -1);
        }

        // 清空用户区域授权信息
        zoneAuthorizeService.save(null, user.getOpened(), user.getId());
        // 清空用户单位和特殊账号信息
        updateStationSql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER +
                " set police_station_id = null, special_sign = 0 where id = ?";

        jdbcTemplate.update(updateStationSql, userId);
    }

    @Override
    public PageDto<UserInfo> queryUnbindedUsers(SearchUserDto searchUserDto) {

        List<UserInfo> result;
        int page = searchUserDto.getPage();
        int pageSize = searchUserDto.getPageSize();

        if (page <= 0)
            page = DEFAULT_PAGE;
        if (pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;

        String sqlString = "SELECT u.*, r.name as role_type_name, s.opened, r.res_ids FROM (" + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                + " u LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_SWITCH + " s ON u.id = s.user_id), " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                + " r where 1 = 1";
        sqlString += " and find_in_set(r.id, u.role_ids) and u.police_station_id is null ";
        if (StringUtils.isNotBlank(searchUserDto.getName())) {
            sqlString += " and (u.name like '%" + searchUserDto.getName() + "%' ";
            sqlString += " or u.login like '%" + searchUserDto.getName() + "%' ";
            sqlString += ")";
        }
        sqlString +=  " order by u.created desc limit " + (page - 1) * pageSize + ", " + pageSize;

        result = jdbcTemplate.query(sqlString, new BeanPropertyRowMapper<>(UserInfo.class));
        if (result == null)
            result = new ArrayList<>();
//        updateUserStationName(result);

        String countSql = "SELECT count(1) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                + " u, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                + " r where 1 = 1";
        countSql += " and find_in_set(r.id, u.role_ids) and u.police_station_id is null";
        if (StringUtils.isNotBlank(searchUserDto.getName())) {
            countSql += " and (u.name like '%" + searchUserDto.getName() + "%' ";
            countSql += " or u.login like '%" + searchUserDto.getName() + "%' ";
            countSql += ")";
        }
        Long count = jdbcTemplate.queryForObject(countSql, Long.class);
        PageDto<UserInfo> pageDto = new PageDto<>(result, count, searchUserDto.getPage(), searchUserDto.getPageSize());
        return pageDto;
    }

    private boolean isResIdsEqual(String resIds1, String resIds2) {
        String[] resIdsArr1;
        String[] resIdsArr2;

        if (resIds1 == null) {
            if (resIds2 == null)
                return true;
            else
                return false;
        }

        resIdsArr1 = resIds1.split(",");
        resIdsArr2 = resIds2.split(",");
        return resArrToSet(resIdsArr1).equals(resArrToSet(resIdsArr2));
    }

    private Set<String> resArrToSet(String[] resArr) {
        Set<String> resSet = new HashSet<>();
        for (int i = 0; i < resArr.length; i++) {
            if (StringUtils.isNotBlank(resArr[i])) {
                resSet.add(resArr[i].trim());
            }
        }
        return resSet;
    }

    private List<String> getStationNames(Tree tree, PoliceStation station) {
        List<TreeNode> nodes = tree.forefatherList(PoliceStation.class, station.getId(), PoliceStation.class, true);

        if (CollectionUtils.isEmpty(nodes))
            return Collections.emptyList();

        List<String> names = new ArrayList<>();
        for (int i = nodes.size() - 1; i >= 0; i--) {
            names.add(((PoliceStation) nodes.get(i)).getStationName());
        }
        return names;
    }

    @Override
    @Transactional
    public void setSpecialSign(Long userId, Integer specialSign) {
        UserInfo user = findOneUserWithAdditionalInfo(userId);
        String sql;

        if (user == null)
            throw new MsgException("用户不存在");

        if (!checkUserPrivilege(user))
            throw new MsgException("没有修改用户:" + user.getName() + "的权限");

        if (((user.getSpecialSign() == 0 && specialSign != 0) || (user.getSpecialSign() != 0 && specialSign == 0))
                && !checkUserSpecialSignPrivilege(CurUserInfoUtil.getUserInfo().getId()))
            throw new MsgException("没有修改全局域搜索账号的权限");

        if (user.getSpecialSign() == 0 && specialSign != 0) {
            if (queryAvailableSpecialSignCount(user.getPoliceStationId()) < 1){
                throw new MsgException("当前分局的可供分配名额不足，如有需要可向管理员申请，或移除现有账号进行重新分配！");
            }
            policeCache.updatePoliceStationTreeValues("specialUseNum", user.getPoliceStationId(), 1);
        } else if (user.getSpecialSign() != 0 && specialSign == 0) {
            policeCache.updatePoliceStationTreeValues("specialUseNum", user.getPoliceStationId(), -1);
        }

        sql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                + " set special_sign = " + specialSign + " where id = " + userId;
        jdbcTemplate.update(sql);
    }

    private boolean loopCheckSpecialHasOrNot(long sId,boolean loopFirst){
        boolean status = false;
        PoliceStation ps = policeStationDao.findOne(sId);
        if(null != ps){
            long parentId = ps.getParentId();
            if(ps.getSpecialTotalNum() - ps.getSpecialUseNum() > 0 && (parentId != 0 || loopFirst)){
               status = true;
            }else if(parentId != 0){
                return loopCheckSpecialHasOrNot(parentId,false);
            }
        }
        return status;
    }
    @Override
    @Transactional
    public void batchUpdateSpecialSign(String userIds, int specialSign) {
        String[] userIdArr = userIds.split(",");
        if (userIdArr == null || userIdArr.length <= 0)
            throw new MsgException("请指定要更新的用户");

        for (String userId : userIdArr)
            if (StringUtils.isNotBlank(userId))
                setSpecialSign(Long.valueOf(userId), specialSign);
    }

    @Override
    public List<UserInfo> queryContact(Long userId) {
        UserInfo userInfo = findOneUserWithAdditionalInfo(userId);
        String sql;
        Tree tree = policeStationCache.tree();
        List<TreeNode> forfathers = tree.forefatherList(PoliceStation.class, userInfo.getPoliceStationId(), PoliceStation.class, true);

        List<UserInfo> userInfoList = null;
        for (TreeNode node : forfathers) {
            PoliceStation station = (PoliceStation) node;
            sql = "SELECT u.*, r.name as role_type_name, s.opened, r.res_ids FROM (" + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                    + " u LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_SWITCH + " s ON u.id = s.user_id), " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                    + " r where u.police_station_id = " + station.getId();
            sql += " and u.special_sign = 2 and find_in_set(r.id, u.role_ids)";
            userInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserInfo.class));
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userInfoList))
                break;
        }
        if (org.apache.commons.collections.CollectionUtils.isEmpty(userInfoList)) {
            sql = "SELECT u.*, r.name as role_type_name, s.opened, r.res_ids FROM (" + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                    + " u LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_SWITCH + " s ON u.id = s.user_id), " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                    + " r where r.name = 'SUPER_ADMIN' and find_in_set(r.id, u.role_ids)";
            userInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserInfo.class));
        }

        if (userInfoList == null)
            userInfoList = new ArrayList<>();

        return userInfoList;
    }

    @Override
    public boolean hasAllAreas() {
        UserInfo loginUser = CurUserInfoUtil.getUserInfo();
        boolean systemSwitch = zoneAuthorizeCache.zoneAuthorizeSwitch();
        boolean userSwitch = zoneAuthorizeCache.userZoneAuthorizeSwitch(loginUser.getId());

        // 获取用户是最新的用户信息. TODO: 很多地方都会有类似问题，考虑更新用户信息后更新缓存的用户信息
        UserInfo updatedUserInfo = (UserInfo) userDao.findById(loginUser.getId());
        if (!systemSwitch || !userSwitch || updatedUserInfo.getSpecialSign() > 0)
            return true;

        String cTypeIds = updatedUserInfo.getcTypeIds();
        if (StringUtils.isBlank(cTypeIds))
            return false;
        Set<String> cTypeIdSet = new HashSet<>(Arrays.asList(cTypeIds.split(",")));
        Set<String> allTypeIdSet = new HashSet<>(Arrays.asList("1", "2", "3", "4"));
        if (!allTypeIdSet.equals(cTypeIdSet))
            return false;

        String sql = "select count(*) from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_DISTRICT
                + " d where d.parent_id = 0 and d.id not in (select district_id from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER_DISTRICT
                + " ud where ud.user_id = " + loginUser.getId() + ")";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        return count == null || count == 0;
    }
    
  /*  public List<UserBaseInfo> findUserBase(){
        String sql = "SELECT id, login, NAME, police_station_id , post FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER;
        return jdbcTemplate.query(sql, new RowMapper<UserBaseInfo>(){
            @Override
            public UserBaseInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserBaseInfo userBaseInfo = new UserBaseInfo();
                userBaseInfo.setId(rs.getLong(1));
                userBaseInfo.setLogin(rs.getString(2));
                userBaseInfo.setName(rs.getString(3));
                userBaseInfo.setPoliceStationId(rs.getLong(4));
                userBaseInfo.setPost(rs.getString(5));
                return userBaseInfo;
            }
        });
    }*/

    @Override
    public List<UserInfo> findUserInfoByFilters(List<String> filterList) {
        String filterSql = SqlUtil.buildFilter(filterList);
       return userDao.findByFilter(filterSql);
    }

    @Override
    public UserInfo findUserInfoById(long id) {
         return (UserInfo) userDao.findById(id);
    }

    @Override
    public List<Long> findFieldByFilter(String fields, String filter) {
      return userDao.findFieldByFilter(fields, filter);
    }
    
    @Override
    public JsonObject getUsers(UserAccountDto user){
        UserInfo userinfo = CurUserInfoUtil.getUserInfo();
        long stationid = userinfo.getPoliceStationId();// 该用户的所在单位的id
        String roleName = CurUserInfoUtil.getRoleInfo().getName();
        long stationId = user.getId();
        int page = user.getPage();
        int pagesize = user.getPageSize();
        List<UserDto> userlist = new ArrayList<UserDto>();
        Pageable<UserDto> pageable = null;
        if (roleName.equals(GlobalConsts.SUPER_ADMIN) || roleName.equals(GlobalConsts.ADMIN)) {
            String username = user.getName();
            if (roleName.equals(GlobalConsts.SUPER_ADMIN)) {
                stationid = stationId;

                if (stationId == 0) {
                    userlist = userDao.findAllBySuperAdmin(username); // 超级管理员选中全部单位的情况
                    pageable = new Pageable<UserDto>(userlist);
                    pageable.setPageSize(pagesize);
                    pageable.setPage(page);
                    return new JsonObject(pageable.getListForPage(), 0, pageable.getMaxPages());
                }

                userlist = userDao.findBySuperAdmin(stationid, username); // 如果是超级管理员的话
                                                                            // 可以选择单位
                pageable = new Pageable<UserDto>(userlist);
                pageable.setPageSize(pagesize);
                pageable.setPage(page);

            }
            if (roleName.equals(GlobalConsts.ADMIN)) {

                userlist = userDao.findByAdmin(stationid, username); // 超级管理员选中全部单位的情况
                pageable = new Pageable<UserDto>(userlist);
                pageable.setPageSize(pagesize);
                pageable.setPage(page);
                return new JsonObject(pageable.getListForPage(), 0, pageable.getMaxPages());
            }

        } else {
            return new JsonObject("对不起，您没有查询权限！", 1001);
        }
        return new JsonObject(pageable.getListForPage(), 0, pageable.getMaxPages());
      
    }
    
    @Override
    public UserRightDto getUserRight(String name){
        try {
            UserInfo userInfo = (UserInfo) this.userDao.findByFilter("login = '"+name+"'").get(0);
            userInfo = (UserInfo) this.userDao.findById(userInfo.getId());
            PoliceStation station = this.policeStationDao.findOne(userInfo.getPoliceStationId());
            userInfo.setPoliceStationName(station.getStationName());
            UserRightDto rightDto = new UserRightDto();
            List<RoleInfo> roleInfoList = new ArrayList<RoleInfo>();
            List<OauthResource> oauthResourceList = new ArrayList<OauthResource>();
            String[] roleIds = userInfo.getRoleIds().split(",");
            // LOG.info("Raw roleIds:" + roleIds); // find bugs invocation of
            // toString on an array 输出的不是数组内容 而是地址了
            LOG.info("Raw roleIds:" + Arrays.toString(roleIds));
            for (int i = 0; i < roleIds.length; i++) {
                long roleId = Long.valueOf(roleIds[i]);
                RoleInfo roleInfo = (RoleInfo) this.roleDao.findById(roleId);
                userInfo.setRoleTypeName(roleInfo.getName());
                roleInfoList.add(roleInfo);
                String[] resIds = roleInfo.getResIds().split(",");
                // LOG.info("Raw resIds:" + resIds);// find bugs invocation of
                // toString on an array 输出的不是数组内容 而是地址了
                LOG.info("Raw resIds:" + Arrays.toString(resIds));
                for (int j = 0; j < resIds.length; j++) {
                    Long resId = Long.valueOf(resIds[j]).longValue();
                    OauthResource oauthResource = (OauthResource) this.resourceDao.findById(resId);
                    oauthResourceList.add(oauthResource);
                }
            }
            LOG.info("after login get user right info,userInfo:" + userInfo + ",roleInfoList:" + roleInfoList
                    + ",oauthResourceList:" + oauthResourceList);
            rightDto.setOauthResourceList(oauthResourceList);
            rightDto.setRoleInfoList(roleInfoList);
            rightDto.setUserinfo(userInfo);
            return rightDto;
        } catch (Exception e) {
            LOG.error("get user right error:", e);
            throw e;
        }
    
    }

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return userDao;
    }

    /**
     * 判断当前用户是否有操作某用户数据权限
     */
    @Override
    public void isUserOperationAccess(long userId) {
        long loginId = CurUserInfoUtil.getUserInfo().getId();
        boolean right = false;
        if(userId == loginId){
            right = true;
        }else{
            List<RoleInfo> rList = roleDao.queryRoleInfoByUserId(loginId);
            if(!CollectionUtils.isEmpty(rList)){
                if(rList.get(0).getName().equals(GlobalConsts.SUPER_ADMIN)){
                    right = true;
                }
            }
        }
        if(!right){
            MsgException msg = new MsgException("用户没有操作其他用户设备权限!",RequestConsts.response_right_error);
            throw msg;
        }
    }

    /**
     * 当前用户是否是超级管理员superuser
     */
    @Override
    public boolean isSuperUser(Long userId) {
        if(null == userId || userId.intValue() == 0){
            userId = CurUserInfoUtil.getUserInfo().getId();
        }
        boolean right = false;
        List<RoleInfo> rList = roleDao.queryRoleInfoByUserId(userId);
        if (!CollectionUtils.isEmpty(rList)) {
            if (rList.get(0).getName().equals(GlobalConsts.SUPER_ADMIN)) {
                right = true;
            }
        }
        return right;
    }

 

   
}