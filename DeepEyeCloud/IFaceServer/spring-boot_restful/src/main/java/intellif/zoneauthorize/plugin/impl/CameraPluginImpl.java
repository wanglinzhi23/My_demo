package intellif.zoneauthorize.plugin.impl;

import java.util.List;

import javax.persistence.Table;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherCameraInfo;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;
import intellif.database.entity.UserCamera;

@Service
public class CameraPluginImpl extends AbstracZonePluginImpl<CameraInfo> implements ZoneAuthorizePluginItf<CameraInfo> {

    @Override
    public Class<CameraInfo> zoneClass() {
        return CameraInfo.class;
    }

    @Override
    public Class<?> userToZoneClass() {
        return UserCamera.class;
    }


    @Override
    public List<CameraInfo> findAll() {
        Table cameraTable = CameraInfo.class.getAnnotation(Table.class);
        Table otherCameraTable = OtherCameraInfo.class.getAnnotation(Table.class);
        String sql = String.format(
                "select `id`,`created`,`updated`,`geo_string`,`geometry`,`addr`,`capability`,`city`,`county`,`cover`,`name`,`password`,`port`,`rtspuri`,`station_id`,`status`,`type`,`c_type`,`uri`,`username`,`short_name`,`in_station`,`liveurl`,`parameter`,`code` from `%s`.`%s`"
                + " union "
                + "select `id`,`created`,`updated`,`geo_string`,`geometry`,`addr`,`capability`,`city`,`county`,`cover`,`name`,`password`,`port`,`rtspuri`,`station_id`,`status`,`type`,`c_type`,`uri`,`username`,`short_name`,`in_station`,`liveurl`,`parameter`,`code` from `%s`.`%s`",
                cameraTable.schema(), cameraTable.name(), otherCameraTable.schema(), otherCameraTable.name());
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(zoneClass()));
    }

    @Override
    public String foreignKey() {
        return "camera_id";
    }
}
