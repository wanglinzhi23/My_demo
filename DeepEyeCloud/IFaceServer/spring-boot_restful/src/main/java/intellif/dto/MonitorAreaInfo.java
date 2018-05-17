package intellif.dto;

import java.util.List;

/**
 * 布控区域类
 * 
 * @author shixiaohua
 *
 */
public class MonitorAreaInfo {
    private long areaId;
    private List<Long> cameraIds;// 选中摄像头集合
    private int allSelected;// 区域是否全选 全选1 非全选0

    public MonitorAreaInfo(){
        
    }
    
    public MonitorAreaInfo(long areaId,List<Long> cameraIds,int allSelected){
        this.areaId = areaId;
        this.cameraIds = cameraIds;
        this.allSelected = allSelected;
    }
    
    
    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

  

    public List<Long> getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(List<Long> cameraIds) {
        this.cameraIds = cameraIds;
    }

    public int getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(int allSelected) {
        this.allSelected = allSelected;
    }

}
