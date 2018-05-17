package intellif.database.dao;

import intellif.database.entity.RoleInfo;

import java.util.List;

public interface RoleDao<T> extends CommonDao<T>{

    List<RoleInfo> queryRoleInfoByUserId(long userId);
}
