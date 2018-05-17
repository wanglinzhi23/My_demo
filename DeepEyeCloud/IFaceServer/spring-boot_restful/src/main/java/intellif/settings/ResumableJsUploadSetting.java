package intellif.settings;

public class ResumableJsUploadSetting {
	
	private static String localPath;
	
	private static String remotePath;

    public static String getLocalPath() {
        return localPath;
    }

    public static void setLocalPath(String localPath) {
        ResumableJsUploadSetting.localPath = localPath;
    }

    public static String getRemotePath() {
        return remotePath;
    }

    public static void setRemotePath(String remotePath) {
        ResumableJsUploadSetting.remotePath = remotePath;
    }

   
}
