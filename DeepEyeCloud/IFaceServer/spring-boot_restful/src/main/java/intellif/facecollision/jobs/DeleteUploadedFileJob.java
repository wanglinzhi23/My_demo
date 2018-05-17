package intellif.facecollision.jobs;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.dao.UploadedFileDao;
import intellif.dao.UploadedStatusDao;
import intellif.settings.ResumableJsUploadSetting;
import intellif.database.entity.ResumableInfoStorage;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;

/**
 * 
 * @author shijt
 *
 */
@Component
public class DeleteUploadedFileJob {
    
    @Autowired
    private UploadedStatusDao uploadedStatusRepository;
    @Autowired
    private UploadedFileDao uploadfileRepository;
    
    private static Logger LOG = LogManager.getLogger(DeleteUploadedFileJob.class);
    ResumableInfoStorage storage=ResumableInfoStorage.getInstance();
 

    @Scheduled(cron = "0 5 0 * * ?")
    public void updatedFileUploadStatus() {
        LOG.info(" delete file start......");
        List<UploadedStatus> statusList=uploadedStatusRepository.findAllDeletedUploadedStatus();
        List<UploadedFile>   fileList=uploadfileRepository.findAllDeletedUploadedFile();
        HashMap<String, UploadedStatus> mHashMap=ResumableInfoStorage.getInstance().getmMap();
        Iterator<Map.Entry<String, UploadedStatus>> iterator=mHashMap.entrySet().iterator();
        
        for(UploadedStatus status:statusList){
            while (iterator.hasNext()) {
                Map.Entry<String, UploadedStatus> entry=iterator.next();
                UploadedStatus uploadedStatus=entry.getValue();
                if(uploadedStatus.getId()==status.getId())
                    iterator.remove();
             }
            File deleteFile=new File(status.getResumableFilePath());
            if(deleteFile.exists()){
                deleteFile.delete();
            }else{
                String new_path=deleteFile.getAbsolutePath();
                String fileSubffix=new_path.substring(new_path.lastIndexOf("."));
                if(".temp".equals(fileSubffix)){
                    new_path =new_path.substring(0, new_path.length() - ".temp".length());
                    deleteFile=new File(new_path);
                    deleteFile.delete();
                }
            }
        }
        for(UploadedFile file:fileList){
            String uri=file.getFileUrl();
            uri=ResumableJsUploadSetting.getLocalPath()+uri.substring(ResumableJsUploadSetting.getRemotePath().length());
            File delFile=new File(uri);
            if(delFile.exists())
                delFile.delete();
        }
        uploadedStatusRepository.delete(statusList);
        LOG.info(" delete uploadedFile and uploadedStatus success!");
    }

}