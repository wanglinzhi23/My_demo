package intellif.database.dao;

import intellif.database.entity.EventInfo;

import java.util.List;

public interface AlarmInfoDao<T> extends CommonDao<T>{
public List<Long> getListLongBySql(String sql);
public List<EventInfo> getPersonEventListBySql(String sql);
}
