package intellif.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.service.FaceCameraCountItf;
import intellif.service.FaceQualityCameraCountItf;
import intellif.database.entity.FaceStatisticCount;

@Service
public class FaceQualityCameraCountImpl implements FaceQualityCameraCountItf {

	private static Logger LOG = LogManager.getLogger(FaceQualityCameraCountImpl.class);
	
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public Map<Long, List<FaceStatisticCount>> findBySourceIdByPeriod(Long[] ids, String startdate, String enddate,int quality) {
		List<FaceStatisticCount> respList = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, source_id, DATE_FORMAT(time, '%Y-%m-%d') as time, ");
		String q = null;
        if(0 == quality){
      	  q = "high_total as count";
        }else if( 0 > quality){
      	  q = "low_total as count";
        }else{
      	  q = "high_total + low_total as count ";
        }
      sb.append(q);
		sb.append(" FROM "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_QUALITY_CAMERA_COUNT
				+ " WHERE source_id in (:ids) and time >= :startdate and time <= :enddate order by time, source_id asc");
		try {
			Query query = entityManager.createNativeQuery(sb.toString(), FaceStatisticCount.class);
			query.setParameter("ids", Arrays.asList(ids));
			query.setParameter("startdate", startdate);
			query.setParameter("enddate", enddate);
			respList = query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		if (respList == null) respList = new ArrayList<>();
		return respList.stream().collect(Collectors.groupingBy(FaceStatisticCount::getSourceId));
	}

	@Override
	public Map<Long, List<FaceStatisticCount>> findByPeriod(String startdate, String enddate,int quality) {
		List<FaceStatisticCount> respList = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, source_id, DATE_FORMAT(time, '%Y-%m-%d') as time, ");
             String q = null;
              if(0 == quality){
            	  q = "high_total as count";
              }else if( 0 > quality){
            	  q = "low_total as count";
              }else{
            	  q = "high_total + low_total as count ";
              }
             sb.append(q);
             sb.append(" FROM "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_QUALITY_CAMERA_COUNT
				+ " WHERE time >= :startdate and time <= :enddate order by time, source_id asc");
		try {
			Query query = entityManager.createNativeQuery(sb.toString(), FaceStatisticCount.class);
			query.setParameter("startdate", startdate);
			query.setParameter("enddate", enddate);
			respList = query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}
		if (respList == null) respList = new ArrayList<>();
		return respList.stream().collect(Collectors.groupingBy(FaceStatisticCount::getSourceId));
	}

}
