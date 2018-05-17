package intellif.service.impl;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.PoliceManAuthorityDao;
import intellif.service.PoliceManAuthorityServiceItf;
import intellif.database.entity.PoliceManAuthority;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PoliceManAuthorityServiceImpl implements
		PoliceManAuthorityServiceItf {

	private static Logger LOG = LogManager
			.getLogger(PoliceManAuthorityServiceImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	private PoliceManAuthorityDao _policemanAuthorityDao;

	@Override
	public boolean batchDelete(int switchType, String policeNoLine) {

		String sqlString = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "."
				+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
				+ " WHERE type = " + switchType + " and police_no in "
				+ policeNoLine;
		try {
			jdbcTemplate.execute(sqlString);
		} catch (Exception e) {
			LOG.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public void addIfNotExsit(String policeno, int authType) {
		List<PoliceManAuthority> exist = _policemanAuthorityDao
				.findByPoliceNoAndAuthType(policeno, authType);
		if (exist == null || exist.size() == 0) {
			PoliceManAuthority policemanAuthority = new PoliceManAuthority();
			policemanAuthority.setPoliceNo(policeno);
			policemanAuthority.setType(authType);
			_policemanAuthorityDao.save(policemanAuthority);
		}
	}

	@Override
	public void deleteIfExsit(String policeno, int authType) {
		List<PoliceManAuthority> exist = _policemanAuthorityDao
				.findByPoliceNoAndAuthType(policeno, authType);
		if (exist!=null&&exist.size()!=0) {
			_policemanAuthorityDao.deleteByPoliceNoAndType(policeno,authType);
		}
	}

}
