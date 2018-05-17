package intellif.service;

import intellif.database.entity.OauthResource;
import intellif.database.entity.RoleResourceDto;

import java.util.List;
import java.util.Set;

public interface ResourceServiceItf {

  
    /**
     * 查询当前登录用户可以给指定角色分配的功能权限列表(过滤当前登录用户没有的权限)
     * @param roleName - 角色名
     * @return 功能权限列表
     */
    List<RoleResourceDto> queryResourcesByCurrentUser(String roleName, Boolean display);

    /**
     * 查询用户对应的功能权限列表
     */
    List<OauthResource> queryUserResources(Long userId);

    // Todo: Configurable

    /**
     * 查询包含的功能权限集合
     * @param resIds
     * @return
     */
    Set<Long> compatibleResIds(List<Long> resIds);

    /**
     * 根据资源id查询名字
     * @param resIds - 资源id列表，逗号隔开
     * @return 资源名字列表，逗号隔开
     */
    String queryResourceNames(String resIds);
}
