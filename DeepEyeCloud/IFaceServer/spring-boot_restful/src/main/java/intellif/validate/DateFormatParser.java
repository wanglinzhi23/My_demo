package intellif.validate;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateFormatParser implements IAnnotationParser{
	private static Logger log = LogManager.getLogger(DateFormatParser.class);

    /**
     * 校验f字段的值是否符合value的日期格式
     * @see DateFormat
     */
    @Override
    public  <T> ValidateResult validate(Field f, Object value,T... pArray) {
        ValidateResult result = new ValidateResult();
        if(f.isAnnotationPresent(DateFormat.class)){
            DateFormat dateFormat = f.getAnnotation(DateFormat.class);
            try {
                if(value != null){
                    SimpleDateFormat format = new SimpleDateFormat(dateFormat.format());
                    format.parse(value.toString());
                }
            } catch (Exception e) {
            	log.error("DateFormatParser parser error:",e);
                result.setMessage(dateFormat.fieldName() + "不满足格式：" + dateFormat.format());
            }   
        }
        return result;
    }
}
