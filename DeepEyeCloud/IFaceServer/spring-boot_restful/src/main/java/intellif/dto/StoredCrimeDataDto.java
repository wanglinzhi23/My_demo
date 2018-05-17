package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StoredCrimeDataDto implements Serializable {

	private static final long serialVersionUID = -9199960616548719231L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String bankName;
	private long personcount;
	private long facecount;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public long getPersoncount() {
		return personcount;
	}
	public void setPersoncount(long personcount) {
		this.personcount = personcount;
	}
	public long getFacecount() {
		return facecount;
	}
	public void setFacecount(long facecount) {
		this.facecount = facecount;
	}
}
