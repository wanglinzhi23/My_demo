package intellif.service;

import java.util.List;
import java.util.Map;

import intellif.database.entity.UserInfo;
import intellif.dto.JsonObject;
import intellif.dto.SearchUserDto;
import intellif.dto.UserAccountDto;
import intellif.dto.UserDto;
import intellif.dto.UserRightDto;
import intellif.exception.MsgException;
import intellif.utils.PageDto;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.UserBaseInfo;

/**
 * The Interface UserServiceItf.
 */
public interface UserServiceItf<T> extends CommonServiceItf<T> {
	List<UserInfo> findUserInfoByFilters(List<String> filterList);
	UserInfo findUserInfoById(long id);
	List<Long> findFieldByFilter(String fields,String filter);
	
    List<UserDto> findByCombinedConditions(UserDto userDto);

	String processAuthority(String sql);

	String getAuthorityIds(int authorityType);
	//跟上面方法的区别只在于 这是找 = type值的bankid  而不是>=type
	String getAuthorityIdsByType(int type);
	//此方法不限定stationId，只根据type进行查询
	List<PoliceStationAuthority> getAuthorityByOnlyType(int type);
	
	void createAuthorityOrIgnore(long stationId, long bankId);


    PageDto<UserInfo> queryUsersByStationId(SearchUserDto searchUserDto);


	/**
	 * 判断用于角色类型是否修改
	 * @param oldRoleId - 旧的角色id
	 * @param newRoleId - 新的角色id
     * @param roleNameInfo - 如果修改了，用于返回旧的角色名和新的角色名
	 */
	boolean isRoleTypeModified(Long oldRoleId, Long newRoleId, Map<String, String> roleNameInfo);

	/**
	 * 判断用于角色功能权限是否修改
	 * @param oldRoleId - 旧的角色id
	 * @param newRoleId - 新的角色id
	 * @param resNameInfo - 如果修改了，用于返回旧的功能权限列表名和新的功能权限列表
	 */
	boolean isResourcesModified(Long oldRoleId, Long newRoleId, Map<String, String> resNameInfo);

	/**
	 * 解除用户与单位的绑定, 清空用户的区域授权信息
	 * @param userId - 要解除绑定的用户
	 */
	void unbindUser(long userId);

	/**
	 * 查询所有待绑定单位的用户
	 */
	PageDto<UserInfo> queryUnbindedUsers(SearchUserDto searchUserDto);

	/**
	 * 移除全区域搜索账号
	 * 移除并不是删除账号， 只是将账号设为普通账号
	 * @param userId
	 */
	public void setSpecialSign(Long userId, Integer specialSign);

	/**
	 * 批量更新全区域搜索账号
	 * @param userIds
	 */
	public void batchUpdateSpecialSign(String userIds, int specialSign);

	/**
	 * 查询账号的联络员
	 * @param userId - 用户id, 如果为空则查当前登录用户的联络员
	 * @return
	 */
	public List<UserInfo> queryContact(Long userId);

	/**
	 * 查询当前登录用户是否有所有区域的权限
	 * @return
	 */
    boolean hasAllAreas();
    /**
     * 子线程查询用
     * @param sql
     * @param userId
     * @return
     */
    public String processAuthorityByThread(String sql,long userId);
    
    public JsonObject getUsers(UserAccountDto user);
    public UserRightDto getUserRight(String name);
    
    public void isUserOperationAccess(long userId);
    public boolean isSuperUser(Long userId);
}
