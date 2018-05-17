package intellif.jobs;

import intellif.dao.TaskInfoDao;
import intellif.enums.CronStatus;
import intellif.enums.TaskStatus;
import intellif.enums.TaskTypes;
import intellif.service.TaskServiceItf;
import intellif.database.entity.TaskInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by yangboz on 9/23/15.
 */
//@Named
public class StopTaskJob implements Job {

    private static Logger LOG = LogManager.getLogger(StopTaskJob.class);
    //
    private long taskId_job;
    //    @Inject
    private TaskInfoDao taskInfoDao_job;
    //    @Inject
    private TaskServiceItf taskServiceItf;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //Do injection with spring
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
//
        LOG.info("StopTaskJob,id:" + taskId_job + ",run() at:" + jobExecutionContext.getFireTime().toString());
        //
        taskId_job = dataMap.getLong("taskId");
        taskInfoDao_job = (TaskInfoDao) dataMap.get("taskInfoDao");
//        taskInfoDao_job = (TaskInfoDao) jobExecutionContext.get("taskInfoDao");
//        _thriftSocketService = (ThriftSocketServiceItf) jobExecutionContext.get("thrift");
        LOG.info("taskId_job:" + taskId_job + ",taskInfoDao_job:" + taskInfoDao_job);
        //
        executeStopTask(taskId_job);
    }

    private void executeStopTask(long taskId) {
        TaskInfo find = taskInfoDao_job.findOne(taskId);
        find.setStatus(TaskStatus.STOP.getValue());
        //update cron status for task
        if (find.getType() > TaskTypes.NORMAL.getValue()) {
            find.setCronStatus(CronStatus.STAND_BY.getValue());
        }
        taskInfoDao_job.save(find);
        // timer.cancel(); //Not necessary because we call System.exit
        taskServiceItf.teardown(taskId);

    }
}
