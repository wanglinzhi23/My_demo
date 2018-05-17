package intellif.zoneauthorize.service;

import java.util.List;
import java.util.Map;

import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.zoneauthorize.bean.UserZoneAuthorize;
import intellif.zoneauthorize.bean.ZoneQuery;

/**
 * 区域授权缓存
 * 
 * @author pengqirong
 */
public interface ZoneAuthorizeCacheItf {


    
    
    
    public void refreshTree();
    
    
	/**
	 * 
     * 用户区域授权信息
     * 
     * @param key 缓存主键
     * @param userId 用户ID
	 * @param tree 区域信息树
	 * @return
	 */
    UserZoneAuthorize userZoneAuthorize(String key, long userId, Tree tree);


    /**
     * 保存用户区域授权信息
     * @param key 缓存主键
     * @param saveZoneAuthorize 需要保存的用户区域授权信息
     * @param oldZoneAuthorize 旧的用户区域授权信息
     * @param loginUserId 登录用户ID
     */
    void saveUserZoneAuthorize(String key, UserZoneAuthorize saveZoneAuthorize, UserZoneAuthorize oldZoneAuthorize, Long loginUserId);

    /**
     * 区域信息树（走缓存）
     * 
     * @return
     */
    Tree tree();

    /**
     * 区域信息树（不走缓存）
     * 
     * @return
     */
    Tree treeWithoutCache();

    /**
     * 更新区域信息树
     * 
     * @return
     */
    Tree updateTree();

    /**
     * 缓存中区域版本信息，用于更新区域缓存
     * 
     * @return
     */
    Map<Class<? extends TreeNode>, Long> versionMap();

    /**
     * 数据库中区域版本信息，用于更新区域缓存
     * 
     * @return
     */
    Map<Class<? extends TreeNode>, Long> versionMapWithoutCache();

    /**
     * 更新区域版本信息
     * 
     * @param versionMap 版本信息
     * @return
     */
    Map<Class<? extends TreeNode>, Long> updateVersionMap(Map<Class<? extends TreeNode>, Long> versionMap);

    /**
     * 区域修改信息，用于更新区域缓存
     * 
     * @param object 用于生成主键的附加对象
     *          
     * @return 缓存主键
     */
    String key(String method, Object... object);

    /**
     * 更新主键
     */
    void refreshKey();

    /**
     * 缓存中区域授权总开关
     * 
     * @return
     */
    boolean zoneAuthorizeSwitch();

    /**
     * 数据库中区域授权总开关
     * 
     * @return
     */
    boolean zoneAuthorizeSwitchWithoutCache();

    /**
     * 用户区域授权开关
     * 
     * @param userId
     * @return
     */
    boolean userZoneAuthorizeSwitch(Long userId);
    
    /**
     * 查询孩子节点列表
     * @param key 主键
     * @param zoneQuery 查询参数
     * @param tree 节点信息树
     * @param loginZoneAuthorize 登录区域授权信息
     * @param editUserZoneAuthorize 编辑用户区域授权信息
     * @return
     */
    List<TreeNode> child(String key, ZoneQuery zoneQuery, Tree tree, UserZoneAuthorize loginZoneAuthorize, UserZoneAuthorize editUserZoneAuthorize);

    /**
     * 获取后代节点
     * @param key 主键
     * @param loginZoneAuthorize 登录用户区域授权信息
     * @param tree 节点信息树
     * @param nodeClass 节点类型
     * @param nodeId 节点ID
     * @param filterClass 过滤类型（返回节点类型）
     * @return
     */
    List<TreeNode> offspring(String key, UserZoneAuthorize loginZoneAuthorize, Tree tree, Class<? extends TreeNode> nodeClass, Long nodeId,
            Class<? extends TreeNode> filterClass);

    /**
     * 用户区域授权主键
     * @param userId
     * @return
     */
    String userZoneAuthorizeKey(long userId);

    /**
     * 查询孩子的主键
     * @param zoneQuery 区域查询参数
     * @param userId 用户ID
     * @param userId 全区域用户标记
     * @param cTypeStr 用户摄像头123类点属性
     * @return
     */
    String childKey(ZoneQuery zoneQuery, Long userId, int specialSign,String cTypeStr);

    /**
     * 查询后代的主键
     * @param userId 用户ID
     * @param nodeClass 用户类型
     * @param nodeId 节点ID
     * @param filterClass 过滤类型（返回类型）
     * @return
     */
    String offspringKey(Long userId, Class<? extends TreeNode> nodeClass, Long nodeId, Class<? extends TreeNode> filterClass);

}
