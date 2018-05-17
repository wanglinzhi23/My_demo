package intellif.database.entity;

import java.io.Serializable;

public class StoredCrimeQuery implements Serializable {

	private static final long serialVersionUID = 9046879711814421561L;
	private short gender; // 0:male, 1:female, 2:male&female
	private long crimeid; // 0:all crime ids
	private long subcrimeid; //0:all subcrime ids
	public short getGender() {
		return gender;
	}
	public void setGender(short gender) {
		this.gender = gender;
	}
	public long getCrimeid() {
		return crimeid;
	}
	public void setCrimeid(long crimeid) {
		this.crimeid = crimeid;
	}
	public long getSubcrimeid() {
		return subcrimeid;
	}
	public void setSubcrimeid(long subcrimeid) {
		this.subcrimeid = subcrimeid;
	}

}
