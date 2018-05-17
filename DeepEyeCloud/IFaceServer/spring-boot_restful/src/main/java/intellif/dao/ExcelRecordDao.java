/**
 * 
 */
package intellif.dao;

import intellif.database.entity.ExcelRecord;

import org.springframework.data.repository.CrudRepository;

/**
 * The Interface BlackBankDao.
 *
 * @author yangboz
 */
//@Transactional
public interface ExcelRecordDao extends CrudRepository<ExcelRecord, Long> {
	
}
