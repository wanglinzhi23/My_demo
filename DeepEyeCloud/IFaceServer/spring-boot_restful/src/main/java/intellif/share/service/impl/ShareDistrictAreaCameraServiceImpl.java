package intellif.share.service.impl;

import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.share.service.ShareDistrictAreaCameraServiceItf;
import intellif.database.entity.CameraInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ShareDistrictAreaCameraServiceImpl implements ShareDistrictAreaCameraServiceItf {

    private static Logger LOG = LogManager.getLogger(ShareDistrictAreaCameraServiceImpl.class);

    @Autowired
    FaceInfoDaoImpl faceInfoDaoImpl;

    @Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Long> findNodeIdsByNodeIds(List<Long> idList,Class sourceClass,Class targetClass) {
        List<TreeNode> returnList = new ArrayList<TreeNode>();
        if(!CollectionUtils.isEmpty(idList)){
            for(Long id : idList){
                List<TreeNode> cameraList = zoneAuthorizeService.offspring(sourceClass, id, targetClass);
               if(!CollectionUtils.isEmpty(cameraList)){
                   returnList.addAll(cameraList);
               }
            }
        }
        Set<Long> idSet = TreeUtil.idMap(returnList).get(CameraInfo.class);
        return null == idSet ? new HashSet<>() : idSet;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<TreeNode> findAll(Class clazz) throws Exception {
        return zoneAuthorizeService.findAll(clazz,null);
    }

}
