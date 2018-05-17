package intellif.zoneauthorize.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;

import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.dao.UserDao;
import intellif.database.dao.RoleDao;
import intellif.database.dao.impl.RoleInfoImpl;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.exception.MsgException;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.ZoneUtil;
import intellif.zoneauthorize.bean.UserZoneAuthorize;
import intellif.zoneauthorize.bean.ZoneQuery;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.itf.Zone;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;
import intellif.zoneauthorize.util.ZoneAuthorizeUtil;

@Service
public class ZoneAuthorizeServiceImpl implements ZoneAuthorizeServiceItf {

    private static Logger LOG = LogManager.getLogger(ZoneAuthorizeServiceImpl.class);

    @Autowired
    ZoneAuthorizeCacheItf zoneAuthorizeCache;
    @Autowired
    UserDao userDao;
    @Autowired
    private RoleInfoImpl roleRepository;
    @Autowired
    private UserServiceItf userService;
    
    @Override
    public String ucsqlManipulate(Class<? extends TreeNode> treeNodeClass, String origin) {
        long userId = userId();
        /*
         * if (!opened(userId)) { return " "; }
         */
        Set<Long> idSet = idSet(treeNodeClass, userId);
        if (CollectionUtils.isEmpty(idSet)) {
            return " " + origin + " (" + Long.MIN_VALUE + ") ";
        }
        return " " + origin + " (" + StringUtils.join(idSet, ", ") + ") ";
    }
    
    @Override
    public String ucsqlManipulateThread(Class<? extends TreeNode> treeNodeClass, String origin, long userId) {
        if(0 == userId){
            userId = userId();
        }
        Set<Long> idSet = idSet(treeNodeClass, userId);
        if (CollectionUtils.isEmpty(idSet)) {
            return " " + origin + " (" + Long.MIN_VALUE + ") ";
        }
        return " " + origin + " (" + StringUtils.join(idSet, ", ") + ") ";
    }

