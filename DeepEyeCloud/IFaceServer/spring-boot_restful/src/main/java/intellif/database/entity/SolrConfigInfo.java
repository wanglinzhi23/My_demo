package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = GlobalConsts.T_NAME_SOLR_CONFIG_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class SolrConfigInfo implements Serializable {

	private static final long serialVersionUID = 7225464949093873209L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 索引数据源Id
    private Long sourceId;
    // Solr服务器Url地址
    private String serverUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

}
