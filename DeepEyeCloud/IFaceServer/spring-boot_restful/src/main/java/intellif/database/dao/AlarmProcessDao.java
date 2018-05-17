package intellif.database.dao;

import intellif.dto.AlarmProcessDetail;

import java.util.List;


public interface AlarmProcessDao<T> extends CommonDao<T>{
    public List<AlarmProcessDetail> findProcessedAlarmByParams(String cIds, int page,
            int pageSize);
}
