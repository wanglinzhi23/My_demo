package intellif.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.code.ssm.api.InvalidateSingleCache;

import intellif.chd.settings.CameraNodeIdSetting;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.AreaAndBlackDetailDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.UserDao;
import intellif.database.dao.CameraInfoDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.RoleDao;
import intellif.database.dao.impl.RoleInfoImpl;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.CameraDto;
import intellif.dto.CameraGeometryInfoDto;
import intellif.dto.CameraIdListDto;
import intellif.dto.CameraQueryDto;
import intellif.service.AreaServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.utils.SqlUtil;
import intellif.utils.ZoneUtil;
import intellif.database.entity.Area;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.AreaCameraStatistic;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.RoleInfo;
import intellif.exception.MsgException;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@Service
public class CameraServiceImpl extends AbstractCommonServiceImpl<CameraInfo> implements CameraServiceItf<CameraInfo> {

    private static Logger LOG = LogManager.getLogger(CameraServiceImpl.class);
    
    private static long cameraId = CameraNodeIdSetting.getNodeId();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;
    @Autowired
    private AreaAndBlackDetailDao _areaAndBlackDetailRepository;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private RoleInfoImpl roleRepository;
    @Autowired
    private CameraInfoDao cameraInfoDao;
    @Autowired
    private AreaServiceImpl areaService;
    @Override
    public List<CameraInfo> findOtherCameraAll() {
        List<CameraInfo> resp = null;
        String sqlString = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_OTHER_CAMERA_INFO + "  WHERE node_id= " + cameraId;
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, CameraInfo.class);
            resp = (ArrayList<CameraInfo>) query.getResultList();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public List<CameraDto> findAllCameraDto(Long userId) {
        List<CameraDto> retList = new ArrayList<>();
        List<CameraInfo> cameraInfoList = zoneAuthorizeService.findAll(CameraInfo.class,userId);
        List<Area> areaList = zoneAuthorizeService.findAll(Area.class,userId);
        final Map<Long, String> areaMap = new HashMap<>();
        areaList.forEach(x -> areaMap.put(x.getId(), x.getName()));
        for (CameraInfo cameraInfo : cameraInfoList) {
            String areaName = areaMap.get(cameraInfo.getPreviousId());
            retList.add(new CameraDto(cameraInfo, null == areaName ? "" : areaName));
        }
        return retList;
    }

