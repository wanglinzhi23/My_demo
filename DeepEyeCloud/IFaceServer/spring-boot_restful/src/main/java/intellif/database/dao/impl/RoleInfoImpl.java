package intellif.database.dao.impl;

import intellif.database.dao.RoleDao;
import intellif.database.entity.RoleInfo;

import java.util.List;

import javax.persistence.Table;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
@Service
public class RoleInfoImpl extends AbstractCommonDaoImpl<RoleInfo> implements RoleDao<RoleInfo>{
  
    @Override
    public Class<RoleInfo> getEntityClass() {
        // TODO Auto-generated method stub
        return RoleInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = RoleInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
    /**
     * 根据用户ID查询权限
     */
    @Override
    public List<RoleInfo> queryRoleInfoByUserId(long userId) {
        String sql = "select r.* from t_role r, t_user u where u.id = "+userId+" and find_in_set(r.id, u.role_ids)";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<RoleInfo>(RoleInfo.class));
    }
}
