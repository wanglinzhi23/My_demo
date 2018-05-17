package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;


/**
 * 用户授权开关表
 * 
 * @author Leazy代码生成器
 */
@Entity
@Table(schema = GlobalConsts.INTELLIF_AREA_AUTHORIZE, name = GlobalConsts.T_NAME_USER_SWITCH)
public class UserSwitch implements Serializable {

    /**
     * 序列化版本
     */
    private static final long serialVersionUID = 1L;

    // ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    // 用户ID
    protected Long userId;

    // 是否打开
    protected Boolean opened;

    // 创建时间
    protected Date created = new Date();

    // 创建人
    protected Long creator;


    /**
     * 获取ID
     * @return ID
     */
     public Long getId() {
         return id;
     }

    /**
     * 设置ID
     */
     public void setId(Long id) {
         this.id = id;
     }
    /**
     * 获取用户ID
     * @return 用户ID
     */
     public Long getUserId() {
         return userId;
     }

    /**
     * 设置用户ID
     */
     public void setUserId(Long userId) {
         this.userId = userId;
     }
    /**
     * 获取是否打开
     * @return 是否打开
     */
     public Boolean getOpened() {
         return opened;
     }

    /**
     * 设置是否打开
     */
     public void setOpened(Boolean opened) {
         this.opened = opened;
     }
    /**
     * 获取创建时间
     * @return 创建时间
     */
     public Date getCreated() {
         return created;
     }

    /**
     * 设置创建时间
     */
     public void setCreated(Date created) {
         this.created = created;
     }
    /**
     * 获取创建人
     * @return 创建人
     */
     public Long getCreator() {
         return creator;
     }

    /**
     * 设置创建人
     */
     public void setCreator(Long creator) {
         this.creator = creator;
     }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserSwitch [id=");
		builder.append(id);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", opened=");
		builder.append(opened);
		builder.append(", created=");
		builder.append(created);
		builder.append(", creator=");
		builder.append(creator);
		builder.append("]");
		return builder.toString();
	}
}