package intellif.database.entity;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(schema = GlobalConsts.INTELLIF_AREA_AUTHORIZE, name = GlobalConsts.T_NAME_TABLE_VERSION)
public class TableVersion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    // ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    // 用户ID
    protected String dbName;

    // 区域ID
    protected String tableName;

    // 创建时间
    protected Long updateVersion;

	public Long getUpdateVersion() {
		return updateVersion;
	}

	public void setUpdateVersion(Long updateVersion) {
		this.updateVersion = updateVersion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableVersion [id=");
		builder.append(id);
		builder.append(", dbName=");
		builder.append(dbName);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", updateVersion=");
		builder.append(updateVersion);
		builder.append("]");
		return builder.toString();
	}
}
