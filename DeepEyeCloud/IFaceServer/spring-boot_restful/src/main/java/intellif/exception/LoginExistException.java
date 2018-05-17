package intellif.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="账号已存在!")  // 406
public class LoginExistException extends RuntimeException {

		public LoginExistException(String msg) { 
			super(msg); 
		} 

}