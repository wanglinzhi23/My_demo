package intellif.validate;

import java.lang.reflect.Field;

public interface IAnnotationParser {
	  /**
     * 校验字段f的值不能为null或者是空字符串，校验结果保存在result中
	 * @param <T>
     */
   
    public <T> ValidateResult validate(Field f, Object value,T... pArray);
}
