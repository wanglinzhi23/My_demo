package intellif.utils;

import intellif.settings.BankImportSetting;
import intellif.settings.ImageSettings;
import intellif.settings.ServerSetting;

import java.util.regex.Pattern;

/**
 * The Class ImageInfoHelper.
 */
public class ImageInfoHelper {

    public static String getRemoteFaceUrl(String remoteImageUrl, Boolean isJar) {
        String fileName = remoteImageUrl.split(getUrlPrefix(isJar))[1];
        String faceUrl = getUrlPrefix(isJar) + fileName.split(Pattern.quote("."))[0] + "_f." + fileName.split(Pattern.quote("."))[1];
        return faceUrl;
    }

    public static String getRemoteImageUrl(String fileName, Boolean isJar) {
        return getUrlPrefix(isJar) + fileName;
    }

    private static String getUrlPrefix(Boolean isJar) {
        if (isJar) {
        	if(ServerSetting.isSeparateIpOn()) {
            	return "ifsrc" + ServerSetting.getWserverPath() + "/uploads/";
        	} else {
                return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort()
                        + ServerSetting.getWserverPath() + "/uploads/";
        	}
        } else {
        	if(ServerSetting.isSeparateIpOn()) {
            	return "ifsrc" + ServerSetting.getContextPath() + "/uploads/";
        	} else {
                return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getPort()
                        + ServerSetting.getContextPath() + "/uploads/";
        	}
        }
    }
    public static String getPKPrefix(Boolean isJar) {
        if (isJar) {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort()
                    + BankImportSetting.getPkDir();
        } else {
            return "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getPort()
                    + ServerSetting.getContextPath() + "/uploads";
        }
    }
    public static String getRemotePKFaceUrl(String remoteImageUrl, Boolean isJar) {
        String fileName = remoteImageUrl.split(getPKPrefix(isJar))[1];
        String faceUrl = getPKPrefix(isJar) + fileName.split(Pattern.quote("."))[0] + "_f." + fileName.split(Pattern.quote("."))[1];
        return faceUrl;
    }

    public static String getMobileCollectSyncPath() {
        return "http://" + ImageSettings.getStoreRemoteHost()
                + "/mobilecollect";
    }
}
