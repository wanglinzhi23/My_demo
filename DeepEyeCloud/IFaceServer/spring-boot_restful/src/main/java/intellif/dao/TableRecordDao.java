package intellif.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

import intellif.consts.GlobalConsts;
import intellif.database.entity.TableRecord;

public interface TableRecordDao extends CrudRepository<TableRecord, Long>{
	
	List<TableRecord> findAllByTableCode(String code);
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_NAME_TABLES + " t where end_time >:date and start_time <=:date order by start_time desc",nativeQuery = true)
	List<TableRecord> findAllByTime(@Param("date") Date date);
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where short_name = :tableName order by start_time desc",nativeQuery = true)
	List<TableRecord> findAllByTableName(@Param("tableName")String tableName);
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_NAME_TABLES + " t order by t.start_time desc limit 1",nativeQuery = true)
	TableRecord findFirstOrderByTime();
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where short_name = 't_face' and start_time BETWEEN :start and :end order by start_time desc",nativeQuery = true)
	List<TableRecord> statisticFaceInfoByTime(@Param("start")String start,@Param("end")String end);
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where short_name = 't_image' and start_time BETWEEN :start and :end order by start_time desc",nativeQuery = true)
	List<TableRecord> statisticImageInfoByTime(@Param("start")String start,@Param("end")String end);
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES  + " WHERE ((start_time <= :start and end_time > :start) or (start_time >= :start and start_time < :end)) and short_name = :tableName order by start_time desc",nativeQuery = true)
	List<TableRecord> findTableByTime(@Param("start")String start,@Param("end")String end,@Param("tableName")String tableName);
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES  + " WHERE ((start_time <= :start and end_time > :start) or (start_time >= :start and start_time < :end)) and short_name = :tableName order by start_time asc",nativeQuery = true)
	List<TableRecord> findTableByTimeASC(@Param("start")String start,@Param("end")String end,@Param("tableName")String tableName);
	
	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where total_num = 0 and table_code <:code order by start_time desc",nativeQuery = true)
	List<TableRecord> findTablesNotCount(@Param("code")long code);
	
	@Query(value = "select sum(total_num) from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where short_name = :tableName ",nativeQuery = true)
	Long countTatalByTable(@Param("tableName")String tableName);

	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_TABLES + " where short_name = :tableName and start_time <= :date and end_time > :date",nativeQuery = true)
	TableRecord getCurTable(@Param("date") String date, @Param("tableName") String tableName);

	@Query(value = "select * from " + GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_NAME_TABLES + " where short_name = :tableName and start_time <= :date  order by start_time asc limit 2",nativeQuery = true)
	List<TableRecord> find2LastOrderByTime(@Param("date") String date, @Param("tableName")String tableName);
}
