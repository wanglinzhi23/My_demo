package intellif.share.service;

import intellif.core.tree.itf.TreeNode;
import intellif.database.entity.Area;

import java.util.List;
import java.util.Set;

public interface ShareDistrictAreaCameraServiceItf {
	
	/**
	 * 查询指定类型节点下的指定类型子节点
	 * @param idList
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
    Set<Long> findNodeIdsByNodeIds(List<Long> idList,Class sourceClass,Class targetClass);

    /**
     * 查询指定类型所有节点
     * 
     * @return
     * @throws Exception
     */
    List<TreeNode> findAll(Class clazz) throws Exception;
}
