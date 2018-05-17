package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_CRIME_SEC_TYPE,schema=GlobalConsts.T_NAME_SOLR_INDEX_SETINAL)
public class IndexFaceRecord implements Serializable{

	private static final long serialVersionUID = -8473185797276849607L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long indexFaceId;

	private Long indexTime;
	
	
	private String indexContents;

	public IndexFaceRecord() {
		
	}

	
	public IndexFaceRecord(long indexFaceId, Long indexTime, String indexContents) {
	    this.indexFaceId = indexFaceId;
	    this.indexTime = indexTime;
	    this.indexContents = indexContents;
    }


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getIndexFaceId() {
		return indexFaceId;
	}


	public void setIndexFaceId(long indexFaceId) {
		this.indexFaceId = indexFaceId;
	}

	public Long getIndexTime() {
		return indexTime;
	}


	public void setIndexTime(Long indexTime) {
		this.indexTime = indexTime;
	}


	public String getIndexContents() {
		return indexContents;
	}


	public void setIndexContents(String indexContents) {
		this.indexContents = indexContents;
	}
	
	
	
}