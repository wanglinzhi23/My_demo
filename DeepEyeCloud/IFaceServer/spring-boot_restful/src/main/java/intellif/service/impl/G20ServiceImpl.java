package intellif.service.impl;

import intellif.dao.PoliceStationAuthorityDao;
import intellif.service.G20Service;
import intellif.database.entity.EventInfo;
import intellif.database.entity.G20Statistic;
import intellif.database.entity.PoliceStationAuthority;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class G20ServiceImpl  implements G20Service{

    private static Logger LOG = LogManager.getLogger(G20ServiceImpl.class);
    
    @PersistenceContext
    EntityManager entityManager;


	@Autowired
	PoliceStationAuthorityDao policeStationAuthorityRepository;
	
    @SuppressWarnings("unchecked")
    public List<G20Statistic> findG20Statistic(String startTime, String endTime) {
        
    	String inoutStatistic = "select cam.type, count(face.id) as statistic from t_camera_info cam "
    			+ "INNER JOIN t_face_info_c face on  face.source_id = cam.id  where face.time between :startTime and :endTime GROUP BY cam.type";
    	
        try {
            Query query = this.entityManager.createNativeQuery(inoutStatistic, G20Statistic.class);
            query.setParameter("startTime", startTime);
            query.setParameter("endTime", endTime);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        // LOG.info("ArrayList<TaskInfo>:" + resp.toString());
        //
        return null;
    }

    @SuppressWarnings("unchecked")
	@Override
	public List<EventInfo> findAllPersonEvents(long stationId, int pageSize, String personIds, double threshold, int important) {
		long startTime = System.currentTimeMillis();
		String sql = "CALL g20_person_events_pro(:stationId, :count, :personIds, :bankIds, :threshold, :important)";
		List<EventInfo> resp = null;

		List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
		String bankIds = "";
		for (PoliceStationAuthority authority : authorityList) {
			bankIds += "," + authority.getBankId();
		}
		try {
			Query query = this.entityManager.createNativeQuery(sql, EventInfo.class);
			query.setParameter("stationId", stationId).setParameter("count", pageSize).setParameter("personIds", personIds)
				.setParameter("bankIds", bankIds).setParameter("threshold", threshold)
				.setParameter("important", important);
			resp = query.getResultList();
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			entityManager.close();
		}

		LOG.info("findEventsByPersonId:" + (System.currentTimeMillis() - startTime));
		return resp;
	}
}
