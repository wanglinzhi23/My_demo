package intellif.jackson;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import intellif.database.entity.PersonDetail;

public class PersonDetailJsonSerializeTest {

	public void voSerializeTest() throws Exception {
		PersonDetail detail = new PersonDetail();
		detail.setAddress("jfajdfja");
		detail.setId(123491234l);
		detail.setBirthday(new Date());
		
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(detail);
		System.out.println(result);
	}
}
