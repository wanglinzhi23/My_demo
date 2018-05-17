package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dto.TaskInfoDto;
import intellif.enums.IFaceSdkTypes;
import intellif.enums.TaskStatus;
import intellif.ifaas.T_IF_TASK_INFO;
import intellif.service.BlackBankServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.TaskServiceItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.SqlUtil;
import intellif.database.dao.TaskInfoDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.TaskInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@Service("taskService")
@Service
public class TaskServiceImpl extends AbstractCommonServiceImpl<TaskInfo> implements TaskServiceItf<TaskInfo>  {

    private static Logger LOG = LogManager.getLogger(TaskServiceImpl.class);
    @Autowired
    public TaskInfoDao<TaskInfo> _taskInfoDao;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PersonDetailServiceItf personDetailServiceItf;
    ///merged from old ThriftSocketService
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskInfo> findBySourceTypeAndUri(String sourceTable, String uri) {
        ArrayList<TaskInfo> resp = new ArrayList<TaskInfo>();
        //
        // LOG.info("entityManager:" + entityManager.toString());
        String sqlString = "SELECT a.* FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " a LEFT JOIN " + sourceTable
                + " b ON a.source_id = b.id WHERE b.uri LIKE '" + uri + "'";
        // @see:
        // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, TaskInfo.class);
            resp = (ArrayList<TaskInfo>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        // LOG.info("ArrayList<TaskInfo>:" + resp.toString());
        //
        return resp;
    }

    @Override
    public List<TaskInfoDto> findByCombinedConditions(TaskInfoDto taskInfoDto) {
        List<TaskInfoDto> resp = null;
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e1");
        String sqlString = "SELECT a.rule_id,a.bank_id,a.id, a.task_name, b.server_name, a.source_type, e.source, d.rule_name, a.decode_type, a.created, a.status, a.cron_status, a.cron_tabs"
                + " FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " a, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_SERVER_INFO + " b, "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_RULE_INFO + " d,(SELECT id, 0 as source_type, name as source FROM " + cameraSql
//                + " UNION SELECT id, 2 as source_type,uri as source FROM " + GlobalConsts.T_NAME_IMAGE_INFO
//                + " UNION SELECT id, 1 as source_type,uri as source FROM " + GlobalConsts.T_NAME_VIDEO_INFO 
                + ") e"
                + " WHERE a.server_id = b.id AND a.bank_id = c.id AND a.rule_id = d.id AND a.source_id = e.id AND a.source_type = e.source_type ";

        if (!"".equals(taskInfoDto.getTaskName())) {
            sqlString += "AND a.task_name LIKE '%" + taskInfoDto.getTaskName() + "%' ";
        }
        if (!"".equals(taskInfoDto.getServerName())) {
            sqlString += "AND b.server_name LIKE '%" + taskInfoDto.getServerName() + "%' ";
        }
        if (!"".equals(taskInfoDto.getSource())) {
            sqlString += "AND e.source LIKE '%" + taskInfoDto.getSource() + "%' ";
        }
        if (taskInfoDto.getSourceType() < 3) {
            sqlString += "AND a.source_type = " + taskInfoDto.getSourceType() + " ";
        }
        if (taskInfoDto.getStatus() < 3) {
            sqlString += "AND a.status = " + taskInfoDto.getStatus() + " ";
        } else {
            sqlString += "AND a.status >= 0 ";
        }
        sqlString += "AND a.created BETWEEN str_to_date('" + taskInfoDto.getStartTime()
                + "','%Y-%m-%d %T') AND str_to_date('" + taskInfoDto.getEndTime() + "','%Y-%m-%d %T')";
        LOG.info("findByCombinedConditions sqlString:" + sqlString);
        // @see:
        // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, TaskInfoDto.class);
            resp = (ArrayList<TaskInfoDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public TaskInfo updateStatus(long id, int status) {
        TaskInfo find = this._taskInfoDao.findById(id);
        find.setStatus(status);
        return this._taskInfoDao.save(find);
    }

  

    @Override
    public void restartTimerTask(long id, String time) {
        RestartTimerTask task_restart = new RestartTimerTask();
        task_restart.taskId = id;
        task_restart.taskInfoDao = this._taskInfoDao;
        // Schedule at some time
        if (null == time) {
            task_restart.run();
        } else {
            prepareTimerTask(time, task_restart);
        }
    }

    @Override
    public void stopTimerTask(long id, String time) {
        StopTimerTask task_stop = new StopTimerTask();
        task_stop.taskId = id;
        task_stop.taskInfoDao = this._taskInfoDao;
        // Schedule at some time
        if (null == time) {
            task_stop.run();
        } else {
            prepareTimerTask(time, task_stop);
        }
    }

    @Override
    public TaskInfo findOne(long id) {
        return _taskInfoDao.findById(id);
    }

    @Override
    public TaskInfo save(TaskInfo info) {
        return _taskInfoDao.save(info);
    }

    @Override
    public TaskInfoDao getDao() {
        LOG.info("getDao:" + this._taskInfoDao);
        return this._taskInfoDao;
    }

    @Override
    public void resumeRelevance() {/*
        //Loop already with start status tasks to resume them.
        List<TaskInfo> resumeTaskInfos = this.findByStatus(TaskStatus.START.getValue());
        LOG.info("Should resumed taskInfos:" + resumeTaskInfos.toString());
        //First off,thrift ping..
        //            try {
        //                for (int i = 0; i < taskInfos.size(); i++) {
        //                    long serverId = taskInfos.get(i).getServerId();
        //                    ServerInfo serverInfo = serverServiceItf.getDao().findOne(serverId);
        //                    LOG.debug("IFaaServiceThriftClient ping...");
        //                    //
        ////                IFaaServiceThriftClient.getInstance();
        //                    IFaaServiceThriftClient iFaaServiceThriftClient = new IFaaServiceThriftClient(serverInfo.getIp(), serverInfo.getPort());
        //                    Assert.assertNotNull(iFaaServiceThriftClient);
        //                    LOG.info("IFaaServiceThriftClient is null?:" + iFaaServiceThriftClient.toString());
        //
        //                }
        //            } catch (TException e) {
        ////                e.printStackTrace();
        //                LOG.error(e.toString());
        //            }
        //Then resume task status:
        for (int i = 0; i < resumeTaskInfos.size(); i++) {
            //                LOG.debug("this.taskServiceItf.restartTimerTask:" + resumeTaskInfos.get(i).getStatus());
            long taskId = resumeTaskInfos.get(i).getId();
            LOG.info("this.taskServiceItf.restartTimerTask,with id:" + taskId);
            this.restartTimerTask(taskId, null);
        }

        // 将布控时间之外的人剔除
        try {
            personDetailServiceItf.refreshPerson();
        } catch (TException e) {
            e.printStackTrace();
        }

    */}

//    //    @Override
//    public int setup_snaper(int sourceType, long sourceId) throws TException {
//        return this.setup_snaper(sourceType, sourceId);
//    }
//
//    //    @Override
//    public int teardown_snaper(int sourceType, long sourceId) throws TException {
//        return this.teardown_snaper(sourceType, sourceId);
//    }

    private void executeStopTask(long taskId, TaskInfoDao taskInfoDao) {
       /* LOG.info("StopTimerTask run()!");
        // timer.cancel(); //Not necessary because we call System.exit
        this.teardown(taskId);
        TaskInfo find = taskInfoDao.findById(taskId);
        find.setStatus(TaskStatus.STOP.getValue());
        taskInfoDao.save(find);*/
    }

    private void executeRestartTask(long taskId, TaskInfoDao taskInfoDao) {/*
        LOG.info("RestartTimerTask run()!");
        // timer.cancel(); //Not necessary because we call System.exit
        TaskInfo find = taskInfoDao.findById(taskId);
        find.setId(taskId);
        find.setCreated(find.getCreated());
        LOG.info("RESTART taskInfo:" + find.toString());
        // LOG.info("thriftSocketService:" +
        // _thriftSocketService.toString());
        int result = this.setup(find);
        if (result != 0) {
            LOG.fatal("ThriftSocketService setup failure!");
        } else {
            find.setStatus(TaskStatus.START.getValue());
            taskInfoDao.save(find);
        }

        LOG.info("thriftSocketService(setup result):" + result);
    */}

    private void prepareTimerTask(String time, TimerTask task) {
        // @see:
        // http://stackoverflow.com/questions/8066141/schedule-java-task-for-a-specified-time
        Timer timer = new Timer("IFTaskTimer", true);
        Calendar cr = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        cr.setTimeInMillis(System.currentTimeMillis());
        long day = TimeUnit.DAYS.toMillis(1);
        // Pay attention - Calendar.HOUR_OF_DAY for 24h day model
        // (Calendar.HOUR is 12h model, with p.m. a.m. )
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");// 2015-07-16_19:08:31
        Date dateTime = null;
        try {
            LOG.info("raw(Time):" + time);//
            dateTime = format.parse(time);
            //
            LOG.info("SimpleDataFormat parsed:" + dateTime);//
        } catch (ParseException e) {
            LOG.error("", e);
        }

    //    cr.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
     //   cr.set(Calendar.MINUTE, dateTime.getMinutes());
        if(dateTime!=null){
        	cr.set(Calendar.HOUR_OF_DAY, dateTime.getHours());       
            cr.set(Calendar.MINUTE, dateTime.getMinutes());  	
        }
        long delay = cr.getTimeInMillis() - System.currentTimeMillis();
        // insurance for case then time of task is before time of schedule
        long adjustedDelay = (delay > 0 ? delay : day + delay);
        // timer.scheduleAtFixedRate(new TaskRestartTimerTask(), adjustedDelay,
        // day);
        LOG.info("adjustedDelay:" + adjustedDelay);
        timer.scheduleAtFixedRate(task, adjustedDelay, day);
        // you can use this schedule instead is sure your time is after
        // current time
        // timer.scheduleAtFixedRate(new StartReportTimerTask(it),
        // cr.getTime(), day);
    }

    // TimerTasks

    private int setupThriftSocketing(TaskInfo tInfo) throws TException {
//		target.processFaceDetectExtract("/media/psf/Share/storage/result.jpg");
//		return "";
//			 target.processFaceFeatureVerify(40.90668869018555, 31.006715774536133);
        LOG.info("setupThriftSocketing() called!");
        T_IF_TASK_INFO info = new T_IF_TASK_INFO();
//        info.BankId = tInfo.getBankId();//1;
        info.SourceId = tInfo.getSourceId();//1;
        info.SourceType = tInfo.getSourceType();//0;//0-camera,1-video,2-image.
        info.Id = tInfo.getId();//3;
//        info.RuleId = tInfo.getRuleId();//1;
//		 info.Status = tInfo.getStatus();//5;
//		 info.TaskName = tInfo.getTaskName();//GlobalConsts.MQTT_TOPIC_ALARM;
        int result = -1 ;
    	IFaceSdkTarget target = null;
		try {
			target = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT, tInfo.getId());
			result = target.processTaskSurveillanceCreate(info);
		} catch (Exception e) {
		 LOG.error(e.getMessage());
		}
        //Terminate task testing.
//		 String result = String.valueOf(target.processTaskSurveillanceTerminate(info));
//		testMqttMessaging(result);
        return result;
    }

    /* (non-Javadoc)
     * @see intellif.service.ThriftSocketServiceItf#setup(intellif.database.entity.TaskInfo)
     */
    @Override
    public int setup(TaskInfo tInfo) {
        int result = -1;
        try {
            result = setupThriftSocketing(tInfo);
        } catch (TException e) {
            LOG.error(e.toString());
        }
        //
//		String assembledTopicName = MqttSettings.getTopicName(tInfo);
//		LOG.info("assembledTopicName:"+assembledTopicName);
//        mqttMessageService.setup(assembledTopicName);
        //
        return result;
    }
//    @Autowired
//    private MqttMessageServiceItf mqttMessageService;

    @Override
    public int teardown(long taskId) {
        int result = -1;
    	IFaceSdkTarget target = null;
        try {
        	target = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT, taskId);
            result = target.processTaskSurveillanceTerminate(taskId);
        } catch (Exception e) {
//			e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return result;
    }

    @Override
    public int setup_snaper(int sourceType, long sourceId) throws TException {
//        return iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT).task_snaper_create(sourceType, sourceId);
    	return 0;
    }

    @Override
    public int teardown_snaper(int sourceType, long sourceId) throws TException {
//        return iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT).task_snaper_terminate(sourceType, sourceId);
    	return 0;
    }

    ///
    class StopTimerTask extends TimerTask {
        public long taskId;// TaskId
        public TaskInfoDao taskInfoDao;// TaskInfoDao

        public void run() {
            executeStopTask(taskId, taskInfoDao);
        }
    }

    class RestartTimerTask extends TimerTask {
        public long taskId;
        public TaskInfoDao taskInfoDao;// TaskInfoDao

        public void run() {
            executeRestartTask(taskId, taskInfoDao);
        }
    }

}
