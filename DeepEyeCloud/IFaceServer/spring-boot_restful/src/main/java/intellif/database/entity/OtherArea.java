package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = GlobalConsts.T_NAME_OTHER_AREA,schema=GlobalConsts.INTELLIF_BASE)
public class OtherArea extends GeometryInfoBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9108083481548226949L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 区域编号
    private String areaNo;
    // 区域名称
    private String areaName;
    // 区域人数限制阈值
    private long personThreshold;
    // 行政区ID
    private long districtId;

    public OtherArea() {
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAreaNo() {
		return areaNo;
	}

	public void setAreaNo(String areaNo) {
		this.areaNo = areaNo;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public long getPersonThreshold() {
		return personThreshold;
	}

	public void setPersonThreshold(long personThreshold) {
		this.personThreshold = personThreshold;
	}

	public long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}

	@Override
	public String toString() {
		return "OtherArea [id=" + id + ", areaNo=" + areaNo + ", areaName=" + areaName
				+ ", personThreshold=" + personThreshold + "]"+ ", districtId=" + districtId + "]";
	}

}
