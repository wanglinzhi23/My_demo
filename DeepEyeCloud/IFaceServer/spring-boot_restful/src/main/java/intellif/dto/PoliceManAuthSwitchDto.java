package intellif.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;


public class PoliceManAuthSwitchDto implements Serializable {

	private static final long serialVersionUID = -8721847450402807625L;

	// 权限类别  1.2.3 (传0时默认为所有权限)
	private int authType;

	// 操作类型  0：关       1：开
	private int switchOnOrOff;

	// 警号列表
	private ArrayList<String> policeNoList;

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

	public int getSwitchOnOrOff() {
		return switchOnOrOff;
	}

	public void setSwitchOnOrOff(int switchOnOrOff) {
		this.switchOnOrOff = switchOnOrOff;
	}

	public ArrayList<String> getPoliceNoList() {
		return policeNoList;
	}

	public void setPoliceNoList(ArrayList<String> policeNoList) {
		this.policeNoList = policeNoList;
	}

	
   



}