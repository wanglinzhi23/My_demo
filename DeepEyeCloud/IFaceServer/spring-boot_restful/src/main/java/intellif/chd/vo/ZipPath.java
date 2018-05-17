package intellif.chd.vo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name = GlobalConsts.T_NAME_ZIP_PATH, schema = GlobalConsts.INTELLIF_BASE)
public class ZipPath {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Date starttime;

	private Date endtime;

	private String path;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "ZipPath [id=" + id + ", starttime=" + starttime + ", endtime=" + endtime + ", path=" + path + "]";
	}

}
