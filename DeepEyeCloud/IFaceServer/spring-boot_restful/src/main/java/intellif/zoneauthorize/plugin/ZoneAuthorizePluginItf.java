package intellif.zoneauthorize.plugin;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import intellif.core.tree.itf.TreeNode;

/**
 * 区域授权插件接口
 * 
 * @author pengqirong
 */
public interface ZoneAuthorizePluginItf<T extends TreeNode> {

    /**
     * 类型
     * 
     * @return
     */
    Class<T> zoneClass();

    /**
     * 用户与区域关联关系类
     * 
     * @return
     */
    Class<?> userToZoneClass();

    /**
     * 查询出所有的PathTreeNode
     * 
     * @return
     */
    List<T> findAll();

    /**
     * 查询出所有的PathTreeNode
     * 
     * @return
     */
    Set<Long> findIdSet(long userId);

    /**
     * 表版本号
     * 
     * @return
     */
    long updateVersion();

    /**
     * 删除用户对应的区域ID
     * 
     * @return
     */
    void remove(long userId);

    /**
     * 保存用户对应的区域ID
     * 
     * @return
     */
    void save(long userId, Collection<Long> ids);
    
    /**
     * 外键
     * 
     * @return
     */
    String foreignKey();
}
