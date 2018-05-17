package intellif.dto;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
*@see http://wiki.fasterxml.com/JacksonInFiveMinutes
*@see http://www.faceplusplus.com/personcreate/
*/
@JsonIgnoreProperties(ignoreUnknown=true)
public class PersonCreate {
	@JsonProperty("response_code")
	private int response_code;
	public int getResponse_code() {
		return response_code;
	}
	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}
	@JsonProperty("added_face")
	private int added_face;
	public int getAdded_face() {
		return added_face;
	}
	public void setAdded_face(int added_face) {
		this.added_face = added_face;
	}
	@JsonProperty("added_group")
	private int added_group;
	public int getAdded_group() {
		return added_group;
	}
	public void setAdded_group(int added_group) {
		this.added_group = added_group;
	}
	@JsonProperty("tag")
	private String tag;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	@JsonProperty("person_name")
	private String person_name;
	public String getPerson_name() {
		return person_name;
	}
	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}
	@JsonProperty("person_id")
	private String person_id;
	public String getPerson_id() {
		return person_id;
	}
	public void setPerson_id(String person_id) {
		this.person_id = person_id;
	}
}
