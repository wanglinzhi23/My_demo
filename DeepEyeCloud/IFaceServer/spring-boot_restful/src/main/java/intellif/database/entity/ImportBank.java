package intellif.database.entity;


public class ImportBank {
private  String imageUrl;
private  long id;
private int type;
private String name;


public ImportBank(String imageUrl,long id,int bankType,String name){
	this.imageUrl = imageUrl;
	this.id = id;
	this.type = bankType;
	this.name = name;
}

public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}


public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}




}
