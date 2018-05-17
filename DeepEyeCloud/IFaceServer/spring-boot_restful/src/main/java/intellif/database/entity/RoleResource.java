package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_ROLE_RESOURCE,schema=GlobalConsts.INTELLIF_BASE)
public class RoleResource implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -6783132457100037740L;
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String roleName;
    private Long resourceId;
    private Boolean must;
    private Boolean display;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public Boolean getMust() {
        return must;
    }

    public void setMust(Boolean must) {
        this.must = must;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

	
}