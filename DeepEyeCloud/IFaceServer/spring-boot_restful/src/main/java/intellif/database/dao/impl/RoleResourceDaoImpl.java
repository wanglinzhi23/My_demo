package intellif.database.dao.impl;

import intellif.consts.GlobalConsts;
import intellif.database.dao.RoleResourceDao;
import intellif.database.entity.RoleResource;
import intellif.database.entity.RoleResourceDto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
@Service
public class RoleResourceDaoImpl extends AbstractCommonDaoImpl<RoleResource> implements RoleResourceDao<RoleResource>{
    @Override
    public List<RoleResourceDto> queryResourcesByRoleName(String roleName, Boolean display) {
        String sql = "select rr.id, rr.role_name, rr.resource_id, "
                + "rr.must, rr.display, re.cn_name from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE_RESOURCE
                + " rr, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_RESOURCE
                + " re where re.id = rr.resource_id and rr.role_name = ?";
        if (display != null) {
            sql += " AND rr.display = " + (display ? "1" : "0");
        }
        List<RoleResourceDto> resources = jdbcTemplate.query(sql, new String[] {roleName},
                new BeanPropertyRowMapper<>(RoleResourceDto.class));
        if (resources == null) {
            resources =  new ArrayList<RoleResourceDto>();
        }
        return resources;
    }

    @Override
    public Class<RoleResource> getEntityClass() {
        // TODO Auto-generated method stub
        return RoleResource.class;
    }

    @Override
    public String getEntityTable() {
        Table table = RoleResource.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
  
}