    @Override
    public List<CameraInfo> findOneCameraDto(long cameraId) {
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraId);
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "a");
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("area_name");
        areaFields.add("id");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "b");
        List<CameraInfo> resp = null;
        String sqlString = "SELECT b.area_name, a.* " + "FROM " + cameraSql + ", "
                + areaSql + " WHERE a.station_id = b.id and a.id = " + cameraId;
        try {
            resp = super.findObjectBySql(sqlString);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public Iterable<CameraInfo> getCameraByPersonId(long id) {
        List<Long> cameraIdList = new ArrayList<Long>();
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        List<CameraAndBlackDetail> cAndBList = _cameraAndBlackDetailRepository.findByBlackdetailId(blackList.get(0).getId());
        for (CameraAndBlackDetail cAndB : cAndBList) {
            cameraIdList.add(cAndB.getCameraId());
        }
        cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class, cameraIdList, null);
		if(cameraIdList==null||cameraIdList.size()==0){
		   return new ArrayList<CameraInfo>(); 
		}
		return findAll(cameraIdList);
    }
    
    @Override
    public  List<Long> getCameraIdsByPersonId(long id) {
        List<Long> cameraIdList = new ArrayList<Long>();
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        List<CameraAndBlackDetail> cAndBList = _cameraAndBlackDetailRepository.findByBlackdetailId(blackList.get(0).getId());
        for (CameraAndBlackDetail cAndB : cAndBList) {
            cameraIdList.add(cAndB.getCameraId());
        }
        cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class, cameraIdList, null);
        return cameraIdList;
    }
    @Override
    public  List<Long> getAreaIdsByPersonId(long id) {
        List<Long> areaIdList = new ArrayList<Long>();
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        List<AreaAndBlackDetail> aAndBList = _areaAndBlackDetailRepository.findByBlackdetailId(blackList.get(0).getId());
        for (AreaAndBlackDetail aAndB : aAndBList) {
            areaIdList.add(aAndB.getAreaId());
        }
        areaIdList = zoneAuthorizeService.filterIds(Area.class, areaIdList, null);
        return areaIdList;
    }
    @Override
    public void addPersonToCamera(long id, List<CameraInfo> cameraList) {
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        for (CameraInfo camera : cameraList) {
            for (BlackDetail black : blackList) {
                this._cameraAndBlackDetailRepository.save(new CameraAndBlackDetail(camera.getId(), black.getId()));
            }
        }
    }

    @Override
    public void delPersonFromCamera(long id, List<CameraInfo> cameraList) {
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        for (CameraInfo camera : cameraList) {
            for (BlackDetail black : blackList) {
                // this._cameraAndBlackDetailRepository.delete(camera.getId(),
                // black.getId());
                this._cameraAndBlackDetailRepository.deleteByCameraIdAndBlackdetailId(camera.getId(), black.getId());
            }
        }
    }

    @Override
    public void addBlackToCamera(long id, Iterable<CameraInfo> cameraList) {
        String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
        List<BlackDetail> blackList = _blackDetailDao.findOne(id, authority.split(","));
        if (blackList.size() <= 0) {
            return;
        }
        BlackDetail black = blackList.get(0);
        for (CameraInfo camera : cameraList) {
            this._cameraAndBlackDetailRepository.save(new CameraAndBlackDetail(camera.getId(), black.getId()));
        }
    }

    @Override
    public void delBlackFromCamera(long id, Iterable<CameraInfo> cameraList) {
        String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
        if (authority.trim().length() == 0) {
            return;
        }
        List<BlackDetail> blackList = _blackDetailDao.findOne(id, authority.split(","));
        if (blackList.size() <= 0)
            return;
        for (CameraInfo camera : cameraList) {
            this._cameraAndBlackDetailRepository.deleteByCameraIdAndBlackdetailId(camera.getId(), blackList.get(0).getId());
        }
    }

    @Override
    public List<CameraGeometryInfoDto> findCameraGeometry() {
        List<CameraGeometryInfoDto> resp = null;
        String sqlString = "SELECT id, name, geo_string, geometry FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO;
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, CameraGeometryInfoDto.class);
            resp = query.getResultList();
            resp = zoneAuthorizeService.filterById(CameraInfo.class, resp, null);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public List<AreaCameraStatistic> findCameraStatistic(Long[] cameraids) {
        List<AreaCameraStatistic> resp = new ArrayList<>();
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("station_id");
        cameraFields.add("name");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("id");
        areaFields.add("area_name");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "area");
        String sqlString = "select camera.id, camera.station_id, area.area_name, camera.name, 0 count from " +cameraSql+ ", " + areaSql
                + " where area.id = camera.station_id ";
        if (cameraids[0] != 0) {
            sqlString += "and camera.id in (:cameraidlist) group by camera.id";
        } else {
            sqlString += "group by camera.id";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, AreaCameraStatistic.class);
            if (cameraids[0] != 0) {
                query.setParameter("cameraidlist", Arrays.asList(cameraids));
            }
            resp = query.getResultList();
            resp = zoneAuthorizeService.filterById(CameraInfo.class, resp, null);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    /*
     * @Override public Page<CameraDto> findAll(CameraDto cameraDto, Pageable
     * pageable) { List<CameraDto> allCameraDtoList = findAllCameraDto(); if
     * (null == allCameraDtoList) { return new PageImpl<>(new ArrayList<>(),
     * pageable, 0); } allCameraDtoList.sort((one, two) ->
     * one.getDisplayName().compareTo(two.getDisplayName())); List<CameraDto>
     * cameraList = null; if (null == cameraDto) { cameraList =
     * allCameraDtoList; } else { cameraList = new ArrayList<>(); List<String>
     * filterList = new ArrayList<>(); if
     * (StringUtils.isNotBlank(cameraDto.getQuery())) { String[] tempArray =
     * cameraDto.getQuery().trim().split(" "); for (String temp : tempArray) {
     * if (StringUtils.isNotBlank(temp)) { filterList.add(temp.trim()); } } }
     * for (CameraDto camera : allCameraDtoList) { boolean isAccord = true;
     * String displayNameTemp; try { displayNameTemp =
     * BeanUtils.getProperty(camera, cameraDto.getQueryBy()); } catch
     * (IllegalAccessException | InvocationTargetException |
     * NoSuchMethodException e) { LOG.error(
     * "catch exception, default query by displayName. ", e); displayNameTemp =
     * camera.getDisplayName(); } if (!filterList.isEmpty()) { for (String
     * filter : filterList) { if (!displayNameTemp.contains(filter)) { isAccord
     * = false; break; } } } if (isAccord && cameraDto.getStationId() > 0 &&
     * cameraDto.getStationId() != camera.getStationId()) { isAccord = false; }
     * if (isAccord && cameraDto.getInStation() >= 0 && cameraDto.getInStation()
     * <= 1 && cameraDto.getInStation() != camera.getInStation()) { isAccord =
     * false; } if (isAccord && cameraDto.getNeedGeoString() &&
     * StringUtils.isBlank(camera.getGeoString())) { isAccord = false; } if
     * (isAccord) { cameraList.add(camera); } } }
     * 
     * int offset = Math.min(pageable.getOffset(), cameraList.size()); int end =
     * Math.min(pageable.getOffset() + pageable.getPageSize(),
     * cameraList.size());
     * 
     * if (offset >= cameraList.size()) { return new PageImpl<>(new
     * ArrayList<>(), pageable, cameraList.size()); } return new
     * PageImpl<>(cameraList.subList(offset, end), pageable, cameraList.size());
     * }
     */

    @Override
    public List<CameraDto> findByIds(List<Long> idList) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "a");
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("area_name");
        areaFields.add("id");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "b");
        
        idList = zoneAuthorizeService.filterIds(CameraInfo.class, idList, null);
        List<CameraDto> resp = null;
        String sqlString = "SELECT b.area_name, a.* " + "FROM " + cameraSql + ", "
                + areaSql + "WHERE a.id in (:ids) AND a.station_id = b.id";
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, CameraDto.class);
            query.setParameter("ids", idList);
            resp = query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }

        return resp;
    }

    @Override
    public List<CameraInfo> findByName(String name) {
        String sql = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO + " t WHERE t.name = ?";
        List<CameraInfo> cameraInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class), name);
        return zoneAuthorizeService.filterById(CameraInfo.class, cameraInfoList, null);
    }

    @Override
    public List<CameraInfo> findByStationId(long areaId) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "t");
        String sql = "SELECT * FROM " + cameraSql+ " WHERE t.station_id = ?";
        List<CameraInfo> cameraInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class), areaId);
        return zoneAuthorizeService.filterById(CameraInfo.class, cameraInfoList, null);
    }

    @Override
    public List<CameraInfo> findInStation() {
        String sql = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO + " t WHERE t.in_station = 1";
        List<CameraInfo> cameraInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class));
        return zoneAuthorizeService.filterById(CameraInfo.class, cameraInfoList, null);
    }

    @Override
    public CameraInfo findById(long cameraId) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "t");
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraId);
        String sql = "SELECT * FROM " + cameraSql + " WHERE t.id = ?";
        List<CameraInfo> cameraInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class), cameraId);
        if (CollectionUtils.isEmpty(cameraInfoList)) {
            return null;
        }
        return cameraInfoList.get(0);
    }

    @Override
    public List<CameraInfo> findAll(Iterable<Long> cameraIdList) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "t");
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraIdList);
        String sql = "SELECT * FROM " + cameraSql + " WHERE t.id in ("
                + StringUtils.join(cameraIdList, ", ") + ")";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class));
    }

    @Override
    public List<CameraInfo> findAll() {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e");
        String sql = "SELECT * FROM " +cameraSql;
        List<CameraInfo> cameraInfoList = super.findObjectBySql(sql);
        return zoneAuthorizeService.filterById(CameraInfo.class, cameraInfoList, null);
    }

    @Override
    public List<CameraInfo> findAllWithoutAuthorize() {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e");
        String sql = "SELECT * FROM " + cameraSql;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class));
    }
    
    /**
     * 根据条件查询全区域摄像头数据
     */
    @Override
    public List<CameraInfo> queryALLCameraInfoByConditions(List<String> filterList) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String tableSql = SqlUtil.buildAllCameraTable(cameraFields, "a");
        String exeSql = SqlUtil.buildSelectSql(tableSql, cameraFields, filterList);
        return jdbcTemplate.query(exeSql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class));
    }
   
    @Override
    public List<CameraInfo> findByTaskId(long taskId){
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("*");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
         String sql = "SELECT camera.* FROM "+ cameraSql + "," + GlobalConsts.INTELLIF_BASE
                + "." + GlobalConsts.T_NAME_TASK_INFO + " task WHERE camera.id = task.source_id and task.id = "+taskId;
         return jdbcTemplate.query(sql, new BeanPropertyRowMapper<CameraInfo>(CameraInfo.class));
    }

    /**
     * 主要是根据stationId、queryBy、query、needGeoString这几个参数做模糊查询
     */
    @Override
    public Page<CameraDto> findAll(CameraQueryDto cameraQuery, Pageable pageable) {
        // 查询该用户所有能看到的摄像头列表
        List<CameraDto> allCameraDtoList = findAllCameraDto(null);
        if (null == allCameraDtoList) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        // 按照显示名称排序
        allCameraDtoList.sort((one, two) -> one.getDisplayName().compareTo(two.getDisplayName()));
        List<CameraDto> cameraList = null;
        // 如果查询条件为空，返回所有
        if (null == cameraQuery) {
            cameraList = allCameraDtoList;
        } else {
            cameraList = new ArrayList<>();
            // 分解query，提炼关键词
            List<String> filterList = new ArrayList<>();
            if (StringUtils.isNotBlank(cameraQuery.getQuery())) {
                String[] tempArray = cameraQuery.getQuery().trim().split(" ");
                for (String temp : tempArray) {
                    if (StringUtils.isNotBlank(temp)) {
                        filterList.add(temp.trim());
                    }
                }
            }
            // 是否需要检查区域ID
            Set<Long> idSet = cameraQuery.idSet();
            Class<? extends TreeNode> nodeClass = ZoneConfig.getNodeTypeMap().get(cameraQuery.getNodeType());
            boolean needCheckZone = !idSet.isEmpty() && null != nodeClass;
            // 是否需要检查 是否在侯问室
            boolean needCheckInStation = null != cameraQuery.getInStation() && cameraQuery.getInStation().longValue() >= 0L
                    && cameraQuery.getInStation().longValue() <= 1;
            UserInfo ui = CurUserInfoUtil.getUserInfo();
            String roleName = roleRepository.findById(ui.getRoleId()).getName();
            ui.setRoleTypeName(roleName);
            for (CameraDto camera : allCameraDtoList) {
                boolean isAccord = true;
                // 将名称或显示名称通过关键词过滤
                String nameTemp = null;
                try {
                    nameTemp = BeanUtils.getProperty(camera, cameraQuery.getQueryBy());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    LOG.error("catch exception, default query by displayName. ", e);
                    nameTemp = camera.getDisplayName();
                }
                if (!filterList.isEmpty()) {
                    for (String filter : filterList) {
                        if (!nameTemp.contains(filter)) {
                            isAccord = false;
                            break;
                        }
                    }
                }
                // 根据是否在侯问室过滤（给人脸检索右侧摄像头模糊搜索使用）
                if (isAccord && needCheckInStation && cameraQuery.getInStation().longValue() != camera.getInStation()) {
                    isAccord = false;
                }
                // 根据是否存在经纬度过滤 （给数据挖掘使用）
                if (isAccord && cameraQuery.getNeedGeoString() && StringUtils.isBlank(camera.getGeoString())) {
                    isAccord = false;
                }
                Tree tree = LocalCache.tree;
                TreeNode tn = tree.treeNodeWithOutTreeInfo(CameraInfo.class, camera.getId());
             
                if(!ZoneUtil.filterCamera(tn,ui)){
                    isAccord = false;
                }
                // 根据区域ID来过滤（给人脸检索右侧摄像头模糊搜索使用）
                if (isAccord && needCheckZone) {
                    isAccord = false;
                    for (Long id : idSet) {
                        if (zoneAuthorizeService.isOffspring(nodeClass, id, CameraInfo.class, camera.getId())) {
                            isAccord = true;
                            break;
                        }
                    }
                }
                if (isAccord) {
                    cameraList.add(camera);
                }
            }
            // 根据用户ID过滤，给修改用户的时候，选择摄像头范围使用
            if (0 != cameraQuery.getUserId()) {
                cameraList = zoneAuthorizeService.filterById(CameraInfo.class, cameraList, cameraQuery.getUserId());
            }
        }

        // 进行分页处理
        int offset = Math.min(pageable.getOffset(), cameraList.size());
        int end = Math.min(pageable.getOffset() + pageable.getPageSize(), cameraList.size());

        if (offset >= cameraList.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, cameraList.size());
        }
        return new PageImpl<>(cameraList.subList(offset, end), pageable, cameraList.size());
    }

    
  
    @Override
    @Transactional
    public CameraInfo save(CameraInfo cameraInfo) {
        Area area = areaService.findById(cameraInfo.getStationId());
        _userService.isUserOperationAccess(area.getUserId());      
        
        String filterSql = "name = '"+cameraInfo.getName()+"' and station_id = "+cameraInfo.getStationId();
        List<CameraInfo> aList = super.findByFilter(filterSql);
        if(!CollectionUtils.isEmpty(aList)){
            throw new MsgException("设备名已存在!",RequestConsts.response_dataresult_error);
        }
        Validate.notNull(cameraInfo, "参数为空!");
        if (0 != cameraInfo.getId()) {
            zoneAuthorizeService.checkIds(CameraInfo.class, cameraInfo.getId());
        }
      
        return (CameraInfo) super.save(cameraInfo);
    }

    @Override
    public void delete(long id) {
        CameraInfo camera = super.findById(id);
        Area area = areaService.findById(camera.getStationId());
       _userService.isUserOperationAccess(area.getUserId());
        super.delete(id);
    }

    @Override
    public CameraInfo update(CameraInfo t) {
        Area area = areaService.findById(t.getStationId());
        _userService.isUserOperationAccess(area.getUserId());
        
        String filterSql = "id != "+t.getId()+" and name = '"+t.getName()+"' and station_id = "+t.getStationId();
        List<CameraInfo> aList = super.findByFilter(filterSql);
        if(!CollectionUtils.isEmpty(aList)){
            throw new MsgException("设备名已存在!",RequestConsts.response_dataresult_error);
        }
        return super.update(t);
    }

    // @Override
    public List<CameraInfo> authorizeQuery(CameraQueryDto cameraQuery) {
        List<CameraInfo> cameraList = findAll();
        // 判断是否需要按照条件过滤
        if (null == cameraQuery || (StringUtils.isBlank(cameraQuery.getQuery()) && 0 == cameraQuery.getUserId())) {
            return cameraList;
        }
        // 根据输入名称进行过滤
        List<CameraInfo> retListTemp = new ArrayList<>();
        if (StringUtils.isNotBlank(cameraQuery.getQuery())) {
            String[] tempArray = cameraQuery.getQuery().split(" ");
            List<String> filterList = new ArrayList<>();
            for (String temp : tempArray) {
                if (StringUtils.isNotBlank(temp)) {
                    filterList.add(temp.trim());
                }
            }
            for (CameraInfo camera : cameraList) {
                boolean isAccord = true;
                String name = camera.getName();
                for (String filter : filterList) {
                    if (!name.contains(filter)) {
                        isAccord = false;
                        break;
                    }
                }
                if (isAccord) {
                    retListTemp.add(camera);
                }
            }
        } else {
            retListTemp.addAll(cameraList);
        }
        if (0 != cameraQuery.getUserId()) {
            retListTemp = zoneAuthorizeService.filterById(CameraInfo.class, retListTemp, cameraQuery.getUserId());
        }

        return retListTemp;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addPersonToCameraAreaIds(long id, List<Long> idsList,Class clazz) {
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        List<Object> cList = new ArrayList<Object>();
        if(!CollectionUtils.isEmpty(idsList)&&!CollectionUtils.isEmpty(blackList)){
        for (Long cId : idsList) {
            for (BlackDetail black : blackList) {
                if(CameraAndBlackDetail.class.getSimpleName().equals(clazz.getSimpleName())){
                    cList.add( new CameraAndBlackDetail(cId, black.getId()));
                }else if(AreaAndBlackDetail.class.getSimpleName().equals(clazz.getSimpleName())){
                    cList.add( new AreaAndBlackDetail(cId, black.getId()));
                }
                }
            }
        BatchInsertDto bid = new BatchInsertDto(cList);
        jdbcTemplate.batchUpdate(bid.getInsertSql(),bid.getInsertSetter());
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void delPersonFromCameraAreaIds(long id, List<Long> idsList,Class clazz) {
        List<BlackDetail> blackList = _blackDetailDao.findByFromPersonId(id);
        String table = null;
        String field = null;
        if(!CollectionUtils.isEmpty(idsList) && !CollectionUtils.isEmpty(blackList)){
            if(CameraAndBlackDetail.class.getSimpleName().equals(clazz.getSimpleName())){
                table = GlobalConsts.T_NAME_CAMERA_BLACKDETAIL;
                field = "camera_id";
            }else if(AreaAndBlackDetail.class.getSimpleName().equals(clazz.getSimpleName())){
                table = GlobalConsts.T_NAME_AREA_BLACKDETAIL;
                field = "area_id";
            }
            
            String fSql = "delete from "+GlobalConsts.INTELLIF_BASE+"."+table+" where 1 = 1";
            StringBuffer sb = new StringBuffer();
            idsList.forEach(cId ->{
                sb.append(",");
                sb.append(String.valueOf(cId));
            });
            String cSql = " and "+field+" in("+sb.toString().substring(1)+")";
            
            blackList.forEach(bd ->{
                String exeSql = fSql + cSql +" and blackdetail_id = "+bd.getId();
                jdbcTemplate.execute(exeSql);
            });
        }
    }

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return cameraInfoDao;
    }

    @Override
    public PageDto<CameraInfo> queryUserCamerasByParams(CameraQueryDto cqd) {

        int page = cqd.getPage();
        if(page <= 0){
            page = 1;
        }
        int pageSize = cqd.getPageSize();
        StringBuffer sb = new StringBuffer();
        String selectSql = "select a.area_name,c.* from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_CAMERA_INFO
                           +" c left join "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA
                           +" a on a.id = c.station_id ";
        String countSql =  "select count(1) from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_CAMERA_INFO
                           +" c left join "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA
                           +" a on a.id = c.station_id ";
        sb.append(" where 1 = 1 ");
        if(StringUtils.isNotBlank(cqd.getAreaIds())){
            sb.append(" and c.station_id  in("+cqd.getAreaIds()+")");
        }
        if(StringUtils.isNotBlank(cqd.getIds())){
            sb.append(" and c.id  in("+cqd.getIds()+")");
        }
        if(StringUtils.isNotBlank(cqd.getSearchName())){
            sb.append(" and (a.area_name like '%"+cqd.getSearchName()
                    +"%' or c.addr like '%"+cqd.getSearchName()+"%'"
                     + "or c.name like '%"+cqd.getSearchName()+"%') ");
        }
        String fSql = sb.toString();
        String limitSql = " order by c.created desc limit "+ (page-1)*pageSize+","+pageSize;
       // List<String> resultList = super.findObjectBySql(fSql);
        List<CameraInfo> resultList =  cameraInfoDao.findObjectBySql(selectSql+fSql+limitSql);
        Long count = super.countBySql(countSql+fSql);
        PageDto<CameraInfo> pageDto = new PageDto<CameraInfo>(resultList, count, page,pageSize);
        return pageDto;
    
    }
}
