package intellif.settings;

import intellif.lire.DownloadClearThread;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JinxinSetting {
    private static Logger LOG = LogManager.getLogger(JinxinSetting.class);
    private static String sendUrl;
    private static String checkUrl;
    private static String dailiUrl;
    private static String pIds;
    private static float confidence = 0.92f;// 推送警信报警
    public static boolean run = false;

    public static String getSendUrl() {
        return sendUrl;
    }
public static void setSendUrl(String sendUrl) {
    JinxinSetting.sendUrl = sendUrl;
}
public static String getCheckUrl() {
    return checkUrl;
}
public static void setCheckUrl(String checkUrl) {
    JinxinSetting.checkUrl = checkUrl;
}
public static String getDailiUrl() {
    return dailiUrl;
}
public static void setDailiUrl(String dailiUrl) {
    JinxinSetting.dailiUrl = dailiUrl;
}
public static float getConfidence() {
    return confidence;
}
public static void setConfidence(float confidence) {
    JinxinSetting.confidence = confidence;
}

    public static String getpIds() {
        String returnStr = null;
        try {
            List<String> aList = new ArrayList<String>();
            if (!StringUtils.isEmpty(pIds)) {
                String[] aArray = pIds.split(",");
                for (String item : aArray) {
                    aList.add(item.trim());
                }
                returnStr = StringUtils.join(aList, ",");
            }
        } catch (Exception e) {
           LOG.error("jinxinSetting getCameraIds error,str:"+pIds+",e:",e);
        }

        return returnStr;
    }

    public static void setpIds(String pIds) {
        JinxinSetting.pIds = pIds;
    }

  

}
