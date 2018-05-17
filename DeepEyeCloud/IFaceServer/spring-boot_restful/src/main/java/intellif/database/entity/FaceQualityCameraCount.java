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
@Table(name=GlobalConsts.T_NAME_FACE_QUALITY_CAMERA_COUNT,schema=GlobalConsts.INTELLIF_BASE)
public class FaceQualityCameraCount  implements Serializable {

	private static final long serialVersionUID = -7038059548512475991L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
    // 数据源Id
    private long sourceId;
    
    // 统计时间
    private Date time;
    
    // 高质量次数
    private long highTotal;
    
    //低质量次数
    private long lowTotal;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public long getHighTotal() {
		return highTotal;
	}

	public void setHighTotal(long highTotal) {
		this.highTotal = highTotal;
	}

	public long getLowTotal() {
		return lowTotal;
	}

	public void setLowTotal(long lowTotal) {
		this.lowTotal = lowTotal;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
