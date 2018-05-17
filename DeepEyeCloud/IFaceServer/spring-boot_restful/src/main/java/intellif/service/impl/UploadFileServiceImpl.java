package intellif.service.impl;

import java.io.File;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedFileDao;
import intellif.dao.UploadedStatusDao;
import intellif.dto.JsonObject;
import intellif.dto.UploadFileDto;
import intellif.facecollision.dao.FaceExtractTaskDao;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.facecollision.vo.FaceExtractTask;
import intellif.service.UploadFileServiceItf;
import intellif.utils.ApplicationResource;
import intellif.utils.DateUtil;
import intellif.database.entity.ResumableInfoStorage;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;
import intellif.database.entity.UserInfo;

@Service
public class UploadFileServiceImpl implements UploadFileServiceItf {
    
    private static Logger LOG=LogManager.getLogger(UploadFileServiceImpl.class);
    @PersistenceContext
    EntityManager entityManager;
    public static BigInteger hisopmaxpage;
    
    @Autowired
    private UploadedStatusDao uploadedStatusRepository;
    @Autowired
    private UploadedFileDao uploadedFileRepository;
    @Autowired
    private FaceExtractTaskDao faceExtractTaskRepository;
    @Autowired
    private FaceServiceImpl faceService;
    @Autowired
    private FaceExtractServiceItf faceExtractService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    ResumableInfoStorage storage=ResumableInfoStorage.getInstance();

