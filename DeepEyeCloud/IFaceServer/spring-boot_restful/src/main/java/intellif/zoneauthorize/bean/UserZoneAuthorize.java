package intellif.zoneauthorize.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.database.entity.UserInfo;
import intellif.zoneauthorize.common.ZoneConstant;
import intellif.zoneauthorize.util.ZoneAuthorizeUtil;

public class UserZoneAuthorize implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1351102376038170396L;

    // 用户ID
    private long userId;

    // 系统开关是否打开
    private boolean opened = true;

    // 区域类型与其ID列表
    private final Map<Class<? extends TreeNode>, Set<Long>> classToIdSetMap = new HashMap<>();

    // 节点状态(全选、部分选中，未选择)
    private final Map<Class<? extends TreeNode>, Map<Long, String>> choiceMap = new HashMap<>();
    
    private UserInfo ui = null;
    
    public UserInfo getUi() {
        return ui;
    }

    public void setUi(UserInfo ui) {
        this.ui = ui;
    }

    public UserZoneAuthorize() {
        super();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean getOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public Map<Class<? extends TreeNode>, Set<Long>> getClassToIdSetMap() {
        return classToIdSetMap;
    }

    public Map<Class<? extends TreeNode>, Map<Long, String>> getChoiceMap() {
        return choiceMap;
    }
    
    public void initChoiceMap(Tree tree) {
        if (null == tree) {
            return;
        }
        if (opened) {
            choiceMap.putAll(ZoneAuthorizeUtil.choiceMap(tree, classToIdSetMap));
        }
    }
    
    public String choice(Class<? extends TreeNode> clazz, Long id) {
        Validate.notNull(clazz, "类型不得为空");
        Validate.notNull(id, "ID不得为空");
        if (!opened) {
            return ZoneConstant.ZONE_CHOICE_ALL;
        }
        if (null == choiceMap.get(clazz) || null == choiceMap.get(clazz).get(id)) {
            return ZoneConstant.ZONE_CHOICE_NONE;
        }
        return choiceMap.get(clazz).get(id);
    }
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAreaAuthorize [userId=");
        builder.append(userId);
        builder.append(", opened=");
        builder.append(opened);
        builder.append(", classToIdSetMap=");
        builder.append(classToIdSetMap);
        builder.append("]");
        return builder.toString();
    }

}
