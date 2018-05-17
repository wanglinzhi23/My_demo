package intellif.fk.service.impl;

import intellif.consts.GlobalConsts;
import intellif.core.tree.itf.TreeNode;
import intellif.fk.dto.FkLocalInstitutionDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.dto.FkSubInstitutionDto;
import intellif.fk.service.FkInstitutionCodeService;
import intellif.fk.vo.FkInstitutionCode;
import intellif.service.UserServiceItf;
import intellif.database.entity.Area;
import intellif.database.entity.DistrictInfo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FkInstitutionCodeServiceImpl implements FkInstitutionCodeService {

    private static Logger LOG = LogManager.getLogger(FkInstitutionCodeServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @Override
    public List<FkSubInstitutionDto> findFkSubStation() {

        List<FkSubInstitutionDto> resp = null;
        // 机构代码 后6位全是0的是分局代码
        String sqlString = "select id,JGDM as sub_station_code,JGMC as sub_station_name from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FK_INSTITUTION_CODE + " where (right(JGDM,6)='000000' || right(JGDM,6)='S00000'|| right(JGDM,6)='T00000') and (right(JGDM,8)!='00000000')";

        try {
            Query query = this.em.createNativeQuery(sqlString, FkSubInstitutionDto.class);
            resp = (ArrayList<FkSubInstitutionDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FkLocalInstitutionDto> findFkLocalStation() {

        List<FkLocalInstitutionDto> resp = null;
        // 机构代码 派出所的后6位非全0
        String sqlString = "select id,JGDM as local_station_code,JGMC as local_station_name from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FK_INSTITUTION_CODE + " where right(JGDM,6)=!'000000'";

        try {
            Query query = this.em.createNativeQuery(sqlString, FkLocalInstitutionDto.class);
            resp = (ArrayList<FkLocalInstitutionDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FkLocalInstitutionDto> findFkLocalStationBySubStation(String subStationJGDM) {
        
        List<FkLocalInstitutionDto> resp = null;
        // 机构代码 派出所的后6位非全0
        String sqlString = "select id,JGDM as local_station_code,JGMC as local_station_name from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FK_INSTITUTION_CODE + " a where right(a.JGDM,6)!='000000' and a.LSJG=:lsjg";

        try {
            Query query = this.em.createNativeQuery(sqlString, FkLocalInstitutionDto.class);
            query.setParameter("lsjg", subStationJGDM);
            resp = (ArrayList<FkLocalInstitutionDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            em.close();
        }
        return resp;
    }

 

}
