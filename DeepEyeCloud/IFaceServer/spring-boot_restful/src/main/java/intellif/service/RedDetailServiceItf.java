package intellif.service;

import java.util.List;

import intellif.dto.JsonObject;
import intellif.dto.RedDto;
import intellif.dto.RedParamDto;
import intellif.database.entity.RedCheckRecord;
import intellif.database.entity.RedDetail;

/**
 * The Interface BlackDetailServiceItf.
 */
public interface RedDetailServiceItf<T> extends CommonServiceItf<T> {
	boolean updateFaceFeature(RedDetail redDetail) throws Exception;
	public JsonObject findRedListByPage(RedDto rd);
	public JsonObject findRedCheckByPage(RedParamDto rpd);
	public JsonObject findUserCheckRecords(RedParamDto rpd);
	public void updateRedCheckFromJinxin(String id,String name,String result,String dateStr);
}
