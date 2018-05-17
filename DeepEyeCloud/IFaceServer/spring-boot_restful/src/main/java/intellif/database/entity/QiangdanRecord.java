package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_QIANGDAN_RECORD,schema=GlobalConsts.INTELLIF_BASE)
public class QiangdanRecord implements Serializable,Cloneable{

    private static final long serialVersionUID = -1907618819651015110L;
    
    public QiangdanRecord()
    {
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
   
    private long faceId;
    
    private long redId;
    
    private int send;//是否发送给信义
    
    private long sourceId;
    
    private Date time;
    

    public long getFaceId() {
        return faceId;
    }

    public void setFaceId(long faceId) {
        this.faceId = faceId;
    }

    public long getRedId() {
        return redId;
    }

    public void setRedId(long redId) {
        this.redId = redId;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}
