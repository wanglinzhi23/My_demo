package intellif.dto;

/**
 * 事由详细信息
 * @author shixiaohua
 *
 */
public class SearchReasonDto {

	private  long reasonId;
	private String reasonDetail;
	public long getReasonId() {
		return reasonId;
	}
	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}
	public String getReasonDetail() {
		return reasonDetail;
	}
	public void setReasonDetail(String reasonDetail) {
		this.reasonDetail = reasonDetail;
	}
	
}
