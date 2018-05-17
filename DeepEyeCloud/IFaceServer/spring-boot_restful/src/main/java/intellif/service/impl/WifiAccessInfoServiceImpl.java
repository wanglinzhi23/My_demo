package intellif.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.service.WifiAccessInfoServiceItf;

@Service
public class WifiAccessInfoServiceImpl implements WifiAccessInfoServiceItf {

	private static Logger LOG = LogManager.getLogger(WifiAccessInfoServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public Long countTotalAccessDevice(String starttime, String endtime) {
		Long total = 0L;
		// String sql = "SELECT COUNT(*) AS count FROM (SELECT mac FROM " +
		// GlobalConsts.INTELLIF_BASE + "."
		// + GlobalConsts.T_NAME_WIFI_ACCESS_INFO + " WHERE miltime >= " +
		// starttime + " and miltime <= " + endtime
		// + " GROUP BY mac) f";
		String sql = "SELECT COUNT(DISTINCT(mac))FROM " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_WIFI_ACCESS_INFO + " WHERE miltime >= " + starttime + " and miltime <= "
				+ endtime;
		try {
			Query query = this.em.createNativeQuery(sql);
			Object result = query.getResultList();
			List<Object> countTempRes = (ArrayList<Object>) result;
			total = ((BigInteger) countTempRes.get(0)).longValue();
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			em.close();
		}
		return total;
	}

}
