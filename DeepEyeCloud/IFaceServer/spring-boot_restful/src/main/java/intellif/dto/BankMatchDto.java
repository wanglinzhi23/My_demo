package intellif.dto;

import java.io.Serializable;
import java.util.Map;

import intellif.database.entity.BankMatchResultTuple;

public class BankMatchDto implements Serializable {

	private static final long serialVersionUID = -5946029967442257067L;
	//总共页数
	private int totalpage;
	//双库碰撞的返回结果
	private Map<Long, BankMatchResultTuple> result;
	public int getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}
	public Map<Long, BankMatchResultTuple> getResult() {
		return result;
	}
	public void setResult(Map<Long, BankMatchResultTuple> result) {
		this.result = result;
	}
}
