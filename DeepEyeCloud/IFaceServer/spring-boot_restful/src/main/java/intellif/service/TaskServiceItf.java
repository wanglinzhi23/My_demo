package intellif.service;

import intellif.dao.TaskInfoDao;
import intellif.dto.TaskInfoDto;
import intellif.database.entity.TaskInfo;

import org.apache.thrift.TException;

import java.util.List;

public interface TaskServiceItf<T> extends CommonServiceItf<T>{

    public List<TaskInfo> findBySourceTypeAndUri(String sourceTable, String uri);

    public List<TaskInfoDto> findByCombinedConditions(TaskInfoDto taskInfoDto);

    public TaskInfo updateStatus(long id, int status);

    public void restartTimerTask(long id, String time) throws TException;

    public void stopTimerTask(long id, String time) throws TException;

    public TaskInfo findOne(long id);

    public TaskInfo save(TaskInfo info);

    public void resumeRelevance();

    //merged from old ThriftSocketService(setup/teardown normal/snaper tasks)
    public int setup(TaskInfo tInfo);

    public int teardown(long taskId);

    public int setup_snaper(int sourceType, long sourceId) throws TException;

    public int teardown_snaper(int sourceType, long sourceId) throws TException;
}
