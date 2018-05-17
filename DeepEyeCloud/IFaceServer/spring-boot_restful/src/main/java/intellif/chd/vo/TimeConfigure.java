package intellif.chd.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name = GlobalConsts.T_NAME_TIME_CONFIGURE,schema=GlobalConsts.INTELLIF_BASE)
public class TimeConfigure {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	private String starttime;
	
	private String endtime;
	
	private String period;
	
	private Float threshold;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "TimeConfigure [id=" + id + ", starttime=" + starttime + ", endtime=" + endtime + ", period=" + period
				+ "]";
	}

    public Float getThreshold() {
        return threshold;
    }

    public void setThreshold(Float threshold) {
        this.threshold = threshold;
    }

}
