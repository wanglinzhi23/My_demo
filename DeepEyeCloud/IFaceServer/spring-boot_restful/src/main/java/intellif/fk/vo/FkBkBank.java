package intellif.fk.vo;

import intellif.consts.GlobalConsts;
import intellif.fk.dto.FkPersonDto;
import intellif.database.entity.InfoBase;

import javax.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = GlobalConsts.T_NAME_FK_BK_BANK,schema=GlobalConsts.INTELLIF_BASE)
public class FkBkBank implements Serializable {

    private static final long serialVersionUID = -409284155257335672L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String shortName;
    
    private String fullName;
 
    private long bankno;

    public FkBkBank() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getBankno() {
        return bankno;
    }

    public void setBankno(long bankno) {
        this.bankno = bankno;
    }


    
    
  
}
