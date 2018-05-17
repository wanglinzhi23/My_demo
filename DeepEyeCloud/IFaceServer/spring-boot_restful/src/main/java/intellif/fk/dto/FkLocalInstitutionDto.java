package intellif.fk.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FkLocalInstitutionDto {

    @Id
    private long id;
    //派出所代码
    private String localStationCode;
    
    //派出所名称
    private String localStationName;

    public String getLocalStationCode() {
        return localStationCode;
    }

    public void setLocalStationCode(String localStationCode) {
        this.localStationCode = localStationCode;
    }

    public String getLocalStationName() {
        return localStationName;
    }

    public void setLocalStationName(String localStationName) {
        this.localStationName = localStationName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

 
    
    


    
}
