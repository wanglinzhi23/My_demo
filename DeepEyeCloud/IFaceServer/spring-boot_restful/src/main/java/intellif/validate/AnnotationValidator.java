package intellif.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import intellif.controllers.FaceController;
import intellif.excel.PersonBankXLS;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 注解校验器
 * @author shixiaohua
 *
 */
public class AnnotationValidator {
	private static Logger log = LogManager.getLogger(AnnotationValidator.class);
    private static ConcurrentHashMap<String,IAnnotationParser> parseList = new ConcurrentHashMap<String,IAnnotationParser>();
   static{
	   parseList.put("NotBlank", new NotBlankParser());
	   parseList.put("SexType", new SexParser());
	   parseList.put("IDCardFormat", new IDCardFormatParser());
	   parseList.put("ImageExist", new ImageExistParser());
	   parseList.put("DateFormat", new DateFormatParser());
	   parseList.put("Jinxin", new JinxinParser());
   }

   private static String stringFormat(String str){
	   String rStr = null;
		try{
		String [] aa = str.split("\\(");
		String[] bb = aa[0].split("\\.");
		 rStr = bb[bb.length-1];
		}catch(Exception e){
			log.error("format string error,str："+str,e);
		}
		return rStr;
   }
    /**
     * 遍历所有字段，用所有解析器进行校验，如果校验失败，则终止校验返回结果，如果校验成功，同样返回校验结果
     * @param t
     * @return
     */
	public   static <T> List<ValidateResult> validate(T t,T... pArray){
        List<ValidateResult> resultList = new ArrayList<ValidateResult>();
        
        for (Field f : t.getClass().getDeclaredFields()) {
        	 f.setAccessible(true);
			  Annotation[] type = f.getAnnotations();
			  for(Annotation item : type){
				String name = stringFormat(item.toString());
				if(null != name){
					IAnnotationParser parser = parseList.get(name);
					if(null != parser){
						try{
							ValidateResult result = parser.validate(f, f.get(t), pArray);
							if(null!=result){
								resultList.add(result);
							}
						}catch(Exception e){
							log.error("validate parser error:",e);
						}
					}
				}
			  }
			
		  }
        
        return resultList;
    }
}