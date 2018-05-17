package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HistoryOperationDto implements Serializable {

    private static final long serialVersionUID = -8721847450402807625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 操作类型
    private String operationtype;

    // 操作详情
    private String detail;

    // 操作人员
    private String operator;
    
    private String policeId;

    // 操作人员所属单位
    private String operatorstation;

    // 操作执行时间
    private String opetime;
    
    // 操作一级详细信息（搜索：搜索事由）
    private String friDetail = "";
    
    //操作二级详细信息 （搜索：搜索原因）
    private String secDetail = "";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperationtype() {
		return operationtype;
	}

	public void setOperationtype(String operationtype) {
		this.operationtype = operationtype;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperatorstation() {
		return operatorstation;
	}

	public void setOperatorstation(String operatorstation) {
		this.operatorstation = operatorstation;
	}

	public String getOpetime() {
		return opetime;
	}

	public void setOpetime(String opetime) {
		this.opetime = opetime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPoliceId() {
		return policeId;
	}

	public void setPoliceId(String policeId) {
		this.policeId = policeId;
	}

	public String getFriDetail() {
		return friDetail;
	}

	public void setFriDetail(String friDetail) {
		this.friDetail = friDetail;
	}

	public String getSecDetail() {
		return secDetail;
	}

	public void setSecDetail(String secDetail) {
		this.secDetail = secDetail;
	}

   

	
   

}