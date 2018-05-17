package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.FaceFilterTypeDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.database.entity.AuditLogInfo;
import intellif.dto.BatchUpdateFilteredFaceDto;
import intellif.dto.FilteredFaceQueryDto;
import intellif.exception.MsgException;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EFakeFilterUpdateType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.FaceFilterServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.database.entity.FaceFilterType;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.FilteredFaceInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Zheng Xiaodong
 */
@Service
public class FaceFilterServiceImpl implements FaceFilterServiceItf {
    private static Logger LOG = LogManager.getLogger(FaceFilterServiceImpl.class);

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private FaceFilterTypeDao faceFilterTypeDao;

    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @Autowired
    private AuditLogDao logDao;

    @Autowired
    private FaceInfoDaoImpl faceInfoDao;

    @Override
    public List<FaceFilterType> getFilterTypes() {
        return faceFilterTypeDao.findAll();
    }

    @Override
    @Transactional
    public void addFilteredFace(Long faceId, Long typeId) throws MsgException {
        FaceInfo faceInfo = faceService.findOne(faceId);
        String addSql = "insert into " + GlobalConsts.INTELLIF_FACE + "."
                + GlobalConsts.T_NAME_FACE_FILTERED + " (id,accessories,race,age,face_feature,from_image_id,"
                +"from_person_id,from_video_id,gender,image_data,indexed,source_id,"
                +"source_type,time,version,json,sequence,quality, filter_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String checkDuplicateSql = "select * from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_FILTERED
                + " where id = ? ";

        if (faceInfo == null) {
            throw new MsgException("指定的人脸不存在");
        }

        List<FilteredFaceInfo> existed = jdbcTemplate.query(checkDuplicateSql,
                new Long[]{faceId}, new BeanPropertyRowMapper<>(FilteredFaceInfo.class));
        if (CollectionUtils.isNotEmpty(existed))
            throw new MsgException("该人脸已经添加");

        jdbcTemplate.update(addSql, faceInfo.getId(), faceInfo.getAccessories(), faceInfo.getRace(), faceInfo.getAge(),
                faceInfo.getFaceFeature(), faceInfo.getFromImageId(), faceInfo.getFromPersonId(), faceInfo.getFromVideoId(),
                faceInfo.getGender(), faceInfo.getImageData(), faceInfo.getIndexed(), faceInfo.getSourceId(), faceInfo.getSourceType(),
                faceInfo.getTime(), faceInfo.getVersion(), faceInfo.getJson(), faceInfo.getSequence(), faceInfo.getQuality(), typeId);

        logAddFilteredFace(faceInfo, typeId);
    }

