package intellif.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Order")
// 404
public class MsgException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 3865515394514761864L;
    private int errorCode;
    private String message;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MsgException(String msg) {
        super(msg);
    }
    public MsgException(String msg,int errorCode) {
       this.message = msg;
       this.errorCode = errorCode;
    }
}