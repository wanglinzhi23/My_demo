package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.database.dao.BlackBankDao;
import intellif.database.dao.CommonDao;
import intellif.dto.BankDto;
import intellif.dto.BankInfoDto;
import intellif.dto.BlackDetailDto;
import intellif.service.BlackBankServiceItf;
import intellif.service.UserServiceItf;
import intellif.database.entity.BlackBank;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class BlackBankServiceImpl extends AbstractCommonServiceImpl<BlackBank> implements BlackBankServiceItf<BlackBank> {

	private static Logger LOG = LogManager.getLogger(BlackBankServiceImpl.class);
	
	@Autowired
	BlackBankDao blackBankDao;

	@Autowired
	BlackDetailDao blackDetailDao;
	
    @Autowired
    UserServiceItf _userService;

	@PersistenceContext
	EntityManager em;


	@SuppressWarnings("unchecked")
	@Override
	public List<BlackDetailDto> findByCombinedConditions(BlackDetailDto blackDetailDto) {
		List<BlackDetailDto> resp = null;
		//
		String sqlString = "SELECT a.id, c.bank_name,c.list_type, a.black_description, a.image_data, a.created, b.real_name, b.real_gender, b.birthday, b.nation, b.cid, b.address,b.type, a.created, a.updated, a.FROM_person_id, a.FROM_image_id"
				+ " FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " a, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " b, "
				+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " c" + " WHERE a.FROM_person_id = b.id AND a.bank_id = c.id ";
		
		sqlString= _userService.processAuthority(sqlString);

		if (!"".equals(blackDetailDto.getRealName())) {
			sqlString += "AND b.real_name LIKE '%" + blackDetailDto.getRealName() + "%' ";
		}
		if (!"".equals(blackDetailDto.getNation())) {
			sqlString += "AND b.nation LIKE '%" + blackDetailDto.getNation() + "%' ";
		}
		if (!"".equals(blackDetailDto.getCid())) {
			sqlString += "AND b.cid LIKE '%" + blackDetailDto.getCid() + "%' ";
		}
		if (!"".equals(blackDetailDto.getAddress())) {
			sqlString += "AND b.address LIKE '%" + blackDetailDto.getAddress() + "%' ";
		}
		if (!"".equals(blackDetailDto.getBlackDescription())) {
			sqlString += "AND a.black_description LIKE '%" + blackDetailDto.getBlackDescription() + "%' ";
		}
		if (blackDetailDto.getRealGender() >= 1 && blackDetailDto.getRealGender() <= 2) {
			sqlString += "AND b.real_gender = " + blackDetailDto.getRealGender() + " ";
		}
		if (!"全部".equals(blackDetailDto.getBankName())) {
			sqlString += "AND c.bank_name = '" + blackDetailDto.getBankName() + "' ";
		}
		sqlString += "AND a.created between str_to_date('" + blackDetailDto.getStartTime()
				+ "','%Y-%m-%d %T') AND str_to_date('" + blackDetailDto.getEndTime() + "','%Y-%m-%d %T') ";

		// @see:
		// http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
		try {
			Query query = this.em.createNativeQuery(sqlString, BlackDetailDto.class);
			resp = (ArrayList<BlackDetailDto>) query.getResultList();
        } catch (Exception e) {
        	LOG.error("", e);
		} finally {
			em.close();
		}
		return resp;
	}
	
	/**
	 * 库查询接口
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BankInfoDto> findBankByCombinedConditions(Long stationId,BankDto bankDto) {
		List<BankInfoDto> resp = null;
		List<Long> pList = bankDto.getpList();
		String idFilterSql = ""; 
		if(!CollectionUtils.isEmpty(pList)){
		    String pIdStr = StringUtils.join(pList,",");
		    idFilterSql = " and pa.station_id in("+pIdStr+") ";
		}
		//SELECT b.*,count(p.id) as totalCount FROM  t_black_bank b LEFT JOIN t_person_detail p on b.id = p.bank_id WHERE b.id in (23,47,29)
		  // and b.bank_name like '%%' and b.list_type=0 and b.created >= '2016-01-01' and b.created <'2017-02-21'  group by b.id order by b.created desc
		
				   String sqlString = "select b.*,ps.station_name,count(p.id) as total_count FROM "
				 + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " b left join  " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " p on b.id = p.bank_id "
				 +" left join "+GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION+" ps on ps.id = b.station_id LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY
				 +" pa on b.id = pa.bank_id"
				 + " WHERE pa.station_id = "+stationId+" and (b.bank_name like '%"+bankDto.getBankName()+"%' or ps.station_name like '%"+bankDto.getBankName()+"%') and b.list_type="+bankDto.getBalckOrWhite()
				 +" and b.created >= '"+bankDto.getStartTime()+"' and b.created <'"+bankDto.getEndTime()
				 +"' and pa.type = "+bankDto.getAuthorityType()+idFilterSql+" group by b.id order by b.created desc" ;
		
		try {
			Query query = this.em.createNativeQuery(sqlString, BankInfoDto.class);
			resp = (ArrayList<BankInfoDto>) query.getResultList();
        } catch (Exception e) {
        	LOG.error("", e);
		} finally {
			em.close();
		}
		return resp;
	}

    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return blackBankDao;
    }
	
}
