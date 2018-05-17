package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: AlgParam.java</p>
 * <p>Description: Solr算法参数对象</p>
 * <p>Copyright: Copyright (c) 2015-2018 深圳云天励飞技术有限公司
 * <p>Company: 深圳云天励飞技术有限公司</p>
 * @author Peng Cheng
 * @version 1.3.2 创建时间：2017年5月23日 下午8:38:42
 */
@Entity
@Table(name = GlobalConsts.T_NAME_ALG_PARAM,schema=GlobalConsts.INTELLIF_BASE)
public class AlgParam {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 算法版本号
	private String version;
	
	// 映射点（基准点）
	private String basePoints;
	
	// 起始点（新点）
	private String newPoints;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBasePoints() {
		return basePoints;
	}

	public void setBasePoints(String basePoints) {
		this.basePoints = basePoints;
	}

	public String getNewPoints() {
		return newPoints;
	}

	public void setNewPoints(String newPoints) {
		this.newPoints = newPoints;
	}

}
