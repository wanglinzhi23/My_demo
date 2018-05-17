package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.service.ResourceServiceItf;
import intellif.database.entity.RoleResourceDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Zheng Xiaodong on 2017/4/20.
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_RESOURCE)
public class ResourceController {
    @Autowired
    private ResourceServiceItf resourceServcie;

    @RequestMapping(method = RequestMethod.GET, value = "/role/name/{roleName}")
    @ApiOperation(httpMethod = "GET", value = "查询当前登录用户可给指定角色分配的功能列表")
    public JsonObject queryResourcesByRoleName(String roleName) {
        List<RoleResourceDto> resources = resourceServcie.queryResourcesByCurrentUser(roleName, true);
        
        return new JsonObject(resources);
    }
}