    @Override
    public PageDto<FilteredFaceInfo> searchFilteredFace(FilteredFaceQueryDto filteredFaceQueryDto) {
        String sql = "select id, accessories, race, age, face_feature, from_image_id, "
                + " from_person_id, from_video_id, gender, image_data, indexed, source_id, source_type, "
                + " time, version, json, sequence, quality, filter_type from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_FILTERED
                + " f where 1 = 1 ";
        String countSql = "select count(*) from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_FILTERED
                + " f where 1 = 1";
        List<Object> args = new ArrayList<>();
        int page = filteredFaceQueryDto.getPage();
        int pageSize = filteredFaceQueryDto.getPageSize();

        if (page <= 0)
            page = DEFAULT_PAGE;
        if (pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;

        if (filteredFaceQueryDto.getTypeId() != null) {
            sql += " and f.filter_type = ? ";
            countSql += " and f.filter_type = ? ";
            args.add(filteredFaceQueryDto.getTypeId());
        }
        sql += " limit " + (page - 1) * pageSize + ", " + pageSize;

        List<FilteredFaceInfo> data = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FilteredFaceInfo.class), args.toArray());
        Long count = jdbcTemplate.queryForObject(countSql, Long.class, args.toArray());
        PageDto<FilteredFaceInfo> pageDto = new PageDto<>(data, count,
                filteredFaceQueryDto.getPage(), filteredFaceQueryDto.getPageSize());
        return pageDto;
    }

    @Override
    @Transactional
    public void batchUpdate(BatchUpdateFilteredFaceDto updateDto) {
        String updateSql = "update " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_FILTERED
                + " f set filter_type = ? where id in ";
        String faceIdStr = "";
        String[] faceIdArr;

        if (StringUtils.isBlank(updateDto.getFaceIds())) {
            throw new MsgException("请指定要更新的人脸");
        }
        faceIdArr = updateDto.getFaceIds().split(",");

        for (int i = 0; i < faceIdArr.length; i++) {
            String faceId = faceIdArr[i];
            faceIdStr += faceId;
            if (i != faceIdArr.length - 1)
                faceIdStr += ",";
        }
        updateSql += "(" + faceIdStr + ")";
        jdbcTemplate.update(updateSql, new Long[] {updateDto.getTypeId()});

    }

    @Override
    @Transactional
    public void delete(BatchUpdateFilteredFaceDto updateDto) {
        String sql = "delete from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_FILTERED
                + " where id in ";
        String faceIdStr = "";
        String[] faceIdArr;

        if (StringUtils.isBlank(updateDto.getFaceIds())) {
            throw new MsgException("请指定要删除的人脸");
        }
        faceIdArr = updateDto.getFaceIds().split(",");

        for (int i = 0; i < faceIdArr.length; i++) {
            String faceId = faceIdArr[i];
            faceIdStr += faceId;
            if (i != faceIdArr.length - 1)
                faceIdStr += ",";
        }
        sql += "(" + faceIdStr + ")";
        jdbcTemplate.update(sql);

        logDeleteFilteredFace(updateDto);
    }

    @Override
    public void noticeEnginceOneFace(Long faceId) {
        try {
            List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
                    .getAllTarget();
            for (IFaceSdkTarget target : targetList) {
                target.iface_engine_ioctrl(
                        EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL
                                .getValue(),
                        EParamIoctrlType.PARAM_IOCTRL_FILTER_UPDATE.getValue(),
                        EFakeFilterUpdateType.FILTER_UPDATE_FACE.getValue(), faceId, 0);
            }
        } catch (Exception e) {
            LOG.error("notice c++ filter face error", e);
        }
    }

    @Override
    public void noticeEnginceAllFace() {
        try {
            List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
                    .getAllTarget();
            for (IFaceSdkTarget target : targetList) {
                target.iface_engine_ioctrl(
                        EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL
                                .getValue(),
                        EParamIoctrlType.PARAM_IOCTRL_FILTER_UPDATE.getValue(),
                        EFakeFilterUpdateType.FILTER_UPDATE_ALL.getValue(), 0, 0);
            }
        } catch (Exception e) {
            LOG.error("notice c++ filter face error", e);
        }
    }

    private void logAddFilteredFace(FaceInfo faceInfo, long typeId) {
        FaceFilterType filterType = faceFilterTypeDao.findOne(typeId);
        AuditLogInfo log = new AuditLogInfo();
        log.setMessage("添加非人脸图片过滤， 过滤类型为:" + filterType.getName() + "，" + faceInfo.getImageData());
        log.setObject(GlobalConsts.T_NAME_FACE_FILTERED);
        log.setObjectId(faceInfo.getId());
        log.setObject_status(12);
        log.setTitle("已添加非人脸图片过滤");
        log.setOperation("add filtered face");
        log.setOwner(CurUserInfoUtil.getUserInfo().getLogin());
        logDao.save(log);
    }

    private void logDeleteFilteredFace(BatchUpdateFilteredFaceDto updateDto) {
        String faceIds = updateDto.getFaceIds();
        String[] faceIdArr = faceIds.split(",");
        List<Long> faceIdList = new ArrayList<>();
        String faceImgUrls = "";

        for (int i = 0; i < faceIdArr.length; i++) {
            faceIdList.add(Long.valueOf(faceIdArr[i]));
        }
        List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
        if (CollectionUtils.isNotEmpty(faceIdList)) {
            for (int j = 0; j < faceList.size(); j++) {
                faceImgUrls += faceList.get(j).getImageData();
                if (j < faceList.size() - 1)
                    faceImgUrls += ";";
            }
        }

        AuditLogInfo log = new AuditLogInfo();
        log.setMessage("删除非人脸图片过滤，" + faceImgUrls);
        log.setObject(GlobalConsts.T_NAME_FACE_FILTERED);
        log.setObject_status(12);
        log.setTitle("已删除非人脸图片过滤");
        log.setOperation("delete filtered face");
        log.setOwner(CurUserInfoUtil.getUserInfo().getLogin());
        logDao.save(log);
    }
}
