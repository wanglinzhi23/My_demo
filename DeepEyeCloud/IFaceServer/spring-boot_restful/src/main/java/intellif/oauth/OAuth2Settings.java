package intellif.oauth;

import java.util.ArrayList;
import java.util.List;

public class OAuth2Settings {
	
	private static int accessTimeInterval;

	private static List<String> whiteList = new ArrayList<String>();
	
	private static List<String> blackList = new ArrayList<String>();
	
	public static int getAccessTimeInterval() {
		return accessTimeInterval;
	}

	public static void setAccessTimeInterval(int accessTimeInterval) {
		OAuth2Settings.accessTimeInterval = accessTimeInterval;
	}

    public static List<String> getWhiteList() {
        return whiteList;
    }

    public static void setWhiteList(List<String> whiteList) {
        OAuth2Settings.whiteList = whiteList;
    }

    public static List<String> getBlackList() {
        return blackList;
    }

    public static void setBlackList(List<String> blackLint) {
        OAuth2Settings.blackList = blackLint;
    }

	
}
