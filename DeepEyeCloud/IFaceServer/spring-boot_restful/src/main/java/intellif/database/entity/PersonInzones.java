package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_PERSON_INZONES,schema=GlobalConsts.INTELLIF_BASE)
public class PersonInzones extends InfoBase implements Serializable{
	
	private static final long serialVersionUID = -2363436111224977748L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 若属于重点人员库则有值与PersonDetail关联，若不属于则为0
	private long person_id;
	
	// 采集到的该人第一张人脸id
    @JsonSerialize(using=ToStringSerializer.class)
	private long face_id;
	
	// 采集所在的区域id
	private long station_id;
	
	// 采集并归并到该人的所有人脸id,逗号分隔
	private String faces_record;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPerson_id() {
		return person_id;
	}

	public void setPerson_id(long person_id) {
		this.person_id = person_id;
	}

	public long getFace_id() {
		return face_id;
	}

	public void setFace_id(long face_id) {
		this.face_id = face_id;
	}

	public long getStation_id() {
		return station_id;
	}

	public void setStation_id(long station_id) {
		this.station_id = station_id;
	}

	public String getFaces_record() {
		return faces_record;
	}

	public void setFaces_record(String faces_record) {
		this.faces_record = faces_record;
	}
	
}
