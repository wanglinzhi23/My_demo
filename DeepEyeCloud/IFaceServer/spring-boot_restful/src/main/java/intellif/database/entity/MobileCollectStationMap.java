package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Zheng Xiaodong
 */
@Entity
@Table(name = GlobalConsts.T_NAME_MOBILE_COLLECT_SYNC_LOG ,schema = GlobalConsts.INTELLIF_BASE)
public class MobileCollectStationMap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long syncStationId;

    private Long mappedStationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSyncStationId() {
        return syncStationId;
    }

    public void setSyncStationId(Long syncStationId) {
        this.syncStationId = syncStationId;
    }

    public Long getMappedStationId() {
        return mappedStationId;
    }

    public void setMappedStationId(Long mappedStationId) {
        this.mappedStationId = mappedStationId;
    }
}
