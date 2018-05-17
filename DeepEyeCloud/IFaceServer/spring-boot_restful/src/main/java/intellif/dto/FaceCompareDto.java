package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import intellif.consts.GlobalConsts;


public class FaceCompareDto  implements Serializable{

	
	private static final long serialVersionUID = 3923743872626773277L;

	
	private long faceIdA;
	
	private long faceIdB;
	
	//图片A所在的库类型  （0：blackdetail；1：faceinfo；2：faceinfo；3：cididetail；4：juzhudetail；5：otherdetail；6：otherdetail）
	private int Atype;
	//图片B所在的库类型  （同上）
	private int Btype;

	public int getAtype() {
		return Atype;
	}

	public void setAtype(int atype) {
		Atype = atype;
	}

	public int getBtype() {
		return Btype;
	}

	public void setBtype(int btype) {
		Btype = btype;
	}

	private float threshold;

	public long getFaceIdA() {
		return faceIdA;
	}

	public void setFaceIdA(long faceIdA) {
		this.faceIdA = faceIdA;
	}

	public long getFaceIdB() {
		return faceIdB;
	}

	public void setFaceIdB(long faceIdB) {
		this.faceIdB = faceIdB;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	
}
