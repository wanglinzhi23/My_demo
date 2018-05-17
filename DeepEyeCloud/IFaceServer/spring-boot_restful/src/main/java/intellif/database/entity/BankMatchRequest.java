package intellif.database.entity;

import java.io.Serializable;

/**
 * 
 * @author yktangint
 * 
 * 双库碰撞请求参数
 *
 */
public class BankMatchRequest implements Serializable {

	private static final long serialVersionUID = 7373695416796928659L;
	//目标库的id
	private long targetType;
	//静态库的id
	private int staticBankId;
	//针对每张目标库人脸，返回最匹配的前多少个
	private int matchnum;
	public long getTargetType() {
		return targetType;
	}
	public void setTargetType(long targetType) {
		this.targetType = targetType;
	}
	public int getMatchnum() {
		return matchnum;
	}
	public void setMatchnum(int matchnum) {
		this.matchnum = matchnum;
	}
	public int getStaticBankId() {
		return staticBankId;
	}
	public void setStaticBankId(int staticBankId) {
		this.staticBankId = staticBankId;
	}
	
}
