package intellif.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.dto.CommonQueryDto;
import intellif.dto.JsonObject;
import intellif.exception.MsgException;
import intellif.service.AreaServiceItf;
import intellif.service.impl.AreaServiceImpl;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_AREA)
public class AreaController {

    private static Logger LOG = LogManager.getLogger(AreaController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    @Autowired
    private AreaServiceImpl areaService;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private ZoneAuthorizeCacheItf zoneAuthorizeCache;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "新建一个区域")
    public JsonObject create(@RequestBody @Valid Area area) {
        Area resp = null;
        try{
            resp = areaService.save(area);
            Tree tree =  LocalCache.tree;
            tree.addTreeNode(Area.class, resp);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),e.getErrorCode()); 
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),RequestConsts.response_system_error); 
        }
        return new JsonObject(resp);
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "条件查询获取区域")
    public JsonObject queryByParams(@RequestBody @Valid CommonQueryDto cqd) {
        PageDto<Area> pageResult = new PageDto<Area>(new ArrayList<Area>());
        try{ 
            String areaIds = cqd.getIds();
          if(StringUtils.isNotBlank(areaIds)){
            zoneAuthorizeService.checkIds(Area.class,areaIds);
          }else{
              //如果不传areaIds则取用户授权区域数据
              List<Area> areaList = zoneAuthorizeService.findAll(Area.class,cqd.getUserId());
              List<Long> curcList = areaList.stream().map(s -> s.getId()).collect(Collectors.toList());
              areaIds = StringUtils.join(curcList, ",");
          }
          if(StringUtils.isNotBlank(areaIds)){          
              cqd.setIds(areaIds);
              pageResult = areaService.queryUserAreasByParams(cqd);        
          }
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }
        catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(pageResult.getData(), 0, pageResult.getMaxPages(), (int) pageResult.getCount());  
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id的区域")
    public JsonObject get(@PathVariable("id") long id) {
        try{
            zoneAuthorizeService.checkIds(Area.class, id);
            return new JsonObject( this.areaService.findById(id));
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "修改指定id区域信息")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid Area area) throws Exception {
        Area resp = null;
        try{
            area.setId(id);
            resp = this.areaService.update(area);
            Tree tree =  LocalCache.tree;
            tree.addTreeNode(Area.class, resp);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),e.getErrorCode() ); 
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),RequestConsts.response_system_error); 
        }
        return new JsonObject(resp);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除指定id的区域")
    public JsonObject delete(@PathVariable("id") long id) {
        try{
            this.areaService.delete(id);
            Tree tree =  LocalCache.tree;
            tree.deleteTreeNode(Area.class, id);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "/threshold/{threshold}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "修改所有区域的最大可容纳人数阈值")
    public JsonObject update(@PathVariable("threshold") long threshold) {
        this.areaService.jdbcBatchUpdate(null,"person_threshold = "+threshold);
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }
}
