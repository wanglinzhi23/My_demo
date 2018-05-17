package intellif.dto;

/**
 * @author yangboz
 * @see http://projectlombok.org/features/GetterSetter.html
 */
public class SourceFile {
	private int id=0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private String name="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String path="";
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
