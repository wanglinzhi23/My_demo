package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CrimeFriType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrimeFriTypeDao extends JpaRepository<CrimeFriType, Long> {

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_FRI_TYPE+" t WHERE full_name like %:name%", nativeQuery = true)
	List<CrimeFriType> queryByText(@Param("name") String name);

    @Query(value = "SELECT t.* FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_FRI_TYPE+" t, "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_SEC_TYPE+" a WHERE a.id = :id and a.fri_id=t.id", nativeQuery = true)
    CrimeFriType queryBySecId(@Param("id") long id);

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CRIME_FRI_TYPE+" t WHERE id <> :id and (full_name = :fullName)", nativeQuery = true)
	List<CrimeFriType> findSame(@Param("id") long id, @Param("fullName") String fullName);
}
