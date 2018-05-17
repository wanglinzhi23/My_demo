package intellif.settings;

public class BankImportSetting {
	 private static String pkDir;
	 private static int corePoolSize = 10;
	public static String getPkDir() {
		return pkDir;
	}
	public static void setPkDir(String pkDir) {
		BankImportSetting.pkDir = pkDir;
	}
	public static int getCorePoolSize() {
		return corePoolSize;
	}
	public static void setCorePoolSize(int corePoolSize) {
		BankImportSetting.corePoolSize = corePoolSize;
	}
	 
	 
	 
}
