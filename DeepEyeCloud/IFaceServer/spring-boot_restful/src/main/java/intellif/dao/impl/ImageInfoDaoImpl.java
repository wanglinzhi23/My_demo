package intellif.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import intellif.consts.GlobalConsts;
import intellif.utils.CommonUtil;
import intellif.utils.DateUtil;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.TableRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageInfoDaoImpl  extends MultiTableBaseDaoImpl<ImageInfo>{
	
	
	private static Logger LOG = LogManager.getLogger(ImageInfoDaoImpl.class);

	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	@Transactional
	public ImageInfo save(ImageInfo entity) {
		String sql;
		boolean isNew = ( null == entity.getId() || entity.getId() == 0);
	  	    
		if(isNew) {
			TableRecord table = tableRecordDao.getCurTable(DateUtil.getDateString(new Date()), GlobalConsts.T_IMAGE_PRE);
			long id = CommonUtil.createId(table.getTableCode());
			entity.setId(id);
			sql = "INSERT INTO "+GlobalConsts.INTELLIF_FACE+"."+table.getTableName()+"(id,face_uri,faces,time,uri) " +
					"VALUES (:id,:face_uri,:faces,:time,:uri)";
		} else {
			long code = CommonUtil.getCode(entity.getId());
			sql = "UPDATE "+GlobalConsts.INTELLIF_FACE+"."+GlobalConsts.T_NAME_IMAGE_INFO+"_"+code+" SET face_uri = :face_uri,faces = :faces,time = :time,uri = :uri" +
					" WHERE id = :id";
		}
		try {
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("id", entity.getId());
			query.setParameter("face_uri", entity.getFaceUri());
			query.setParameter("faces", entity.getFaces());
			query.setParameter("time", entity.getTime());
			query.setParameter("uri", entity.getUri());
			
			query.executeUpdate();
		} catch (Exception e) {
			LOG.error("出错Sql："+sql);
			LOG.error("ERROR:", e);
		} finally {
			entityManager.close();
		}
		return entity;
	}

	
}
