package intellif.jobs;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
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
import java.util.Calendar;
import java.util.Date;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: AnnualFlowStatisticsJob.java
 * @Package intellif.jobs
 * @Description
 * @date 2018 05-07 17:24.
 */
@Component
public class AnnualFlowStatisticsJob {
	private static Logger logger = LogManager.getLogger(AnnualFlowStatisticsJob.class);
	@Autowired
	private TableRecordDao tableRecordDao;
	@PersistenceContext
	EntityManager entityManager;
	@Transactional
	@Scheduled(cron = "0 0 0 1 1 ?")//每日0点
	public void annualFlowStatistics() {
		logger.log(Level.INFO, ("每年统计定时任务开始---:"));
		Date now = new Date();
		DateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -1);
		String startTime = dff.format(calendar.getTime());
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -1);
		String endTime = dff.format(calendar.getTime());
		StringBuilder sqlString1 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE+"."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString1.append("select CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time,sum(count) as count,0 as age,0 as gender,person_type,30 as time_type,source_id");
		sqlString1.append(" from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_PERSON_STATISTIC);
		sqlString1.append(" where person_type=10 and time  BETWEEN :startTime and :endTime ");
		sqlString1.append(" group by source_id,DATE_FORMAT(time,   '%Y') order by DATE_FORMAT(time,   '%Y')");

		StringBuilder sqlString2 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE + "."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString2.append("select CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time,sum(count) as count,0 as age,gender ,person_type,30 as time_type,source_id");
		sqlString2.append(" from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_PERSON_STATISTIC);
		sqlString2.append(" where person_type=20 and time  BETWEEN :startTime and :endTime ");
		sqlString2.append(" group by source_id,DATE_FORMAT(time,   '%Y'),gender order by DATE_FORMAT(time,   '%Y')");

		StringBuilder sqlString3 = new StringBuilder("insert into " + GlobalConsts.INTELLIF_FACE + "."+"t_person_statistic (time,count,age,gender,person_type,time_type,source_id) ");
		sqlString3.append("select CONCAT(DATE_FORMAT(time,   '%Y'),'-01-01') as time,sum(count) as count,age,0 as gender,person_type,30 as time_type,source_id");
		sqlString3.append(" from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_PERSON_STATISTIC);
		sqlString3.append(" where person_type=30 and time BETWEEN :startTime and :endTime ");
		sqlString3.append(" group by source_id,DATE_FORMAT(time,   '%Y'),age order by DATE_FORMAT(time,   '%Y')");
		//每年的总榜统计
		Query query1 = entityManager.createNativeQuery(sqlString1.toString());
		query1.setParameter("startTime", startTime);
		query1.setParameter("endTime", endTime);
		query1.executeUpdate();
		//每年的性别统计
		Query query2 = entityManager.createNativeQuery(sqlString2.toString());
		query2.setParameter("startTime", startTime);
		query2.setParameter("endTime", endTime);
		query2.executeUpdate();
		//每年的按年龄统计
		Query query3 = entityManager.createNativeQuery(sqlString3.toString());
		query3.setParameter("startTime", startTime);
		query3.setParameter("endTime", endTime);
		query3.executeUpdate();
		logger.log(Level.INFO, ("每年统计定时任务结束---:"));
	}
}
