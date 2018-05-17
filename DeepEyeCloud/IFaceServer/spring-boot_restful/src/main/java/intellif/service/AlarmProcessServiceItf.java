package intellif.service;

import intellif.dto.AlarmProcessDetail;
import intellif.dto.QueryInfoDto;

import java.util.List;


public interface AlarmProcessServiceItf<T> extends CommonServiceItf<T>{
    public List<AlarmProcessDetail> findProcessedAlarmByParams(QueryInfoDto queryInfoDto);
}
