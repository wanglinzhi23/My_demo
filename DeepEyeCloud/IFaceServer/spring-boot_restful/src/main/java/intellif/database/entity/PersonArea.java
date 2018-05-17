package intellif.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PersonArea {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String realName;
	private String realGender;
	private String description;
	private String areas;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRealGender() {
		return realGender;
	}
	public void setRealGender(String realGender) {
		this.realGender = realGender;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAreas() {
		return areas;
	}
	public void setAreas(String areas) {
		this.areas = areas;
	}
	
}
