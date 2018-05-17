package intellif.utils;

import java.util.HashSet;
import java.util.Set;

import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.database.entity.UserInfo;
import intellif.database.entity.CameraInfo;
import intellif.zoneauthorize.common.LocalCache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;

public class ZoneUtil {
    private static Logger LOG = LogManager .getLogger(DateUtil.class);
    public static boolean filterCamera(TreeNode tn,UserInfo ui){
        boolean status = true;
        try{
            if(null != ui){
                boolean isSpecial = ui.getRoleTypeName().equals(GlobalConsts.SUPER_ADMIN) || (ui.getSpecialSign() > 0);
                if(!isSpecial && StringUtil.isNotBlank(ui.getcTypeIds()) && (tn.getClass().getSimpleName().equals(CameraInfo.class.getSimpleName()))){
                    String cTypeIds = ","+ui.getcTypeIds()+",";
                    CameraInfo ci = (CameraInfo) tn;
                    if(null != ci){
                        if(cTypeIds.indexOf(","+String.valueOf(ci.getcType())+",") < 0) {
                            status = false;
                        }
                    }
            }
        
        }}catch(Exception e){
            LOG.error("filter camerainfo by cType error,cameraId:"+tn.getId()+",error:",e);
        }     
       return status;
}
    
    /**
     * 从区域集合过滤出条件camera数据
     * @param idSet
     * @param treeNodeClass
     * @param userId
     */
    public static  Set<Long> filterCameraByCType(Set<Long> idSet,Class<? extends TreeNode> treeNodeClass, UserInfo ui){
        Tree tree = LocalCache.tree;
        if(null != ui){
        boolean isSpecial = ui.getRoleTypeName().equals(GlobalConsts.SUPER_ADMIN) || (ui.getSpecialSign() > 0);
        if(!isSpecial && (treeNodeClass.getSimpleName().equals(CameraInfo.class.getSimpleName()))){
           String cTypeIds = ","+ui.getcTypeIds()+",";
            if(StringUtil.isNotBlank(cTypeIds)){
                Set<Long> newIdSet = new HashSet<Long>();
                for(Long id : idSet){
                    try{
                        CameraInfo ci = (CameraInfo) tree.treeNodeWithOutTreeInfo(treeNodeClass, id);
                        if(null != ci){
                            if(cTypeIds.indexOf(","+String.valueOf(ci.getcType())+",") >= 0) {
                                newIdSet.add(id);
                            }
                        }
                    }catch(Exception e){
                        LOG.error("filter camerainfo by cType error,cameraId:"+id+",error:",e);
                    }
                }
                LOG.info("end filterCameraByCType method,userId:"+ui.getId());
                return newIdSet;
            }
            
        }
        }
        //不满足条件跳过过滤
        return idSet;
    }
    
}
