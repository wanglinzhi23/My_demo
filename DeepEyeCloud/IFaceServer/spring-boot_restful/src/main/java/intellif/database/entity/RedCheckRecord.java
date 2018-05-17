package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.dto.RedDto;
import intellif.dto.RedParamDto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Entity
@Table(name = GlobalConsts.T_NAME_RED_CHECK,schema=GlobalConsts.INTELLIF_BASE)
public class RedCheckRecord extends InfoBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5435759019312470342L;


	/**
	 * 红名单审核信息记录
	 */
	
	private static Logger LOG = LogManager.getLogger(RedCheckRecord.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String message;//申请记录
	
	private String applyPerson;//申请人员
	
	private String checkPerson;//审核人员
	
	private String cmpPerson;//比中人员
	
	private String result;//比对结果
	
	private String station;//单位
	
	private long sId;//搜索图片ID
	
	private long rId;//比中图片ID
	
	@Transient
	private String sUrl;//搜索图片url
	@Transient
	private String rUrl;//比中图片url
	
	private int searchDataType = 1;//搜索图片来源
	
	
	public RedCheckRecord(){
		
	}
	public RedCheckRecord(String message,String applyPerson,String cmpPerson,String station,String result,long sId,long rId,int type){
	     this.message = message;
	     this.applyPerson = applyPerson;
	     this.cmpPerson = cmpPerson;
	     this.station = station;
	     this.result = result;
	     this.sId = sId;
	     this.rId = rId;
	     this.searchDataType = type;
		}
	
	
	 public void updateRedCheck(RedParamDto rDto){
	    	if(null != rDto.getResult() && rDto.getResult().trim().length() > 0){
	    		this.result = rDto.getResult();
	    	}
	    	//待扩展
		}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getApplyPerson() {
		return applyPerson;
	}

	public void setApplyPerson(String applyPerson) {
		this.applyPerson = applyPerson;
	}

	public String getCheckPerson() {
		return checkPerson;
	}

	public void setCheckPerson(String checkPerson) {
		this.checkPerson = checkPerson;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public long getsId() {
		return sId;
	}

	public void setsId(long sId) {
		this.sId = sId;
	}

	public long getrId() {
		return rId;
	}

	public void setrId(long rId) {
		this.rId = rId;
	}

	public String getsUrl() {
		return sUrl;
	}

	public void setsUrl(String sUrl) {
		this.sUrl = sUrl;
	}

	public String getrUrl() {
		return rUrl;
	}

	public void setrUrl(String rUrl) {
		this.rUrl = rUrl;
	}

	public String getCmpPerson() {
		return cmpPerson;
	}

	public void setCmpPerson(String cmpPerson) {
		this.cmpPerson = cmpPerson;
	}
    public int getSearchDataType() {
        return searchDataType;
    }
    public void setSearchDataType(int searchDataType) {
        this.searchDataType = searchDataType;
    }

}
