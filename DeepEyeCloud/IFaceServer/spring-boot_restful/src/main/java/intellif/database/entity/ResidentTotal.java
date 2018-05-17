package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = GlobalConsts.T_NAME_RESIDENT_TOTAL,schema=GlobalConsts.INTELLIF_BASE)
public class ResidentTotal extends InfoBase implements Serializable {

	private static final long serialVersionUID = 234288567704140491L;

	private static Logger LOG = LogManager.getLogger(ResidentTotal.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // 人脸编号
    private long id;
    //总人口数
    private Long totalNum;
    //总常住人口数
    private Long residentNum;
    //地区ID
    private Long anaAreaId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}
	public Long getResidentNum() {
		return residentNum;
	}
	public void setResidentNum(Long residentNum) {
		this.residentNum = residentNum;
	}
	public Long getAnaAreaId() {
		return anaAreaId;
	}
	public void setAnaAreaId(Long anaAreaId) {
		this.anaAreaId = anaAreaId;
	}
    
    
    
}
