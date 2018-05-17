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
 * 系统开关（全局开关）
 * 
 * @author Leazy代码生成器
 */
@Entity
@Table(schema = GlobalConsts.INTELLIF_AREA_AUTHORIZE, name = GlobalConsts.T_NAME_SYSTEM_SWITCH)
public class SystemSwitch implements Serializable {

    /**
     * 序列化版本
     */
    private static final long serialVersionUID = 1L;
    
    public static final String SWITCH_TYPE_AREA_AUTHORIZE = "AREA_AUTHORIZE";

    // ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    // 开关类型
    protected String switchType;

    // 是否打开
    protected Boolean opened;

    // 创建时间
    protected Date created;


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
     * 获取开关类型
     * @return 开关类型
     */
     public String getSwitchType() {
         return switchType;
     }

    /**
     * 设置开关类型
     */
     public void setSwitchType(String switchType) {
         this.switchType = switchType;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SystemSwitch [id=");
		builder.append(id);
		builder.append(", switchType=");
		builder.append(switchType);
		builder.append(", opened=");
		builder.append(opened);
		builder.append(", created=");
		builder.append(created);
		builder.append("]");
		return builder.toString();
	}
}