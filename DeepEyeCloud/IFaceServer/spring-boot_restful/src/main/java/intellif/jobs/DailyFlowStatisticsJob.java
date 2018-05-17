package intellif.jobs;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.database.entity.TableRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: DailyFlowStatisticsJob.java
 * @Package intellif.jobs
 * @Description
 * @date 2018 05-07 13:33.
 */
@Component
public class DailyFlowStatisticsJob {
	private static Logger logger = LogManager.getLogger(DailyFlowStatisticsJob.class);
	@Autowired
	private TableRecordDao tableRecordDao;

	@PersistenceContext
	EntityManager entityManager ;

	@Scheduled(cron = "0 0 0 * * ?")//每日0点
	@Transactional
	public void dailyFlowStatistics() {
		logger.log(Level.INFO, ("每日统计定时任务开始---:"));
		Date lastday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		DateFormat dffull = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		DateFormat dff = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String startTime = dff.format(lastday);
		String endTime = dffull.format(lastday);
		String tableName = "t_face";
		TableRecord tableRecord = tableRecordDao.getCurTable(startTime, tableName);
		StringBuilder sqlString1 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE + "."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString1.append("select date_format(time,'%Y-%m-%d') as time, count(1) as count,0 as age,0 as gender,10 as person_type,10 as time_type,source_id ");
		sqlString1.append("from " + GlobalConsts.INTELLIF_FACE + "." + tableRecord.getTableName());
		sqlString1.append(" where 1=1 and time  BETWEEN :startTime and :endTime ");
		sqlString1.append("group by date_format(time, '%Y-%m-%d'),source_id order by date_format(time,'%Y-%m-%d')");

		StringBuilder sqlString2 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE + "."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString2.append("select date_format(time,'%Y-%m-%d') as time, count(1) as count,0 as age,gender as gender,20 as person_type,10 as time_type,source_id ");
		sqlString2.append("from " + GlobalConsts.INTELLIF_FACE + "." + tableRecord.getTableName());
		sqlString2.append(" where 1=1 and time  BETWEEN :startTime and :endTime ");
		sqlString2.append("group by source_id,date_format(time, '%Y-%m-%d'),gender order by date_format(time,'%Y-%m-%d')");

		StringBuilder sqlString3 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE + "."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString3.append("select date_format(time,'%Y-%m-%d') as time, count(1) as count,age as age,0 as gender,30 as person_type,10 as time_type,source_id ");
		sqlString3.append("from " + GlobalConsts.INTELLIF_FACE + "." + tableRecord.getTableName());
		sqlString3.append(" where 1=1 and time  BETWEEN :startTime and :endTime ");
		sqlString3.append("group by source_id,date_format(time, '%Y-%m-%d'),age order by date_format(time,'%Y-%m-%d')");
		//每日的总榜统计
		Query query1 = entityManager.createNativeQuery(sqlString1.toString());
		query1.setParameter("startTime", startTime);
		query1.setParameter("endTime", endTime);
		query1.executeUpdate();
		//每日的性别统计
		Query query2 = entityManager.createNativeQuery(sqlString2.toString());
		query2.setParameter("startTime", startTime);
		query2.setParameter("endTime", endTime);
		query2.executeUpdate();
		//每日的按年龄统计
		Query query3 = entityManager.createNativeQuery(sqlString3.toString());
		query3.setParameter("startTime", startTime);
		query3.setParameter("endTime", endTime);
		query3.executeUpdate();
		logger.log(Level.INFO, ("每日统计定时任务结束---:"));
	}
}
