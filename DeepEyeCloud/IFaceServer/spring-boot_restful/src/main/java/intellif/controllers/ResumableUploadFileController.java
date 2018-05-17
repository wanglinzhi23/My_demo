package intellif.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.service.ResumableJsServiceItf;

@RestController
@RequestMapping(value=GlobalConsts.R_ID_RESUMABLE_UPLOAD)
public class ResumableUploadFileController{
	
    @Autowired
    private ResumableJsServiceItf resumableJsService;
    
    @ApiOperation(httpMethod="GET",value="检查chunk是否已上传接口")
    @RequestMapping(value="/true",method=RequestMethod.GET)
    public JsonObject doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        return resumableJsService.checkifUploaded(request,response);
    }

    @ApiOperation(httpMethod="POST",value="上传chunk接口")
    @RequestMapping(value="/true",method=RequestMethod.POST)
    public JsonObject doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        return resumableJsService.resumableUpload(request,response);
    }
    
    @ApiOperation(httpMethod="DELETE",value="取消上传接口")
    @RequestMapping(value="/status/{uploadIdentifier}",method=RequestMethod.DELETE)
    public JsonObject uploadCancle(@PathVariable("uploadIdentifier") String uploadIdentifier){
        return resumableJsService.uploadCancle(uploadIdentifier);
    }
   
    @ApiOperation(httpMethod="GET",value="检查用户正在上传中的文件")
    @RequestMapping(value="/files",method=RequestMethod.GET)
    public JsonObject checkUploadingFile(){
        return resumableJsService.uploadingFiles();
    }

    @ApiOperation(httpMethod="GET",value="根据id查找文件接口")
    @RequestMapping(value="/file/{id}",method=RequestMethod.GET)
    public JsonObject findFileById(@PathVariable("id") Long id){
        return resumableJsService.findFileById(id);
    }
}
