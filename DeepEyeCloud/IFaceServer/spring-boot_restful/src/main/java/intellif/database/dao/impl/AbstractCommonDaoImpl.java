package intellif.database.dao.impl;

import intellif.database.dao.CommonDao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
/**
 * 
 * @author shixiaohua
 *  数据库访问公共方法实现抽象类
 *
 * @param <T>
 */
public abstract class AbstractCommonDaoImpl<T> implements CommonDao<T> {

    private static Logger LOG = LogManager.getLogger(AbstractCommonDaoImpl.class);
    private static int patchSize = 100;//一次批量插入数目
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @PersistenceContext
    protected EntityManager entityManager;
    

    @SuppressWarnings("unchecked")
    @Override
    public T findById(long id) {
       if(0 == id){
           return null;
       }
       return entityManager.find(getEntityClass(), id);
  
    }

    @Override
    public List<T> findAll() {
        String tableName = getEntityTable();
        String sql = "select * from "+ tableName;
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(getEntityClass()));

    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void delete(long id) {
            if (0 != id) {
                String tableName = getEntityTable();
                String sql = "delete from " + tableName + " where id = " + id;
                jdbcTemplate.execute(sql);
            }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public T save(T t) {
        Object o = t;
        Field f;
        try {
            boolean isInsert = true;
            f = o.getClass().getDeclaredField("id");
            f.setAccessible(true);
            Object id = f.get(o);
            if(null != id){
                Long v = (Long) id;
                if(v.longValue() != 0){
                    isInsert = false;
                }
            }
           if(!isInsert){
               entityManager.merge(t);//update
           }else{
               entityManager.persist(t);//save
           }
        } catch (Throwable e) {
            LOG.info("DB entity save error:",e);
        }finally{
            entityManager.close();
        }
            return t;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public T update(T t) {
            entityManager.merge(t);
            entityManager.close();
            return t;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void batchSave(List<T> tList) {
       if(!CollectionUtils.isEmpty(tList)){
           int current = 0;
           for(T item : tList){
               current++;
               entityManager.persist(item);
               if(current % patchSize == 0){
                   entityManager.flush();
                   entityManager.clear();
               }
           }
       }
       entityManager.close();
    }
    

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void jdbcBatchSave(String valueSql) {
        String tableName = getEntityTable();
        String sql = "insert into "+ tableName+" values "+valueSql;
        LOG.info("DB SQL:"+sql);
        jdbcTemplate.execute(sql);
    }
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void batchUpdate(List<T> tList) {
        if(!CollectionUtils.isEmpty(tList)){
            int current = 0;
            for(T item : tList){
                current++;
                entityManager.merge(item);
                if(current % patchSize == 0){
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }
        entityManager.close();
        
    }

 

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void jdbcBatchUpdate(String filterSql, String updateSql) {
        String fSql = "";
        if(StringUtils.isNotBlank(filterSql)){
            fSql = " and "+filterSql;
        }
        String tableName = getEntityTable();
        String sql = "update " + tableName + " set "+ updateSql + " where 1 = 1 " + fSql;
        LOG.info("DB SQL:"+sql);
        jdbcTemplate.execute(sql);
    }

    @Override
    public List<T> findByFilter(String filterSql) {
        String fSql = "";
        if(StringUtils.isNotBlank(filterSql)){
            fSql = " and "+filterSql;
        }
        String tableName = getEntityTable();
        String sql = "select * from " + tableName + " where 1 = 1 " + fSql;
        LOG.info("DB SQL:"+sql);
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(getEntityClass()));
    }
    
    @Override
    public List<Map<String,Object>> findFieldsByFilter(List<String> fieldList,String filterSql) {
        String fSql = "";
        if(StringUtils.isNotBlank(filterSql)){
            fSql = " and "+filterSql;
        }
        if(!CollectionUtils.isEmpty(fieldList)){
            StringBuffer buffer = new StringBuffer();
            for(String field : fieldList){
                buffer.append(",");
                buffer.append(field);
            }
            String fieldSql = buffer.toString().substring(1);
            String tableName = getEntityTable();
            String sql = "select " + fieldSql + " from " + tableName + " where 1 = 1 " + fSql;
            LOG.info("DB SQL:"+sql);
            return  jdbcTemplate.queryForList(sql);
        }else{
            return null;
        }
        
    }
    @Override
    public List<Long> findFieldByFilter(String field,String filterSql) {
        String fSql = "";
        if(StringUtils.isNotBlank(filterSql)){
            fSql = " and "+filterSql;
        }
        if(StringUtils.isNotBlank(field)){
            String tableName = getEntityTable();
            String sql = "select " + field + " from " + tableName + " where 1 = 1 " + fSql;
            LOG.info("DB SQL:"+sql);
            return jdbcTemplate.queryForList(sql, Long.class);
        }else{
            return null;
        }
        
    }

    @Override
    public void deleteByFilter(String filterSql) {
        if(StringUtils.isNotBlank(filterSql)){
            String tableName = getEntityTable();
            String sql = "delete from " + tableName + " where " + filterSql;
            LOG.info("DB SQL:"+sql);
            jdbcTemplate.execute(sql);
        }
    }

    @Override
    public Long count() {
        String tableName = getEntityTable();
        String sql = "select count(1) from " + tableName;
        LOG.info("DB SQL:"+sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public Long countByFilter(String filter) {
        String tableName = getEntityTable();
        String sql = "";
       if(!StringUtils.isEmpty(filter)){
           sql = "select count(1) from " + tableName +" where " + filter;
       }else{
           sql = "select count(1) from " + tableName;
       }
       LOG.info("DB SQL:"+sql);
       return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public List<T> findObjectBySql(String sql) {
        LOG.info("DB SQL:"+sql);
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(getEntityClass()));
    }

    @Override
    public Long countBySql(String sql) {
        LOG.info("DB SQL:"+sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


}
