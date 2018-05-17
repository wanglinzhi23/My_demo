package intellif.fk.vo;

import intellif.consts.GlobalConsts;
import intellif.database.entity.GeometryInfoBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_FK_PLACE, schema = GlobalConsts.INTELLIF_BASE)
public class FkPlace extends GeometryInfoBase{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 场所编号
    private String placeNo;
    // 场所名称
    private String placeName;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPlaceNo() {
        return placeNo;
    }
    public void setPlaceNo(String placeNo) {
        this.placeNo = placeNo;
    }
    public String getPlaceName() {
        return placeName;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


}
