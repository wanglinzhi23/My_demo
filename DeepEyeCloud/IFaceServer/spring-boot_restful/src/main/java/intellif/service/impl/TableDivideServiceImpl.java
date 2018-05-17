package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.service.TableDivideServiceItf;
import intellif.database.entity.TableRecord;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TableDivideServiceImpl implements TableDivideServiceItf {

	private static Logger LOG = LogManager
			.getLogger(TableDivideServiceImpl.class);

	@PersistenceContext
	EntityManager em;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	private TableRecordDao tableRecordoDao;

	@Override
	public void createTables(Date startTime, Date endTime, long code) {

		String faceTableCre = "CREATE TABLE IF NOT EXISTS intellif_face."
				+ GlobalConsts.T_FACE_PRE +"_"+ code + "("
				+ "id bigint(64) NOT NULL DEFAULT 0,"
				+ "accessories int(11) NOT NULL COMMENT '穿戴',"
				+ "race int(11) NOT NULL COMMENT '种族',"
				+ "age int(11) NOT NULL COMMENT '年龄',"
				+ "face_feature longblob,"
				+ "from_image_id bigint(64) DEFAULT NULL,"
				+ "from_person_id bigint(20) DEFAULT NULL,"
				+ "from_video_id bigint(20) DEFAULT NULL,"
				+ "gender int(11) NOT NULL COMMENT '性别',"
				+ "image_data varchar(255) DEFAULT NULL,"
				+ "indexed int(11) NOT NULL DEFAULT 0,"
				+ "source_id bigint(20) NOT NULL,"
				+ "source_type int(11) NOT NULL,"
				+ "time datetime DEFAULT NULL," + "version int(11) NOT NULL DEFAULT 0,"
				+ "json varchar(255) DEFAULT NULL,"
				+ "sequence bigint(64) NOT NULL AUTO_INCREMENT,"
				+ "quality int(11) NOT NULL DEFAULT 0,"
				+ "PRIMARY KEY (sequence)," + "KEY t_face_id (id),"
				+ "KEY t_face_source_id_time (source_id,time),"
				+ "KEY t_face_time_source_id (time,source_id),"
				+ "KEY t_face_time (time)," 
				+ "KEY t_face_race (race,time)"
				+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";

		String imageTableCre = "CREATE TABLE IF NOT EXISTS intellif_face."
				+ GlobalConsts.T_IMAGE_PRE +"_"+ code + "("
				+ "id bigint(64) NOT NULL DEFAULT 0,"
				+ "face_uri varchar(255) DEFAULT NULL,"
				+ "faces int(11) NOT NULL DEFAULT 0," + "time datetime DEFAULT NULL,"
				+ "uri varchar(255) DEFAULT NULL,"
				+ "sequence bigint(64) NOT NULL AUTO_INCREMENT,"
				+ "PRIMARY KEY (sequence),"
				+ "KEY t_image_info_id (id)"
				+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";

		try {

			jdbcTemplate.execute(faceTableCre);
			jdbcTemplate.execute(imageTableCre);
			TableRecord faceTr = new TableRecord(startTime, endTime, code,
					GlobalConsts.T_FACE_PRE);
			TableRecord imageTr = new TableRecord(startTime, endTime, code,
					GlobalConsts.T_IMAGE_PRE);
			tableRecordoDao.save(faceTr);
			tableRecordoDao.save(imageTr);
			LOG.info("success create face table, code:"+code);

		} catch (Exception e) {
			LOG.error("create face or image table error", e);
		}
	}

	@Override
	public void dropTables(Date date) throws Exception {
		List<TableRecord> tableList = tableRecordoDao.findAllByTime(date);
		TableRecord newTable = tableRecordoDao.findFirstOrderByTime();
		
		if(tableList!= null && !tableList.isEmpty()){
			long code = tableList.get(0).getTableCode();
			long lastCode = newTable.getTableCode();
			String deleteStr = "delete  from " + GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_NAME_TABLES + " where table_code >="+code;
			jdbcTemplate.execute(deleteStr);
			
			for(long i=code;i<=lastCode;i++){
				String dropFaceStr = "DROP TABLE IF EXISTS "+GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_FACE_PRE+"_"+i;
				String dropImageStr = "DROP TABLE IF EXISTS "+GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_IMAGE_PRE+"_"+i;
				jdbcTemplate.execute(dropFaceStr);
				jdbcTemplate.execute(dropImageStr);
			}
		}
		
}
}
