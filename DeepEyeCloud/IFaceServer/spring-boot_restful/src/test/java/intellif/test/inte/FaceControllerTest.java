package intellif.test.inte;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.test.util.TestUtil;
import intellif.database.entity.FaceInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")   // starts a server on a random port
@ActiveProfiles("test")
@TestPropertySource(properties = { "isJar = true" })
@Transactional
public class FaceControllerTest {


	@Autowired
    private WebApplicationContext wac;
	
	@Autowired
	private Filter springSecurityFilterChain;
	
	@Autowired
	private FaceInfoDaoImpl faceDao;
	
	@Value("${local.server.port}")
	int port;
	
	private MockMvc mockMvc;
	
	private static String accessToken;
	
	@Before
    public void setUp() {
		if ("".equals(accessToken) || null == accessToken) {
			accessToken = TestUtil.login("http://192.168.2.69:" + port, "lilan", "b7ac008d2a0f3a3b262efa43018d0412");
		}
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
    }
	
	
	
	////////////////face一比一
	  @Test
	    public void testFaceCompare() {
		  Map<String, String> map = new HashMap<String, String>();
		  
		   List<FaceInfo> fList = faceDao.findLast(" face_feature is not null limit 2");
		  
		/*	map.put("faceIdA", "23081052761541536");
			map.put("faceIdB", "23081052761541557");*/
		    map.put("faceIdA", fList.get(0).getId()+"");
			map.put("faceIdB", fList.get(1).getId()+"");
			map.put("threshold", "0.9");
			map.put("atype", "1");
			map.put("btype", "1");
			
			System.err.println("face A的id是:"+fList.get(0).getId()+" face B的id是  :"+fList.get(1).getId());
						
			ObjectMapper mapper = new ObjectMapper();
			try {
			String jsonStr = mapper.writeValueAsString(map);
			
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GlobalConsts.R_ID_FACE+"/compare").
					header("Authorization", "Bearer " + accessToken).content(jsonStr).contentType(MediaType.APPLICATION_JSON);
			
			MvcResult mvcResult;
		
    		/*mvcResult = this.mockMvc.perform(requestBuilder).andExpect(status().isOk())
						.andExpect(jsonPath("$.data.city").value("深圳")).andReturn();*/
			mvcResult = this.mockMvc.perform(requestBuilder).andReturn();
	
		   String content = mvcResult.getResponse().getContentAsString();
		   System.err.println(content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    }
	
}
