package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import intellif.annotation.MapDTO;
import intellif.annotation.MapDTOSplitStr;

/**
 * 
 * @author yktangint
 * 这个类的对象是从数据库中读取的结果，该对象需要转化为CameraStatisticInfoDto的对象，以便于进一步处理。
 * 转化用字段的反射，这里自己定义了@MapDTO和@MapDTOSplitStr两个注解来转化。
 *
 */
public class CameraStatisticInfo implements Serializable {

	private static final long serialVersionUID = -8218065162756486216L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@MapDTO(dtofieldname = "stationid")
	private long stationid;
	@MapDTO(dtofieldname = "stationname")
	private String stationname;
	@MapDTO(dtofieldname = "quantity")
	private long quantity;
	@MapDTOSplitStr(bychar = true, separatorchar = (char)(0x1D), dtofieldname = "cameranamelist")
	private String cameranames;
	
	public long getStationid() {
		return stationid;
	}
	public void setStationid(long stationid) {
		this.stationid = stationid;
	}
	public String getStationname() {
		return stationname;
	}
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public String getCameranames() {
		return cameranames;
	}
	public void setCameranames(String cameranames) {
		this.cameranames = cameranames;
	}
}
