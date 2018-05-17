package intellif.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedFileDao;
import intellif.dao.UploadedStatusDao;
import intellif.dto.JsonObject;
import intellif.dto.UploadedStatusDto;
import intellif.facecollision.dao.FaceExtractTaskDao;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.facecollision.vo.FaceExtractTask;
import intellif.service.ResumableJsServiceItf;
import intellif.settings.ResumableJsUploadSetting;
import intellif.utils.DateUtil;
import intellif.database.entity.ResumableInfoStorage;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;
import intellif.database.entity.UserInfo;

@Service
public class ResumableJsServiceImpl implements ResumableJsServiceItf {
    
    @Autowired
    private UploadedStatusDao uploadedStatusRepository;
    @Autowired
    private UploadedFileDao uploadfileRepository;
    @Autowired
    private FaceExtractServiceItf faceExtractService;
    @Autowired
    private FaceExtractTaskDao faceExtractTaskRepository;
    
    private static Logger LOG = LogManager.getLogger(ResumableJsServiceImpl.class);
    
    ResumableInfoStorage storage = ResumableInfoStorage.getInstance();
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public JsonObject checkifUploaded(HttpServletRequest request, HttpServletResponse response){
        UploadedStatusDto statusDto=getParamatersFromRequest(request);//获取参数
        if (!vaild(statusDto)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("文件参数错误！",1001);
        }
        
        UploadedStatus info = this.getResumableUploadedStatus(statusDto);
        if(info==null||info.getUploadFile()==null){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("获取上传信息失败，请删除文件重新上传！",1001);
        }
        
        //根据上传状态跳转到相应代码执行
        if(GlobalConsts.FILE_TASK_CREATED==info.getUploadFile().getStatus()){//任务创建已完成
           return new JsonObject(info.getUploadFile());
        }
       
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);//chunk 未上传
        return new JsonObject(info.getUploadFile());
        
    }

    @Override
    public JsonObject resumableUpload(HttpServletRequest request,HttpServletResponse response) {
        UploadedStatusDto statusDto=getParamatersFromRequest(request);
        if (!vaild(statusDto)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("文件参数错误！",1001);
        }
        UploadedStatus info = this.getResumableUploadedStatus(statusDto);
        if(info==null||info.getUploadFile()==null){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("获取上传信息失败，请删除文件重新上传！",1001);
        }
        
        //保存到文件中
        InputStream is = null;
        try {
            is = request.getInputStream();
        } catch (IOException e) {
            LOG.error("从requst获取输入流错误：",e);
        }
        long readed = 0;
        long content_length = request.getContentLength();
        byte[] bytes = new byte[1024 * 1024];
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(info.getResumableFilePath(), "rw");
        } catch (FileNotFoundException e) {
            IOUtils.closeQuietly(raf);
            IOUtils.closeQuietly(is);
            LOG.error(info.getResumableFilePath()+"RandomAccessFile创建错误：",e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("文件上传错误,请删除文件重新上传！",1001);
        }
        try {
            //寻找文件写入位置
            raf.seek((statusDto.getResumableChunkNumber() - 1) * (long)info.getResumableChunkSize());
            while(readed < content_length) {
                int r = is.read(bytes);
                if (r < 0)  {
                    break;
                }
                raf.write(bytes, 0, r);
                readed += r;
            }
            raf.close();
            is.close();
        } catch (Exception e) {
            IOUtils.closeQuietly(raf);
            IOUtils.closeQuietly(is);
            LOG.error("文件写入错误",e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new JsonObject("文件写入错误，请删除文件重新上传！",1001);
        }
        info.setUpdated(new Date());
        info.getUploadFile().setUpdated(new Date());
        //标记为已上传
        info.setUploadChunkSet(statusDto.getResumableChunkNumber()+"");
        //检查是否全部chunk已上传
        if (this.checkIfUploadFinished(info)) {
            UploadedFile uploadedFile=this.uploadFinished(info,response);
            if(response.getStatus()==HttpServletResponse.SC_OK){
                return new JsonObject(uploadedFile);
            }else{
                return new JsonObject("上传错误，请删除文件重新上传！",1001);
            }
         } else {
             return new JsonObject(this.uploading(info));
         }
}
    
    @Override
    public UploadedFile uploading(UploadedStatus info) {
         //上传未完成，没上传一定的chunk保存一次上传状态
        if(info.getUploadChunkSet().size()%GlobalConsts.SAVE_DATA_PER_CHUNKS==0){
            try {
                storage.getFileQueue().put(info.getUploadFile());
                storage.getStatusQueue().put(info);
            } catch (InterruptedException e) {
                LOG.error("put status and file to queue error:",e);
            }
        }
        return info.getUploadFile();
    }

    /**
     * 检查参数是否正确
     */
    private boolean vaild(UploadedStatusDto statusDto){
        if (statusDto.getResumableChunkSize() < 0 || statusDto.getResumableTotalSize() < 0||statusDto.getFileType()<0||StringUtils.isBlank(statusDto.getResumableIdentifier())
                ||StringUtils.isBlank(statusDto.getResumableFilename())||StringUtils.isBlank(statusDto.getResumableRelativePath())||StringUtils.isBlank(statusDto.getFileHash())
                ||(GlobalConsts.FILE_TYPE_IMG==statusDto.getFileType()&&(statusDto.getPicsCount()==null||statusDto.getPicsCount()<0||statusDto.getPicTotalSize()==null||StringUtils.isBlank(statusDto.getUploadIdentifier())))) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * 检查文件是否上传完成
     */
    @Override
    public synchronized boolean checkIfUploadFinished(UploadedStatus info) {
        int count = (int) Math.ceil(((double) info.getResumableTotalSize()) / ((double) info.getResumableChunkSize()));
        info.setProgress(info.getUploadChunkSet().size()*100/count);//上传进度
        if(info.getFileType()!=GlobalConsts.FILE_TYPE_IMG){
            info.getUploadFile().setProgress(info.getUploadChunkSet().size()*100/count);
        }else{
            UploadedFile uploadFile=info.getUploadFile();
            int uploadedCount=getUploadedImgCount(uploadFile.getId());
            uploadFile.setProgress(uploadedCount*100/uploadFile.getPicsCount());
        }
        if(info.getUploadChunkSet().size()==count)
            return true;
        return false;
    }
    
    private synchronized int getUploadedImgCount(Long fileId) {
        HashMap<String, UploadedStatus> mHashMap=storage.getmMap();
        int count=0;
        for(UploadedStatus value:mHashMap.values()){
            if(fileId.equals(value.getFileId())&&value.getIsFinished()==GlobalConsts.IS_FINISHED_YES)
                count++;
        }
        return count;
    }

    @Override
    public  UploadedFile uploadFinished(UploadedStatus info, HttpServletResponse response){
        File file = new File(info.getResumableFilePath());
        UploadedFile uploadFile=info.getUploadFile();
        uploadFile.setUpdated(new Date());
        uploadFile.setStatus(GlobalConsts.FILE_RENAMEING);
        try {
            storage.getFileQueue().put(uploadFile);
        } catch (InterruptedException e) {
            LOG.error("put into upload file queue error:",e);
        }
        //上传已完成，修改文件名
        boolean changeNameSuccess=true;
        String new_path=file.getAbsolutePath();
        String fileSubffix=new_path.substring(new_path.lastIndexOf("."));
        if(".temp".equals(fileSubffix)){
            new_path =new_path.substring(0, new_path.length() - ".temp".length());
            changeNameSuccess=ChangeTempFileName(file,new_path,response);
        }
        
        if(changeNameSuccess){//修改文件名成功
            synchronized (this) {
                File newFile=new File(new_path);
                info.setResumableFilePath(new_path);
                if(info.getFileType()!=GlobalConsts.FILE_TYPE_IMG){
                    uploadFile.setFileUrl(uploadFile.getFileUrl().substring(0,uploadFile.getFileUrl().lastIndexOf(File.separator)+1)+newFile.getName());
                    uploadFile.setFileSize(newFile.length());
                    uploadFile.setProgress(100);
                    info.setIsFinished(GlobalConsts.IS_FINISHED_YES);
                    uploadFile.setStatus(GlobalConsts.FILE_FINISHED);
                    try {
                        storage.getFileQueue().put(uploadFile);
                        storage.getStatusQueue().put(info);
                    } catch (InterruptedException e) {
                        LOG.error("put status and file to queue error:",e);
                    }
                    ResumableInfoStorage.getInstance().getmMap().remove(info.getResumableIdentifier()+"-"+info.getUploadIdentifier());
                }else{
                    uploadFile.setStatus(GlobalConsts.FILE_WRITTING);
                    info.setIsFinished(GlobalConsts.IS_FINISHED_YES);
                    int uploadedCount=getUploadedImgCount(uploadFile.getId());
                    uploadFile.setProgress(uploadedCount*100/uploadFile.getPicsCount());
                   //图片全部上传完成 进入压缩阶段
                    if(uploadedCount==uploadFile.getPicsCount()){
                        uploadFile.setProgress(100);
                        uploadFile.setStatus(GlobalConsts.FILE_ZIPING);
                    }
                    try {
                        storage.getFileQueue().put(uploadFile);
                        storage.getStatusQueue().put(info);
                    } catch (InterruptedException e) {
                        LOG.error("put status and file to queue error:",e);
                    }
                    
                }
            }
        }
        return uploadFile;
    }
    
    private synchronized boolean ChangeTempFileName(File tempFile,String new_path, HttpServletResponse response) {
        //上传已完成，修改文件名
        boolean result=true;
        if(tempFile.exists()){
            Path source = tempFile.toPath();
            try {
                Files.move(source, source.resolveSibling(new_path));
            }catch (IOException e) {
                result=false;
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.error("文件名修改错误：",e);
            }
        }
        return result;
    }
    
    /**
     * 从 mMap中获取resumableinfo 或者重新创建一个.
     * @param resumableChunkSize
     * @param resumableTotalSize
     * @param resumableIdentifier
     * @param resumableFilename
     * @param resumableRelativePath
     * @param resumableFilePath
     * @param position 
     * @param fileType 
     * @return
     */
    @Override
    @Transactional
    public synchronized UploadedStatus getResumableUploadedStatus(UploadedStatusDto statusDto) {
        UploadedStatus info = storage.getmMap().get(statusDto.getResumableIdentifier()+"-"+statusDto.getUploadIdentifier());
        if(null==info){
            if(statusDto.getFileType()==GlobalConsts.FILE_TYPE_IMG){
                 info=uploadedStatusRepository.findByIdentifierAndUploadIdentifier(statusDto.getResumableIdentifier(),statusDto.getUploadIdentifier());
            }else{
                info=uploadedStatusRepository.findByIdentifier(statusDto.getResumableIdentifier());
            }
            if (info == null) {
                Date nowTime=new Date();
                long userId=((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
                
                //创建临时文件
                String base_dir = ResumableJsUploadSetting.getLocalPath()+DateUtil.getDateString(nowTime);
                new File(base_dir).mkdirs();
                    //在未上传完成的文件后面加上.temp后缀。
                String fileSuffix=statusDto.getResumableFilename().substring(statusDto.getResumableFilename().lastIndexOf("."));
                File tempFile=createFile(base_dir,fileSuffix);
                statusDto.setResumableFilePath(tempFile.getAbsolutePath() + ".temp");
                //创建UploadedStatus和UploadedFile信息
                info = new UploadedStatus();
                info.setUserId(userId);
                info.setCreated(nowTime);
                info.setUpdated(nowTime);
                info.setResumableChunkSize(statusDto.getResumableChunkSize());
                info.setResumableTotalSize(statusDto.getResumableTotalSize());
                info.setResumableIdentifier(statusDto.getResumableIdentifier());
                info.setResumableFileName(statusDto.getResumableFilename());
                info.setResumableRelativePath(statusDto.getResumableRelativePath());
                info.setResumableFilePath(statusDto.getResumableFilePath());
                info.setFileType(statusDto.getFileType());
                info.setUploadIdentifier(statusDto.getUploadIdentifier());
                info.setIsFinished(GlobalConsts.IS_FINISHED_NO);
                if(info.getFileType()==GlobalConsts.FILE_TYPE_IMG){
                    UploadedStatus uploadedStatus=uploadedStatusRepository.findByUploadIdentifierLimitOne(statusDto.getUploadIdentifier());
                    UploadedFile uploadFile=null;
                    if(uploadedStatus!=null)
                         uploadFile=uploadfileRepository.findOne(uploadedStatus.getFileId());
                    if(uploadFile!=null){
                        info.setUploadFile(uploadFile);
                    }else{
                        String resumableFilePath= statusDto.getResumableFilePath();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
                        String fileName = sdf.format(nowTime);
                        File zipFile=createFile(base_dir, ".zip");
                        String fileUrl=ResumableJsUploadSetting.getRemotePath()+resumableFilePath.substring(ResumableJsUploadSetting.getLocalPath().length(),resumableFilePath.lastIndexOf(File.separator)+1)+zipFile.getName();
                        info.getUploadFile().setFileUrl(fileUrl);
                        info.getUploadFile().setCreated(nowTime);
                        info.getUploadFile().setUpdated(nowTime);
                        info.getUploadFile().setUserId(userId);
                        if(statusDto.getPicsCount()>1){
                            info.getUploadFile().setFileName(fileName+"多图上传.zip");
                        }else{
                            info.getUploadFile().setFileName(fileName+"单图上传.zip");
                        }
                        info.getUploadFile().setFileType(statusDto.getFileType());
                        info.getUploadFile().setPicsCount(statusDto.getPicsCount());
                        info.getUploadFile().setFileSize(statusDto.getPicTotalSize());
                    }
                }else{
                    String fileUrl=ResumableJsUploadSetting.getRemotePath()+statusDto.getResumableFilePath().substring(ResumableJsUploadSetting.getLocalPath().length());
                    info.getUploadFile().setFileUrl(fileUrl);
                    info.getUploadFile().setCreated(nowTime);
                    info.getUploadFile().setUpdated(nowTime);
                    info.getUploadFile().setUserId(userId);
                    info.getUploadFile().setFileName(statusDto.getResumableFilename());
                    info.getUploadFile().setFileType(statusDto.getFileType());
                    info.getUploadFile().setFileSize(statusDto.getResumableTotalSize());
                }
                
                this.uploadfileRepository.save(info.getUploadFile());
                info.setFileId(info.getUploadFile().getId());
                this.uploadedStatusRepository.save(info);               
            }else{
                UploadedFile uploadedFile=this.uploadfileRepository.findOne(info.getFileId());
                if(uploadedFile==null)
                    LOG.error("upload data error:uploadedStatus is not null but uploadedFile is null!");
                info.setUploadFile(uploadedFile);
                if(!StringUtils.isEmpty(info.getUploadedChunks())){
                    info.getUploadChunkSet().addAll(Arrays.asList(info.getUploadedChunks().split(",")));
                }
            }
            storage.getmMap().put(info.getResumableIdentifier()+"-"+info.getUploadIdentifier(),info);
        }
        return info;
    }

    @Override
    public synchronized File createFile(String pathname, String suffix) {
        long now =new Date().getTime();
        Random random=new Random();
        long randomNum=random.nextInt(999);
        String fileName=(576460752303423488L|(now<<15)|randomNum)+"";
        File file=new File(pathname+File.separator+fileName+suffix);
        if(file.exists()){
            while (true) {
                now=new Date().getTime();
                randomNum=random.nextInt(999);
                fileName=(576460752303423488L|(now<<15)|randomNum)+"";
                file=new File(pathname+File.separator+fileName+suffix);
                if(!file.exists()){
                    try {
                        file.createNewFile();
                        break;
                    } catch (IOException e) {
                        LOG.error("file uploading create file error:",e);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    LOG.error("create file thread sleep error:",e);
                }
            }
        }
        return file;
    }
    
    @Override
    public JsonObject uploadingFiles(){
        long userId=((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<UploadedStatus> status=uploadedStatusRepository.findFileByUserAndStatus(userId, GlobalConsts.IS_FINISHED_NO);
        return new JsonObject(status);
    }
    
    @Override
    public JsonObject findFileById(Long id){
        UploadedFile file=uploadfileRepository.findOne(id);
        if(file==null)
            return new JsonObject("文件不存在！",1001);
        UploadedStatus status=uploadedStatusRepository.findByFileIdLimitOne(id);
        if(status!=null)
            file.setUploadIdentifier(status.getUploadIdentifier());
        return new JsonObject(file);
    }
    
    /**
     * 取消上传
     */
    @Override
    public JsonObject uploadCancle(String uploadIdentifier){
        long userId=((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        new Thread(()->{
            //从内存删除
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<String, UploadedStatus> mHashMap=ResumableInfoStorage.getInstance().getmMap();
            Iterator<Map.Entry<String, UploadedStatus>> iterator=mHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
               Map.Entry<String, UploadedStatus> entry=iterator.next();
               UploadedStatus uploadedStatus=entry.getValue();
               if(userId==uploadedStatus.getUserId()&&uploadedStatus.getUploadIdentifier().equals(uploadIdentifier)){
                   iterator.remove();
               }
            } 
        }).start();
        
        List<UploadedStatus> statusList=uploadedStatusRepository.findByUploadIdentifierAndUser(uploadIdentifier,userId);
        if(statusList!=null&&!statusList.isEmpty()){
            UploadedFile uploadedFile=uploadfileRepository.findOne(statusList.get(0).getFileId());
            if(uploadedFile.getStatus()==GlobalConsts.FILE_TASK_CREATED){
                FaceExtractTask task = faceExtractTaskRepository.findByFileId(uploadedFile.getId());
                if(task!=null){
                    faceExtractService.deleteTask(task.getId());
                }
            }
            try{
                uploadedFile.setIsDeleted(GlobalConsts.IS_FILE_DELETED_YES);
                uploadfileRepository.save(uploadedFile);
                for(UploadedStatus vo:statusList){
                    vo.setIsDeleted(GlobalConsts.IS_FILE_DELETED_YES);
                }
                uploadedStatusRepository.save(statusList);
            }catch (Exception e) {
                LOG.error("deleteFile error:",e);
                return new JsonObject("删除文件失败，请稍后重试！",1001);
            }
        }
        return new JsonObject("取消上传成功！");
    }
    
    /**
     * 从request中获取参数，使用@RequestBody会有mediatype错误
     * @param request
     * @return
     */
    private UploadedStatusDto getParamatersFromRequest(HttpServletRequest request) {
        UploadedStatusDto statusDto=new UploadedStatusDto();
        statusDto.setResumableChunkNumber(NumberUtils.toInt(request.getParameter("resumableChunkNumber"),-1));
        statusDto.setResumableChunkSize(NumberUtils.toInt(request.getParameter("resumableChunkSize"), -1));
        statusDto.setResumableTotalSize(NumberUtils.toLong(request.getParameter("resumableTotalSize"), -1));
        statusDto.setFileType(NumberUtils.toInt(request.getParameter("fileType"),-1));
        statusDto.setResumableFilename(request.getParameter("resumableFilename"));
        statusDto.setResumableRelativePath(request.getParameter("resumableRelativePath"));
        statusDto.setUploadIdentifier(request.getParameter("uploadIdentifier"));
        Long picTotalSize=NumberUtils.toLong(request.getParameter("picTotalSize"),-1);
        Integer picsCount=NumberUtils.toInt(request.getParameter("picsCount"),-1);
        picTotalSize=picTotalSize==-1?null:picTotalSize;
        picsCount=picsCount==-1?null:picsCount;
        statusDto.setPicTotalSize(picTotalSize);
        statusDto.setPicsCount(picsCount);
        long userId=((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        statusDto.setFileHash(request.getParameter("fileHash"));
        statusDto.setResumableIdentifier(userId+"-"+statusDto.getFileHash());
        return statusDto;
    }
    
    @Override
    public void updateTableBatch(){
        BlockingQueue<UploadedStatus> statusQueue=storage.getStatusQueue();

        if(!statusQueue.isEmpty()){
            UploadedStatus status=null;
            String tableSql=" replace into "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS
                    +"(id,created,updated,user_id,resumable_chunk_size,resumable_total_size,resumable_identifier,resumable_file_name,"
                    + "resumable_relative_path,resumable_file_path,uploaded_chunks,file_type,file_id,is_finished,progress,upload_identifier,is_deleted)";
            StringBuffer sBuffer=new StringBuffer(" values ");
            int i=0;
            while(!statusQueue.isEmpty()){
                try {
                    status =statusQueue.take();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 UploadedStatus statusInDb=uploadedStatusRepository.findOne(status.getId());
                 if(statusInDb!=null){
                     status.setIsDeleted(status.getIsDeleted()|statusInDb.getIsDeleted());
                 }
                 String valueSql="("+status.getId()+",'"+DateUtil.getformatDate(status.getCreated())+"','"+DateUtil.getformatDate(status.getUpdated())+"',"+status.getUserId()+","+status.getResumableChunkSize()+","+status.getResumableTotalSize()
                        +",'"+status.getResumableIdentifier()+"','"+status.getResumableFileName()+"','"+status.getResumableRelativePath()+"','"+status.getResumableFilePath()+"','"+status.getUploadedChunks()
                        +"',"+status.getFileType()+","+status.getFileId()+","+status.getIsFinished()+","+status.getProgress()+",'"+status.getUploadIdentifier()+"',"+status.getIsDeleted()+")";
                 if(statusQueue.isEmpty()){
                     sBuffer.append(valueSql);
                 }else{
                     sBuffer.append(valueSql+",");
                 }
                 ++i;
                 if(i>GlobalConsts.MAX_BATCH_UPDATE_NUM)break;
                 
            }
            for(int j=0;j<10;j++){
                try {
                    LOG.error(" file uploading batch update mysql table start...... ");
                    jdbcTemplate.execute(tableSql+sBuffer.toString());
                    LOG.error(" file uploading batch update mysql table stop...... ");
                    break;
                } catch (Exception e) {
                    LOG.error("jdbcTemplate error:",e);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
    
}