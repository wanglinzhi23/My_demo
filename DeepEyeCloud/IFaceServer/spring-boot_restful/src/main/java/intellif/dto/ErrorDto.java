package intellif.dto;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: ErrorDto.java
 * @Package intellif.dto
 * @Description
 * @date 2018 05-09 20:13.
 */
public class ErrorDto  {
	String errCode;
	String errorMessage;
    public ErrorDto(){

    }

	public ErrorDto(String errCode, String errorMessage) {
		this.errCode = errCode;
		this.errorMessage = errorMessage;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
