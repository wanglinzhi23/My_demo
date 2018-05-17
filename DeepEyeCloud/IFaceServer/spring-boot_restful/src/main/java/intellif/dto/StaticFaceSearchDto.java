package intellif.dto;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * 静态库条件检索dto
 *
 */
public class StaticFaceSearchDto implements Serializable {

	private static final long serialVersionUID = -203248779095704774L;
    //姓名
    private String name;
    //性别 (1：男    2：女    0：全部)
    private String sex;
    //手机号
    private String phone;
    //身份证
    private String idCard;
    //静态库种类  (3是cid库  4是juzhu库 5是在逃库 6是警综库)
    private int type;
    
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
    
}
