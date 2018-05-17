package intellif.settings;

public class ResidentSetting {
	
	private static float rate;//常住人口判断标准 大于等于rate为常住，小于rate为非常住
    private static String ip;
    private static String username;
    private static String password;
    private static String command;
	public static String getCommand() {
		return command;
	}

	public static void setCommand(String command) {
		ResidentSetting.command = command;
	}

	public static float getRate() {
		return rate;
	}

	public static void setRate(float rate) {
		ResidentSetting.rate = rate;
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		ResidentSetting.ip = ip;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		ResidentSetting.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		ResidentSetting.password = password;
	}
	

}
