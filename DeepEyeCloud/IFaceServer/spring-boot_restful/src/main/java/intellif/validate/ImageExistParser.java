package intellif.validate;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;

/**
 * 图片是否存在校验
 * @author shixiaohua
 *
 */
public class ImageExistParser implements IAnnotationParser {
	private static Logger log = LogManager.getLogger(ImageExistParser.class);
    /**
     * 校验字段f的值不能为null或者是空字符串，校验结果保存在result中
     */
    @SuppressWarnings("unchecked")
	@Override
    public  <T> ValidateResult validate(Field f, Object value,T... pArray) {
        ValidateResult result = new ValidateResult();
        if(f.isAnnotationPresent(ImageExist.class)){
        	ImageExist imageExist = f.getAnnotation(ImageExist.class);
            if(value != null && value.toString().length() != 0){
            	String basePath = null;
            	try{
                basePath = (String) pArray[0];
            	Map<String,String> fileExtMap = (Map<String, String>) pArray[1];
            	String ext = fileExtMap.get(value.toString());
            	String path = basePath+value+"."+ext;
            	File file = new File(path);
            	if(!file .exists() || file .isDirectory()){
            	 result.setErrorMessage(imageExist.fieldName() + "图片不存在",1001);
            	}
            	}catch(Exception e){
            	   log.error("ImageExistParser parse error,basePath:"+basePath,e);
            	   result.setErrorMessage(imageExist.fieldName() + "图片不存在",1001);
            	}
            }
        }
        return result;
    }

}