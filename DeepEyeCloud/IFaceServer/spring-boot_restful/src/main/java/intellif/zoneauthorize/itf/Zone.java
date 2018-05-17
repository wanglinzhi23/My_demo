package intellif.zoneauthorize.itf;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.Validate;

public interface Zone {
    
    Long zoneId();
	/**
	 * ID
	 * 
	 * @return
	 */
	/*default Long zoneId() {
		// 创建属性描述器
		PropertyDescriptor descriptor;
		try {
			descriptor = new PropertyDescriptor("sourceId", this.getClass());
			// 获得读方法
			Method readMethod = descriptor.getReadMethod();
			// 返回读方法运行结果
			Long id = (Long) readMethod.invoke(this);
			Validate.notNull(id, "id is null! ");
			return id;
		} catch (Throwable e) {
			throw new NotImplementedException("Can not find getId() method. Please override id() method by yourself! ",
					e);
		}
	}*/
}
