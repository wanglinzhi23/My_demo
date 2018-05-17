package intellif.facecollision.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellif.core.cluster.vo.Cluster;

import intellif.database.entity.FaceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zheng Xiaodong
 */
public class FaceCollisionResult implements Serializable {

    private static final long serialVersionUID = 8072035544706156935L;

    @JsonIgnore
    private Cluster<FaceInfo> sourceCluster;

    private List<FaceInfo> targetFaces = new ArrayList<FaceInfo>();
    
    private int sourceCount;
    
    private int targetCount;
    

    private long order;
    
    private int mode;//手动 2 自动 1

    public Cluster<FaceInfo> getSourceCluster() {
        return sourceCluster;
    }

    public void setSourceCluster(Cluster<FaceInfo> sourceCluster) {
        this.sourceCluster = sourceCluster;
    }

    public long getSourceCount() {
        return sourceCount;
    }

    public long getTargetCount() {
        return targetCount;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public List<FaceInfo> getSourceFaces() {
        return sourceCluster.getFaceList();
    }

    public FaceInfo getPersonFace() {
        return sourceCluster.getFace();
    }

    public List<FaceInfo> getTargetFaces() {
        if(null == targetFaces){
         targetFaces = new ArrayList<FaceInfo>();  
        }
        return targetFaces;
    }

    public void setTargetFaces(List<FaceInfo> targetFaces) {
        this.targetFaces = targetFaces;
    }

    public void setSourceCount(int sourceCount) {
        this.sourceCount = sourceCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

  
    
}
