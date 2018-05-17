package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import intellif.annotation.MultiTablePrefix;
import intellif.consts.GlobalConsts;

@Entity
@MultiTablePrefix(shortName = GlobalConsts.T_NAME_FACE_INFO,schema=GlobalConsts.INTELLIF_FACE)
public class FaceStatisticCount implements Serializable {

	private static final long serialVersionUID = 6141364209075071423L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long sourceId;
	private String time;
	private long count;
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
