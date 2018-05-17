package intellif.settings;

public class CasSSOSetting {
    private static String ticketValidateUrl;
    
    private static String fkUserMd5ValidateUrl;

    public static String getTicketValidateUrl() {
        return ticketValidateUrl;
    }

    public static void setTicketValidateUrl(String ticketValidateUrl) {
        CasSSOSetting.ticketValidateUrl = ticketValidateUrl;
    }

    public static String getFkUserMd5ValidateUrl() {
        return fkUserMd5ValidateUrl;
    }

    public static void setFkUserMd5ValidateUrl(String fkUserMd5ValidateUrl) {
        CasSSOSetting.fkUserMd5ValidateUrl = fkUserMd5ValidateUrl;
    }
    

}
