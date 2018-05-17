package intellif.dao;

import intellif.database.entity.FaceInfo;
import intellif.database.entity.TableRecord;

import java.util.List;

public interface MultiTableBaseDao<T> {

	/**
	 * 新建或更新对象
	 * @param entity
	 * @return
	 */
	public T save(T entity); 

	/**
	 * 批量更新数据
	 * @param faceList
	 * @return
	 */
	public Integer update(List<T> faceList);

	/**
	 * 统计数据量总数（不包括当天数据）
	 * @return
	 */
	public Long count();

	/**
	 * 根据查询对象id查询结果
	 * @param idList
	 * @return
	 */
	public List<T> findByIds(List<?> idList); 

	/**
	 *  根据其他表id关联查询结果
	 * @param idList
	 * @param joinField
	 * @return
	 */
	public List<T> findByIdsFromOther(List<Long> idList, String joinField);

	/**
	 * 查询最近抓拍的人脸
	 * @param startTime
	 * @param endTime
	 * @param timeField
	 * @param queryString
	 * @param page
	 * @param pageSize
	 * @param lastId
	 * @param sourceIds
	 * @return
	 */
    List<T> findByTimeUsingStatistic(String startTime, String endTime, String timeField, String queryString,
                                     int page, int pageSize, String lastId, String sourceIds);

    /**
	 * 根据时间段查询结果
	 * @param startTime
	 * @param endTime
	 * @param timeField
	 * @param queryString
	 * @param page
	 * @param pagesize
	 * @return
	 */
	public List<T> findByTime(String startTime, String endTime, String timeField, String queryString, int page, int pagesize,String lastId);

	/**
	 * 根据时间段查询结果，包括其他行政区域的数据
	 * @param startTime
	 * @param endTime
	 * @param timeField
	 * @param queryString
	 * @param page
	 * @param pagesize
	 * @return
	 */
	public List<T> findByDistrictAndTime(String startTime, String endTime, String timeField,
										 long districtId, String queryString, int page, int pagesize,String lastId);
	
	/** 
	 * 根据查询条件全表查询结果
	 * @param queryString
	 * @return
	 */
	public List<T> findAll(String queryString);
	
	/** 
	 * 根据对象id全表查询结果
	 * @param queryString
	 * @return
	 */
	public List<T> findLast(String queryString);

	/** 
	 * 根据时间断查询结果,按需要的类型返回结果
	 * @param selectString
	 * @param startTime
	 * @param endTime
	 * @param timeField
	 * @param queryString
	 * @param entityClass
	 * @param page
	 * @param pagesize
	 * @return
	 */
	public <E> List<E> findByTime(String selectString, String startTime, String endTime, String timeField, String queryString, Class<E> entityClass, int page, int pagesize, boolean isPo,String lastId);

	/**
	 * 根据时间断查询结果,按需要的数据返回Object[]对象
	 * @param selectString
	 * @param startTime
	 * @param endTime
	 * @param timeField
	 * @param queryString
	 * @param page
	 * @param pagesize
	 * @return
	 */
	public List<Object[]> findByTime(String selectString, String startTime, String endTime, String timeField, String queryString, int page, int pagesize,String lastId);

	/**
	 * 根据Sql统计数据量
	 * @param sql
	 * @return
	 */
	public long countResult(String sql);
	
	
	/** 
     * 根据时间段查询结果
     * @param startTime
     * @param endTime
     * @param timeField
     * @param queryString
     * @param page
     * @param pagesize
     * @return
     */
  /*  public List<T> findByTime(String startTime, String endTime, String timeField, String queryString, int page, int pagesize);
*/    
    public List<T> findByTimeLatest(String startTime, String endTime, String timeField, String queryString,
            String latestTimeQueryString, int page, int pagesize);
	
//	/**
//	 * 获取指定时间表对象
//	 * @param date
//	 * @return
//	 */
//	public TableRecord getCurTable(String date);
    
    public List<T> findByTimeOffsetForDayun(String startTime, String endTime, String timeField, String queryString, long start, int pagesize,
            Long sequence);
}
