package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.service.ImageServiceItf;
import intellif.database.entity.TableRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DivideTableCountThread extends Thread {

	private static Logger LOG = LogManager
			.getLogger(DivideTableCountThread.class);

	@Autowired
	private TableRecordDao tableRecordDao;

	@Autowired
	private FaceInfoDaoImpl faceInfoDaoImpl;

    @Autowired
    private ImageServiceItf _imageServiceItf;

	@Scheduled(cron = "0 1 0 * * ?")
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		try {
			Calendar can = Calendar.getInstance();
			long time = can.getTimeInMillis() - 1000 * 60 * 60;
			can.setTimeInMillis(time);
			List<TableRecord> nowTableList = tableRecordDao.findAllByTime(can
					.getTime());
			if (null != nowTableList && !nowTableList.isEmpty()) {
				// 每天0点1分定期统计前一天face和image表数据总数
				updateTableRecordCountList(nowTableList);

				long code = nowTableList.get(0).getTableCode();
				List<TableRecord> notCountList = tableRecordDao
						.findTablesNotCount(code);
				if (null != notCountList && !notCountList.isEmpty()) {
					// 每天定期检查已经不更新的face和image表的统计个数信息，如果为0，则更新总数
					updateTableRecordCountList(notCountList);
				}
			}
			
			//大图每天统计总数，统计t_tables各t_image对应数目
			GlobalConsts.imageBaseCount = this._imageServiceItf.count();
	    	GlobalConsts.imageDayCount = 0;
	    	GlobalConsts.imageMinCount = 0;
	    	LOG.info("count image num every day 0 clock, count:"+GlobalConsts.imageBaseCount);
			

		} catch (Exception e) {
			LOG.error("count face and image table error", e);
		}

	}

	private void updateTableRecordCountList(List<TableRecord> recordList) {
		for (TableRecord item : recordList) {
			String tableName = item.getTableName();
			String countSql = "select count(1) from "
					+ GlobalConsts.INTELLIF_FACE + "." + tableName;
			long num = faceInfoDaoImpl.countResult(countSql);
			item.setTotalNum(num);
			tableRecordDao.save(item);
		}
	}
}
