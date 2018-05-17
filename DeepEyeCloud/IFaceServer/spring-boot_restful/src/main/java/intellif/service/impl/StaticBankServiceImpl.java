package intellif.service.impl;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.dao.CidInfoDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherInfoDao;
import intellif.dto.CidInfoDto;
import intellif.dto.PoliceManDto;
import intellif.dto.StaticFaceSearchDto;
import intellif.service.StaticBankServiceItf;
import intellif.database.entity.CidInfo;
import intellif.database.entity.JuZhuInfo;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.StaticBankInfo;

@Service
public class StaticBankServiceImpl implements StaticBankServiceItf {

	@PersistenceContext
	EntityManager entityManager;
	/*
	 * @Autowired JdbcTemplate jdbcTemplate;
	 */
	@Autowired
	private CidInfoDao cidInfoRepository;
	@Autowired
	private JuZhuInfoDao juzhuInfoRepository;
	@Autowired
	private OtherInfoDao otherInfoRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List findByCondition(StaticFaceSearchDto staticFaceSearchDto,
			int page, int pageSize) {

		String sql = "";
		List resultList = null;
		switch (staticFaceSearchDto.getType()) {
		case 3: {
			sql = "select a.*,b.id as detail_id from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_CID_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_CID_DETAIL+" b where a.id=b.from_cid_id ";
			break;
		}
		case 4: {
			sql = "select a.*,b.id as detail_id from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_JUZHU_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_JUZHU_DETAIL+" b where a.id=b.from_cid_id ";
			break;
		}
		case 5: {
			sql = "select a.*,b.id as detail_id from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_DETAIL+" b where a.id=b.from_cid_id and type=5 ";
			break;
		}
		case 6: {
			sql = "select a.*,b.id as detail_id from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_DETAIL+" b where a.id=b.from_cid_id and type=6 ";
			break;
		}
		}

		if (staticFaceSearchDto.getSex().equals("0")) {
			sql += " and (a.XB = '1' or a.XB = '2') ";
		} else {
			sql += " and a.XB = :sex ";

		}
		if (staticFaceSearchDto.getName() != null
				&& staticFaceSearchDto.getName().trim() != "") {
			sql += " and a.xm = :name";

		}
		if (staticFaceSearchDto.getPhone() != null
				&& staticFaceSearchDto.getPhone().trim() != "") {
			sql += " and a.SJHM = :phone";

		}
		if (staticFaceSearchDto.getIdCard() != null
				&& staticFaceSearchDto.getIdCard().trim() != "") {
			sql += " and a.GMSFHM = :idCard";

		}
		sql += " and a.PHOTO=b.image_data limit :start,:pageSize ";
		
		Query query = null;
		
		query = this.entityManager.createNativeQuery(sql, StaticBankInfo.class);

	/*	switch (staticFaceSearchDto.getType()) {
		case 3: {
			query = this.entityManager.createNativeQuery(sql, CidInfo.class);
			break;
		}
		case 4: {
			query = this.entityManager.createNativeQuery(sql, JuZhuInfo.class);
			break;
		}
		case 5: {
			query = this.entityManager.createNativeQuery(sql, OtherInfo.class);
			break;
		}
		case 6: {
			query = this.entityManager.createNativeQuery(sql, OtherInfo.class);
			break;
		}
		}*/

		try {

			if (!staticFaceSearchDto.getSex().equals("0")) {
				query.setParameter("sex", staticFaceSearchDto.getSex());

			}
			if (staticFaceSearchDto.getName() != null
					&& staticFaceSearchDto.getName().trim() != "") {

				query.setParameter("name", staticFaceSearchDto.getName());

			}
			if (staticFaceSearchDto.getPhone() != null
					&& staticFaceSearchDto.getPhone().trim() != "") {

				query.setParameter("phone", staticFaceSearchDto.getPhone());

			}
			if (staticFaceSearchDto.getIdCard() != null
					&& staticFaceSearchDto.getIdCard().trim() != "") {

				query.setParameter("idCard", staticFaceSearchDto.getIdCard());

			}
			query.setParameter("start", (page - 1) * pageSize);
			query.setParameter("pageSize", pageSize);

			resultList = query.getResultList();	

		} catch (Exception e) {
			System.err.println("出错Sql：" + sql);
			System.err.println("ERROR:" + e.getMessage());
		} finally {
			entityManager.close();
		}

		return resultList;
	}
	@Override
	public BigInteger CountByCondition(StaticFaceSearchDto staticFaceSearchDto) {
		String sql = "";
		BigInteger count = null;
		switch (staticFaceSearchDto.getType()) {
		case 3: {
			sql = "select count(1) from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_CID_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_CID_DETAIL+" b where a.id=b.from_cid_id ";
			break;
		}
		case 4: {
			sql = "select count(1) from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_JUZHU_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_JUZHU_DETAIL+" b where a.id=b.from_cid_id ";
			break;
		}
		case 5: {
			sql = "select count(1) from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_DETAIL+" b where a.id=b.from_cid_id and type=5 ";
			break;
		}
		case 6: {
			sql = "select count(1) from " + GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_INFO + " a,"+GlobalConsts.INTELLIF_STATIC + "."
					+ GlobalConsts.T_NAME_OTHER_DETAIL+" b where a.id=b.from_cid_id and type=6 ";
			break;
		}
		}

		if (!staticFaceSearchDto.getSex().equals("0")) {
			sql += " and XB = (:sex) ";
		} else {
			sql += " and (XB = '1' or XB = '2')  ";
		}
		if (staticFaceSearchDto.getName() != null
				&& staticFaceSearchDto.getName().trim() != "") {
			sql += "and xm = (:name)";
		}
		if (staticFaceSearchDto.getPhone() != null
				&& staticFaceSearchDto.getPhone().trim() != "") {
			sql += "and SJHM = (:phone)";
		}
		if (staticFaceSearchDto.getIdCard() != null
				&& staticFaceSearchDto.getIdCard().trim() != "") {
			sql += "and GMSFHM = (:idCard)";
		}

		/*
		 * switch (staticFaceSearchDto.getType()) { case 3: { count =
		 * jdbcTemplate.queryForObject(sql, new Object[] {}, Integer.class); }
		 * case 4: { count = jdbcTemplate.queryForObject(sql, new Object[] {},
		 * Integer.class); } case 5: { count = jdbcTemplate.queryForObject(sql,
		 * new Object[] {}, Integer.class); } case 6: { count =
		 * jdbcTemplate.queryForObject(sql, new Object[] {}, Integer.class); } }
		 */

		sql += " and a.PHOTO=b.image_data ";
		
		try {
			Query query = this.entityManager.createNativeQuery(sql);

			if (!staticFaceSearchDto.getSex().equals("0")) {
				query.setParameter("sex", staticFaceSearchDto.getSex());
			}
			if (staticFaceSearchDto.getName() != null
					&& staticFaceSearchDto.getName().trim() != "") {
				query.setParameter("name", staticFaceSearchDto.getName());
			}
			if (staticFaceSearchDto.getPhone() != null
					&& staticFaceSearchDto.getPhone().trim() != "") {
				query.setParameter("phone", staticFaceSearchDto.getPhone());
			}
			if (staticFaceSearchDto.getIdCard() != null
					&& staticFaceSearchDto.getIdCard().trim() != "") {
				query.setParameter("idCard", staticFaceSearchDto.getIdCard());
			}

			count = (BigInteger) query.getSingleResult();

		} catch (Exception e) {
			System.err.println("出错Sql：" + sql);
			System.err.println("ERROR:" + e);
		} finally {
			entityManager.close();
		}

		return count;
	}


	@Override
	public void updateIndexOfIds(String tableName, List<Long> idsList) {
		StringBuffer sb = new StringBuffer();
		for(long id: idsList){
			sb.append(",");
			sb.append(String.valueOf(id));
		}
		String sql = sb.toString().substring(1);
		String exeSql = "update "+GlobalConsts.INTELLIF_STATIC+"."+tableName+" set indexed = 1 where id in("+sql+")";
		jdbcTemplate.execute(exeSql);
		
	}

	

}
