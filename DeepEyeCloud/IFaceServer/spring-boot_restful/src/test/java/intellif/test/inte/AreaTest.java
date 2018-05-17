package intellif.test.inte;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;

import intellif.Application;
import intellif.consts.GlobalConsts;
import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")   // starts a server on a random port
@ActiveProfiles("test")
@TestPropertySource(properties = { "isJar = true" })
public class AreaTest {
	
	@Value("${local.server.port}")
	int port;
	
	String accessToken;
	
	@Before
    public void setUp() {
		accessToken = login("zxd", "123456");
        RestAssured.port = port;
    }
	
	@Test()
    public void testList() {
		given().auth().oauth2(accessToken).get(getApiPath() + "").then().statusCode(HttpStatus.SC_OK);
    }
	
	@Test()
    public void testList2() {
		given().auth().oauth2(accessToken).get(getApiPath() + "").then().statusCode(HttpStatus.SC_OK);
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
	
	private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_AREA;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }
	
}
