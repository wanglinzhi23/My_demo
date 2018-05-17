package intellif.service;

import intellif.database.entity.Area;
import intellif.dto.CommonQueryDto;
import intellif.utils.PageDto;

import java.util.List;

public interface AreaServiceItf<T> extends CommonServiceItf<T>{
    public List<Area> queryALLAreaInfoByConditions(List<String> filterList);
    public PageDto<Area> queryUserAreasByParams(CommonQueryDto cpd);
}
