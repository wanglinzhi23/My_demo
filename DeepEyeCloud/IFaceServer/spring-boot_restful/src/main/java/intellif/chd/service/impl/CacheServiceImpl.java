 package intellif.chd.service.impl;

 import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import intellif.chd.consts.Constant;
import intellif.chd.dao.FilterFaceDao;
import intellif.chd.service.CacheService;
import intellif.chd.util.FaceUtil;
import intellif.chd.vo.FilterFace;
import intellif.consts.GlobalConsts;

@Service
public class CacheServiceImpl implements CacheService {
	
	private static final Logger LOG = LogManager.getLogger(CacheServiceImpl.class);
	
	public static final String FILTER_FACE_SQL = "select * from " + GlobalConsts.INTELLIF_BASE + "."
	+ Constant.T_NAME_FILTER_FACE + " order by id asc";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private FilterFaceDao filterFaceDao;

	@Override
	@Cacheable(cacheNames = "filterFace")
	public List<FilterFace> filterFaceList() {
		long startTime = System.currentTimeMillis();
		List<FilterFace> filterFaceList = jdbcTemplate.query(FILTER_FACE_SQL, new BeanPropertyRowMapper<>(FilterFace.class));
		List<FilterFace> retFaceList = new ArrayList<>();
		for (FilterFace filterFace : filterFaceList) {
			if (null != filterFace.takeFeatureFloat()) {
				retFaceList.add(filterFace);
				continue;
			}
			LOG.warn("filterFace id is {}, featurn is not valid");
		}
		
		LOG.info("cccccc not use cache, need {}ms, retFaceList size is {}",
				System.currentTimeMillis() - startTime, retFaceList.size());
		return retFaceList;
	}
	

	@CacheEvict(cacheNames = "taskOutput", key = "#taskId")
	public void deleteOutput(long taskId) {
		FaceUtil.deleteOutputFile(taskId);
	}
	
	@Override
	@CacheEvict(cacheNames = "filterFace")
	public void saveFilterFace(FilterFace filterFace) {
		filterFaceDao.save(filterFace);
	}
	
	@Override
	@CacheEvict(cacheNames = "filterFace")
	public void saveFilterFace(List<FilterFace> filterFaceList) {
		filterFaceDao.save(filterFaceList);
	}
}
