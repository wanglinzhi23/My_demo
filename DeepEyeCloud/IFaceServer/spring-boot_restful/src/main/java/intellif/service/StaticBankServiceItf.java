package intellif.service;


import intellif.dto.StaticFaceSearchDto;

import java.math.BigInteger;
import java.util.List;

/**
 * 
 * @author Administrator 静态库条件查询
 * 
 */

public interface StaticBankServiceItf {

	List findByCondition(StaticFaceSearchDto staticFaceSearchDto, int page,
			int pageSize);

	BigInteger CountByCondition(StaticFaceSearchDto staticFaceSearchDto);
    public void updateIndexOfIds(String tableName,List<Long> idsList);

}
