package intellif.database.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 统计某区域常住人口详细信息和各指标统计信息
 * @author shixiaohua
 *
 */
public class ResidentResult implements Serializable {

	
	private static final long serialVersionUID = 5500833164786947358L;
	
	private Map<String,Integer> residentMap;
	private List<ResidentPerson> residentList;
	public Map<String, Integer> getResidentMap() {
		return residentMap;
	}
	public void setResidentMap(Map<String, Integer> residentMap) {
		this.residentMap = residentMap;
	}
	public List<ResidentPerson> getResidentList() {
		return residentList;
	}
	public void setResidentList(List<ResidentPerson> residentList) {
		this.residentList = residentList;
	}
	
	

}
