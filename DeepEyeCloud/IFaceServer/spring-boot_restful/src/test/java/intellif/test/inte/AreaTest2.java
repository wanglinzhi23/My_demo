package intellif.test.inte;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
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
public class AreaTest2 {
	
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
	
	@Test
    public void testList() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GlobalConsts.R_ID_AREA).
				header("Authorization", "Bearer " + accessToken);
		
		this.mockMvc.perform(requestBuilder).
		                        andExpect(status().is(HttpStatus.SC_OK))
		                        .andExpect(jsonPath("$.data[0].areaName").value("科学馆1楼"));
		
    }
	
	@Test
    public void testCreate() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("areaNo", "111");
		map.put("areaName", "area1");
		map.put("personThreshold", "333");
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(map);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GlobalConsts.R_ID_AREA)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonStr);
		
		this.mockMvc.perform(requestBuilder).
		                        andExpect(status().is(HttpStatus.SC_OK))
		                        .andExpect(jsonPath("$.data.areaName").value("area1"));
		
    }
	
	private String login(String username, String password) {
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httppost = new HttpPost("http://192.168.2.92:" + port + "/api/oauth/token");  
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
