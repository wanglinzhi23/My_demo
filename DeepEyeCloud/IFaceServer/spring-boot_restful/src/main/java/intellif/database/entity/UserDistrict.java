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
 * 用户与分局映射关系表
 * 
 * @author Leazy代码生成器
 */
@Entity
@Table(schema = GlobalConsts.INTELLIF_AREA_AUTHORIZE, name = GlobalConsts.T_NAME_USER_DISTRICT)
public class UserDistrict implements Serializable {

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

    // 分局ID
    protected Long districtId;

    // 创建时间
    protected Date created = new Date();


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
     * 获取分局ID
     * @return 分局ID
     */
     public Long getDistrictId() {
         return districtId;
     }

    /**
     * 设置分局ID
     */
     public void setDistrictId(Long districtId) {
         this.districtId = districtId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserArea [id=");
		builder.append(id);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", districtId=");
		builder.append(districtId);
		builder.append(", created=");
		builder.append(created);
		builder.append("]");
		return builder.toString();
	}
}