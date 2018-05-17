package intellif.dto;

import java.util.ArrayList;
/**
*@see http://wiki.fasterxml.com/JacksonInFiveMinutes
*@see http://www.faceplusplus.com/groupcreate/
*/
public class GroupCreate {
	private int response_code;
	public int getResponse_code() {
		return response_code;
	}
	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}
	private int added_person;
	public int getAdded_person() {
		return added_person;
	}
	public void setAdded_person(int added_person) {
		this.added_person = added_person;
	}
	private String tag;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	private String group_name;
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	private String group_id;
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}
