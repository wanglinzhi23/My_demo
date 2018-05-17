package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.dto.RedDto;
import intellif.validate.SexType;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Entity
@Table(name = GlobalConsts.T_NAME_RED_PERSON,schema=GlobalConsts.INTELLIF_BASE)
public class RedPerson extends InfoBase implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2621972550185089164L;


	/**
	 * 红名单人员信息
	 */
	
	
	private static Logger LOG = LogManager.getLogger(RedPerson.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 人物名稱
	private String name;
	
	private String sex;
	
	private String remarks;
	
    private String policePhone;
	
	private String faceUrl;
	public RedPerson(){
		
	}
    public RedPerson(RedDto rDto){
		this.name = rDto.getName();
		this.remarks = rDto.getRemarks();
		this.sex = rDto.getSex();
		this.policePhone = rDto.getPolicePhone();
	}
    public void updateRedPerson(RedDto rDto){
    	if(rDto.getId()!= 0){
    		this.id = rDto.getId();
    	}
    	if(null != rDto.getName() && rDto.getName().trim().length() > 0){
    		this.name = rDto.getName();
    	}
    	if(null != rDto.getRemarks()){
    		this.remarks = rDto.getRemarks();
    	}
    	if(null != rDto.getSex() && rDto.getSex().trim().length() > 0){
    		this.sex = rDto.getSex();
    	}
    	if(null != rDto.getPolicePhone()){
    		this.policePhone = rDto.getPolicePhone();
    	}
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public String getFaceUrl() {
		return faceUrl;
	}
	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}
	public String getPolicePhone() {
		return policePhone;
	}
	public void setPolicePhone(String policePhone) {
		this.policePhone = policePhone;
	}
	@Override
	public RedPerson clone() {   
        try {   
            return (RedPerson) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    } 
	
}
