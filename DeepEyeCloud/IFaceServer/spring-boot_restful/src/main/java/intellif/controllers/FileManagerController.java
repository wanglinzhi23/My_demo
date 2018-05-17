package intellif.controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedFileDao;
import intellif.dto.JsonObject;
import intellif.dto.UploadFileDto;
import intellif.facecollision.dao.FaceExtractTaskDao;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.facecollision.service.impl.FaceExtractServiceImpl;
import intellif.facecollision.vo.FaceExtractTask;
import intellif.service.UploadFileServiceItf;
import intellif.service.impl.UploadFileServiceImpl;
import intellif.database.entity.ResumableInfoStorage;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;

@RestController
@RequestMapping(value=GlobalConsts.R_ID_FILE_MANAGEMENT)
public class FileManagerController {
    
    private static Logger LOG = LogManager.getLogger(FaceExtractServiceImpl.class);
    
    @Autowired
    private UploadFileServiceItf uploadFileService;
    @Autowired
    private UploadedFileDao uploadedFileRepository;
    @Autowired
    private FaceExtractServiceItf faceExtractService;
    @Autowired
    private FaceExtractTaskDao faceExtractTaskRepository;
    
    @ApiOperation(httpMethod="POST",value="分页查询用户上传的文件")
    @RequestMapping(value="/page/{page}/pagesize/{pagesize}",method=RequestMethod.POST)
    public JsonObject getFiles(@RequestBody UploadFileDto uploadedFileDto,@PathVariable("page")int page,@PathVariable("pagesize")int pagesize) {
        ArrayList<UploadedFile> resultList=uploadFileService.findByPage(page,pagesize,uploadedFileDto);
        BigInteger biginteger=UploadFileServiceImpl.hisopmaxpage;
        int maxpage=0;
        if(biginteger!=null){
            if(((biginteger.intValue())%pagesize)==0){
                maxpage=(biginteger.intValue())/pagesize;
            }else{
                maxpage=(biginteger.intValue())/pagesize+1;
            }
        }
        return new JsonObject(resultList,0,maxpage);
    }
    
    @ApiOperation(httpMethod="DELETE",value="文件删除接口")
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JsonObject deleteFile(@PathVariable("id")long fileId) {
        UploadedFile file=uploadedFileRepository.findOne(fileId);
        if(file==null)
            return new JsonObject("文件不存在",1001);
        JsonObject result = null;
        if(file.getStatus()==GlobalConsts.FILE_TASK_CREATED){
            FaceExtractTask task = faceExtractTaskRepository.findByFileId(fileId);
            if(task!=null){
                try {
                    faceExtractService.deleteTask(task.getId());
                } catch (Exception e) {
                    return new JsonObject("无法删除相应的解析任务，请联系管理员",1001);
                }
            }
        }
        result=uploadFileService.deleteFile(file);
        return result;
    }
    
    @ApiOperation(httpMethod="POST",value="刷新文件状态")
    @RequestMapping(value="/status",method=RequestMethod.POST)
    public JsonObject refreshFilesProgress(@RequestBody UploadFileDto uploadFileDto){
        if(StringUtils.isNotBlank(uploadFileDto.getIds())){
            String[] idsArray=uploadFileDto.getIds().split(",");
            List<UploadedFile> fileList=new ArrayList<>();
            HashMap<String, UploadedStatus> mHashMap=ResumableInfoStorage.getInstance().getmMap();
            for(int i=0;i<idsArray.length;i++){
                Long fileId=null;
                try {
                    if(StringUtils.isNotBlank(idsArray[i]))
                        fileId=Long.parseLong(idsArray[i]);
                } catch (Exception e) {
                    LOG.error("uploaded status refresh ID format error",e);
                    return new JsonObject("ID{"+idsArray[i]+"}格式错误",1001);
                }
                if(fileId!=null){
                    try {
                        UploadedFile file=uploadedFileRepository.findOne(fileId);
                        if(file!=null&&file.getIsDeleted()==GlobalConsts.IS_FILE_DELETED_NO){
                            uploadFileService.getUploadStatus(file, mHashMap);
                            uploadFileService.getFileExtractStatus(file);
                            fileList.add(file);
                        } 
                    } catch (Exception e) {
                        LOG.error("刷新文件状态异常：",e);
                    }
                    
                }
            }
            return new JsonObject(fileList);
        }else{
            return new JsonObject("参数ids为空!");
        }
    }
    
}