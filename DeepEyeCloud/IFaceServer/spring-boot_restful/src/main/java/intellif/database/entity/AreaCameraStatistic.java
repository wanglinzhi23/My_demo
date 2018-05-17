package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;

import intellif.zoneauthorize.itf.Zone;

@Entity
public class AreaCameraStatistic implements Serializable, Zone {

	private static final long serialVersionUID = 7621267722783357812L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long stationId;
	private String areaName;
	private String name;
	private long count;
	
	public AreaCameraStatistic(){
		
	}
	
	public AreaCameraStatistic(long id,long stationId,String areaName,String name){
		this.stationId = stationId;
		this.areaName = areaName;
		this.name = name;
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStationId() {
		return stationId;
	}
	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
    @Autowired
    public Long zoneId() {
        return id;
    }
}
