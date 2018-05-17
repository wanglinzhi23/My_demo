package intellif.zoneauthorize.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.ssm.api.InvalidateAssignCache;
import com.google.code.ssm.api.InvalidateSingleCache;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughAssignCache;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.code.ssm.api.ReturnDataUpdateContent;
import com.google.code.ssm.api.UpdateAssignCache;
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;
import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.Tree.TreeBuilder;
import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.ZoneUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.UserInfo;
import intellif.zoneauthorize.bean.UserZoneAuthorize;
import intellif.zoneauthorize.bean.ZoneQuery;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;
import intellif.database.entity.SystemSwitch;
import intellif.database.entity.UserSwitch;

@Service
public class ZoneAuthorizeCacheImpl implements ZoneAuthorizeCacheItf {
    
    private static Logger LOG = LogManager.getLogger(ZoneAuthorizeCacheImpl.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleRepository;

    private volatile long keyBase = System.currentTimeMillis();

    /**
     * 查询系统区域授权开关SQL
     */
    private static final String SYSTEM_SWITCH_SQL = "select * from " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "."
            + GlobalConsts.T_NAME_SYSTEM_SWITCH + " where opened = ? and switch_type = ?";

    /**
     * 查询用户区域授权开关SQL
     */
    private static final String USER_SWITCH_SQL = "select * from " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "."
            + GlobalConsts.T_NAME_USER_SWITCH + " where opened = ? and user_id = ?";

    /**
     * 删除用户区域授权开关SQL
     */
    private static final String DELETE_USER_SWITCH = "delete from " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "."
            + GlobalConsts.T_NAME_USER_SWITCH + " where user_id = ?";
    
    /**
     * 保存用户区域授权开关SQL
     */
    private static final String INSERT_USER_SWITCH = "insert into " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "."
            + GlobalConsts.T_NAME_USER_SWITCH + " (user_id, opened, creator) values (?, ?, ?)";


    @Override
    //@ReadThroughSingleCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 120 * 60)
    public UserZoneAuthorize userZoneAuthorize(@ParameterValueKeyProvider String key, long userId, Tree tree) {
        LOG.info("userCameraDto not use cache, userId is {}", userId);
        UserZoneAuthorize userAreaAuthorize = new UserZoneAuthorize();
        userAreaAuthorize.setUserId(userId);
        userAreaAuthorize.setOpened(userZoneAuthorizeSwitch(userId));

        if (!userAreaAuthorize.getOpened()) {
            return userAreaAuthorize;
        }
        
        Map<Class<? extends TreeNode>, Set<Long>> map = userAreaAuthorize.getClassToIdSetMap();
        // 查询区域列表
        for (Class<? extends TreeNode> clazz : ZoneConfig.getClassList()) {
            try {
                map.put(clazz, ZoneConfig.getPluginMap().get(clazz).findIdSet(userId));
            } catch(Exception e) {
                LOG.error("get " + clazz + " update version catch exception: ", e);
            }
        }
        userAreaAuthorize.initChoiceMap(tree);
        return userAreaAuthorize;
    }
    
    @Override
    public String userZoneAuthorizeKey(long userId) {
        return key("userZoneAuthorizeKey", userId);
    }
    
    @Override
    public boolean userZoneAuthorizeSwitch(Long userId) {
        // 查询系统开关
      /*  boolean zoneAuthorizeSwitch = zoneAuthorizeSwitch();
        if (!zoneAuthorizeSwitch) {
            return false;
        }*/

      /*  // 查询用户开关
        List<UserSwitch> userSwitchList = jdbcTemplate.query(USER_SWITCH_SQL,
                new BeanPropertyRowMapper<UserSwitch>(UserSwitch.class), true, userId);
        if (CollectionUtils.isEmpty(userSwitchList)) {
            return false;
        }*/
        return true;
    }
    
    @Override
    public String childKey(ZoneQuery zoneQuery, Long userId,int specialSign,String cTypeStr) {
        return key("child", zoneQuery, userId, specialSign,cTypeStr);
    }
    
    //@ReadThroughSingleCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 120 * 60)
    public List<TreeNode> child(@ParameterValueKeyProvider String key, ZoneQuery zoneQuery, Tree tree, UserZoneAuthorize loginZoneAuthorize, UserZoneAuthorize editUserZoneAuthorize) {
        List<TreeNode> tempList = new ArrayList<>();
        // 如果树为空，则直接返回空列表
        if (null == tree || tree.isEmpty()) {
            return tempList;
        }
        // 如果查询参数为空或者ID为0，如果nodeType也为空或是分局，则取所有根节点，否则取空
        if (null == zoneQuery || null == zoneQuery.getId() || zoneQuery.getId().equals(0L)) {
            if (StringUtils.isBlank(zoneQuery.getNodeType()) || TreeUtil.nodeType(tree.getClassList().get(0)).equals(zoneQuery.getNodeType().trim())) {
                for (TreeNode treeNode : tree.getRootList()) {
                    tempList.add(treeNode);
                }
            } else {
                return tempList;
            }
        } else {
        	// 将nodeType转成类型
            Class<? extends TreeNode> clazz = ZoneConfig.getNodeTypeMap().get(zoneQuery.getNodeType().trim());
            // 获取入参ID和nodeType对应的节点
            TreeNode treeNode = tree.treeNode(clazz, zoneQuery.getId());
            if (null == treeNode) {
                return tempList;
            }
            // 如果节点有孩子，则将所有孩子加入到临时列表中
            if (treeNode.getHasChild()) {
                for (TreeNode temp : treeNode.getChildList()) {
                    tempList.add(temp);
                }
            }
            // 如果节点有下级，则将所有下级加入到临时列表中
            if (treeNode.getHasNext()) {
                for (TreeNode temp : treeNode.getNextList()) {
                    tempList.add(temp);
                }
            }
        }

        // 处理（是否授权、统计、查询临时列表中节点的孩子）临时列表
        List<TreeNode> retList = new ArrayList<>();
        for (TreeNode temp : tempList) {
            TreeNode retNode = child(temp, ZoneConfig.getNodeTypeMap().get(zoneQuery.getSpreadNodeType()),
                    ZoneConfig.getNodeTypeMap().get(zoneQuery.getCountNodeType()), tree, loginZoneAuthorize, editUserZoneAuthorize,zoneQuery);
            if (null != retNode) {
                retList.add(retNode);
            }
        }

        // 根据节点中sort字段进行排序
        Collections.sort(retList);
        return retList;
    }

    private TreeNode child(TreeNode treeNode, Class<? extends TreeNode> spreadClass, Class<? extends TreeNode> countClass, Tree tree,
            UserZoneAuthorize userZoneAuthorize, UserZoneAuthorize editUserZoneAuthorize,ZoneQuery zoneQuery) {
        if (null == treeNode) {
            return null;
        }
        // 如果需要展开后代，则判断当前节点是否为展开类型或是展开类型的祖先类型，如果不是，则返回null
        if (null != spreadClass) {
            boolean holdTreeNode = false;
            for (Class<? extends TreeNode> clazz = spreadClass; null != clazz; clazz = TreeUtil.previousClass(clazz)) {
                if (clazz.equals(treeNode.getClass())) {
                    holdTreeNode = true;
                    break;
                }
            }
            if (!holdTreeNode) {
                return null;
            }
        }
        UserInfo ui = userZoneAuthorize.getUi();
        if(null == ui){
            ui = userDao.findOne(userZoneAuthorize.getUserId());
            String roleName = roleRepository.findOne(ui.getRoleId()).getName();
            ui.setRoleTypeName(roleName);
            userZoneAuthorize.setUi(ui);
        }
        if(null == ui || ui.getSpecialSign() == 0 || null != editUserZoneAuthorize){
            //在编辑用户或非全区域login用户条件下进行节点权限判断
            if (userZoneAuthorize.getOpened() && ZoneConstant.ZONE_CHOICE_PART.compareTo(userZoneAuthorize.choice(treeNode.getClass(), treeNode.getId())) > 0) {
                return null;
            }
        }
        
        if(!ZoneUtil.filterCamera(treeNode,ui)){
            return null;//摄像头高清过滤
        }
        
        // 复制一个不带后代的节点
        TreeNode temp = treeNode.copyWithoutTreeInfo();


        // 如果需要统计，则将该节点下所有要统计后代查询出来，计算其个数
        if (null != countClass) {
            int count = offspring(key("offspring", userZoneAuthorize.getUserId(), temp.getClass(), temp.getId(), countClass), userZoneAuthorize, tree, temp.getClass(), temp.getId(), countClass).size();
            temp.setCountLeaf(count);
        }

        // 如果有编辑用户，需要处理节点的选中状态信息
        if (null != editUserZoneAuthorize) {
            String choice = editUserZoneAuthorize.choice(temp.getClass(), temp.getId());
            if (choice.equals(ZoneConstant.ZONE_CHOICE_NONE) || choice.equals(ZoneConstant.ZONE_CHOICE_ALL)) {
                temp.setChoice(choice);
            } else {
                Map<Class<? extends TreeNode>, Set<Long>> editMap = TreeUtil
                        .idMap(leaf(Lists.newArrayList(treeNode), null, ZoneConstant.ZONE_CHOICE_PART, editUserZoneAuthorize));
                Map<Class<? extends TreeNode>, Set<Long>> loginMap = TreeUtil
                        .idMap(leaf(Lists.newArrayList(treeNode), null, ZoneConstant.ZONE_CHOICE_PART, userZoneAuthorize));
                if (editMap.equals(loginMap)) {
                    temp.setChoice(ZoneConstant.ZONE_CHOICE_ALL);
                } else {
                    temp.setChoice(ZoneConstant.ZONE_CHOICE_PART);
                }
            }
        }
        // 如果不需要展开，则返回节点自身
        if (null == spreadClass) {
            return temp;
        }
        // 如果需要展开并且有孩子，则需要嵌套调用自身分析
        if (treeNode.getHasChild()) {
            List<TreeNode> childList = new ArrayList<>();
            for (TreeNode node : treeNode.getChildList()) {
                TreeNode tempNode = child(node, spreadClass, countClass, tree, userZoneAuthorize, editUserZoneAuthorize,zoneQuery);
                if (null != tempNode && filterByTreeName(tempNode,zoneQuery.getName())) {
                    childList.add(tempNode);
                }
            }
            temp.setChildList(childList);
            // temp.setHasChild(!childList.isEmpty());
        }
        // 如果需要展开并且有下代，则需要嵌套调用自身分析
        if (treeNode.getHasNext()) {
            List<TreeNode> nextList = new ArrayList<>();
            for (TreeNode node : treeNode.getNextList()) {
                TreeNode tempNode = child(node, spreadClass, countClass, tree, userZoneAuthorize, editUserZoneAuthorize,zoneQuery);
                if (null != tempNode && filterByTreeName(tempNode,zoneQuery.getName())) {
                    nextList.add(tempNode);
                }
            }
            temp.setNextList(nextList);
           
            // temp.setHasNext(!nextList.isEmpty());
        }
        // 如果展开，则将叶子节点的半选设置成全选
        if (!temp.getHasChild() && !temp.getHasNext() && ZoneConstant.ZONE_CHOICE_PART.compareTo(temp.getChoice()) <= 0) {
            temp.setChoice(ZoneConstant.ZONE_CHOICE_ALL);
        }
        //模糊查询时，如果没有子节点或子节点没有任何一个满足要求，则当前节点也需要进行模糊过滤
        if(temp.getNextList().isEmpty() && temp.getChildList().isEmpty() && !filterByTreeName(temp,zoneQuery.getName())){
            return null;
        }
        return temp;
    }
    
    
 
    
    private boolean filterByTreeName(TreeNode tn,String searchName){
        boolean status = true;
        try{
            if(StringUtil.isNotBlank(searchName)){
               String name = tn.getName();
               if(name.indexOf(searchName) < 0 ){
                   status = false;
               }
            
        }}catch(Exception e){
            LOG.error("filter tree by searchName error,searchName:"+searchName+",error:",e);
        }     
       return status;
}
    
    /**
     * 查询叶子节点列表
     * @param treeNodeList 节点列表
     * @param filterClass 过滤类型
     * @param minChoice 最小选择类型（不选、半选、全选）
     * @param userZoneAuthorize 用户区域授权信息
     * @return
     */
    private List<TreeNode> leaf(List<TreeNode> treeNodeList, Class<? extends TreeNode> filterClass, String minChoice, UserZoneAuthorize userZoneAuthorize) {
        List<TreeNode> retList = new ArrayList<>();
        minChoice = StringUtils.isBlank(minChoice) ? ZoneConstant.ZONE_CHOICE_NONE : minChoice;
        if (CollectionUtils.isEmpty(treeNodeList)) {
            return retList;
        }
        for (TreeNode node : treeNodeList) {
            if (null == node) {
                continue;
            }
            if (!node.getHasChild() && !node.getHasNext() && minChoice.compareTo(userZoneAuthorize.choice(node.getClass(), node.getId())) <= 0) {
                if (null == filterClass || filterClass.equals(node.getClass())) {
                    retList.add(node);
                }
                continue;
            }
            if (node.getHasChild()) {
                retList.addAll(leaf(node.getChildList(), filterClass, minChoice, userZoneAuthorize));
            }
            if (node.getHasNext()) {
                retList.addAll(leaf(node.getNextList(), filterClass, minChoice, userZoneAuthorize));
            }
        }
        return retList;
    }
    
    /**
     * 
     */
    @Override
    public String offspringKey(Long userId, Class<? extends TreeNode> nodeClass, Long nodeId, final Class<? extends TreeNode> filterClass) {
        return key("offspring", userId, nodeClass, nodeId, filterClass);
    }

    @Override
    public List<TreeNode> offspring(String key, UserZoneAuthorize loginZoneAuthorize, Tree tree, Class<? extends TreeNode> nodeClass, Long nodeId, final Class<? extends TreeNode> filterClass) {
        final List<TreeNode> allList = new ArrayList<>();
        //long userId = userId();
        //UserInfo ui = userDao.findOne(userId);
        UserInfo ui = loginZoneAuthorize.getUi();
        if(null == ui){
            ui = userDao.findOne(loginZoneAuthorize.getUserId());
            String roleName = roleRepository.findOne(ui.getRoleId()).getName();
            ui.setRoleTypeName(roleName);
            loginZoneAuthorize.setUi(ui);
        }
        if (null == nodeId || nodeId.equals(0L)) {
            if ((null == nodeClass || nodeId.equals(ZoneConfig.getClassList().get(ZoneConfig.getClassList().size() - 1)))) {
                allList.addAll(tree.getTreeNodeListWithoutTreeInfo(filterClass));
            } else {
                return allList;
            }
        } else {
            allList.addAll(tree.offspringList(nodeClass, nodeId, filterClass, true));
        }
        List<TreeNode> retList = new ArrayList<>();
        for (TreeNode temp : allList) {
            if (ui.getSpecialSign() > 0 || ZoneConstant.ZONE_CHOICE_PART.compareTo(loginZoneAuthorize.choice(filterClass, temp.getId())) <= 0) {
             if(ZoneUtil.filterCamera(temp, ui)) {
                 retList.add(temp);
             }
            }
        }
        return retList;
    }
    
    private long userId() {
        // 获取登录用户的ID
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        Validate.notNull(userInfo, "获取登录信息失败！请您重新登录！");
        Long userId = userInfo.getId();
        Validate.notNull(userId, "获取登录信息失败！请您重新登录！");
        return userId;
    }
    @Override
    @Transactional
   // @InvalidateSingleCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE)
    public void saveUserZoneAuthorize(@ParameterValueKeyProvider String key, UserZoneAuthorize saveZoneAuthorize, UserZoneAuthorize oldZoneAuthorize, Long loginUserId) {
        jdbcTemplate.update(DELETE_USER_SWITCH, saveZoneAuthorize.getUserId());
        if (!saveZoneAuthorize.getOpened()) {
            return;
        }
        jdbcTemplate.update(INSERT_USER_SWITCH, saveZoneAuthorize.getUserId(), saveZoneAuthorize.getOpened(), loginUserId);
        for (Class clazz : ZoneConfig.getClassList()) {
            ZoneAuthorizePluginItf<? extends TreeNode> plugin = ZoneConfig.getPluginMap().get(clazz);
            if (null != plugin) {
                plugin.remove(saveZoneAuthorize.getUserId());
                plugin.save(saveZoneAuthorize.getUserId(), saveZoneAuthorize.getClassToIdSetMap().get(clazz));
            }
        }
        refreshKey();
    }

    @Override
    //@ReadThroughAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 0, assignedKey = "tree")
    public Tree tree() {
        return treeWithoutCache();
    }
    
    @Override
    public Tree treeWithoutCache() {
        TreeBuilder builder = TreeBuilder.newInstance();
        // 查询区域列表
        for (Class<? extends TreeNode> clazz : ZoneConfig.getClassList()) {
            try {
                ZoneAuthorizePluginItf<? extends TreeNode> plugin = ZoneConfig.getPluginMap().get(clazz);
                List<? extends TreeNode> treeNodeList = plugin.findAll();
                builder.add(clazz, treeNodeList);
            } catch(Exception e) {
                LOG.error("get " + clazz + " update version catch exception: ", e);
            }
        }
        return builder.build();
    }

    @Override
    //@UpdateAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 0, assignedKey = "tree")
    //@ReturnDataUpdateContent
    public Tree updateTree() {
        return treeWithoutCache();
    }

    @Override
   // @ReadThroughAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 0, assignedKey = "versionMap")
    public Map<Class<? extends TreeNode>, Long> versionMap() {
        return versionMapWithoutCache();
    }

    
    @Override
    public Map<Class<? extends TreeNode>, Long> versionMapWithoutCache() {
        Map<Class<? extends TreeNode>, Long> versionMap = new HashMap<>();
        for (Class<? extends TreeNode> clazz : ZoneConfig.getClassList()) {
            try {
                versionMap.put(clazz, ZoneConfig.getPluginMap().get(clazz).updateVersion());
            } catch(Exception e) {
                LOG.error("get " + clazz + " update version catch exception: ", e);
            }
        }
        return versionMap;
    }

    @Override
    @UpdateAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 0, assignedKey = "versionMap")
    @ReturnDataUpdateContent
    public Map<Class<? extends TreeNode>, Long> updateVersionMap(Map<Class<? extends TreeNode>, Long> versionMap) {
        return versionMap;
    }

