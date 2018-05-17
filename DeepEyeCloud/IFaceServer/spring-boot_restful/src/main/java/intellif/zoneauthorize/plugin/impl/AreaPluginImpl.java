package intellif.zoneauthorize.plugin.impl;

import java.util.List;

import javax.persistence.Table;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import intellif.database.entity.Area;
import intellif.database.entity.OtherArea;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;
import intellif.database.entity.UserArea;

@Service
public class AreaPluginImpl extends AbstracZonePluginImpl<Area> implements ZoneAuthorizePluginItf<Area> {

    @Override
    public Class<Area> zoneClass() {
        return Area.class;
    }

    @Override
    public Class<?> userToZoneClass() {
        return UserArea.class;
    }
    
    @Override
    public List<Area> findAll() {
        Table areaTable = Area.class.getAnnotation(Table.class);
        Table otherAreaTable = OtherArea.class.getAnnotation(Table.class);
        String sql = String.format("select `id`,`created`,`updated`,`geo_string`,`geometry`,`area_name`,`area_no`,`person_threshold`,`district_id`,`parent_id` from `%s`.`%s`"
                + " union "
                + "select `id`,`created`,`updated`,`geo_string`,`geometry`,`area_name`,`area_no`,`person_threshold`,`district_id`,`parent_id` from `%s`.`%s`", 
                areaTable.schema(), areaTable.name(), otherAreaTable.schema(), otherAreaTable.name());
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(zoneClass()));
    }

    @Override
    public String foreignKey() {
        return "area_id";
    }
}
