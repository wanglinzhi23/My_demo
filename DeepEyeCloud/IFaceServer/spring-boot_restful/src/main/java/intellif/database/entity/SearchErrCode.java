package intellif.database.entity;

import java.util.Date;
import intellif.consts.GlobalConsts;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_SEARCH_RESULT_CODE, schema = GlobalConsts.INTELLIF_BASE)
public class SearchErrCode{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	//搜索错误码编号
	private int resultCodeId;

	//搜索错误码名称
	private String resultCodeName;

	public SearchErrCode() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getResultCodeId() {
		return resultCodeId;
	}

	public void setResultCodeId(int resultCodeId) {
		this.resultCodeId = resultCodeId;
	}

	public String getResultCodeName() {
		return resultCodeName;
	}

	public void setResultCodeName(String resultCodeName) {
		this.resultCodeName = resultCodeName;
	}




}
