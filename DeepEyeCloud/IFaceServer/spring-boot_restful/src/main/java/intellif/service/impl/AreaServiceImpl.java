package intellif.service.impl;

import intellif.chd.settings.CameraNodeIdSetting;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.dao.AreaAndBlackDetailDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.database.dao.AreaDao;
import intellif.database.dao.BlackBankDao;
import intellif.database.dao.CameraInfoDao;
import intellif.database.dao.CommonDao;
import intellif.database.dao.RoleDao;
import intellif.database.dao.UserAreaDao;
import intellif.database.dao.UserDao;
import intellif.database.dao.impl.AreaDaoImpl;
import intellif.database.dao.impl.UserDaoImpl;
import intellif.database.entity.Area;
import intellif.database.entity.BlackBank;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserArea;
import intellif.database.entity.UserInfo;
import intellif.dto.CommonQueryDto;
import intellif.dto.JsonObject;
import intellif.dto.PersonFullDto;
import intellif.exception.MsgException;
import intellif.service.AreaServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.utils.SqlUtil;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.code.ssm.api.InvalidateAssignCache;
import com.google.code.ssm.api.InvalidateSingleCache;

@Service
public class AreaServiceImpl extends AbstractCommonServiceImpl<Area> implements AreaServiceItf<Area> {

    private static Logger LOG = LogManager.getLogger(AreaServiceImpl.class);
    
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
    private BlackBankDao bankDao;
    @Autowired
    private UserDaoImpl userDao;
    @Autowired
    private AreaDaoImpl areaDao;
    @Autowired
    private UserAreaDao userAreaDao;
    @Autowired
    private CameraInfoDao cameraInfoDao;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private RoleDao roleDao;
    /**
     * 根据条件查询全区域数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Area> queryALLAreaInfoByConditions(List<String> filterList) {
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("*");
        String tableSql = SqlUtil.buildAllCameraTable(areaFields, "a");
        String exeSql = SqlUtil.buildSelectSql(tableSql, areaFields, filterList);
        return areaDao.findObjectBySql(exeSql);
    }

    @Override
    public Area save(Area t) {
        //创建门店时自动授权给操作用户
        long userId = t.getUserId();
        UserInfo ui = CurUserInfoUtil.getUserInfo();
        if(0 == userId){
            userId = ui.getId();
        }
         _userService.isUserOperationAccess(userId);   
     
        String filterSql = "area_name = '"+t.getName()+"'";
        List<Area> aList = super.findByFilter(filterSql);
        if(!CollectionUtils.isEmpty(aList)){
            throw new MsgException("店名已存在!",RequestConsts.response_dataresult_error);
        }
       
        UserInfo user = userDao.findById(userId);
        
        t.setUserId(userId);
        Area area = super.save(t);
        UserArea ua = new UserArea();
        ua.setAreaId(area.getId());
        ua.setUserId(userId);
        userAreaDao.save(ua);
        BlackBank bb = new BlackBank();
        bb.setBankName(area.getAreaName()+"-库");
        bb.setStationId(area.getId());
        bb.setCreateUser(user.getName());
        bankDao.save(bb);
        return area;
    }

    @Override
    @Transactional
    public void delete(long id) {
        Area area = super.findById(id);
       _userService.isUserOperationAccess(area.getUserId());   
        super.delete(id);
        String fSql = "area_id = "+ id;
        userAreaDao.deleteByFilter(fSql);
        String fSql1 = "station_id = "+ id;
        bankDao.deleteByFilter(fSql1);
        cameraInfoDao.deleteByFilter(fSql1);
        
    }

    @Override
    public Area update(Area t) {
        Area area = areaDao.findById(t.getId());
        _userService.isUserOperationAccess(area.getUserId());   
        String filterSql = "id != " + t.getId() + " and area_name = '"+t.getName()+"'";
        List<Area> aList = super.findByFilter(filterSql);
        if(!CollectionUtils.isEmpty(aList)){
            throw new MsgException("店名已存在!",RequestConsts.response_dataresult_error);
        }
        return super.update(t);
    }

    @Override
    public Area findById(long id) {
        Area area = super.findById(id);
        String filterSql = "station_id = "+id;
        Long count = cameraInfoDao.countByFilter(filterSql);
        area.setCountLeaf(count.intValue());
        return area;
    }

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return areaDao;
    }
   /**
    * 条件分页查询Area数据
   */
    @SuppressWarnings("unchecked")
    @Override
    public PageDto<Area> queryUserAreasByParams(CommonQueryDto cpd) {
        int page = cpd.getPage();
        if(page <= 0){
            page = 1;
        }
        int pageSize = cpd.getPageSize();
        String selSql = "select a.*,count(c.id) as count_leaf from "
                     +GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA
                     + " a LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_CAMERA_INFO
                     +" c on a.id = c.station_id where ";
        String countSql = "select count(1) from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA+" a where ";
        StringBuffer sb = new StringBuffer();
        sb.append(" 1 = 1 ");
     
        if(StringUtils.isNotBlank(cpd.getIds())){
            sb.append(" and a.id in("+cpd.getIds()+")");
        }
        if(StringUtils.isNotBlank(cpd.getSearchName())){
            sb.append(" and (a.area_name like '%"+cpd.getSearchName()+"%' or a.geo_string like '%"+cpd.getSearchName()+"%') ");
        }
        
        String fSql = sb.toString();
        String limitSql = " group by a.id order by a.created desc limit "+ (page-1)*pageSize+","+pageSize;
        List<Area> resultList = super.findObjectBySql(selSql+fSql+limitSql);
        Long count = super.countBySql(countSql+fSql);
        PageDto<Area> pageDto = new PageDto<Area>(resultList, count, page,pageSize);
        return pageDto;
    }
}
