package intellif.database.dao.impl;

import java.util.List;

import javax.persistence.Table;

import intellif.consts.GlobalConsts;
import intellif.database.dao.UserDao;
import intellif.database.entity.UserInfo;
import intellif.dto.UserDto;
import intellif.database.entity.Area;
import intellif.service.impl.PoliceStationServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDaoImpl  extends AbstractCommonDaoImpl<UserInfo> implements UserDao<UserInfo>{
    private static Logger LOG = LogManager.getLogger(UserDaoImpl.class);
  
    @Override
    public Class<UserInfo> getEntityClass() {
        // TODO Auto-generated method stub
        return UserInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = UserInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }

    @Override
    public UserInfo findOneUserWithAdditionalInfo(long id) {
        UserInfo user = null;
        String sqlString = "SELECT u.*, r.name as role_type_name, r.res_ids FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER
                + " u, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                + " r where u.id = ?";
        sqlString += " and find_in_set(r.id, u.role_ids)";

        try {
            user = jdbcTemplate.queryForObject(sqlString, new String[]{"" + id}, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
        } catch (Exception e) {
            LOG.error("get user by id error,id:"+id+",e:",e);
            return null;
        }
        return user;
    }
  
    @Override
    public  List<UserDto> findAllBySuperAdmin(String userName) {
        List<UserDto> userList = null;
        String sqlString = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,a.age,a.post,"
                + "a.special_sign,b.station_name policeStationName,c.cn_name roles,c.id roleId,"
                + "a.camera_rights,a.c_type_ids "
                + "FROM "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "
                +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+" b, "
                +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                +" c where (a.police_station_id=b.id) and (a.role_ids=c.id and "
                + "(login like '%"+userName+"%' or a.name like '%"+userName+"%' or post like '%"+userName+"%' "
                + "or station_name like '%"+userName+"%' or cn_name like '%"+userName+"%' ) )order by a.created desc";

        try {
            userList = jdbcTemplate.query(sqlString, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }
    
    @Override
    public  List<UserDto> findBySuperAdmin(long stationid,String username) {
        List<UserDto> userList = null;
        String sqlString = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,"
                + "a.age,a.special_sign,a.post,b.station_name policeStationName,c.cn_name roles,"
                + "c.id roleId,a.camera_rights,a.c_type_ids FROM "+
                GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "
                +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION
                +" b, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE
                +" c where (a.police_station_id = "+stationid+") and  (a.police_station_id=b.id "
                + "and a.role_ids=c.id and (login like '%"+username+"%' or a.name like '%"+username+"%'"
                + "or post like '%"+username+"%' or station_name like '%"+username+"%' or cn_name like '%"+username+"%'))"
                + " order by a.created desc";

        try {
            userList = jdbcTemplate.query(sqlString, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }
    
    @Override
    public  List<UserDto> findByAdmin(long stationid,String username) {
        List<UserDto> userList = null;
        String sqlString = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,"
                + "a.age,a.post,a.special_sign,  b.station_name policeStationName,"
                + "c.cn_name roles,c.id roleId,a.camera_rights,a.c_type_ids FROM "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "
                +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+
                " b, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE+
                " c where (a.police_station_id = "+stationid+") and (a.police_station_id=b.id "
                + "and a.role_ids=c.id and (login like '%"+username+"%' or a.name like '%"+username+"%' "
                + "or post like '%"+username+"%' or cn_name like '%"+username+"%') ) order by a.created desc";
        try {
            userList = jdbcTemplate.query(sqlString, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }
}
