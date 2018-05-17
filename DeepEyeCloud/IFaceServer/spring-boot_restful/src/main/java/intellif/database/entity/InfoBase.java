/**
 *
 */
package intellif.database.entity;

import intellif.audit.EntityAuditListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The Class InfoBase for each column with CREATED/UPDATED field.
 *
 * @author yangboz
 */
@MappedSuperclass
@EntityListeners({EntityAuditListener.class})
public abstract class InfoBase implements Serializable, Cloneable {

    private static final long serialVersionUID = 8344897915260312570L;
    // @Version
    // @Temporal(TemporalType.DATE)
    // @Column(name = "created", nullable = false)
    // @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(name = "CREATED", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED", nullable = false)
    private Date updated;

    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public Date getUpdated() {
        return updated;
    }

}