    @Override
    public String key(String method, Object... object) {
        StringBuilder sb = new StringBuilder(method).append("_").append(String.valueOf(keyBase)).append("_").append(StringUtils.join(object, "_"));
        return MD5.digest(sb.toString());
    }

    @Override
    @InvalidateAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, assignedKey = "zoneAuthorizeSwitch")
    public void refreshKey() {
        keyBase = System.currentTimeMillis();
    }
    
    @Override
    @ReadThroughAssignCache(namespace = ZoneConstant.ZONE_CACHE_NAMESPACE, expiration = 0, assignedKey = "zoneAuthorizeSwitch")
    public boolean zoneAuthorizeSwitch() {
        return zoneAuthorizeSwitchWithoutCache();
    }

    @Override
    public boolean zoneAuthorizeSwitchWithoutCache() {
        List<SystemSwitch> systemSwitchList = jdbcTemplate.query(SYSTEM_SWITCH_SQL,
                new BeanPropertyRowMapper<SystemSwitch>(SystemSwitch.class), true,
                SystemSwitch.SWITCH_TYPE_AREA_AUTHORIZE);
        if (CollectionUtils.isEmpty(systemSwitchList)) {
            return false;
        }
        return systemSwitchList.get(0).getOpened();
    }

    @Override
    public void refreshTree() {
        LocalCache.tree = tree();
        versionMap();
        updateVersionMap(versionMapWithoutCache());
    }

   
}
