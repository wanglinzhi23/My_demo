package intellif.database.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.database.dao.AlarmProcessDao;
import intellif.database.entity.AlarmProcess;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.dto.AlarmProcessDetail;

import javax.persistence.Query;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
@Service
public class AlarmProcessDaoImpl extends AbstractCommonDaoImpl<AlarmProcess> implements AlarmProcessDao<AlarmProcess>{
  
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    FaceInfoDaoImpl faceInfoDao;
    @Autowired
    ImageInfoDaoImpl imageInfoDao;
    @Override
    public Class<AlarmProcess> getEntityClass() {
        // TODO Auto-generated method stub
        return AlarmProcess.class;
    }

    @Override
    public String getEntityTable() {
        Table table = AlarmProcess.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }

   /**
    * 条件查询已处理报警
    * @param cIds
    * @param page
    * @param pageSize
    * @return
    */
    public List<AlarmProcessDetail> findProcessedAlarmByParams(String cIds, int page,
            int pageSize) {
        List<AlarmProcessDetail> resp = null;
        String sql = "select ap.*, b.image_data AS black_smallurl, 0 AS black_bigurl,b.from_image_id as black_image_id,p.description,p.real_name as b_name,c.name AS camera_name,c.addr as address "              
     +"FROM (SELECT a.face_id, 0 as alarm_smallurl, 0 as alarm_bigurl,a.task_id,"
                +"a.black_id,a.confidence as threshold,a.time as created,p.id,p.alarm_id,p.type,p.created as process_time,p.user_id "
        +"FROM t_alarm_info a INNER JOIN t_alarm_process p ON a.id = p.alarm_id "
        +"WHERE  a.status  in(1,2) and p.type in(1,2) group by p.alarm_id ORDER BY p.created DESC)ap "
        +"INNER JOIN t_black_detail b ON b.id = ap.black_id "
        +"INNER JOIN t_person_detail p ON p.id = b.from_person_id "
        +"INNER JOIN t_task_info t ON t.id = ap.task_id "
        +"INNER JOIN t_camera_info c ON c.id = t.source_id where c.id in ("+cIds+") ORDER BY ap.process_time desc LIMIT "+(page - 1) * pageSize+","+pageSize;
        
        Query query = this.entityManager.createNativeQuery(sql, AlarmProcessDetail.class);
        resp = (ArrayList<AlarmProcessDetail>) query.getResultList();
        //根据alarmFaceId和blackImageId查询对应face table url路径
        updateFaceInfoToAlarmProcessDetail(resp);   
        return resp;
    }
    
    /**
     * 获取alarm对应图片信息
     * @param alarmList
     */
    private void updateFaceInfoToAlarmProcessDetail(List<AlarmProcessDetail> alarmList) {
        if (null != alarmList && !alarmList.isEmpty()) {
            Map<Long, List<AlarmProcessDetail>> alarmMap = new HashMap<Long, List<AlarmProcessDetail>>();
            Map<Long, FaceInfo> faceMap = new HashMap<Long, FaceInfo>();
            Map<Long, List<AlarmProcessDetail>> blackAlarmMap = new HashMap<Long, List<AlarmProcessDetail>>();
            List<Long> faceIdList = new ArrayList<Long>();
            List<Long> imageIdList = new ArrayList<Long>();
            List<Long> blackIdList = new ArrayList<Long>();
            for (AlarmProcessDetail alarm : alarmList) {
                faceIdList.add(alarm.getFaceId());
                blackIdList.add(alarm.getBlackImageId());

                List<AlarmProcessDetail> fAlarmList = alarmMap.get(alarm
                        .getFaceId());
                if (null == fAlarmList) {
                    fAlarmList = new ArrayList<AlarmProcessDetail>();
                    alarmMap.put(alarm.getFaceId(), fAlarmList);
                }
                fAlarmList.add(alarm);

                List<AlarmProcessDetail> bAlarmList = blackAlarmMap.get(alarm
                        .getFaceId());
                if (null == bAlarmList) {
                    bAlarmList = new ArrayList<AlarmProcessDetail>();
                    blackAlarmMap.put(alarm.getBlackImageId(), bAlarmList);
                }
                bAlarmList.add(alarm);

            }

            List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
            if (null != faceList && !faceList.isEmpty()) {
                for (FaceInfo face : faceList) {
                    long faceId = face.getId();
                    List<AlarmProcessDetail> fAlarmList = alarmMap.get(faceId);
                    for (AlarmProcessDetail item : fAlarmList) {
                        item.setAlarmSmallurl(face.getImageData());
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

                        List<AlarmProcessDetail> fAlarmList = alarmMap.get(faceId);
                        for (AlarmProcessDetail item : fAlarmList) {
                            item.setAlarmBigurl(image.getUri());
                        }
                    }
                }
            }

            List<ImageInfo> blackImageList = imageInfoDao
                    .findByIds(blackIdList);
            if (null != blackImageList && !blackImageList.isEmpty()) {
                for (ImageInfo image : blackImageList) {
                    long imageId = image.getId();
                    List<AlarmProcessDetail> fAlarmList = blackAlarmMap.get(imageId);
                    for (AlarmProcessDetail item : fAlarmList) {
                        item.setBlackBigurl(image.getUri());
                    }
                }
            }
        }
    }

}
