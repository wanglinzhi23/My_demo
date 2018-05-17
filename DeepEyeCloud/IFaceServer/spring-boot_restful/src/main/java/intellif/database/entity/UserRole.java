package intellif.database.entity;

import intellif.consts.GlobalConsts;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created by yangboz on 11/11/15.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_USER_ROLE,schema=GlobalConsts.INTELLIF_BASE)
public class UserRole {

    @NotEmpty
    private Integer userId;
    @NotEmpty
    private Integer roleId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
