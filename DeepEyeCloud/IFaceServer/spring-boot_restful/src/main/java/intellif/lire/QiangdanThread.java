package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.PersonRedDao;
import intellif.dao.RedDetailDao;
import intellif.dto.QiangdanDto;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.settings.XinYiSettings;
import intellif.utils.DateUtil;
import intellif.utils.HttpUtil;
import intellif.utils.JinxinUtil;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.QiangdanRecord;
import intellif.database.entity.RedDetail;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class QiangdanThread extends Thread {

    private static Logger LOG = LogManager.getLogger(QiangdanThread.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedDetailDao redDetailDao;
    @Autowired
    private PersonRedDao redPersonDao;
    @Autowired
    private ImageServiceItf imageService;
    @Autowired
    private FaceServiceItf faceService;
  
    //@Scheduled(fixedRate = 2000)
    public void run() {
        try{
            String sql = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_QIANGDAN_RECORD + " q WHERE q.send = 0 limit 0,100";
            List<QiangdanRecord> cameraInfoList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<QiangdanRecord>(QiangdanRecord.class));
            List<Long> idList = new ArrayList<Long>();
            if(!CollectionUtils.isEmpty(cameraInfoList)){
                for(QiangdanRecord item : cameraInfoList){
                    try{
                        long rId = item.getRedId();
                        RedDetail rd = redDetailDao.findOne(rId);
                        String phone = redPersonDao.findOne(rd.getFromPersonId()).getPolicePhone();
                        FaceInfo fi = faceService.findOne(item.getFaceId());
                        String faceUrl = fi.getImageData();
                        ImageInfo ii = imageService.findById(fi.getFromImageId());
                        String imageUrl = ii.getUri();
                        String dateStr = DateUtil.getformatDate(item.getTime());
                        QiangdanDto dto = new QiangdanDto(phone, String.valueOf(item.getSourceId()), faceUrl, imageUrl, dateStr);
                        JSONObject ss =JSONObject.fromObject(dto);
                        JSONObject oo = new JSONObject();
                        oo.put("param", ss.toString());
                        String result = JinxinUtil.caller(XinYiSettings.getQiangdanUrl(), oo.toString());
                        LOG.info("qiangdan send result:"+result+" record id:"+item.getId());
                        idList.add(item.getId());
                    }catch(Exception e){
                        LOG.error("dangyao record error,id:"+item.getId()+",error:",e);
                    }
                   
                }
            }
            
            if(!CollectionUtils.isEmpty(idList)){
                String idStr = StringUtils.join(idList, ",");
                String uSql = "update "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_QIANGDAN_RECORD+" set send = 1 where id in("+idStr+")"; 
                jdbcTemplate.execute(uSql);
            }
    }catch(Exception e){
        LOG.error("QiangdanThread task error:",e);
    }
    }
}
