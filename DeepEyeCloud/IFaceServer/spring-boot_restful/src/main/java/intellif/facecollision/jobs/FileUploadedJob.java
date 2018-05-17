package intellif.facecollision.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedStatusDao;
import intellif.service.UploadFileServiceItf;
import intellif.settings.ResumableJsUploadSetting;
import intellif.utils.ApplicationResource;
import intellif.database.entity.ResumableInfoStorage;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;

/**
 * 
 * @author shijt
 *
 */
@Component
public class FileUploadedJob {
    
    @Autowired
    private UploadedStatusDao uploadedStatusRepository;
    @Autowired
    private UploadFileServiceItf uploadedFileService;
    
    private static Logger LOG = LogManager.getLogger(FileUploadedJob.class);
    ResumableInfoStorage storage=ResumableInfoStorage.getInstance();
 

    @Scheduled(fixedDelay = 4 * 1000)
    public void updatedFileUploadStatus() {
        List<UploadedFile> uploadedFiles=uploadedFileService.findAllUploadedButNotCreatedTaskFile();
        for(UploadedFile vo:uploadedFiles){
            int row=uploadedFileService.updateZipingStatus(vo.getId(),GlobalConsts.FILE_ZIPING_BY_OTHER_THREAD);
            if(row==1){
                ApplicationResource.THREAD_POOL.submit(()->zipStatus(vo));
            }else{
                LOG.error(" create zip thread error....");
            }
        }
    }

    private void zipStatus(UploadedFile uploadFile) {
        boolean zipResult=false;
        try {
            LOG.info("zip file id:"+uploadFile.getId()+" start......");
            String zipUri=ResumableJsUploadSetting.getLocalPath()+uploadFile.getFileUrl().substring(ResumableJsUploadSetting.getRemotePath().length());
            File zipFile=new File(zipUri);
            List<UploadedStatus> statusList=uploadedStatusRepository.findByFileId(uploadFile.getId());
            if(zipPics(zipFile,statusList)){
                LOG.info(" zip file id:"+uploadFile.getId()+" success......");
                zipResult=true;
                uploadFile.setFileSize(zipFile.length());
                uploadFile.setStatus(GlobalConsts.FILE_FINISHED);
            }else{
                LOG.info(" zip "+zipFile.getName()+" file failed....");
                uploadFile.setStatus(GlobalConsts.FILE_ZIP_FAILED);
                try {
                    storage.getFileQueue().put(uploadFile);
                } catch (InterruptedException ee) {
                    LOG.error("put file to queue error:",ee);
                }
            }
        } catch (Exception e) {
            LOG.error("zip file error:",e);
            uploadFile.setStatus(GlobalConsts.FILE_ZIP_FAILED);
            try {
                storage.getFileQueue().put(uploadFile);
            } catch (InterruptedException ee) {
                LOG.error("put file to queue error:",ee);
            }
        }
        if(zipResult){
            try {
                uploadedFileService.createTaskStatus(uploadFile);
            } catch (Exception e) {
                LOG.error("create extract task error:",e);
                try {
                    storage.getFileQueue().put(uploadFile);
                } catch (InterruptedException ee) {
                    LOG.error("put file to queue error:",ee);
                }
            }
        }
        
    }


    private boolean zipPics(File zipFile, List<UploadedStatus> statusList) {
        boolean result=true;
        try {
            if(!zipFile.exists())
                zipFile.createNewFile();
            FileOutputStream fOut=new FileOutputStream(zipFile);
            ZipOutputStream zipOut=new ZipOutputStream(fOut);
            try{
                for(UploadedStatus status:statusList){
                    File imgFile=new File(status.getResumableFilePath());
                    if(!imgFile.exists()){
                        for(int i=1;i<10;i++){
                            Thread.sleep(1000);
                            status=uploadedStatusRepository.findOne(status.getId());
                            imgFile=new File(status.getResumableFilePath());
                            if(imgFile.exists())break;
                        }
                    }
                    if(!imgFile.exists()){
                        String new_path=imgFile.getAbsolutePath();
                        String fileSubffix=new_path.substring(new_path.lastIndexOf("."));
                        if(".temp".equals(fileSubffix)){
                            new_path =new_path.substring(0, new_path.length() - ".temp".length());
                            imgFile=new File(new_path);
                        }
                        if(imgFile.exists()){
                            status.setResumableFilePath(new_path);
                            status.setProgress(100);
                            status.setIsFinished(GlobalConsts.IS_FINISHED_YES);
                            uploadedStatusRepository.save(status);
                        }
                    }
                    if(imgFile.exists()){
                        InputStream input = new FileInputStream(imgFile);
                        try{
                            ZipEntry zipEntry=new ZipEntry(imgFile.getName());
                            zipOut.putNextEntry(zipEntry);
                        }catch (Exception e) {
                            input.close();
                            LOG.error("create zipEntry error:",e);
                        }
                        int nNumber;
                        byte[] buffer = new byte[1024*1024];
                        while ((nNumber = input.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, nNumber);
                        }
                        input.close();
                    }else{
                        LOG.error("create zip "+zipFile.getName()+" uploadedStatus table ID is"+status.getId()+" image is not exists.");
                        result=false;
                        break;
                    }
                }
            }catch (Exception e) {
                result=false;
                LOG.error("writting to zip file error：",e);
            }finally {
                zipOut.close();
                fOut.close();
            }
        } catch (Exception e) {
            result=false;
            LOG.error("image zip error：",e);
        }
        return result;
    }
}