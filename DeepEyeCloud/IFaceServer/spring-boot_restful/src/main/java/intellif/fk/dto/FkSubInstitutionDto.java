package intellif.fk.dto;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class FkSubInstitutionDto {

    @Id
    private long id;
    //分局代码
    private String subStationCode;
    
    //分局名称
    private String subStationName;
    
    //派出所列表
    @Transient
    private List<FkLocalInstitutionDto> localStationList;

    public String getSubStationCode() {
        return subStationCode;
    }

    public void setSubStationCode(String subStationCode) {
        this.subStationCode = subStationCode;
    }

    public String getSubStationName() {
        return subStationName;
    }

    public void setSubStationName(String subStationName) {
        this.subStationName = subStationName;
    }

    public List<FkLocalInstitutionDto> getLocalStationList() {
        return localStationList;
    }

    public void setLocalStationList(List<FkLocalInstitutionDto> localStationList) {
        this.localStationList = localStationList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    
  

}
