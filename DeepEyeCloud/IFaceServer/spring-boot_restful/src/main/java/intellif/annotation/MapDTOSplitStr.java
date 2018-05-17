package intellif.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author yktang on V1.1.2
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapDTOSplitStr {
	String dtofieldname();
	String separator() default ",";
	char separatorchar() default 0x1D;
	boolean bychar() default false;
	boolean bystring() default false;
}
