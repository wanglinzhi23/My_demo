package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

/**
 * 
 * @author weiyunyun
 * 
 * @date 2016.8.31
 */
@Entity
@Table(name = GlobalConsts.T_NAME_TABLES,schema=GlobalConsts.INTELLIF_FACE)
public class TableRecord implements Serializable {

	private static final long serialVersionUID = -5476786160052688058L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private Date startTime;
	
	private Date endTime;
	
	private String tableName;
	
	private long tableCode;
	
	private String shortName;
	
	private long totalNum;
	
	public TableRecord(Date sTime,Date eTime,long code,String sName){
		this.startTime = sTime;
		this.endTime = eTime;
		this.tableCode = code;
		this.shortName = sName;
		this.tableName = sName+"_"+code;
	}
	public TableRecord(){
		
	};

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public long getTableCode() {
		return tableCode;
	}

	public void setTableCode(long tableCode) {
		this.tableCode = tableCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public long getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(long totalNum) {
		this.totalNum = totalNum;
	}
	
}
