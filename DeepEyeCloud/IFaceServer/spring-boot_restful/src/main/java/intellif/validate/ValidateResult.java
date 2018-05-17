package intellif.validate;
/**
 * 校验结果类
 * @author shixiaohua
 *
 */
public class ValidateResult {

	private int code;
	private String message;
	
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setErrorMessage(String message,int code){
		this.message = message;
		this.code = code;
	}
	public boolean isValid(){
		return code == 0;
	}
}
