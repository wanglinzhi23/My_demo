package intellif.settings;

public class MiningSetting {
	
	private static String miningUrlBase;

	public static String getMiningUrlBase() {
		return miningUrlBase;
	}

	public static void setMiningUrlBase(String miningUrlBase) {
		MiningSetting.miningUrlBase = miningUrlBase;
	}
}
