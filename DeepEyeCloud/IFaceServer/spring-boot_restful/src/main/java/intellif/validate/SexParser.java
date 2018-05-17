package intellif.validate;

import java.lang.reflect.Field;

/**
 * 性别校验器
 * @author shixiaohua
 *
 */
public class SexParser implements IAnnotationParser {

    /**
     * 校验字段f的值不能为null或者是空字符串，校验结果保存在result中
     */
    @Override
    public <T> ValidateResult validate(Field f, Object value,T... pArray) {
        ValidateResult result = new ValidateResult();
        if(f.isAnnotationPresent(SexType.class)){
        	SexType sexType = f.getAnnotation(SexType.class);
            if(value == null || value.toString().length() == 0){
                result.setErrorMessage(sexType.fieldName() + "不能为空",1001);
            }else{
            	if(!"男".equals(value.toString())&&!"女".equals(value.toString())){
            	result.setErrorMessage(sexType.fieldName() + "性别有误",1001);
            	}
            }
        }
        return result;
    }

}