package intellif.validate;

import java.util.List;

public class ValidateUtil {
public static boolean validateResult(Object oj){
	 List<ValidateResult> result = AnnotationValidator.validate(oj, null);
	for(ValidateResult item : result){
		if(0 != item.getCode()){
			return false;
		}
	}
	return true;
}
}
