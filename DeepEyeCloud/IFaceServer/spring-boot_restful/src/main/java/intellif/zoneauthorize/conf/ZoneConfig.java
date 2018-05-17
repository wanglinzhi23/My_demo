package intellif.zoneauthorize.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import intellif.core.tree.itf.TreeNode;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.DistrictInfo;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;

public class ZoneConfig {

    private static final List<Class<? extends TreeNode>> classList = Lists.newArrayList(DistrictInfo.class, Area.class, CameraInfo.class);

    private static final Map<Class<? extends TreeNode>, ZoneAuthorizePluginItf<? extends TreeNode>> pluginMap = new HashMap<>();
    
    private static final Map<String, Class<? extends TreeNode>> nodeTypeMap = new HashMap<>();

    public static List<Class<? extends TreeNode>> getClassList() {
        return classList;
    }

    public static Map<Class<? extends TreeNode>, ZoneAuthorizePluginItf<? extends TreeNode>> getPluginMap() {
        return pluginMap;
    }

    public static Map<String, Class<? extends TreeNode>> getNodeTypeMap() {
        return nodeTypeMap;
    }
}
