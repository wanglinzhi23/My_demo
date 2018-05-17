package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_AREA_BLACKDETAIL,schema=GlobalConsts.INTELLIF_BASE)
public class AreaAndBlackDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // 编号id
    private long id;

    private long areaId;

    private long blackdetailId;

    public AreaAndBlackDetail(long areaId, long blackdetailId) {
        super();
        this.areaId = areaId;
        this.blackdetailId = blackdetailId;
    }

    public AreaAndBlackDetail() {
        // TODO Auto-generated constructor stub
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public long getBlackdetailId() {
        return blackdetailId;
    }

    public void setBlackdetailId(long blackdetailId) {
        this.blackdetailId = blackdetailId;
    }

}
