package intellif.settings;

public class XinYiSettings {
	
	private static String userApiUrl;
	private static String identityQueryApiUrlBegin;
	private static String identityQueryApiUrlEnd;
	private static String xinyiSwitch;
	private static String qiangdanUrl;
	private static boolean xinyiUserSwitch = false;
	private static String qiangdanSyncUrl;
	
    public static String getQiangdanSyncUrl() {
        return qiangdanSyncUrl;
    }

    public static void setQiangdanSyncUrl(String qiangdanSyncUrl) {
        XinYiSettings.qiangdanSyncUrl = qiangdanSyncUrl;
    }
	private static String vehicleApiUrl;
	
	public static String getIdentityQueryApiUrlBegin() {
		return identityQueryApiUrlBegin;
	}

	public static void setIdentityQueryApiUrlBegin(String identityQueryApiUrlBegin) {
		XinYiSettings.identityQueryApiUrlBegin = identityQueryApiUrlBegin;
	}

	public static String getIdentityQueryApiUrlEnd() {
		return identityQueryApiUrlEnd;
	}

	public static void setIdentityQueryApiUrlEnd(String identityQueryApiUrlEnd) {
		XinYiSettings.identityQueryApiUrlEnd = identityQueryApiUrlEnd;
	}

	public static String getUserApiUrl() {
		return userApiUrl;
	}

	public static void setUserApiUrl(String userApiUrl) {
		XinYiSettings.userApiUrl = userApiUrl;
	}

	public static String getXinyiSwitch() {
		return xinyiSwitch;
	}

	public static void setXinyiSwitch(String xinyiSwitch) {
		XinYiSettings.xinyiSwitch = xinyiSwitch;
	}

    public static boolean isXinyiUserSwitch() {
        return xinyiUserSwitch;
    }

    public static void setXinyiUserSwitch(boolean xinyiUserSwitch) {
        XinYiSettings.xinyiUserSwitch = xinyiUserSwitch;
    }
	
		public static String getVehicleApiUrl() {
		return vehicleApiUrl;
	}

	public static void setVehicleApiUrl(String vehicleApiUrl) {
		XinYiSettings.vehicleApiUrl = vehicleApiUrl;
	}

    public static String getQiangdanUrl() {
        return qiangdanUrl;
    }

    public static void setQiangdanUrl(String qiangdanUrl) {
        XinYiSettings.qiangdanUrl = qiangdanUrl;
    }

}
