package intellif.service;


import intellif.database.entity.PoliceStation;

import java.util.List;

public interface PoliceStationServiceItf<T> extends CommonServiceItf<T> {
	public List<Object> findPoliceByTaskId(long id);

	/**
	 * 查询当前用户有权限的单位列表<br>
	 * 业务规则：<br>
     *  1. 超级管理员有所有单位的权利<br>
	 *  2. 中级管理员只有自己所在分局下的单位的权利<br>
     *
	 *  @return
	 */
	public List<PoliceStation> queryCurrentUserStations();

	/**
	 * 添加或修改单位<br>
	 * 业务规则：<br>
	 * 1. 超级管理员能管理所有单位<br>
	 * 2. 中级管理员只能管理自己所在分局下的单位<br>
     * 3. 如果父id为空，则将单位添加到自己有权限的根单位下<br>
	 * @param station - 要添加的单位
	 * @return 添加后的单位
	 */
	public PoliceStation saveStation(PoliceStation station);

	/**
	 * 删除单位<br>
	 * 业务规则：<br>
	 * 1. 超级管理员能删除所有单位<br>
	 * 2. 中级管理员只能删除自己所在分局下的单位<br>
	 * @param id - 要删除的单位的id
	 * @return 更新后的单位
	 */
	public void deleteStation(long id);

	/**
	 * 判断当前登录用户是否有操作单位的权限
	 * @param stationId
	 * @return
	 */
	public boolean checkPrivilege(long stationId);

	/**
	 * 返回单位及其子单位的id,拼接后以,隔开
	 */
	public String getStationIds(long stationId);

	/**
	 * 添加单位更新日志
	 * @param newinfo - 修改后的单位
	 * @param oldinfo - 修改前的单位
	 */
    void addAuditLogForUpdate(PoliceStation newinfo, PoliceStation oldinfo);

	/**
	 * 根据名字模糊查找单位及其子单位的id, 拼接后以，隔开
	 * @param stationId - 单位id
	 * @return - 以,隔开的单位id
	 */
	String searchStationIdsByName(Long stationId, String searchName);

	/**
	 * 根据id集合查询单位
	 * @param policeIds
	 * @return
	 */
    List<PoliceStation> queryNamesByIds(String policeIds);
    
    /**
     * 根据一个单位的stationId,获得它的上级单位列表
     * @param stationId
     * @return
     */
    List<PoliceStation> getForefathers(long stationId);
}
