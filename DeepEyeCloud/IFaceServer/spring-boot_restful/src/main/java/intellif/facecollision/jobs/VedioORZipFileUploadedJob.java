package intellif.facecollision.jobs;

import java.util.List;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.dao.UploadedFileDao;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.UploadFileServiceItf;
import intellif.database.entity.UploadedFile;

/**
 * 
 * @author shijt
 *
 */
@Component
public class VedioORZipFileUploadedJob {
    
    @Autowired
    private UploadedFileDao uploadfileRepository;
    @Autowired
    private UploadFileServiceItf uploadedFileService;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @Scheduled(fixedDelay = 5 * 1000)
    public void updatedFileUploadStatus() {
        IFaaServiceThriftClient client = null;
        try {
            client = iFaceSdkServiceItf.getCenterServer();
        } catch (TException e) {
            e.printStackTrace();
        }
        if(client!=null){
            List<UploadedFile> uploadedFiles=uploadfileRepository.findUnCreatedTaskFile();
            for(UploadedFile vo:uploadedFiles){
                uploadedFileService.createTaskStatus(vo);
            }
        }
        
    }
}