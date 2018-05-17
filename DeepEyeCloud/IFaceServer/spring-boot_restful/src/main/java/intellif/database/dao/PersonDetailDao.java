package intellif.database.dao;

import intellif.dto.PersonFullDto;

import java.util.List;

public interface PersonDetailDao<T> extends CommonDao<T>{
public List<PersonFullDto> getPersonsBySql(String sql);
}
