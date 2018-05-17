/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellif.dao;

import java.util.List;

import org.jruby.util.Glob;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UserInfo;
import intellif.dto.UserDto;
import intellif.database.entity.UserBaseInfo;

public interface UserDao extends CrudRepository<UserInfo, Long> {

    UserInfo findByLogin(String login);
    
    List<UserInfo> findByName(String name);

	List<UserInfo> findByPoliceStationId(long l);

    long countByPoliceStationId(long l);

	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " where police_station_id = :sourceId  and "
			+ "(login like %:name% or name like %:name% or post like %:name%) order by created desc",nativeQuery = true)
	List<UserInfo> findByPoliceStationId(@Param("sourceId") long sourceId,@Param("name")String name);

    ///////////////////////11.23
    @Query(value = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,a.age,a.special_sign,a.post,b.station_name policeStationName,c.cn_name roles,c.id roleId,a.camera_rights,a.c_type_ids FROM "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+" b, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE+" c where (a.police_station_id = :stationid) and  (a.police_station_id=b.id and a.role_ids=c.id and (login like %:username% or a.name like %:username% or post like %:username% or station_name like %:username% or cn_name like %:username%) ) order by a.created desc",nativeQuery = true)
    List<UserDto> findBySuperAdmin(@Param("stationid")long stationid,@Param("username")String username);

    //超级管理员 选择全部单位用户的情况  11.23
    @Query(value = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,a.age,a.post,a.special_sign,b.station_name policeStationName,c.cn_name roles,c.id roleId,a.camera_rights,a.c_type_ids FROM "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+" b, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE+" c where (a.police_station_id=b.id) and (a.role_ids=c.id and (login like %:username% or a.name like %:username% or post like %:username% or station_name like %:username% or cn_name like %:username% ) )order by a.created desc",nativeQuery = true)
    List<UserDto> findAllBySuperAdmin(@Param("username")String username);
    
    //如果只是管理员的话  不对单位名称进行全文检索
    @Query(value = "SELECT a.id,a.name,a.login,a.password,a.gender,a.mobile,a.age,a.post,a.special_sign,  b.station_name policeStationName,c.cn_name roles,c.id roleId,a.camera_rights,a.c_type_ids FROM "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER+" a, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+" b, "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ROLE+" c where (a.police_station_id = :stationid) and  (a.police_station_id=b.id and a.role_ids=c.id and (login like %:username% or a.name like %:username% or post like %:username% or cn_name like %:username%) ) order by a.created desc",nativeQuery = true)
    List<UserDto> findByAdmin(@Param("stationid")long stationid,@Param("username")String username);
    
    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " WHERE login in (:logins)", nativeQuery = true)
    List<UserInfo> findByLoginList(@Param("logins") String[] logins);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " SET name = :name, post = :post, police_station_id = :policeStationId WHERE login = :policeId", nativeQuery = true)
    void updateUserInfo(@Param("policeId") String policeId, @Param("name") String name, @Param("post") String post, @Param("policeStationId") Long policeStationId);
    
    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " WHERE police_station_id = :stationId and special_sign > 0", nativeQuery = true)
    List<UserInfo> findSpecialUsersByPoliceStationId(@Param("stationId") int stationId);
    
}
