package intellif.database.entity;

import intellif.consts.GlobalConsts;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by yangboz on 11/17/15.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_RESOURCE,schema=GlobalConsts.INTELLIF_BASE)
public class OauthResource implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3656054041888370643L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    private String uri;

    private String cnName;

    @Column(name = "scopes", nullable = false, columnDefinition = "varchar(255) default 'read,write'")
    private String scopes;//read,write

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "OauthResource,id:" + id + ",cnName:" + this.getCnName() + ",uri:" + this.getUri() + ",scopes:" + this.getScopes();
    }
}
