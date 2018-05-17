package intellif.settings;

/**
 * Created by yangboz on 9/21/15.
 */
public class ImageSettings {
    private static String storeRemoteHost;
    private static String storeRemoteUrl;
    private static String storeLocalPath;
    private static int faceScale;
    private static int faceOffsetX;
    private static int faceOffsetY;
    private static String uploadDir;
    private static boolean jsonSwitch;
    
  
    
    public static String getUploadDir() {
		return uploadDir;
	}

	public static void setUploadDir(String uploadDir) {
		ImageSettings.uploadDir = uploadDir;
	}

	public static int getFaceOffsetX() {
        return faceOffsetX;
    }

    public static void setFaceOffsetX(int faceOffsetX) {
        ImageSettings.faceOffsetX = faceOffsetX;
    }

    public static int getFaceOffsetY() {
        return faceOffsetY;
    }

    public static void setFaceOffsetY(int faceOffsetY) {
        ImageSettings.faceOffsetY = faceOffsetY;
    }

    public static String getStoreRemoteUrl() {
        return storeRemoteUrl;
    }

    public static void setStoreRemoteUrl(String storeRemoteUrl) {
        ImageSettings.storeRemoteUrl = storeRemoteUrl;
    }

    public static String getStoreLocalPath() {
        return storeLocalPath;
    }

    public static void setStoreLocalPath(String storeLocalPath) {
        ImageSettings.storeLocalPath = storeLocalPath;
    }

    public static int getFaceScale() {
        return faceScale;
    }

    public static void setFaceScale(int faceScale) {
        ImageSettings.faceScale = faceScale;
    }

    public static String getStoreRemoteHost() {
        return storeRemoteHost;
    }

    public static void setStoreRemoteHost(String storeRemoteHost) {
        ImageSettings.storeRemoteHost = storeRemoteHost;
    }

	public static boolean isJsonSwitch() {
		return jsonSwitch;
	}

	public static void setJsonSwitch(boolean jsonSwitch) {
		ImageSettings.jsonSwitch = jsonSwitch;
	}
    
}
