package intellif.settings;

public class TableDivideSetting {
	private static int table_divide_size;
	private static String table_divide_starttime;

	public static int getTable_divide_size() {
		return table_divide_size;
	}

	public static void setTable_divide_size(int table_divide_size) {
		TableDivideSetting.table_divide_size = table_divide_size;
	}

	public static String getTable_divide_starttime() {
		return table_divide_starttime;
	}

	public static void setTable_divide_starttime(String table_divide_starttime) {
		TableDivideSetting.table_divide_starttime = table_divide_starttime;
	}

}
