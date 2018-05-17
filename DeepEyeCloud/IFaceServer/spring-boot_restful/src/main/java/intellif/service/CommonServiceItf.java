package intellif.service;



import intellif.database.dao.CommonDao;

import java.util.List;
import java.util.Map;


/**
 * 数据库访问原始接口，接口方法为每个表共性方法
 * @author shixiaohua
 *
 * @param <T>
 */
public interface CommonServiceItf<T> {

    /**
     * 根据主键查询对象
     * 
     * @return
     */
    public T findById(long id);
    
    /**
     * 查询出所有的T对象
     * 
     * @return List<T>
     */
    public List<T> findAll();

    /**
     * 根据主键删除对象
     * 
     * @return
     */
    public void delete(long id);

    /**
     * 保存单一对象
     * 
     * @return T
     */
    public T save(T t);
    
    /**
     * 更新单一对象
     * 
     * @return T
     */
    public T update(T t);
    
    /**
     * 统计表个数
     * 
     * @return T
     */
    public Long count();
    
    /**
     * 按条件统计表个数
     * 
     * @return T
     */
    public Long countByFilter(String Filter);
    
    /**
     * jpa批量保存对象
     * 
     * @return
     */
    public void batchSave(List<T> tList);
    
    /**
     * jdbc批量保存对象 
     * 
     * @param valueSql 例:(1,2),(3,4)...
     */
    public void jdbcBatchSave(String valueSql);
    
    /**
     * 批量更新对象
     * 
     * @return
     */
    public void batchUpdate(List<T> tList);
    
    /**
     * 特定条件批量更新特定字段
     * 
     * @param filterSql 过滤条件(例 a = 1 and b=2...)
     * @param updateSql 更新语句(例 a = 1 and b = 2...)
     */
    public void jdbcBatchUpdate(String filterSql,String updateSql);
    
    
    /**
     * 特定条件查询数据集
     * 
     * @return List<T>
     */
    public List<T> findByFilter(String filterSql);
    
    /**
     * 多表查询返回数据集
     * 
     * @return List<T>
     */
    public List<T> findObjectBySql(String sql);
    
    
    /**
     * 按sql统计返回结果
     * 
     * @return List<T>
     */
    public Long countBySql(String sql);
    
    
    /**
     * 特定条件删除
     * 
     * @return List<T>
     */
    public void deleteByFilter(String filterSql);
    
    /**
     * 获取Dao
     * @return
     */
    public CommonDao getDao();
 
    
    /**
     * 条件查询返回特定字段集合
     * @return
     */
    public List<Map<String,Object>> findFieldsByFilter(List<String> fieldList,String filterSql);

    /**
     * 根据条件查询指定一个字段结果集
     * @param field
     * @param filterSql
     * @return
     */
    public List<Long> findFieldByFilter(String field,String filterSql);

}
