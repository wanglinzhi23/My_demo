package intellif.test.inte;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import intellif.Application;
import intellif.consts.GlobalConsts;
import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")   // starts a server on a random port
@ActiveProfiles("test")
@TestPropertySource(properties = { "isJar = true" })
@Transactional
public class AlarmTest {

	@Autowired
    private WebApplicationContext wac;
	
	@Autowired
	private Filter springSecurityFilterChain;
	
	@Value("${local.server.port}")
	int port;
	
	private MockMvc mockMvc;
	
	private String accessToken;
	
	@Before
    public void setUp() {
		if ("".equals(accessToken) || null == accessToken) {
			accessToken = login("zxd", "123456");
		}
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
    }
	
	//@Test
	public void testcreate() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", 51);
		map.put("faceId", 111);
		map.put("blackId", 1);
		map.put("confidence", 0.92);
		map.put("level", 1);
		map.put("status", 0);
		map.put("send", 0);
		
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(map);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GlobalConsts.R_ID_ALARM)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonStr)
				.contentType(MediaType.APPLICATION_JSON_UTF8);
		ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is(HttpStatus.SC_OK));
	}
	
	//@Test
	public void testget() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GlobalConsts.R_ID_ALARM + "/1")
				.header("Authorization", "Bearer " + accessToken);
		ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is(HttpStatus.SC_OK));
	}
	
	@Test
	public void testupdate() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", 51);
		map.put("faceId", 111);
		map.put("blackId", 1);
		map.put("confidence", 0.93);
		map.put("level", 1);
		map.put("status", 0);
		map.put("send", 0);
		
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(map);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(GlobalConsts.R_ID_ALARM + "/1")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonStr);
		ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is(HttpStatus.SC_OK));
	}
	
	//@Test
	public void testdelete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(GlobalConsts.R_ID_ALARM + "/1")
				.header("Authorization", "Bearer " + accessToken);
		ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is(HttpStatus.SC_OK));
	}
	
	//@Test
	public void testsend() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(GlobalConsts.R_ID_ALARM + "/1/send/1")
				.header("Authorization", "Bearer " + accessToken);
		ResultActions resultActions = this.mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is(HttpStatus.SC_OK));
	}
	
	private String login(String username, String password) {
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httppost = new HttpPost("http://localhost:" + port + "/api/oauth/token");  
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("username", username));  
        formparams.add(new BasicNameValuePair("password", password));  
        formparams.add(new BasicNameValuePair("grant_type", "password"));  
        formparams.add(new BasicNameValuePair("scope", "read write"));
        formparams.add(new BasicNameValuePair("client_secret", "123456"));
        formparams.add(new BasicNameValuePair("client_id", "clientapp"));
        httppost.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString("clientapp:123456".getBytes()));
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(entity, "UTF-8"));  
                    
                    Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);  
                      
                    for(Entry<String,Object> entry : mapJson.entrySet()){  
                        if ("access_token".equals(entry.getKey())) {
                        	return entry.getValue().toString();
                        }
                    }  
                }
            } finally {  
                response.close();  
            }
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        
        return null;
	}
}
