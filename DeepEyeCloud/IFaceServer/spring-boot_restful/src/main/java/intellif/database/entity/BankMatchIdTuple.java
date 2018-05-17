package intellif.database.entity;

public class BankMatchIdTuple {
	//目标库id
	private Long targetbankid;
	//静态库id
	private Integer staticbankid;
	
	public BankMatchIdTuple(Long targetbankid, Integer staticbankid) {
		super();
		this.targetbankid = targetbankid;
		this.staticbankid = staticbankid;
	}
	public Long getTargetbankid() {
		return targetbankid;
	}
	public void setTargetbankid(Long targetbankid) {
		this.targetbankid = targetbankid;
	}
	public Integer getStaticbankid() {
		return staticbankid;
	}
	public void setStaticbankid(Integer staticbankid) {
		this.staticbankid = staticbankid;
	}
}