    public ArrayList<UploadedFile> findByPage(int page, int pagesize, UploadFileDto uploadedFileDto){
        ArrayList<UploadedFile> resultFiles=null;
        long userId=((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        StringBuffer sql=new StringBuffer("select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" where is_deleted=0 and user_id="+userId+" ");
        if(StringUtils.isNotBlank(uploadedFileDto.getFileName()))
            sql.append(" and file_name like %"+uploadedFileDto.getFileName()+"% ");
        if(null!=uploadedFileDto.getFileType())
            sql.append(" and file_type="+uploadedFileDto.getFileType());
        if(null!=uploadedFileDto.getStatus())
            sql.append(" and status="+uploadedFileDto.getStatus());
        
        hisopmaxpage=getMaxPage(sql.toString());
        sql.append(" order by created desc limit "+(page-1)*pagesize+","+pagesize);
        try {
            Query query = this.entityManager.createNativeQuery(sql.toString(), UploadedFile.class);
            resultFiles =  (ArrayList<UploadedFile>) query.getResultList();
        } catch (Exception e) {
            LOG.error("文件分页查询错误：", e);
        } finally {
            entityManager.close();
        }
        HashMap<String, UploadedStatus> mHashMap=ResumableInfoStorage.getInstance().getmMap();
        for(UploadedFile file:resultFiles){
            this.getUploadStatus(file,mHashMap);
            this.getFileExtractStatus(file);
        }
        return resultFiles;
    }
    
    @Override
    public void getUploadStatus(UploadedFile file, HashMap<String, UploadedStatus> mHashMap) {
        UploadedStatus status=null;
        if(file.getStatus()<GlobalConsts.FILE_ZIPING){
            for(UploadedStatus value:mHashMap.values()){
                if(value.getFileId().equals(file.getId())){
                    if(status==null){
                        status=value;
                    }else{
                        if(status!=null&&value!=null&&status.getUploadFile()!=null&&value.getUploadFile()!=null
                                &&status.getUploadFile().getProgress()!=null&&status.getUploadFile().getProgress()!=null
                                &&status.getUploadFile().getProgress()<value.getUploadFile().getProgress())
                            status=value;     
                    }
                }
            }
            if(status==null){
                status=uploadedStatusRepository.findByFileIdLimitOne(file.getId());
                if(status!=null)
                    file.setUploadIdentifier(status.getUploadIdentifier());
            }else{
                file.setProgress(status.getUploadFile().getProgress());
                file.setUploadIdentifier(status.getUploadIdentifier());
            }
        }else{
            status=uploadedStatusRepository.findByFileIdLimitOne(file.getId());
            if(status!=null)
                file.setUploadIdentifier(status.getUploadIdentifier());
        }
        
    }

    @Override
    public void getFileExtractStatus(UploadedFile file){
        FaceExtractTask task=faceExtractTaskRepository.findByFileId(file.getId());
        if(task!=null){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            startTime.setTime(task.getCreated());
            endTime.setTime(task.getUpdated());
            // 时间范围扩大5分钟，降低服务器时间不准的影响
            startTime.add(Calendar.MINUTE, -5);
            endTime.add(Calendar.MINUTE, 5);
            String startTimeStr = df.format(startTime.getTime());
            String endTimeStr = df.format(endTime.getTime());
            long total=faceService.countBySourceId(task.getId(), startTimeStr, endTimeStr);
            file.setExtractStatus(task.getStatus());
            file.setExtractTotal(total);
            if(task.getCurrent()!=null&&task.getTotal()!=null&&task.getTotal()!=0){
                Double extractProgress=task.getCurrent()*100D/task.getTotal();
                file.setExtractProgress(extractProgress);
            }else{
                file.setExtractProgress(0D);
            }
        }
    }

    public BigInteger getMaxPage(String sql) {
        BigInteger maxpage=null;
        sql =sql.replace("*", "count(*)");
        try{
            Query query=this.entityManager.createNativeQuery(sql);
            maxpage= (BigInteger) query.getSingleResult();
        }catch (Exception e) {
            LOG.error("获取文件最大页数错误：", e);
            return null; 
        }finally {
            this.entityManager.close();
        }
        return maxpage;
    }
    
    public JsonObject deleteFile(UploadedFile uploadedFile){
        //从内存中删除
        new Thread(()->{
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
               if(uploadedFile.getUserId()==uploadedStatus.getUserId()&&uploadedStatus.getFileId().equals(uploadedFile.getId())){
                   iterator.remove();
               }
            }
        }).start();
        List<UploadedStatus> statusList=uploadedStatusRepository.findByFileId(uploadedFile.getId());
        try {
            for(UploadedStatus status:statusList){
                status.setIsDeleted(GlobalConsts.IS_FILE_DELETED_YES);
            }
            uploadedStatusRepository.save(statusList);
            uploadedFile.setIsDeleted(GlobalConsts.IS_FILE_DELETED_YES);
            uploadedFileRepository.save(uploadedFile);
            
        } catch (Exception e) {
            LOG.error("deleteFile error:",e);
            return new JsonObject("删除文件失败，请稍后重试！",1001);
        }
        return new JsonObject("删除文件成功！");
    }
    
    @Override
    public void updateTableBatch(){
        BlockingQueue<UploadedFile> fileQueue=storage.getFileQueue();
        if(!fileQueue.isEmpty()){
            UploadedFile file=null;
            String tableSql=" replace into "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" (id,created,updated,user_id,file_name,file_url,"
                    + "file_type,pics_count,file_size,progress,status,is_deleted) ";
            StringBuffer sBuffer=new StringBuffer(" values ");
            int i=0;
            while(!fileQueue.isEmpty()){
                try {
                    file =fileQueue.take();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 UploadedFile fileInDB=uploadedFileRepository.findOne(file.getId());
                 if(fileInDB!=null){
                     file.setIsDeleted(fileInDB.getIsDeleted()|file.getIsDeleted());
                 }
                 String valueSql="("+file.getId()+",'"+DateUtil.getformatDate(file.getCreated())+"','"+DateUtil.getformatDate(file.getUpdated())+"',"+file.getUserId()+",'"+file.getFileName()+"','"+file.getFileUrl()+"',"
                        +file.getFileType()+","+file.getPicsCount()+","+file.getFileSize()+","+file.getProgress()+","+file.getStatus()+","+file.getIsDeleted()+")";
                 if(fileQueue.isEmpty()){
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
                    LOG.error(" file uploading batch update mysql table end...... ");
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
    
    @Override
    @Transactional
    public void createTaskStatus(UploadedFile uploadFile) {
        faceExtractService.createTask(uploadFile.getId());
        this.updateStatus(uploadFile.getId(),GlobalConsts.FILE_TASK_CREATED);
        if(uploadFile.getFileType()==GlobalConsts.FILE_TYPE_IMG){
            ApplicationResource.THREAD_POOL.submit(()->deletingStatus(uploadFile));
        }
    }
    
    /**
     * 打包并创建解析任务成功之后删除上传的图片
     * @param uploadedFile
     */
    private void deletingStatus(UploadedFile uploadedFile) {
        List<UploadedStatus> statusList=uploadedStatusRepository.findByFileId(uploadedFile.getId());
        for(UploadedStatus status:statusList){
            File imgFile=new File(status.getResumableFilePath());
            if(imgFile.exists())
                imgFile.delete();
        }
        for(UploadedStatus status:statusList){
            ResumableInfoStorage.getInstance().getmMap().remove(status.getResumableIdentifier()+"-"+status.getUploadIdentifier());
        }
        
    }
    
    @Override
    public int updateZipingStatus(long id ,int status){
        String sql=" update "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" set status="+status+" where id="+id+" and is_deleted=0 and status="+GlobalConsts.FILE_ZIPING;
        int row=jdbcTemplate.update(sql);
        return row;
    }
    
    @Override
    public int updateStatus(long id ,int status){
        String sql=" update "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" set status="+status+" where id="+id;
        int row=jdbcTemplate.update(sql);
        return row;
    }
    
    @Override
    public List<UploadedFile> findAllUploadedButNotCreatedTaskFile(){
        String sql=" select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" t where t.is_deleted=0 and t.status="+GlobalConsts.FILE_ZIPING
                +" and pics_count=(select count(*) from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" s where s.file_id=t.id and s.is_deleted=0 and s.is_finished=1 ) ";
        List<UploadedFile> results=jdbcTemplate.query(sql,new BeanPropertyRowMapper<UploadedFile>(UploadedFile.class));
        return results;
    }
    
}