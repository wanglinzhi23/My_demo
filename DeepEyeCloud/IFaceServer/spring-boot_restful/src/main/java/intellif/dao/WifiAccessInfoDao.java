package intellif.dao;

import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.WifiAccessInfo;

public interface WifiAccessInfoDao extends CrudRepository<WifiAccessInfo, Long> {

}
