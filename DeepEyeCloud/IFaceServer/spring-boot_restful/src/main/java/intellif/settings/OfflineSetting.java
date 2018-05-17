package intellif.settings;


public class OfflineSetting { 
    public static boolean run=false;

    public static boolean isRun() {
        return run;
    }

    public static void setRun(boolean run) {
        OfflineSetting.run = run;
    }
    

}
