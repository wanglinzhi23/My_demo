package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


@Entity
public class ResidentPerson implements Serializable {
	private static final long serialVersionUID = -1549627325855780955L;
	private static Logger LOG = LogManager.getLogger(ResidentPerson.class);
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    @JsonSerialize(using=ToStringSerializer.class)
	private long imageId;
	private String personId;
	@Transient
	private String imageData;
	@Transient
	private int age;
	@Transient
	private int gender;
    private Date created;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getImageData() {
		return imageData;
	}
	public void setImageData(String imageData) {
		this.imageData = imageData;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	

}
