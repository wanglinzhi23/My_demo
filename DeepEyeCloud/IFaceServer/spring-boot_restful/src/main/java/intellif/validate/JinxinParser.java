package intellif.validate;

import java.lang.reflect.Field;

/**
 * 警信格式检验 6位数字
 * @author shixiaohua
 *
 */
public class JinxinParser implements IAnnotationParser {

    /**
     * 校验字段f的值检验 6位数字，校验结果保存在result中
     */
    @Override
    public  <T> ValidateResult validate(Field f, Object value,T... pArray) {
        ValidateResult result = new ValidateResult();
        if(f.isAnnotationPresent(Jinxin.class)){
            Jinxin jinxin = f.getAnnotation(Jinxin.class);
            if(value != null && value.toString().trim().length() > 0){
                String str = value.toString().trim();
                if(str.length()==6){
                    try{
                      Integer.valueOf(str);
                    }catch(Exception ex){
                        result.setErrorMessage(jinxin.fieldName() + "格式错误，必须为6位数字",1001);
                    }
                 }else{
                     result.setErrorMessage(jinxin.fieldName() + "格式错误，必须为6位数字",1001);
                 }
            }
        }
        return result;
    }

}