package intellif.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlComputeTask<T> extends RecursiveTask<ArrayList<T>> {

	private static final long serialVersionUID = -8245803974899324296L;

	private static Logger LOG = LogManager.getLogger(SqlComputeTask.class);

	EntityManager entityManager;

	private List<String> sqlList;
	private Class<T> className;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public SqlComputeTask(List<String> sqlList, Class<T> className, EntityManager entityManager) {
        this.sqlList = sqlList;
        this.className = className;
        this.entityManager = entityManager;
    }

    private ArrayList<T> compute(String Sql) {
    	LOG.info(Sql);
    	ArrayList<T> resp = new ArrayList<T>();
		try {
			Query query = this.entityManager.createNativeQuery(Sql, className);
			resp = (ArrayList<T>) query.getResultList();
		} catch (Exception e) {
			LOG.error("出错Sql："+Sql);
			LOG.error("ERROR:", e);
		} finally {
			entityManager.close();
		}
        return resp;
    }

    public ArrayList<T> compute() {
    	if(null == sqlList||sqlList.isEmpty()) return null;
    	if(sqlList.size() == 1) {
    		return compute(sqlList.get(0));
    	}
    	ArrayList<T> result = new ArrayList<T>();
    	if(sqlList.size()>1) {
    		SqlComputeTask<T> t1 = new SqlComputeTask<T>(sqlList.subList(0, sqlList.size()/2), className, entityManager);
    		SqlComputeTask<T> t2 = new SqlComputeTask<T>(sqlList.subList(sqlList.size()/2, sqlList.size()), className, entityManager);
    		t1.fork();
    		t2.fork();
            result.addAll(t1.join());
            result.addAll(t2.join());
    	}
        return result;
    }
}