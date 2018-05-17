package intellif.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class CollectionUtil {
	/**
	 * 取两个列表的交集
	 * 
	 * @param aList
	 * @param bList
	 * @return
	 */
	public static <T> List<T> mixed(List<T> aList, List<T> bList) {
		List<T> cList = new ArrayList<T>();
		if (CollectionUtils.isEmpty(aList) || CollectionUtils.isEmpty(bList)) {
			return cList;
		}
		cList.addAll(aList);
		cList.retainAll(bList);
		return cList;
	}

	/**
	 * 第一个列表减去第二个列表
	 * 
	 * @param aList
	 * @param bList
	 * @return
	 */
	public static <T> List<T> remove(List<T> aList, List<T> bList) {
		List<T> cList = new ArrayList<T>();
		if (CollectionUtils.isEmpty(aList)) {
			return cList;
		}
		cList.addAll(aList);
		if (CollectionUtils.isEmpty(bList)) {
			return cList;
		}

		cList.removeAll(bList);
		return cList;
	}
	
	/**
     * 将Iterable转成List
     * 
     * @param list
     * @return
     */
    public static <T> List<T> convert(Iterable<T> list) {
        List<T> retList = new ArrayList<T>();
        if (null == list) {
            return retList;
        }
        for (T t : list) {
            retList.add(t);
        }
        return retList;
    }
}
