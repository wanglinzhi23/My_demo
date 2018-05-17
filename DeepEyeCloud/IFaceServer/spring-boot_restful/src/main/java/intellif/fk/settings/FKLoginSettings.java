package intellif.fk.settings;

public class FKLoginSettings {
    private static String loginUrl;
    
    private static String tokenUrl;
    
    private static String alarmUrl;
    
    private static String applicationId;

    public static String getLoginUrl() {
        return loginUrl;
    }

    public static void setLoginUrl(String loginUrl) {
        FKLoginSettings.loginUrl = loginUrl;
    }

    public static String getTokenUrl() {
        return tokenUrl;
    }

    public static void setTokenUrl(String tokenUrl) {
        FKLoginSettings.tokenUrl = tokenUrl;
    }

    public static String getAlarmUrl() {
        return alarmUrl;
    }

    public static void setAlarmUrl(String alarmUrl) {
        FKLoginSettings.alarmUrl = alarmUrl;
    }

    public static String getApplicationId() {
        return applicationId;
    }

    public static void setApplicationId(String applicationId) {
        FKLoginSettings.applicationId = applicationId;
    }
    
    

}
