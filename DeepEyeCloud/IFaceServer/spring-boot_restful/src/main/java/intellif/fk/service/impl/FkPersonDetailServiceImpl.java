package intellif.fk.service.impl;

import intellif.consts.GlobalConsts;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.dto.FindFkPersonDto;
import intellif.fk.dto.FkLocalInstitutionDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.service.FkInstitutionCodeService;
import intellif.fk.service.FkPersonDetailService;
import intellif.service.UserServiceItf;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FkPersonDetailServiceImpl implements FkPersonDetailService {

    private static Logger LOG = LogManager.getLogger(FkPersonDetailServiceImpl.class);

    @Autowired
    private UserServiceItf _userService;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeServiceItf;
    @Autowired
    private FkInstitutionCodeService fkInstitutionCodeService;
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
    
    @Override
    public List<FkPersonResultDto> findFkPerson(FindFkPersonDto findFkPersonDto) {

      /*  long districtId = findFkPersonDto.getDistrictId();
        long areaId = findFkPersonDto.getAreaId();*/
        String subInstitutionCode = findFkPersonDto.getFkSubInstitutionCode();
        String localInstitutionCode = findFkPersonDto.getFkLocalInstitutionCode();
        
        String districtIds = findFkPersonDto.getDistrictIds();
        String areaIds = findFkPersonDto.getAreaIds();
                
        String fkType = findFkPersonDto.getFkType();
        String name = findFkPersonDto.getRealName();
        String cid = findFkPersonDto.getCid();

        List<FkPersonResultDto> resp = null;
        String sqlString = "SELECT distinct c.id ,c.photo_data,c.cid,c.real_name, c.nation,b.register_address ,a.id as black_detail_id,c.bank_id FROM "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " a, " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_FK_PERSON_ATTR + " b, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL
                + " c WHERE a.from_person_id = c.id and b.from_person_id=c.id and a.image_data=c.photo_data";

        if (name != null && name.trim() != "") {
            sqlString = sqlString + " and c.real_name= '" + name + "'";
        }
        if (cid != null && cid.trim() != "") {
            sqlString = sqlString + " and c.cid = '" + cid + "'";
        }
        if (fkType != null && fkType.trim() != "") {
            sqlString = sqlString + " and c.fk_type in (" + fkType + ")";
        }
        if (!StringUtils.isEmpty(subInstitutionCode)) {
            sqlString = sqlString + " and (b.fk_sub_institution_code in('" + subInstitutionCode+"') or (b.fk_sub_institution_code='440300000000' and b.fk_local_institution_code in('" + subInstitutionCode+"'))" ;
        }
        if (!StringUtils.isEmpty(districtIds)) {
            sqlString = sqlString + "  or b.distric_id in(" + districtIds+")" ;
        }
        if (!StringUtils.isEmpty(subInstitutionCode)) {
            sqlString = sqlString + ")";
        }                              
        if (!StringUtils.isEmpty(localInstitutionCode)) {
            sqlString = sqlString + " and (b.fk_local_institution_code in('" + localInstitutionCode+"')";
        }
        
        if (!StringUtils.isEmpty(areaIds)) {
            sqlString = sqlString + " or b.area_id in(" + areaIds+")" ;
        }
        if (!StringUtils.isEmpty(localInstitutionCode)) {
            sqlString = sqlString + ")";
        }
       
        

        try {
            Query query = this.em.createNativeQuery(sqlString, FkPersonResultDto.class);
            resp = (ArrayList<FkPersonResultDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }

        return resp;
    }
    
}
