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
public class MobileCollectSyncLog extends InfoBase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date fileDate;

    private String fileName;

    private Integer syncStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFileDate() {
        return fileDate;
    }

    public void setFileDate(Date fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }
}
