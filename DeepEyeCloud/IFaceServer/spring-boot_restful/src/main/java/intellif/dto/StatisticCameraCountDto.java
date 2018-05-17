package intellif.dto;

import java.io.Serializable;
import java.util.Map;

public class StatisticCameraCountDto implements Serializable {
	private static final long serialVersionUID = -4128939739614255275L;
	private Object countData;
	private Map<Long, AreaStatisticCameraDataDto> detailData;
	
	public StatisticCameraCountDto(Object countData, Map<Long, AreaStatisticCameraDataDto> detailData) {
		super();
		this.countData = countData;
		this.detailData = detailData;
	}
	public Object getCountData() {
		return countData;
	}
	public void setCountData(Object countData) {
		this.countData = countData;
	}
	public Map<Long, AreaStatisticCameraDataDto> getDetailData() {
		return detailData;
	}
	public void setDetailData(Map<Long, AreaStatisticCameraDataDto> detailData) {
		this.detailData = detailData;
	}
}
