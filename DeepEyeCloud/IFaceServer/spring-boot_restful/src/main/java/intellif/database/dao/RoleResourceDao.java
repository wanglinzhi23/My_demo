package intellif.database.dao;

import intellif.database.entity.RoleResourceDto;

import java.util.List;

public interface RoleResourceDao<T> extends CommonDao<T>{
    /**
     * 根据角色名查询功能权限列表
     * @param roleName - 角色名
     * @param display - 是否显示(null:查询所有, true:只查询显示的, false:只查询不显示的)
     * @return 功能权限列表
     */
    List<RoleResourceDto> queryResourcesByRoleName(String roleName, Boolean display);

}
