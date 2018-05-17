package intellif.enums;

import intellif.settings.ServerSetting;
import intellif.settings.SolrCloudSetting;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.ShardParams;

public enum SolrCloudUntils {
	FACE_CLOUD("/facesearch", SolrCloudSetting.getZkServers()) {
		
		@Override
		public SolrQuery getQuery() {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler(this.getHandler());
			query.set("iff", "true");
			query.set("rows", 2000000000);
			query.set(ShardParams.SHARDS_TOLERANT, true);
			return query;
		}

		@Override
		public void init() {
			getCloudClient().setDefaultCollection("intellifusion");
		}
	},

	BLACK_CLOUD("/facesearch", SolrCloudSetting.getZkServers()) {
		
		@Override
		public SolrQuery getQuery() {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler(this.getHandler());
			query.set("iff", "true");
			query.set("rows", 2000000000);
			query.set(ShardParams.SHARDS_TOLERANT, true);
			return query;
		}

		@Override
		public void init() {
			getCloudClient().setDefaultCollection("intellif");
		}
	},

	CID_CLOUD("/facesearch", SolrCloudSetting.getZkServers()) {
		
		@Override
		public SolrQuery getQuery() {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler(this.getHandler());
			query.set("iff", "true");
			query.set("rows", 2000000000);
			query.set(ShardParams.SHARDS_TOLERANT, true);
			return query;
		}

		@Override
		public void init() {
			getCloudClient().setDefaultCollection("cidinfo");
		}
	},

	JUZHU_CLOUD("/facesearch", SolrCloudSetting.getZkServers()) {
		
		@Override
		public SolrQuery getQuery() {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler(this.getHandler());
			query.set("iff", "true");
			query.set("rows", 2000000000);
			query.set(ShardParams.SHARDS_TOLERANT, true);
			return query;
		}

		@Override
		public void init() {
			getCloudClient().setDefaultCollection("juzhuinfo");
		}
	},

	OTHER_CLOUD("/facesearch", SolrCloudSetting.getZkServers()) {
		
		@Override
		public SolrQuery getQuery() {
			SolrQuery query = new SolrQuery();
			query.setRequestHandler(this.getHandler());
			query.set("iff", "true");
			query.set("rows", 2000000000);
			query.set(ShardParams.SHARDS_TOLERANT, true);
			return query;
		}

		@Override
		public void init() {
			getCloudClient().setDefaultCollection("otherinfo");
		}
	};

	private String handler;

	private CloudSolrClient cloudClient;

	private SolrCloudUntils(String handler, String cloudServer) {
		this.handler = handler;

		CloudSolrClient cloud = new CloudSolrClient(cloudServer);
		cloud.setZkClientTimeout(ServerSetting.getSolrSearchTimeOutTime());
		cloud.setZkConnectTimeout(ServerSetting.getSolrServerConnectOutTime());

		this.cloudClient = cloud;
		
		init();
	}

	public String getHandler() {
		return handler;
	}

	public void setCloudClient(CloudSolrClient cloudClient) {
		this.cloudClient = cloudClient;
	}

	public CloudSolrClient getCloudClient() {
		return this.cloudClient;
	}

	public abstract void init();

	public abstract SolrQuery getQuery();
}
