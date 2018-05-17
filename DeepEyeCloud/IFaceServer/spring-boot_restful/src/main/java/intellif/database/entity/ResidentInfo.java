package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

public class ResidentInfo implements Serializable {

private static final long serialVersionUID = -7139074519566000106L;

private long totalNum;//总人口个数
private long residentNum;//总常住人口数
private float rate;//常驻人口比例
private long area_id; //区域
private Date created;
public long getTotalNum() {
	return totalNum;
}
public void setTotalNum(long totalNum) {
	this.totalNum = totalNum;
}
public float getRate() {
	return rate;
}
public void setRate(float rate) {
	this.rate = rate;
}
public long getResidentNum() {
	return residentNum;
}
public void setResidentNum(long residentNum) {
	this.residentNum = residentNum;
}
public long getArea_id() {
	return area_id;
}
public void setArea_id(long area_id) {
	this.area_id = area_id;
}
public Date getCreated() {
	return created;
}
public void setCreated(Date created) {
	this.created = created;
}



}
