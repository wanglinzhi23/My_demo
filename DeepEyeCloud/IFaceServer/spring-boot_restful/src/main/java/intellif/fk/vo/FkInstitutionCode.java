package intellif.fk.vo;

import intellif.consts.GlobalConsts;
import intellif.fk.dto.FkPersonDto;
import intellif.database.entity.InfoBase;

import javax.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = GlobalConsts.T_NAME_FK_INSTITUTION_CODE,schema=GlobalConsts.INTELLIF_BASE)
public class FkInstitutionCode implements Serializable {

    private static final long serialVersionUID = -409284155257335672L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    //机构代码一共12位 前6位表示分局代码  后6位表示派出代码
    private String JGDM;
    
    private String JGMC;
    //上级机构
    private String LSJG;
    
    private String JB;

    private String SFYXJDW;
    
    private String IS_TEMP;
    
    private String SFKY;
    
    private String JGJC;
    
    private String JGPY;
    
    private String JGPX;
    
    private String BRANCHKEY;
    
    private String STATIONKEY;

    private String MS;
    
    private String CJR;
    
    private String CJRDW;
    
    private String CJSJ;
    
    private String LY;
    

    public FkInstitutionCode() {
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getJGDM() {
        return JGDM;
    }


    public void setJGDM(String jGDM) {
        JGDM = jGDM;
    }


    public String getJGMC() {
        return JGMC;
    }


    public void setJGMC(String jGMC) {
        JGMC = jGMC;
    }


    public String getLSJG() {
        return LSJG;
    }


    public void setLSJG(String lSJG) {
        LSJG = lSJG;
    }


    public String getJB() {
        return JB;
    }


    public void setJB(String jB) {
        JB = jB;
    }


    public String getSFYXJDW() {
        return SFYXJDW;
    }


    public void setSFYXJDW(String sFYXJDW) {
        SFYXJDW = sFYXJDW;
    }


    public String getIS_TEMP() {
        return IS_TEMP;
    }


    public void setIS_TEMP(String iS_TEMP) {
        IS_TEMP = iS_TEMP;
    }


    public String getSFKY() {
        return SFKY;
    }


    public void setSFKY(String sFKY) {
        SFKY = sFKY;
    }


    public String getJGJC() {
        return JGJC;
    }


    public void setJGJC(String jGJC) {
        JGJC = jGJC;
    }


    public String getJGPY() {
        return JGPY;
    }


    public void setJGPY(String jGPY) {
        JGPY = jGPY;
    }


    public String getJGPX() {
        return JGPX;
    }


    public void setJGPX(String jGPX) {
        JGPX = jGPX;
    }


    public String getBRANCHKEY() {
        return BRANCHKEY;
    }


    public void setBRANCHKEY(String bRANCHKEY) {
        BRANCHKEY = bRANCHKEY;
    }


    public String getSTATIONKEY() {
        return STATIONKEY;
    }


    public void setSTATIONKEY(String sTATIONKEY) {
        STATIONKEY = sTATIONKEY;
    }


    public String getMS() {
        return MS;
    }


    public void setMS(String mS) {
        MS = mS;
    }


    public String getCJR() {
        return CJR;
    }


    public void setCJR(String cJR) {
        CJR = cJR;
    }


    public String getCJRDW() {
        return CJRDW;
    }


    public void setCJRDW(String cJRDW) {
        CJRDW = cJRDW;
    }


    public String getCJSJ() {
        return CJSJ;
    }


    public void setCJSJ(String cJSJ) {
        CJSJ = cJSJ;
    }


    public String getLY() {
        return LY;
    }


    public void setLY(String lY) {
        LY = lY;
    }


  
}
