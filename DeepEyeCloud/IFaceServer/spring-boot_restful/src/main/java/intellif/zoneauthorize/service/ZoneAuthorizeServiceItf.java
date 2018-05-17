package intellif.zoneauthorize.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import intellif.core.tree.itf.TreeNode;
import intellif.database.entity.CameraInfo;
import intellif.zoneauthorize.bean.ZoneQuery;
import intellif.zoneauthorize.itf.Zone;

/**
 * 区域授权服务
 * 
 * @author pengqirong
 */
public interface ZoneAuthorizeServiceItf {

    /**
     * 拼装部分sql e.g. 输入“and c.id in”, 输出"and c.id in(1,2,3)"
     * @param treeNodeClass
     * @param origin
     * @return
     */
    String ucsqlManipulate(Class<? extends TreeNode> treeNodeClass, String origin);

    /**
     * 根据类型查询所有有权限的节点列表
     */
    <T extends TreeNode> List<T> findAll(Class<T> treeNodeClass,Long userId);

    /**
     * 区域ID集合
     * 
     * @return 区域ID集合
     */
    Set<Long> idSet(Class<? extends TreeNode> treeNodeClass, Long userId);

    /**
     * 根据cameraId过滤列表
     * 
     * @return
     */
    <T extends Zone> List<T> filterById(Class<? extends TreeNode> treeNodeClass, Collection<T> ts, Long userId);

    /**
     * 过滤cameraId列表
     * 
     * @return
     */
    List<Long> filterIds(Class<? extends TreeNode> treeNodeClass, Collection<Long> ids, Long userId);

    /**
     * 当前登录用户是否有某些摄像头的权限,没有的话会抛出异常
     * 
     * @param ids
     *            支持String[], String（用英文逗号分隔多id）, Long[],
     *            Iterable&lt;String&gt;, Iterable&lt;Long&gt;
     * @return
     */
    void checkIds(Class<? extends TreeNode> treeNodeClass, Object ids);

    /**
     * 获取用户ID
     * 
     * @return
     */
    long userId();

    /**
     * 保存区域和摄像头
     * 
     * @param userAreaAuthorize
     *            用户区域授权
     */
    void save(Map<String, String> idMap, boolean opened, Long userId);


    /**
     * 查询孩子
     * 
     * @param areaQuery
     */
    List<TreeNode> child(ZoneQuery areaQuery);

    /**
     * 是否为自己
     * 
     * @param class1
     * @param id1
     * @param class2
     * @param id2
     * @return
     */
    boolean isSelf(Class<? extends TreeNode> class1, Long id1, Class<CameraInfo> class2, Long id2);

    /**
     * 是否为后代
     * 
     * @param forefatherClass
     * @param forefatherId
     * @param offspringClass
     * @param offspringForefatherId
     * @return
     */
    boolean isOffspring(Class<? extends TreeNode> forefatherClass, Long forefatherId, Class<CameraInfo> offspringClass, Long offspringForefatherId);

    /**
     * 获取节点信息
     * 
     * @param treeNodeClass
     * @param id
     * @return
     */
    <T extends TreeNode> T treeNode(Class<T> treeNodeClass, Long id,Long userId);

    /**
     * 祖先节点列表
     * 
     * @param zoneQuery
     * @return
     */
    List<TreeNode> forefatherList(ZoneQuery zoneQuery);

    /**
     * 获取所有后代列表
     * 
     * @param nodeClass
     * @param nodeId
     * @param filterClass
     * @return
     */
    List<TreeNode> offspring(Class<? extends TreeNode> nodeClass, Long nodeId, Class<? extends TreeNode> filterClass);

    /**
     * 用户区域授权开关
     * 
     * @param userId
     * @return
     */
    boolean userZoneAuthorizeSwitch(Long userId);
    
    /**
     * 区域授权总开关
     * 
     * @return
     */
    boolean zoneAuthorizeSwitch();
    
    /**
     * 子线程调用，需要手动传userId参数
     * @param treeNodeClass
     * @param origin
     * @param userId
     * @return
     */
    public String ucsqlManipulateThread(Class<? extends TreeNode> treeNodeClass, String origin, long userId);
   
    /**
     * 过滤出非通过的数据集合
     * @param treeNodeClass
     * @param ids
     * @param userId
     * @return
     */
    public List<Long> filterIdsNotPass(Class<? extends TreeNode> treeNodeClass, Collection<Long> ids, Long userId);

    /**
     * 查找指定类型用户授权区域集合，不进行摄像头cType 1234类检查
     * @param treeNodeClass
     * @param userId
     * @return
     */
    public Set<Long> idSetNotCTypeCheck(Class<? extends TreeNode> treeNodeClass, Long userId);
}
