package intellif.settings;

/**
 * Created by shixiaohua 
 */
public class PerformParamSetting {
    private static int  selectAlarmNum = 60000;
    private static int scanCount = 110;
    private static int bankPersonNum = 200;
    public static int getSelectAlarmNum() {
        return selectAlarmNum;
    }
    public static void setSelectAlarmNum(int selectAlarmNum) {
        PerformParamSetting.selectAlarmNum = selectAlarmNum;
    }
    public static int getScanCount() {
        return scanCount;
    }
    public static void setScanCount(int scanCount) {
        PerformParamSetting.scanCount = scanCount;
    }
    public static int getBankPersonNum() {
        return bankPersonNum;
    }
    public static void setBankPersonNum(int bankPersonNum) {
        PerformParamSetting.bankPersonNum = bankPersonNum;
    }
   
  
 
}
