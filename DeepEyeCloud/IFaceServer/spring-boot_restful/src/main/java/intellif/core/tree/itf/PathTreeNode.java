package intellif.core.tree.itf;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import intellif.core.tree.annotation.PreviousClass;
import intellif.core.tree.util.TreeUtil;

@SuppressWarnings("unchecked")
public interface PathTreeNode {
    
    /**
     * ID
     * 
     * @return
     */
    default Long id() {
        // 创建属性描述器
        PropertyDescriptor descriptor;
        try {
            descriptor = new PropertyDescriptor("id", this.getClass());
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
    }

	/**
	 * 路径
	 * 
	 * @return
	 */
	default String path() {
		// 创建属性描述器
		PropertyDescriptor descriptor;
		try {
			descriptor = new PropertyDescriptor("path", this.getClass());
			// 获得读方法
			Method readMethod = descriptor.getReadMethod();
			// 返回读方法运行结果
			String path = (String) readMethod.invoke(this);
			// 如果路径为空，则返回一个分隔符
			if (StringUtils.isBlank(path)) {
				return pathSeparator();
			}
			return path.trim();
		} catch (Throwable e) {
			throw new NotImplementedException(
					"Can not find getPath() method. Please override path() method by yourself! ", e);
		}
	}

	/**
	 * 路径分隔符
	 * 
	 * @return
	 */
	default String pathSeparator() {
		return "/";
	}

	/**
	 * 获取父节点ID
	 * 
	 * @return
	 */
	default Long father() {
		List<Long> forefather = forefather();
		if (CollectionUtils.isEmpty(forefather)) {
			return null;
		}
		return forefather.get(forefather.size() - 1);
	}

	/**
	 * 祖先节点ID列表
	 * 
	 * @return
	 */
	default List<Long> forefather() {
		String path = path();
		String[] tempArray = path.split(pathSeparator());
		List<Long> parentIdList = new ArrayList<>();
		for (String temp : tempArray) {
			if (StringUtils.isNotBlank(temp)) {
				parentIdList.add(Long.valueOf(temp));
				continue;
			}
		}
		return parentIdList;
	}

	/**
	 * 孩子节点路径
	 * 
	 * @return
	 */
	default String childPath() {
		return new StringBuilder(path()).append(id()).append(pathSeparator()).toString();
	}

	/*
	 * 获取ID类型
	 * 
	 * @return
	 *//*
		 * default Class<Long> idClass() { try { Type[] typeArray =
		 * this.getClass().getGenericInterfaces(); for (Type type : typeArray) {
		 * if (type instanceof ParameterizedType) { if (((ParameterizedType)
		 * type).getRawType().getTypeName().equals(PathTreeNode.class.getName())
		 * ) { return (Class<Long>) Class .forName(((ParameterizedType)
		 * type).getActualTypeArguments()[0].getTypeName()); } } } throw new
		 * NullPointerException("Can not find id class! "); } catch (Throwable
		 * e) { throw new NotImplementedException(
		 * "Catch exception by default implement idClass(). Please override path() method by yourself! "
		 * , e); } }
		 */

	/**
	 * 是否为叶子节点
	 * 
	 * @return
	 */

	default boolean leaf() {
		// 创建属性描述器
		PropertyDescriptor descriptor;
		try {
			descriptor = new PropertyDescriptor("leaf", this.getClass());
			// 获得读方法
			Method readMethod = descriptor.getReadMethod();
			// 返回读方法运行结果
			Boolean leaf = (Boolean) readMethod.invoke(this);
			Validate.notNull(leaf, "leaf is null! ");
			return leaf;
		} catch (Throwable e) {
			throw new NotImplementedException(
					"Can not find getLeaf() method. Please override leaf() method by yourself! ", e);
		}
	}
	
	/**
     * 获取上一张表格ID
     * @return 上一张表格ID
     */
    default Long previousTableId() {
        return null;
    }
    
    /**
     * 节点类型, 外键一般为nodeType() + "_id"
     * 
     * @return
     */
    @Transient
    default String getNodeType() {
        return TreeUtil.nodeType(this.getClass());
    }
    
    /**
     * 上一个表的Class
     * 
     * @return
     */
    default Class<?> previousClass() {
        PreviousClass previouse = this.getClass().getAnnotation(PreviousClass.class);
        return null == previouse ? null : previouse.value();
    }
}
