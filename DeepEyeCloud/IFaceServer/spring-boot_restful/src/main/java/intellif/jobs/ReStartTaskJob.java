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
public class ReStartTaskJob implements Job {

    private static Logger LOG = LogManager.getLogger(ReStartTaskJob.class);

    //
//    @Autowired
//    private ThriftSocketServiceItf _thriftSocketService;

    //
    private long taskId_job;
    //    @Inject
    private TaskServiceItf taskServiceItf;
    private TaskInfoDao taskInfoDao_job;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //Do injection with spring
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
//
        LOG.info("ReStartTaskJob,id:" + taskId_job + ", run() at:" + jobExecutionContext.getFireTime().toString());
        //
        taskId_job = dataMap.getLong("taskId");
        taskInfoDao_job = (TaskInfoDao) dataMap.get("taskInfoDao");
        taskServiceItf = (TaskServiceItf) dataMap.get("taskService");
//        taskInfoDao_job = (TaskInfoDao) jobExecutionContext.get("taskInfoDao");
//        _thriftSocketService = (ThriftSocketServiceItf) jobExecutionContext.get("thrift");
        LOG.info("taskId_job:" + taskId_job + ",taskInfoDao_job:" + taskInfoDao_job + ",taskService:" + taskServiceItf);
        //
        executeRestartTask(taskId_job);
    }

    private void executeRestartTask(long taskId) {
        // timer.cancel(); //Not necessary because we call System.exit
        TaskInfo find = taskInfoDao_job.findOne(taskId);
        find.setId(taskId);
        find.setCreated(find.getCreated());
        //update cron status for task
        if (find.getType() > TaskTypes.NORMAL.getValue()) {
            find.setCronStatus(CronStatus.EXECUTED.getValue());
        }
        taskInfoDao_job.save(find);
        LOG.info("ReStart taskInfo:" + find.toString());
        int result = -1;
        if (find.getStatus() == TaskStatus.START.getValue())//already started
        {
//            _thriftSocketService.teardown(taskId);//FIXME: Sequence error found!!!
        } else {
            result = taskServiceItf.setup(find);//then start the task again.
        }
        // LOG.info("thriftSocketService:" +
        // _thriftSocketService.toString());
        if (result != 0) {
            LOG.fatal("TaskService setup failure!");
            // TODO:rollback the taskInfoDao.save();
        }
        LOG.info("TaskService(setup result):" + result);
    }
}
