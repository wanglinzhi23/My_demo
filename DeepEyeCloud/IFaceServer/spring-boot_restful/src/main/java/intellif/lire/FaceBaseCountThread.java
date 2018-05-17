package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.FaceCameraCountDao;
import intellif.service.FaceServiceItf;
import intellif.utils.DateUtil;
import intellif.database.entity.FaceCameraCount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FaceBaseCountThread extends Thread {
public static void main(String[] args) {
    List<String> aaList = new ArrayList<String>();
    aaList.add("1");
    aaList.add("22");
    
    
   
}

    private static Logger LOG = LogManager.getLogger(FaceBaseCountThread.class);
    private final static String dateFormatYMD = GlobalConsts.YMD;
    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private FaceCameraCountDao faceCameraCountRepository;

    //@Scheduled(cron = "0 0 0 * * ?")
    public void totalCountRun() {
        GlobalConsts.faceDayCount = 0;
        GlobalConsts.faceMinCount = 0;
        GlobalConsts.faceBaseCount = this.faceService.count();
        LOG.info("count face num every day 0 clock, count:" + GlobalConsts.faceBaseCount);
    }

    //@Scheduled(fixedRate = 3600 * 1000)
    public void cameraCountRun() {

        if (GlobalConsts.run) {
            // 统计昨天各摄像头采集总数并缓存
            List<Object[]> statisticDayList = faceService.statisticYesterdayByCamera();
            if (null != statisticDayList && !statisticDayList.isEmpty()) {
                for (Object[] statistic : statisticDayList) {
                    FaceCameraCount count = new FaceCameraCount();
                    count.setSourceId(Long.valueOf(statistic[0].toString()));
                    count.setTime((Date) statistic[1]);
                    count.setCount(Long.valueOf(statistic[2].toString()));

                    List<FaceCameraCount> countList = faceCameraCountRepository.findBySourceIdAndTime(count.getSourceId(),
                            DateUtil.getDateString(count.getTime()));
                    if (null == countList || countList.isEmpty()) {
                        faceCameraCountRepository.save(count);
                    }
                }
            }
        }
    }

}
