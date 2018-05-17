package intellif.chd.service;

import java.util.List;

import intellif.chd.vo.Cluster;
import intellif.database.entity.FaceInfo;

public interface ClusterServiceItf {
	List<Cluster> faceInfoClusterAndSort(List<FaceInfo> faceInfoList, float threshold);

	List<Cluster> faceCluster(List<FaceInfo> faceInfoList, float threshold);

}
