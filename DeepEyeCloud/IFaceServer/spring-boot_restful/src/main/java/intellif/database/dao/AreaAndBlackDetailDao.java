package intellif.database.dao;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.repository.query.Param;

public interface AreaAndBlackDetailDao<T> extends CommonDao<T>{
    List<BigInteger> findAreaIdsByPersonId(@Param("pId") long pId);

}
