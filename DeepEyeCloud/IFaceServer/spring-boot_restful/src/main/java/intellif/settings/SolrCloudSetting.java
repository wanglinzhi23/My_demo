package intellif.settings;

public class SolrCloudSetting {

	private static String zkServers;

	public static String getZkServers() {
		return zkServers;
	}

	public static void setZkServers(String zkServers) {
		SolrCloudSetting.zkServers = zkServers;
	}
	
}