    /**
     * 返回指定用户的所有指定区域或摄像头集合
     */
    @Override
    public Set<Long> idSet(Class<? extends TreeNode> treeNodeClass, Long userId) {
        LOG.info("start idSet method,userId:"+userId);
        Validate.notNull(treeNodeClass, "区域类型为空！");
        try {
            // 获取用户信息以及区域信息
            if (null == userId) {
                userId = userId();
            }
             UserInfo ui = userDao.findOne(userId);
             String roleName = roleRepository.findById(ui.getRoleId()).getName();
             ui.setRoleTypeName(roleName);
             boolean isSpecial = ui.getRoleTypeName().equals(GlobalConsts.SUPER_ADMIN) || (ui.getSpecialSign() > 0);
            Tree tree = LocalCache.tree;
            Set<Long> idSet = new HashSet<>();
            if(isSpecial){
                //全区域搜索账户，返回所有区域
                return tree.idSet(treeNodeClass);
            }
            UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);

            // 如果开关为关,返回整棵树中符合条件的ID
            if (!userZoneAuthorize.getOpened()) {
                return tree.idSet(treeNodeClass);
            } else {
            	// 如果开关为开，则返回半选以及全选的符合条件的ID
                Map<Long, String> choiceStatusMap = userZoneAuthorize.getChoiceMap().get(treeNodeClass);
                if (MapUtils.isEmpty(choiceStatusMap)) {
                    return ZoneUtil.filterCameraByCType(idSet, treeNodeClass, ui);
                }
                for (Map.Entry<Long, String> entry : choiceStatusMap.entrySet()) {
                    if (entry.getValue().compareTo(ZoneConstant.ZONE_CHOICE_NONE) > 0) {
                        idSet.add(entry.getKey());
                    }
                }
            }
            LOG.info("end idSet method,userId:"+userId);
            return ZoneUtil.filterCameraByCType(idSet, treeNodeClass, ui);
        } catch (Exception e) {
            LOG.error("treeNodeClass is " + treeNodeClass + ", userId is " + userId + ", catch exception: ", e);
            throw new IllegalArgumentException("获取区域ID集合失败", e);
        }
    }

    /**
     * 返回指定用户的所有指定区域或摄像头集合,不进行摄像头1234检查
     */
    @Override
    public Set<Long> idSetNotCTypeCheck(Class<? extends TreeNode> treeNodeClass, Long userId) {
        LOG.info("start idSet method,userId:"+userId);
        Validate.notNull(treeNodeClass, "区域类型为空！");
        try {
            // 获取用户信息以及区域信息
            if (null == userId) {
                userId = userId();
            }
             UserInfo ui = userDao.findOne(userId);
             String roleName = roleRepository.findById(ui.getRoleId()).getName();
             ui.setRoleTypeName(roleName);
             boolean isSpecial = ui.getRoleTypeName().equals(GlobalConsts.SUPER_ADMIN) || (ui.getSpecialSign() > 0);
            Tree tree = LocalCache.tree;
            Set<Long> idSet = new HashSet<>();
            if(isSpecial){
                //全区域搜索账户，返回所有区域
                return tree.idSet(treeNodeClass);
            }
            UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);

            // 如果开关为关,返回整棵树中符合条件的ID
            if (!userZoneAuthorize.getOpened()) {
                return tree.idSet(treeNodeClass);
            } else {
                // 如果开关为开，则返回半选以及全选的符合条件的ID
                Map<Long, String> choiceStatusMap = userZoneAuthorize.getChoiceMap().get(treeNodeClass);
                if (MapUtils.isEmpty(choiceStatusMap)) {
                    return idSet;
                }
                for (Map.Entry<Long, String> entry : choiceStatusMap.entrySet()) {
                    if (entry.getValue().compareTo(ZoneConstant.ZONE_CHOICE_NONE) > 0) {
                        idSet.add(entry.getKey());
                    }
                }
            }
            LOG.info("end idSet method,userId:"+userId);
            return idSet;
        } catch (Exception e) {
            LOG.error("treeNodeClass is " + treeNodeClass + ", userId is " + userId + ", catch exception: ", e);
            throw new IllegalArgumentException("获取区域ID集合失败", e);
        }
    }
  
  
    
    @Override
    public <T extends Zone> List<T> filterById(Class<? extends TreeNode> treeNodeClass, Collection<T> ts, Long userId) {
        try {
            LOG.info("start filterById method,userId:"+userId);
            if (null == ts) {
                return null;
            }

            // 如果用户ID不传，则使用当前登录用户ID
            if (null == userId) {
                userId = userId();
            }
            List<T> retList = new ArrayList<>();
            // 根据区域的ID进行过滤
            Set<Long> idSet = idSet(treeNodeClass, userId);
            if (CollectionUtils.isEmpty(idSet)) {
                return new ArrayList<>();
            }

            for (T t : ts) {
                if (null == t) {
                    continue;
                }
                if (idSet.contains(t.zoneId())) {
                    retList.add(t);
                }
            }
            LOG.info("end filterById method,userId:"+userId);
            return retList;
        } catch (Exception e) {
            LOG.error("treeNodeClass is " + treeNodeClass + ", userId is " + userId + ", ts is " + ts + ", catch exception: ", e);
            throw new IllegalArgumentException("根据用户授权区域ID过滤列表失败", e);
        }
    }

    @Override
    public List<Long> filterIds(Class<? extends TreeNode> treeNodeClass, Collection<Long> ids, Long userId) {
        LOG.info("start filterIds method,userId:"+userId);
        try {
            if (null == ids) {
                return null;
            }

            // 如果用户ID不传，则使用当前登录用户ID
            if (null == userId) {
                userId = userId();
            }
            List<Long> retList = new ArrayList<>();
            // 根据区域的ID进行过滤
            Set<Long> idSet = idSet(treeNodeClass, userId);
            if (CollectionUtils.isEmpty(idSet)) {
                return new ArrayList<>();
            }
            for (Long id : ids) {
                if (null == id) {
                    continue;
                }
                if (idSet.contains(id)) {
                    retList.add(id);
                }
            }
            LOG.info("end filterIds method,userId:"+userId);
            return retList;
        } catch (Exception e) {
            LOG.error("treeNodeClass is " + treeNodeClass + ", userId is " + userId + ", ids is " + ids + ", catch exception: ", e);
            throw new IllegalArgumentException("根据用户授权区域ID过滤ID列表失败", e);
        }
    }
    
    /**
     * 过滤出未通过的数据集合
     * @param treeNodeClass
     * @param ids
     * @param userId
     * @return
     */
    @Override
    public List<Long> filterIdsNotPass(Class<? extends TreeNode> treeNodeClass, Collection<Long> ids, Long userId) {
        LOG.info("start filterIdsNotPass method,userId:"+userId);
        try {
            if (null == ids) {
                return null;
            }

            // 如果用户ID不传，则使用当前登录用户ID
            if (null == userId) {
                userId = userId();
            }
            List<Long> retList = new ArrayList<>();
            // 根据区域的ID进行过滤
            Set<Long> idSet = idSet(treeNodeClass, userId);
            if (CollectionUtils.isEmpty(idSet)) {
                return new ArrayList<>();
            }
            for (Long id : ids) {
                if (null == id) {
                    continue;
                }
                if (!idSet.contains(id)) {
                    retList.add(id);
                }
            }
            LOG.info("end filterIdsNotPass method,userId:"+userId);
            return retList;
        } catch (Exception e) {
            LOG.error("treeNodeClass is " + treeNodeClass + ", userId is " + userId + ", ids is " + ids + ", catch exception: ", e);
            throw new IllegalArgumentException("根据用户授权区域ID过滤ID列表失败", e);
        }
    }

    @Override
    public void checkIds(Class<? extends TreeNode> treeNodeClass, Object ids) {
        Validate.notNull(ids, "入参为空！");
        Validate.notNull(treeNodeClass, "入参为空！");
        List<Long> inputIds = ZoneAuthorizeUtil.convertList(ids);
        Validate.notEmpty(inputIds, "ID列表为空！");
        long userId = userId();

        Set<Long> idSet = idSet(treeNodeClass, userId);
        inputIds.removeAll(idSet);
        if (!inputIds.isEmpty()) {
            throw new MsgException("没有ID(" + StringUtils.join(inputIds, ", ") + ")的权限！",RequestConsts.response_right_error);
        }

    }

    @Override
    public long userId() {
        // 获取登录用户的ID
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        Validate.notNull(userInfo, "获取登录信息失败！请您重新登录！");
        Long userId = userInfo.getId();
        Validate.notNull(userId, "获取登录信息失败！请您重新登录！");
        return userId;
    }

    @Override
    @Transactional
    public void save(Map<String, String> idMap, boolean opened, Long userId) {
        // 如果系统区域授权开关是关闭的，则不处理该用户的区域授权数据
        if (!zoneAuthorizeCache.zoneAuthorizeSwitch()) {
            return;
        }
        Tree tree = LocalCache.tree;
        Long loginId = userId();
        UserZoneAuthorize loginZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId()), loginId, tree);
        UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);
        // 如果登录用户区域授权开关为开，他不能将其他用户的区域授权开关设置为关
        Validate.isTrue(!(loginZoneAuthorize.getOpened() && !opened && userZoneAuthorize.getOpened()), "您无权关闭其它用户的区域授权！");

        UserZoneAuthorize saveZoneAuthorize = new UserZoneAuthorize();
        saveZoneAuthorize.setOpened(opened);
        saveZoneAuthorize.setUserId(userId);
        if (!opened) {
            // 保存用户区域授权信息
            zoneAuthorizeCache.saveUserZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), saveZoneAuthorize, userZoneAuthorize, loginId);
            return;
        }

        // 将 idMap 转换成 class 与 id集合的映射
        Map<Class<? extends TreeNode>, Set<Long>> newClassToIdSetMap = convertClassToIdSetMap(idMap);

        // 如果登录用户的区域授权开关为关，则直接保存此次的用户授权区域信息
        if (!loginZoneAuthorize.getOpened()) {
            saveZoneAuthorize.getClassToIdSetMap().clear();
            saveZoneAuthorize.getClassToIdSetMap().putAll(newClassToIdSetMap);
        } else {
            Map<Class<? extends TreeNode>, Set<Long>> map = parse(tree, userZoneAuthorize.getClassToIdSetMap(), newClassToIdSetMap,
                    loginZoneAuthorize.getClassToIdSetMap());
            saveZoneAuthorize.getClassToIdSetMap().clear();
            saveZoneAuthorize.getClassToIdSetMap().putAll(map);
        }
        // 保存用户区域授权信息
        zoneAuthorizeCache.saveUserZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), saveZoneAuthorize, userZoneAuthorize, loginId);
    }

    /**
     * 将 idMap 转换成 class 与 id集合的映射
     * @param idMap
     * @return
     */
    private Map<Class<? extends TreeNode>, Set<Long>> convertClassToIdSetMap(Map<String, String> idMap) {
        Map<Class<? extends TreeNode>, Set<Long>> retMap = new HashMap<>();
        if (MapUtils.isEmpty(idMap)) {
            return retMap;
        }
        for (Map.Entry<String, String> entry : idMap.entrySet()) {
            Class<? extends TreeNode> clazz = ZoneConfig.getNodeTypeMap().get(entry.getKey());
            if (null == clazz) {
                continue;
            }
            retMap.put(clazz, Sets.newHashSet(ZoneAuthorizeUtil.convertList(entry.getValue())));
        }
        return retMap;
    }

    /**
     * 分析出用户最终要保存的区域授权信息
     * @param tree 区域信息树
     * @param oldClassToIdSetMap 旧的编辑用户授权区域类型与ID集合映射
     * @param newClassToIdSetMap 新的编辑用户授权区域类型与ID集合映射
     * @param loginClassToIdSetMap 登录用户授权区域类型与ID集合映射
     * @return 需要保存的编辑用户授权区域类型与ID集合映射
     */
    private Map<Class<? extends TreeNode>, Set<Long>> parse(Tree tree, Map<Class<? extends TreeNode>, Set<Long>> oldClassToIdSetMap,
            Map<Class<? extends TreeNode>, Set<Long>> newClassToIdSetMap, Map<Class<? extends TreeNode>, Set<Long>> loginClassToIdSetMap) {
        // 寻找叶子
        Map<Class<? extends TreeNode>, Set<Long>> oldLeafMap = leaf(tree, oldClassToIdSetMap);
        Map<Class<? extends TreeNode>, Set<Long>> newLeafMap = leaf(tree, newClassToIdSetMap);
        Map<Class<? extends TreeNode>, Set<Long>> loginLeafMap = leaf(tree, loginClassToIdSetMap);
        // 让每个类都有不为null的集合
        for (Class clazz : tree.getClassList()) {
            oldLeafMap.put(clazz, null == oldLeafMap.get(clazz) ? new HashSet<>() : oldLeafMap.get(clazz));
            newLeafMap.put(clazz, null == newLeafMap.get(clazz) ? new HashSet<>() : newLeafMap.get(clazz));
            loginLeafMap.put(clazz, null == loginLeafMap.get(clazz) ? new HashSet<>() : loginLeafMap.get(clazz));
        }

        // 先用 oldLeafMap remove loginLeafMap
        for (Class clazz : tree.getClassList()) {
            oldLeafMap.get(clazz).removeAll(loginLeafMap.get(clazz));
        }

        // 用 newLeafMap retain loginLeafMap
        for (Class clazz : tree.getClassList()) {
            newLeafMap.get(clazz).retainAll(loginLeafMap.get(clazz));
        }

        // 最后 newLeafMap add oldLeafMap
        for (Class clazz : tree.getClassList()) {
            newLeafMap.get(clazz).addAll(oldLeafMap.get(clazz));
        }

        // 将 newLeafMap 压缩
        return compress(tree, newLeafMap);
    }

    /**
     * 压缩授权区域类型与ID集合映射
     * @param tree
     * @param classToIdSetMap
     * @return
     */
    protected Map<Class<? extends TreeNode>, Set<Long>> compress(Tree tree, Map<Class<? extends TreeNode>, Set<Long>> classToIdSetMap) {
        Map<Class<? extends TreeNode>, Set<Long>> retMap = new HashMap<>();
        compress(tree, tree.getRootList(), classToIdSetMap, retMap);
        return retMap;
    }
    /**
     * 嵌套压缩授权区域类型与ID集合映射
     * @param treeNodeList 节点列表
     * @param classToIdSetMap 授权区域类型与ID集合映射
     * @return
     */
    protected void compress(Tree tree, List<TreeNode> treeNodeList, Map<Class<? extends TreeNode>, Set<Long>> classToIdSetMap,
            Map<Class<? extends TreeNode>, Set<Long>> retMap) {
        Map<Class<? extends TreeNode>, Set<Long>> leafMap = new HashMap<>();
        for (TreeNode treeNode : treeNodeList) {
        	// 避免过多gc，共用一个叶子Map实例，每次先清空
            leafMap.clear();
            // 获得节点对应的叶子Map
            leaf(tree, treeNode, leafMap);
            // 分析出当前节点的选择状态（不选，半选，全选）
            String choice = choice(classToIdSetMap, leafMap);
            // 如果全选，则将该节点加入返回Map中
            if (ZoneConstant.ZONE_CHOICE_ALL.equals(choice)) {
                Set<Long> set = retMap.get(treeNode.getClass());
                if (null == set) {
                    set = new HashSet<>();
                    retMap.put(treeNode.getClass(), set);
                }
                set.add(treeNode.getId());
                continue;
            }
            // 如果是不选，则忽略该节点
            if (ZoneConstant.ZONE_CHOICE_NONE.equals(choice)) {
                continue;
            }
            // 如果是半选，则嵌套压缩它的孩子节点
            if (treeNode.getHasChild()) {
                compress(tree, treeNode.getChildList(), classToIdSetMap, retMap);
            }
            // 如果是半选，则嵌套压缩它的下级节点
            if (treeNode.getHasNext()) {
                compress(tree, treeNode.getNextList(), classToIdSetMap, retMap);
            }
        }
    }

    /**
     * 根据两个叶子集合的包含关系，给出应该不选、半选、还是全选
     * @param bigClassToIdSetMap 大的叶子集合
     * @param smallClassToIdSetMap 小的叶子集合
     * @return
     */
    protected String choice(Map<Class<? extends TreeNode>, Set<Long>> bigClassToIdSetMap, Map<Class<? extends TreeNode>, Set<Long>> smallClassToIdSetMap) {
        boolean hasSame = false;
        boolean hasNotSame = false;
        if (MapUtils.isEmpty(smallClassToIdSetMap)) {
            return ZoneConstant.ZONE_CHOICE_ALL;
        }
        if (MapUtils.isEmpty(bigClassToIdSetMap)) {
            return ZoneConstant.ZONE_CHOICE_NONE;
        }
        for (Map.Entry<Class<? extends TreeNode>, Set<Long>> entry : smallClassToIdSetMap.entrySet()) {
            Set<Long> smallSet = entry.getValue();
            Set<Long> bigSet = bigClassToIdSetMap.get(entry.getKey());
            if (!CollectionUtils.isEmpty(smallSet)) {
                if (CollectionUtils.isEmpty(bigSet)) {
                    hasNotSame = true;
                }
                for (Long id : smallSet) {
                    if (bigSet.contains(id)) {
                        hasSame = true;
                    } else {
                        hasNotSame = true;
                    }
                }
            }
        }
        if (!hasSame) {
            return ZoneConstant.ZONE_CHOICE_NONE;
        }
        if (!hasNotSame) {
            return ZoneConstant.ZONE_CHOICE_ALL;
        }
        return ZoneConstant.ZONE_CHOICE_PART;
    }

    @Override
    public List<TreeNode> child(ZoneQuery zoneQuery) {
        long userId = userId();
        UserInfo ui = userDao.findOne(userId);
        Tree tree = LocalCache.tree;
        UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);
        UserZoneAuthorize editUserZoneAuthorize = null;
        if (null != zoneQuery.getUserId() && zoneQuery.getUserId().longValue() > 0L) {
            editUserZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(zoneQuery.getUserId()), zoneQuery.getUserId(), tree);
        }
        return zoneAuthorizeCache.child(zoneAuthorizeCache.childKey(zoneQuery, userId,ui.getSpecialSign(),ui.getcTypeIds()), zoneQuery, tree, userZoneAuthorize, editUserZoneAuthorize);
    }

    /**
     * 获取叶子 类型与ID集合列表
     * @param tree 树
     * @param classToIdSetMap 类型与ID集合的映射
     */
    private Map<Class<? extends TreeNode>, Set<Long>> leaf(Tree tree, Map<Class<? extends TreeNode>, Set<Long>> classToIdSetMap) {
        Map<Class<? extends TreeNode>, Set<Long>> map = new HashMap<>();
        for (Map.Entry<Class<? extends TreeNode>, Set<Long>> entry : classToIdSetMap.entrySet()) {
            for (Long id : entry.getValue()) {
                TreeNode treeNode = tree.treeNode(entry.getKey(), id);
                leaf(tree, treeNode, map);
            }
        }
        return map;
    }


    private void leaf(Tree tree, TreeNode treeNode, Map<Class<? extends TreeNode>, Set<Long>> leafMap) {
        if (null == treeNode) {
            return;
        }
        if (!treeNode.getHasChild() && !treeNode.getHasNext()) {
            Set<Long> idSet = leafMap.get(treeNode.getClass());
            if (null == idSet) {
                idSet = new HashSet<>();
                leafMap.put(treeNode.getClass(), idSet);
            }
            idSet.add(treeNode.getId());
        }
        if (treeNode.getHasChild()) {
            for (TreeNode temp : treeNode.getChildList()) {
                leaf(tree, temp, leafMap);
            }
        }
        if (treeNode.getHasNext()) {
            for (TreeNode temp : treeNode.getNextList()) {
                leaf(tree, temp, leafMap);
            }
        }
    }



    /**
     * 统计叶子数量，暂未使用
     * @param node 节点
     * @param leafClass 叶子类型
     * @param userZoneAuthorize 用户区域授权信息
     * @return
     */
    private int countLeaf(TreeNode node, Class leafClass, UserZoneAuthorize userZoneAuthorize) {
        if (null == node) {
            return 0;
        }
        if (leafClass.equals(node.getClass()) && !node.getHasChild() && !node.getHasNext()) {
            if (!userZoneAuthorize.getOpened() || ZoneConstant.ZONE_CHOICE_PART.compareTo(userZoneAuthorize.choice(node.getClass(), node.getId())) <= 0) {
                return 1;
            }
        }
        int count = 0;
        if (node.getHasChild()) {
            for (TreeNode temp : node.getChildList()) {
                count += countLeaf(temp, leafClass, userZoneAuthorize);
            }
        }
        if (node.getHasNext()) {
            for (TreeNode temp : node.getNextList()) {
                count += countLeaf(temp, leafClass, userZoneAuthorize);
            }
        }
        return count;
    }

    @Override
    public boolean isSelf(Class<? extends TreeNode> class1, Long id1, Class<CameraInfo> class2, Long id2) {
        if (null == class1 || null == id1 || null == class2 || null == id2) {
            return false;
        }
        return class1.equals(class2) && id1.equals(id2);
    }

    @Override
    public boolean isOffspring(Class<? extends TreeNode> forefatherClass, Long forefatherId, Class<CameraInfo> offspringClass, Long offspringForefatherId) {
        Tree tree = LocalCache.tree;
        return tree.isOffspring(forefatherClass, forefatherId, offspringClass, offspringForefatherId);
    }

    @Override
    public <T extends TreeNode> List<T> findAll(Class<T> treeNodeClass,Long userId) {
        if(null == userId || userId.intValue() == 0){
            userId = userId();
        }else{
          userService.isUserOperationAccess(userId);
        }
        UserInfo ui = userDao.findOne(userId);
        boolean isSuper = userService.isSuperUser(userId);
        Tree tree = LocalCache.tree;
        UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);
        List<T> nodeList = tree.findAllWithoutTreeInfo(treeNodeClass);
        if (!userZoneAuthorize.getOpened() || ui.getSpecialSign() > 0 || isSuper) {
            return nodeList;
        }
        List<T> retList = new ArrayList<>();
        for (TreeNode node : nodeList) {
            if (ZoneConstant.ZONE_CHOICE_PART.compareTo(userZoneAuthorize.choice(node.getClass(), node.getId())) <= 0) {
                retList.add((T) node);
            }
        }
        return retList;
    }

    @Override
    public <T extends TreeNode> T treeNode(Class<T> treeNodeClass, Long id, Long userId) {
        if (null == treeNodeClass || null == id) {
            return null;
        }
        if(null == userId){
            userId = userId();
        }
        Tree tree = LocalCache.tree;
        UserZoneAuthorize userZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId), userId, tree);
        T t = (T) tree.treeNodeWithOutTreeInfo(treeNodeClass, id);
        if (null == t) {
            return t;
        }
        // 如果有权限，则返回
        if (ZoneConstant.ZONE_CHOICE_PART.compareTo(userZoneAuthorize.choice(t.getClass(), t.getId())) <= 0) {
            return t;
        }
        return null;
    }

    @Override
    public List<TreeNode> forefatherList(ZoneQuery zoneQuery) {
        Tree tree = LocalCache.tree;
        return tree.forefatherList(ZoneConfig.getNodeTypeMap().get(zoneQuery.getNodeType()), zoneQuery.getId(), null, true);
    }

    @Override
    public List<TreeNode> offspring(Class<? extends TreeNode> nodeClass, Long nodeId, final Class<? extends TreeNode> filterClass) {
        Tree tree = LocalCache.tree;
        Long loginId = userId();
        UserZoneAuthorize loginZoneAuthorize = zoneAuthorizeCache.userZoneAuthorize(zoneAuthorizeCache.userZoneAuthorizeKey(userId()), loginId, tree);
        return zoneAuthorizeCache.offspring(zoneAuthorizeCache.offspringKey(loginId, nodeClass, nodeId, filterClass), loginZoneAuthorize, tree, nodeClass, nodeId, filterClass);
    }

    @Override
    public boolean userZoneAuthorizeSwitch(Long userId) {
        return zoneAuthorizeCache.userZoneAuthorizeSwitch(userId);
    }

	@Override
	public boolean zoneAuthorizeSwitch() {
		return zoneAuthorizeCache.zoneAuthorizeSwitch();
	}
}
