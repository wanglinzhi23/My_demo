package intellif.chd.settings;

/**
 * @author Zheng Xiaodong
 */
public class MobileCollectSyncSetting {
    private static String SERVER_URL;
    private static String cron;

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    public static String getCron() {
        return cron;
    }

    public static void setCron(String cron) {
        MobileCollectSyncSetting.cron = cron;
    }
}
