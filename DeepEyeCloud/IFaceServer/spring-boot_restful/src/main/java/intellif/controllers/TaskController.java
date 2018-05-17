/**
 *
 */
package intellif.controllers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.TaskInfoDao;
import intellif.dto.JsonObject;
import intellif.dto.TaskCronTabs;
import intellif.dto.TaskInfoDto;
import intellif.enums.TaskTypes;
import intellif.guice.QuartJobModule;
import intellif.jobs.CronJobFactory;
import intellif.jobs.ReStartTaskJob;
import intellif.jobs.StopTaskJob;
import intellif.service.TaskServiceItf;
import intellif.database.entity.TaskInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <h1>The Class TaskController.</h1>
 * The TaskController which serves request of the form /task and returns a JSON object representing an instance of TaskInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc.
 * (see <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and static data storages),
 * while REST is a very-high-level API style (mostly for webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-05-31
 */
@RestController
//@RequestMapping("/intellif/task")
@RequestMapping(GlobalConsts.R_ID_TASK)
public class TaskController {
    private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(TaskController.class);
    //
    public long taskId_job;
    //
    @Autowired
    private TaskServiceItf _taskService;
    @Autowired
    private TaskInfoDao _taskInfoDao;
    @Autowired
    private AlarmInfoDao _alarmInfoDao;

    //
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the task is successfully created or not."
            , notes = "Oneway communication to Thrift socket server. And cronTabs examples:0 10 14 ? # FRI, 0 0/5 # # # ?")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.CREATED)
    public JsonObject create(@RequestBody @Valid TaskInfo taskInfo) {
        LOG.info("POST taskInfo:" + taskInfo.toString());
        TaskInfo respTaskInfo = _taskInfoDao.save(taskInfo);
        // LOG.info("thriftSocketService:" + _thriftSocketService.toString());
//        int result = _thriftSocketService.setup(respTaskInfo);
//        LOG.info("thriftSocketService(setup result):" + result);
//        if (result != 0) {
//            LOG.fatal("ThriftSocketService setup failure!");
//            // TODO:rollback the taskInfoDao.save();
//        } else {
        //if cron tab
        //Assemble TaskCronTabs object
        TaskCronTabs taskCronTabs = new TaskCronTabs();
        taskCronTabs.setTaskId(respTaskInfo.getId());
        scheduleWithTaskInfo(taskInfo, taskCronTabs);
        //
        return new JsonObject(respTaskInfo);
    }

    private void scheduleWithTaskInfo(@RequestBody @Valid TaskInfo taskInfo, TaskCronTabs taskCronTabs) {
        if (TaskTypes.SCHEDULE.getValue() == taskInfo.getType() && (null != taskInfo.getCronTabs()) && (!taskInfo.getCronTabs().equals(""))) {
            List<String> rawCronTabs = Arrays.asList(taskInfo.getCronTabs().split(";"));
            List<List<String>> cronList = analyzeCronStr(rawCronTabs);
            List<String> startCronTabs = cronList.get(0);
            List<String> stopCronTabs = cronList.get(1);
//            List<String> startCronTabs = rawCronTabs;
//            List<String> stopCronTabs = new ArrayList<>(Arrays.asList("0 /59 * * * ?"));//Very hour every day.(0 * /1 * * ?)
            LOG.info("CronTabs value,start at:" + startCronTabs + ",stop at:" + stopCronTabs);
            //@see: https://github.com/jmrozanec/cron-utils
//        //define your own cron: arbitrary fields are allowed and last field can be optional
//        //or get a predefined instance
//            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
//            //create a descriptor for a specific Locale
//            CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINA);
//            //create a parser based on provided definition
//            CronParser parser = new CronParser(cronDefinition);
//        Cron quartzCron = parser.parse("0 23 ? * * 1-5 *");
////parse some expression and ask descriptor for description
//        String description = descriptor.describe(parser.parse("*/45 * * * * *"));
////description will be: "every 45 seconds"
//        description = descriptor.describe(quartzCron);
//        LOG.info("description:" + description);
////description will be: "every hour at minute 23 every day between Monday and Friday"
////which is the same description we get for the cron below:
//        LOG.info(descriptor.describe(parser.parse("0 23 ? * * MON-FRI *")));
//            Calculate time from/to execution
//            FastDateFormat formatter = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT;
//            for (int i = 0; i < rawCronTabs.size(); i++) {
//                //Get date for last execution
//                DateTime now = DateTime.now().toDateTime(DateTimeZone.UTC);
//                Cron quartzCron = parser.parse(rawCronTabs.get(i));
////            LOG.info("quartzCron:" + quartzCron.getCronDefinition());
////            LOG.info("quartzCron:" + quartzCron.retrieveFieldsAsMap());
//                CronField hourCronField = quartzCron.retrieve(CronFieldName.HOUR);
////            LOG.info("quartzCron,HOUR:" + hourCronField.getExpression());
//                String description = descriptor.describe(quartzCron);
//////description will be: "every 45 seconds"
////        description = descriptor.describe(quartzCron);
//                LOG.info("quartzCron description:" + description);
//                ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
//                DateTime lastExecution = executionTime.lastExecution(now);
//                LOG.info("lastExecution:" + lastExecution);
////Get date for next execution
//                DateTime nextExecution = executionTime.nextExecution(now);
//                LOG.info("nextExecution:" + nextExecution);
////Time from last execution
//                Duration timeFromLastExecution = executionTime.timeFromLastExecution(now);
//                LOG.info("timeFromLastExecution:" + timeFromLastExecution);
////Time to next execution
//                Duration timeToNextExecution = executionTime.timeToNextExecution(now);
//                LOG.info("timeToNextExecution:" + timeToNextExecution);
//                // Get nearest hour
////            Date nearestHour = DateUtils.round(lastExecution, Calendar.HOUR);
////            LOG.info("raw nearestHour = " + nearestHour);
////            LOG.info("format nearestHour = " + formatter.format(nearestHour));
////            DateTime.Property lastHour = lastExecution.hourOfDay();
////            LOG.info("lastHour:" + lastHour.getDateTime().getHourOfDay());
//            }
            // Schedule or unSchedule(by calculation) with cronTabs value
            taskCronTabs.setCronTabs(startCronTabs);
            scheduleCronTasks(taskCronTabs, ReStartTaskJob.class);
            //
            taskCronTabs.setCronTabs(stopCronTabs);//Very hour every day.
            scheduleCronTasks(taskCronTabs, StopTaskJob.class);
//        }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of task that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._taskInfoDao.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this._taskInfoDao.findOne(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  task is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid TaskInfo taskInfo) throws SchedulerException {
        TaskInfo find = this._taskInfoDao.findOne(id);
        taskInfo.setId(id);
        taskInfo.setCreated(find.getCreated());
        if (TaskTypes.NORMAL.getValue() == taskInfo.getType()) {
            //IFaceEngine IoController,just restart the task.
            _taskService.teardown(id);
            //XXX:Then sync the task status
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
            //
            _taskService.setup(taskInfo);
        } else if (TaskTypes.SCHEDULE.getValue() == taskInfo.getType()) {
            //un-schedule some old cron tabs.
            this.unscheduleSomeCronTask(id);
            //update the cron tab values.
            //Assemble TaskCronTabs object
            TaskCronTabs taskCronTabs = new TaskCronTabs();
            taskCronTabs.setTaskId(id);
            scheduleWithTaskInfo(taskInfo, taskCronTabs);
        }
        //
        return new JsonObject(this._taskInfoDao.save(taskInfo));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the task is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) throws SchedulerException {
        TaskInfo find = this._taskInfoDao.findOne(id);
        LOG.info("DELETE taskInfo(id):" + id + ",type:" + find.getType());
        _taskService.teardown(id);
        //Deleting a Job and un-scheduling
//        LOG.info("TaskTypes.SCHEDULE.getValue() == find.getType():" + (TaskTypes.SCHEDULE.getValue() == find.getType()));
        if (TaskTypes.SCHEDULE.getValue() == find.getType()) {
            Scheduler scheduler = schedulerFactory.getScheduler();
            List<String> rawCronTabs = Arrays.asList(find.getCronTabs().split(";"));
            int size = rawCronTabs.size();
            // Schedule the job with the trigger
            for (int i = 0; i < size; i++) {
                JobKey jobKey = new JobKey(GlobalConsts.QUARTZ_JOB_TRIGGER_PREFIX + rawCronTabs.get(i), GlobalConsts.QUARTZ_JOB_GROUP_PREFIX + taskId_job);
                LOG.info("scheduler.deleteJob jobKey,group:" + jobKey.getGroup() + ",name:" + jobKey.getName());
                scheduler.deleteJob(jobKey);
            }
            //un-schedule force by shutdown
//        if (!scheduler.isShutdown()) {
//            scheduler.shutdown();
//        }
        }
        //FIXME: using soft-delete,@see: http://featurenotbug.com/2009/07/soft-deletes-using-hibernate-annotations/
        try {
            find.setTaskName(find.getTaskName() + "(已删除)");
            find.setStatus(-1);
            this._taskInfoDao.save(find);
//            this._taskInfoDao.delete(id);
//            this._alarmInfoDao.delAlarmsByTaskId(id + "");
//            this._alarmInfoDao.deleteByTaskId(id);
        } catch (Exception e) {
            LOG.warn(e);
            return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    // stop
    @RequestMapping(value = "/stop/{id}/at/{time}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the task is successfully stopped or not at some time.")
    public JsonObject stop(@PathVariable("id") long id, @RequestParam(value = "time", required = false) String time) throws SchedulerException {
        LOG.info("STOP taskInfo(id):" + id);
        try {
            _taskService.stopTimerTask(id, time);
        } catch (Exception e) {
           LOG.error(" stop task error:",e);
        }
        //
        return new JsonObject(true);
    }

    // restart
    @RequestMapping(value = "/restart/{id}/at/{time}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  task is successfully restart or not at some time.")
    public JsonObject restartAtTime(@PathVariable("id") long id,
                                    @RequestParam(value = "time", required = false) String time) {
//
        try {
            _taskService.restartTimerTask(id, time);
        } catch (Exception e) {
            LOG.error(" restart task error:",e);
        }
        return new JsonObject(true);
    }

    @RequestMapping(value = "/restart/schedule", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  task is successfully restart or not under schedule.")
    public JsonObject restartUnderSchedule(@RequestBody @Valid TaskCronTabs taskCronTabs) {
        //
        scheduleCronTasks(taskCronTabs, ReStartTaskJob.class);
        return new JsonObject(true);
    }

    private void scheduleCronTasks(TaskCronTabs taskCronTabs, Class taskClass) {
        this.taskId_job = taskCronTabs.getTaskId();
//        this.taskInfoDao_job = this._taskInfoDao;
        // Schedule with cronTabs value
        for (int i = 0; i < taskCronTabs.getCronTabs().size(); i++) {
            String crobTab = taskCronTabs.getCronTabs().get(i);
            try {
                scheduleSomeCronTask(crobTab, taskClass);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/stop/schedule", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  task is successfully stop or not under schedule.")
    public JsonObject stopUnderSchedule(@RequestBody @Valid TaskCronTabs taskCronTabs) {
        //
        scheduleCronTasks(taskCronTabs, StopTaskJob.class);
        return new JsonObject(true);
    }

    // FindBySourceId
    @RequestMapping(value = "/source/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task source id is successfully get or not.")
    public JsonObject findBySourceId(@PathVariable("id") int id) {
        return new JsonObject(this._taskInfoDao.findBySourceId(id));
    }

    // FindBySourceType
    @RequestMapping(value = "/source/type/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task source type id is successfully get or not.")
    public JsonObject findBySourceType(@PathVariable("id") int id) {
        return new JsonObject(this._taskInfoDao.findBySourceType(id));
    }

    // FindByTaskName
    @RequestMapping(value = "/name/{value}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task name is successfully get or not.")
    public JsonObject findByName(@PathVariable("value") String value) {
        return new JsonObject(this._taskInfoDao.findByTaskName(value));
    }

    // FindByTaskStatus
    @RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task status id is successfully get or not.")
    public JsonObject findByStatus(@PathVariable("id") int id) {
        return new JsonObject(this._taskInfoDao.findByStatus(id));
    }

    // FindByServerName
    @RequestMapping(value = "/server/name/{value}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the server name is successfully get or not.")
    public JsonObject findByServerName(@PathVariable("value") String value) {
        return new JsonObject(this._taskInfoDao.findByServerName(value));
    }

    // FindByTimePeriod
    @RequestMapping(value = "/time/{start}/{end}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the time period is successfully get or not.")
    public JsonObject findByTimePeriod(@PathVariable("start") String start, @PathVariable("end") String end) {
        return new JsonObject(this._taskInfoDao.findByTimePeriod(start, end));
    }

    // FindByBankName
    @RequestMapping(value = "/bank/name/{value}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the bank name is successfully get or not.")
    public JsonObject findByBankName(@PathVariable("value") String value) {
        return new JsonObject(this._taskInfoDao.findByBankName(value));
    }

    // FindBySourceTypeAndUri
    @RequestMapping(value = "/source/{type}/{uri}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the source type and uri is successfully get or not.")
    // public JsonObject findBySourceTypeAndUri(@PathVariable("type") int
    // type,@PathVariable("uri") String uri ) {
    public JsonObject findBySourceTypeAndUri(
            @RequestParam(value = "type", required = true, defaultValue = "0") int type,
            @RequestParam(value = "uri", required = false, defaultValue = "192.168.2.64") String uri) {
        String sourceTable = GlobalConsts.TABLE_NAMES_SOURCE[type];
        LOG.info("sourceTable:" + sourceTable);
        // return new
        // JsonObject(this._taskInfoDao.findBySourceTypeAndUri(sourceTable,
        // uri));
        // return new JsonObject(this._taskInfoDao.findBySourceTypeAndUri(uri));
        return new JsonObject(this._taskService.findBySourceTypeAndUri(sourceTable, uri));
    }

    // findByCombinedConditions
    //
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the task is successfully created or not.", notes = "Oneway communication to Thrift socket server.")
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findByCombinedConditions(@RequestBody @Valid TaskInfoDto taskInfoDto) {
        LOG.info("POST taskInfoDto:" + taskInfoDto.toString());
        List<TaskInfoDto> respTaskInfoDto = _taskService.findByCombinedConditions(taskInfoDto);
        return new JsonObject(respTaskInfoDto);
        //
    }

    private void scheduleSomeCronTask(String cronTab, Class clazz) throws SchedulerException {
        //@see: http://www.themoderngeek.co.uk/software-development/2015/01/19/quartz-scheduler-part-1.html
//        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        //
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", taskId_job);
        //
//        scheduler.getContext().put("taskInfoDao", _taskInfoDao);
//        scheduler.getContext().put("thrift", _thriftSocketService);
        //Autowire them...
        jobDataMap.put("taskInfoDao", _taskInfoDao);
        jobDataMap.put("taskService", _taskService);
        //@see:http://quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-03
        JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(GlobalConsts.QUARTZ_JOB_IDENTIFY_PREFIX + cronTab, GlobalConsts.QUARTZ_JOB_GROUP_PREFIX + taskId_job)
//                .usingJobData("taskId", taskId)
                .usingJobData(jobDataMap)
                .build();
//        Date triggerEndDate = new Date();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(GlobalConsts.QUARTZ_JOB_TRIGGER_PREFIX + cronTab, GlobalConsts.QUARTZ_JOB_GROUP_PREFIX + taskId_job)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronTab))
//                .endAt(triggerEndDate)
                .build();
        scheduler.start();

        // The Guice injector used to create instances.
        Injector injector = Guice.createInjector(new QuartJobModule());
        // Here we tell the Quartz scheduler to use our factory.
        scheduler.setJobFactory(injector.getInstance(CronJobFactory.class));
        scheduler.scheduleJob(jobDetail, trigger);
        LOG.info("scheduleSomeCronTask at:" + scheduler.getJobGroupNames() + ",taskId_job:" + taskId_job + ",class:" + clazz.toString());
        //
    }

    private void unscheduleSomeCronTask(long taskId_job) throws SchedulerException {
        //@see: http://www.themoderngeek.co.uk/software-development/2015/01/19/quartz-scheduler-part-1.html
//        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        //Unscheduling a Particular Trigger of Job
//        List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
//        for (JobExecutionContext jobCtx : currentJobs) {
//            LOG.info("the job is already running:" + jobCtx.getJobDetail().toString());
//        }

        //
        TaskInfo find = this._taskInfoDao.findOne(taskId_job);
        List<String> rawCronTabs = Arrays.asList(find.getCronTabs().split(";"));
        int size = rawCronTabs.size();
        //un-schedule cron tab item.
        for (int i = 0; i < size; i++) {
            TriggerKey triggerKey = new TriggerKey(GlobalConsts.QUARTZ_JOB_TRIGGER_PREFIX + rawCronTabs.get(i), GlobalConsts.QUARTZ_JOB_GROUP_PREFIX + taskId_job);
            LOG.info("unscheduleSomeCronTask triggerKey,group:" + triggerKey.getGroup() + ",name:" + triggerKey.getName());
            scheduler.unscheduleJob(triggerKey);
        }

    }

    public List<List<String>> analyzeCronStr(List<String> cronStrList) {
        // TODO Auto-generated method stub
        List<List<String>> cronList = new ArrayList<List<String>>();
        List<String> startCronList = new ArrayList<String>();
        List<String> endCronList = new ArrayList<String>();

        for (String cronStr : cronStrList) {
            String[] data = cronStr.split(" ")[2].split(",");
            int size = data.length;
            int curr = -1;
            int next = -1;
            String start = "";
            String end = "";
            String startCron = cronStr.replace(cronStr.split(" ")[2], "@clock");
            String endCron = startCron;
            List<Integer> startlist = new ArrayList<Integer>();
            List<Integer> endlist = new ArrayList<Integer>();
            for (int i = 0; i < size; i++) {
                curr = Integer.valueOf(data[i]);
                if (i == 0) {
                    startlist.add(curr);//第一个元素
                }
                if ((i + 1) == size) {
                    if (curr < 23) {
                        endlist.add(curr + 1);//最后一个元素
                    } else {
                        endCronList.add(endCron.replace("0 0 ", "59 59 ").replace("@clock", "23"));
                    }
                    break;
                }
                next = Integer.valueOf(data[i + 1]);
                if (Math.abs((curr - next)) != 1) {
                    endlist.add(curr + 1);
                    startlist.add(next);
                }
            }

            for (int i = 0; i < startlist.size(); i++) {
                if (i == startlist.size() - 1) {
                    start += startlist.get(i);
                } else {
                    start += startlist.get(i) + ",";
                }
            }
            startCronList.add(startCron.replace("@clock", start));

            for (int i = 0; i < endlist.size(); i++) {
                if (i == endlist.size() - 1) {
                    end += endlist.get(i);
                } else {
                    end += endlist.get(i) + ",";
                }
            }
            endCronList.add(endCron.replace("@clock", end));
        }
        cronList.add(startCronList);
        cronList.add(endCronList);

        for (int i = 0; i < startCronList.size(); i++) {
            System.out.println(startCronList.get(i));
        }
        System.out.println("");
        for (int i = 0; i < endCronList.size(); i++) {
            System.out.println(endCronList.get(i));
        }
        return cronList;
    }
//    public TaskInfoDao taskInfoDao_job;

//    @RequestMapping(value = "/snaper/type/{type}/id/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
//    @ApiOperation(httpMethod = "POST", value = "Response a snaper task is successfully created or not.")
//    public int snaper_create(@PathVariable("type") int type, @PathVariable("id") long id) throws TException {
//        return _thriftSocketService.setup_snaper(type, id);
//    }
//
//    @RequestMapping(value = "/snaper/type/{type}/id/{id}", method = RequestMethod.DELETE)
//    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the snaper task is successfully delete or not.")
//    public ResponseEntity<Boolean> snaper_teardown(@PathVariable("type") int type, @PathVariable("id") long id) throws TException {
//        int result = _thriftSocketService.teardown_snaper(type, id);
//        return new ResponseEntity<Boolean>((result >= 0 ? Boolean.TRUE : Boolean.FALSE), HttpStatus.OK);
//    }
//
}
