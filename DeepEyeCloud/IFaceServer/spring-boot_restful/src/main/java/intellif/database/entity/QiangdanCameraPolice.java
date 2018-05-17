package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_QIANGDAN_CAMERA_POLICE,schema=GlobalConsts.INTELLIF_BASE)
public class QiangdanCameraPolice implements Serializable,Cloneable{

    private static final long serialVersionUID = 292143628265638616L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private long cameraId;
    
    private long policeNo;
    
    private Date createTime;
    
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private Date endTime;
    
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public long getId() {
        return id;
    }

    public QiangdanCameraPolice() {
        super();
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCameraId() {
        return cameraId;
    }

    public void setCameraId(long cameraId) {
        this.cameraId = cameraId;
    }

    public long getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(long policeNo) {
        this.policeNo = policeNo;
    }


}
