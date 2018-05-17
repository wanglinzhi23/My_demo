package intellif.chd.consts;

public class GenerateId {

	private static long id = System.currentTimeMillis();

	public static synchronized long getId() {
		return id++;
	}
}
