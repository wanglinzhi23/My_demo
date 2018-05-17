package intellif.utils;

import intellif.validate.ValidateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUploadUtil {
	 private static Logger LOG = LogManager
	            .getLogger(FileUploadUtil.class);
	/**
	 * 处理excel文件格式信息并返回
	 * 
	 * @param map
	 * @param path
	 * @param num
	 * @return
	 */
	public static int processexcelValidateResult(Map<Integer, List<ValidateResult>> map,
			Map<Integer, List<ValidateResult>> returnMap, String path, int num) {
		int a = 0;
		for (int i = 1; i <= num; i++) {
			List<ValidateResult> resultList = map.get(i);
			if (null != resultList && !resultList.isEmpty()) {
				StringBuffer buffer = new StringBuffer();
				int ii = i + 1;
				buffer.append("第" + ii + "行: ");
				List<ValidateResult> tempList = new ArrayList<ValidateResult>();
				int b = 0;
				for (ValidateResult item : resultList) {
					if (0 != item.getCode()) {
						b++;
						buffer.append(b + "、" + item.getMessage() + " ");
						a++;
						if (a <= 50) {
							tempList.add(item);
						}
					}
				}
				if (!tempList.isEmpty()) {
					returnMap.put(ii, tempList);
				}
				FileUtil.writeStringToFile(buffer.toString() + "\r\n", path);
			}
		}
		LOG.info("process check excel complete path:" + path + " size:" + a);
		return a;
	}
}
