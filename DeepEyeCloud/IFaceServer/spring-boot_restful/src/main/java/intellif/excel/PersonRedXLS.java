package intellif.excel;

import intellif.validate.ImageExist;
import intellif.validate.Jinxin;
import intellif.validate.NotBlank;
import intellif.validate.SexType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.blogspot.na5cent.exom.annotation.Column;

public class PersonRedXLS {
	private static Logger log = LogManager.getLogger(PersonRedXLS.class);
	
	@NotBlank(fieldName = "红名单名称:")
	@Column(name = "红名单名称")
	private String name;// 姓名
	
    @SexType(fieldName="性别:")
	@Column(name = "性别")
	private String gender;// 性别

	@Column(name = "备注")
	private String remark;// 备注

	@Jinxin(fieldName = "警信号:")
	@Column(name = "警信号")
	private String policePhone;// 警信号

	
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


	@Override
	public String toString() {
		return "name: " + name + ",gender: " + gender + ",remark: " + remark +",policePhone: " + policePhone+",imageName1: " + imageName1+ ",imageName2: " + imageName2+ 
				",imageName3: " + imageName3;
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

	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getPolicePhone() {
		return policePhone;
	}


	public void setPolicePhone(String policePhone) {
		this.policePhone = policePhone;
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

	
	public String getImageName(int index){
		if(index == 1){
			return imageName1;
		}else if(index == 2){
			return imageName2;
		}else if(index == 3) {
			return imageName3;
		}else{
		    return null;
		}
	}
}
