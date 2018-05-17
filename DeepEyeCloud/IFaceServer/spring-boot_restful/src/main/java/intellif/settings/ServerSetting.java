package intellif.settings;

import java.util.List;

public class ServerSetting {
    private static Integer port;
    private static String contextPath;
    private static String solrServer;
    private static Integer wserverPort;
    private static String wserverPath;//web server contextPath
    private static Long indexRate;
    private static Long indexHours;
    private static Long deleteRate;
    private static Float threshold;
    private static boolean isEngineStatusOn = false;
    private static boolean isSeparateIpOn = false;

    private static Integer indexStep;

	private static Integer solrSearchTimeOutTime = 8000;
	private static Integer solrServerConnectOutTime = 1000;
	private static Integer solrResultMaxSize = 30000;
	private static Integer solrStaticResultMaxSize = 500;
	private static boolean useRabbit = false;

	private static List<Integer> algVersionList;
    
    public static Integer getIndexStep() {
		return indexStep;
	}

	public static void setIndexStep(Integer indexStep) {
		ServerSetting.indexStep = indexStep;
	}

	public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        ServerSetting.port = port;
    }

    public static String getContextPath() {
        return contextPath;
    }

    public static void setContextPath(String contextPath) {
        ServerSetting.contextPath = contextPath;
    }

    public static String getSolrServer() {
        return solrServer;
    }

    public static void setSolrServer(String solrServer) {
        ServerSetting.solrServer = solrServer;
    }

    public static Integer getWserverPort() {
        return wserverPort;
    }

    public static void setWserverPort(Integer wserverPort) {
        ServerSetting.wserverPort = wserverPort;
    }

    public static String getWserverPath() {
        return wserverPath;
    }

    public static void setWserverPath(String wserverPath) {
        ServerSetting.wserverPath = wserverPath;
    }

	public static Long getIndexRate() {
		return indexRate;
	}

	public static void setIndexRate(Long indexRate) {
		ServerSetting.indexRate = indexRate;
	}

    public static Long getIndexHours() {
        return indexHours;
    }

    public static void setIndexHours(Long indexHours) {
        ServerSetting.indexHours = indexHours;
    }

    public static void setDeleteRate(Long deleteRate) {   
		ServerSetting.deleteRate = deleteRate;
	}
	
	public static Long getDeleteRate() {
		return deleteRate;
	}

	public static Float getThreshold() {
		return threshold;
	}

	public static void setThreshold(Float threshold) {
		ServerSetting.threshold = threshold;
	}
	
	public static boolean isEngineStatusOn() {
		return isEngineStatusOn;
	}

	public static void setEngineStatusOn(boolean isEngineStatusOn) {
		ServerSetting.isEngineStatusOn = isEngineStatusOn;

	}
	
	public static boolean isSeparateIpOn() {
		return isSeparateIpOn;
	}

	public static void setSeparateIpOn(boolean isSeparateIpOn) {
		ServerSetting.isSeparateIpOn = isSeparateIpOn;
	}


	public static Integer getSolrSearchTimeOutTime() {
		return solrSearchTimeOutTime;
	}

	public static void setSolrSearchTimeOutTime(Integer solrSearchTimeOutTime) {
		ServerSetting.solrSearchTimeOutTime = solrSearchTimeOutTime;
	}

	public static Integer getSolrServerConnectOutTime() {
		return solrServerConnectOutTime;
	}

	public static void setSolrServerConnectOutTime(Integer solrServerConnectOutTime) {
		ServerSetting.solrServerConnectOutTime = solrServerConnectOutTime;
	}

	public static Integer getSolrResultMaxSize() {
		return solrResultMaxSize;
	}

	public static void setSolrResultMaxSize(Integer solrResultMaxSize) {

		ServerSetting.solrResultMaxSize = solrResultMaxSize;

	}

	public static List<Integer> getAlgVersionList()
	{
		return algVersionList;
	}

	public static void setAlgVersionList(List<Integer> algVersionList)
	{
		ServerSetting.algVersionList = algVersionList;
	}

    public static Integer getSolrStaticResultMaxSize() {
        return solrStaticResultMaxSize;
    }

    public static void setSolrStaticResultMaxSize(Integer solrStaticResultMaxSize) {
        ServerSetting.solrStaticResultMaxSize = solrStaticResultMaxSize;
    }

    public static boolean isUseRabbit() {
        return useRabbit;
    }

    public static void setUseRabbit(boolean useRabbit) {
        ServerSetting.useRabbit = useRabbit;
    }

}