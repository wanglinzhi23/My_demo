package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CrimeSecType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrimeSecTypeDao extends JpaRepository<CrimeSecType, Long> {

	List<CrimeSecType> findByFriId(long id);
	
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_SEC_TYPE+" t WHERE name like %:name%", nativeQuery = true)
	List<CrimeSecType> queryByText(@Param("name") String name);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_SEC_TYPE+" t WHERE name = :name", nativeQuery = true)
	List<CrimeSecType> findSame(@Param("name") String name);
	
	@Query(value = "SELECT ts.* FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_SEC_TYPE+" ts left join "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_FRI_TYPE+" tf on ts.fri_id = tf.id where ts.name= :secName and tf.full_name= :firName",nativeQuery = true)
	List<CrimeSecType> findCrimeSecByNames(@Param("secName") String secName, @Param("firName") String firName);
}
