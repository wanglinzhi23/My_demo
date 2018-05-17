package intellif.service;

import java.util.Date;

public interface TableDivideServiceItf {
/**
 * face和image分表操作，每执行一次创建一个新表
 * @param startTime
 * @param endTime
 * @param code 
 */
	public void createTables(Date startTime,Date endTime,long code);
	
	/**
	 * 删除指定时间以后的所有分表和表记录信息
	 * @param date
	 */
	public void dropTables(Date date) throws Exception;
}
