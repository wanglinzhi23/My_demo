package intellif.lire;

import intellif.dao.CameraInfoDao;
import intellif.dao.OtherCameraDao;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherCameraInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CameraInfoThread extends Thread {

    private static Logger LOG = LogManager.getLogger(CameraInfoThread.class);
    public static ConcurrentHashMap<Long,CameraInfo> cameraMap = new ConcurrentHashMap<Long,CameraInfo>();

    @Autowired
    private CameraInfoDao _cameraInfoDao;
    @Autowired
    private OtherCameraDao _otherCameraDao;
    
    //@Scheduled(cron = "0 0 0 * * ?")
    public void run() {
        initCamera();
    }
    private void initCamera(){
        LOG.info("init camera start");
        ConcurrentHashMap<Long,CameraInfo> cMap = new  ConcurrentHashMap<Long,CameraInfo>();
        List<CameraInfo> cameraList = (List<CameraInfo>) _cameraInfoDao.findAll();
        List<OtherCameraInfo> otherList = (List<OtherCameraInfo>) _otherCameraDao.findAll();
       if(null != cameraList && !cameraList.isEmpty()){
           for(CameraInfo ci : cameraList){
               cMap.put(ci.getId(), ci);
           }
       
        }
       if(null != otherList && !otherList.isEmpty()){
           for(OtherCameraInfo oci : otherList){
              CameraInfo cii = new CameraInfo(oci);
              cMap.put(cii.getId(), cii);
           }
       
        }
        
       cameraMap = cMap;
       LOG.info("init camera end");
    }
}
