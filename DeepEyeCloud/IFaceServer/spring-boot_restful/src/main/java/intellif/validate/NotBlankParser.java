package intellif.validate;

import java.lang.reflect.Field;

/**
 * 不能为空白校验器
 * @author shixiaohua
 *
 */
public class NotBlankParser implements IAnnotationParser {

    /**
     * 校验字段f的值不能为null或者是空字符串，校验结果保存在result中
     */
    @Override
    public  <T> ValidateResult validate(Field f, Object value,T... pArray) {
        ValidateResult result = new ValidateResult();
        if(f.isAnnotationPresent(NotBlank.class)){
            NotBlank notBlank = f.getAnnotation(NotBlank.class);
            if(value == null || value.toString().length() == 0){
                result.setErrorMessage(notBlank.fieldName() + "不能为空",1001);
            }
        }
        return result;
    }

}