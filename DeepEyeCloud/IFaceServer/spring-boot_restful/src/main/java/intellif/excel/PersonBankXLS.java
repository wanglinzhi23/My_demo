package intellif.excel;

import intellif.validate.AnnotationValidator;
import intellif.validate.IDCardFormat;
import intellif.validate.ImageExist;
import intellif.validate.NotBlank;
import intellif.validate.SexType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;
import org.springframework.util.CollectionUtils;

import com.blogspot.na5cent.exom.annotation.Column;

public class PersonBankXLS {
	private static Logger log = LogManager.getLogger(PersonBankXLS.class);
	
	@NotBlank(fieldName = "嫌疑人名称:")
	@Column(name = "嫌疑人名称")
	private String name;// 姓名
	
    @SexType(fieldName="性别:")
	@Column(name = "性别")
	private String gender;// 性别

	@Column(name = "出生日期")
	private String birthday;// 出生日期

	@Column(name = "民族")
	private String nation;// 民族
	@IDCardFormat(fieldName = "身份证:")
	@Column(name = "身份证")
	private String cid;// 证件号

	@Column(name = "原住址")
	private String address;// 家庭住址

	@Column(name = "所属人员库名")
	private String bankName;// 所属人员库名

	@Column(name = "说明")
	private String descritpion;// 描述信息
	
	@Column(name = "犯罪地点")
	private String crimeDes;// 犯罪地点
	
	@Column(name = "犯罪类型")
	private String crimeType;// 犯罪类型
	
	@NotBlank(fieldName = "人脸照片名称1:")
	@ImageExist(fieldName="人脸照片名称1:")
	@Column(name = "人脸照片名称1")
	private String imageName1;// 图片文件名
	
	@ImageExist(fieldName="人脸照片名称2:")
	@Column(name = "人脸照片名称2")
	private String imageName2;// 图片文件名
	
	@ImageExist(fieldName="人脸照片名称3:")
	@Column(name = "人脸照片名称3")
	private String imageName3;// 图片文件名
	
	@ImageExist(fieldName="人脸照片名称4:")
	@Column(name = "人脸照片名称4")
	private String imageName4;// 图片文件名
	
	@Column(name="布控设置")
	private String dispatchSetting;//布控设置

	@Override
	public String toString() {
		return "name: " + name + ",gender: " + gender + ", birthday: " + birthday + ",nation: " + nation + ",cid: "
				+ cid + ",address: " + address + ",bankName: " + bankName + ",imageName1: " + imageName1+ ",imageName2: " + imageName2+ 
				",imageName3: " + imageName3+ ",imageName4: " + imageName4+ ",crimeDes: " + crimeDes+",crimeType: " + crimeType+ 
				",descritpion: " + descritpion + ",dispatchSetting:"+dispatchSetting;
	}

	
	public boolean isExist(){
		boolean state = true;
		try{
			for (Field f : this.getClass().getDeclaredFields()) {
				 Annotation[] type = f.getAnnotations();
				if(null != type && type.length>0){
					f.setAccessible(true);
					Object value = f.get(this);
					if(value == null || value.toString().length() == 0){
						state = false;	
					}else{
						return true;
					}
				}
			}
		}catch(Exception e){
			log.error("check xlsItem isExist error:",e);
		}
	  return state;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}
	
	public int getIntGender() {
		return gender.equals("男")?0:1;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}
	
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");//2015-07-16
	public Date getBirthdayDt() throws ParseException {
		return format.parse(birthday);
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescritpion() {
		return descritpion;
	}

	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}

	

	public String getCrimeDes() {
		return crimeDes;
	}

	public void setCrimeDes(String crimeDes) {
		this.crimeDes = crimeDes;
	}

	public String getCrimeType() {
		return crimeType;
	}

	public void setCrimeType(String crimeType) {
		this.crimeType = crimeType;
	}

	public String getImageName1() {
		return imageName1;
	}

	public void setImageName1(String imageName1) {
		this.imageName1 = imageName1;
	}

	public String getImageName2() {
		return imageName2;
	}

	public void setImageName2(String imageName2) {
		this.imageName2 = imageName2;
	}

	public String getImageName3() {
		return imageName3;
	}

	public void setImageName3(String imageName3) {
		this.imageName3 = imageName3;
	}

	public String getImageName4() {
		return imageName4;
	}

	public void setImageName4(String imageName4) {
		this.imageName4 = imageName4;
	}

	public String getBankName() {
		return bankName;
	}
	public String getImageName(int index){
		if(index == 1){
			return imageName1;
		}else if(index == 2){
			return imageName2;
		}else if(index == 3){
			return imageName3;
		}else{
			return imageName4;
		}
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	
	public String getDispatchSetting() {
		return dispatchSetting;
	}

	public void setDispatchSetting(String dispatchSetting) {
		this.dispatchSetting = dispatchSetting;
	}

	public boolean isDispatch(){
		if("是".equals(this.dispatchSetting)){
			return true;
		}else{
			return false;
		}
	}
}
