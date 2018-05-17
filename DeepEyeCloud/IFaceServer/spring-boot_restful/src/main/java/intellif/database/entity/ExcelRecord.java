package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_EXCEL_RECORD,schema=GlobalConsts.INTELLIF_BASE)
public class ExcelRecord extends InfoBase implements Serializable {
	private static final long serialVersionUID = -8970483466796860314L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	// 所属人Id
	private long userId;
	// excel名称
	private String excelName;
	// 执行到第几行
	private int record;
	//代表当次导入
	private String uKey; 

	public ExcelRecord(){
		
	}
     public ExcelRecord(String excelName,String key,int record,long userId){
		this.excelName = excelName;
		this.uKey = key;
		this.record = record;
		this.userId = userId;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getExcelName() {
		return excelName;
	}

	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	public long getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}
	public String getuKey() {
		return uKey;
	}
	public void setuKey(String uKey) {
		this.uKey = uKey;
	}

	
}
