package intellif.database.entity;

import java.util.ArrayList;
import java.util.List;

public class CellValidateResult {
private int num;
private List<String> errorList = new ArrayList<String>();
public int getNum() {
	return num;
}
public void setNum(int num) {
	this.num = num;
}
public List<String> getErrorList() {
	return errorList;
}
public void setErrorList(List<String> errorList) {
	this.errorList = errorList;
}

}
