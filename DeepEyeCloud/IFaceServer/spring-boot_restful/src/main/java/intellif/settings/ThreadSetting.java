package intellif.settings;

public class ThreadSetting {
private static int blackThreadsNum = 10;

public static int getBlackThreadsNum() {
	return blackThreadsNum;
}

public static void setBlackThreadsNum(int blackThreadsNum) {
	ThreadSetting.blackThreadsNum = blackThreadsNum;
}

}
