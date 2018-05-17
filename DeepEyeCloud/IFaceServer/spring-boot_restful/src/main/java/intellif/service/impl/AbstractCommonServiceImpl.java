package intellif.service.impl;

import intellif.database.dao.CommonDao;
import intellif.service.CommonServiceItf;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCommonServiceImpl<T> implements CommonServiceItf<T> {

    private static Logger LOG = LogManager.getLogger(AbstractCommonServiceImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public T findById(long id) {
        CommonDao dao = getDao();
        return (T) dao.findById(id);
       }

    @Override
    public List<T> findAll() {
        CommonDao dao = getDao();
        return dao.findAll();
    }

    @Override
    public void delete(long id) {
        CommonDao dao = getDao();
        dao.delete(id);
    }

    @Override
    public T save(T t) {
        CommonDao dao = getDao();
        dao.save(t);        
        return t;
    }

    @Override
    public T update(T t) {
        CommonDao dao = getDao();
        dao.update(t);        
        return t;
    }

    @Override
    public void batchSave(List<T> tList) {
        CommonDao dao = getDao();
        dao.batchSave(tList);        
    }
    

    @Override
    public void jdbcBatchSave(String valueSql) {
        CommonDao dao = getDao();
        dao.jdbcBatchSave(valueSql); 
    }
    @Override
    public void batchUpdate(List<T> tList) {  
        CommonDao dao = getDao();
        dao.batchUpdate(tList); 
    }

 

    @Override
    public void jdbcBatchUpdate(String filterSql, String updateSql) {
        CommonDao dao = getDao();
        dao.jdbcBatchUpdate(filterSql,updateSql); 
    }

    @Override
    public List<T> findByFilter(String filterSql) {
        CommonDao dao = getDao();
        return dao.findByFilter(filterSql); 
        }
    
    @Override
    public List<Map<String,Object>> findFieldsByFilter(List<String> fieldList,String filterSql) {
        CommonDao dao = getDao();
        return dao.findFieldsByFilter(fieldList,filterSql); 
        
    }
    @Override
    public List<Long> findFieldByFilter(String field,String filterSql) {
        CommonDao dao = getDao();
        return dao.findFieldByFilter(field,filterSql); 
    }

    @Override
    public void deleteByFilter(String filterSql) {
        CommonDao dao = getDao();
        dao.deleteByFilter(filterSql); 
    }

    @Override
    public Long count() {
        CommonDao dao = getDao();
        return dao.count(); 
    }

    @Override
    public Long countByFilter(String filter) {
        CommonDao dao = getDao();
        return dao.countByFilter(filter); 
    }

    @Override
    public List<T> findObjectBySql(String sql) {
        CommonDao dao = getDao();
        return dao.findObjectBySql(sql); 
    }

    @Override
    public Long countBySql(String sql) {
        CommonDao dao = getDao();
        return dao.countBySql(sql); 
    }


}
