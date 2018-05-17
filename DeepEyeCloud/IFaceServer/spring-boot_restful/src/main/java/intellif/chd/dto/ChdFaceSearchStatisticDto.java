package intellif.chd.dto;

import java.io.Serializable;
import java.util.List;

import intellif.dto.CidInfoDto;
import intellif.database.entity.CidInfo;


public class ChdFaceSearchStatisticDto implements Serializable {
	
	private static final long serialVersionUID = -3744792988975709916L;

	private int total;
	
	private String url;
	
	private int type;
	
	private List<CidInfoDto> infoList;
	
	public void setTotal(int total) {
		this.total = total;
	}

	public Integer getTotal() {
		return total;
	}

	public List<CidInfoDto> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<CidInfoDto> infoList) {
		this.infoList = infoList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


}
