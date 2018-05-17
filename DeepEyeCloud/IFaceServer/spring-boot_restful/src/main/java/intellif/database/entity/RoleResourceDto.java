package intellif.database.entity;

/**
 * Created by Zheng Xiaodong on 2017/4/20.
 */
public class RoleResourceDto {
    private Long id;
    private String roleName;
    private Long resourceId;
    private String cnName;
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

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
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
