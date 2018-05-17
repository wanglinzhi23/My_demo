package intellif.test.inte;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

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

import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.service.CameraServiceItf;
import intellif.test.util.TestUtil;
import intellif.database.entity.CameraInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")   // starts a server on a random port
@ActiveProfiles("test")
@TestPropertySource(properties = { "isJar = true" })
@Transactional
public class CameraTest {
	
	@Autowired
    private WebApplicationContext wac;
	
	@Autowired
	private Filter springSecurityFilterChain;
	
	@Autowired
	private CameraServiceItf cameraService;
	
	@Value("${local.server.port}")
	int port;
	
	private MockMvc mockMvc;
	
	private static String accessToken;
	
	@Before
    public void setUp() {
		if ("".equals(accessToken) || null == accessToken) {
			accessToken = TestUtil.login("http://192.168.2.92:" + port, "zxd", "123456");
		}
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
    }
	
	@Test
    public void testCreate() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("city", "深圳");
		map.put("county", "龙岗－派出所");
		map.put("addr", "布吉所侯问室");
		map.put("rtspuri", "rtmp://192.168.2.150:5119/live/OfficeDome");
		map.put("uri", "192.168.2.204");
		map.put("status", "1");
		map.put("port", "8000");
		map.put("shortName", "fus");
		map.put("name", "南岭所侯问室");
		map.put("username", "fusrodoh");
		map.put("password", "introcks1234");
		map.put("capability", "5");
		map.put("type", "2");
		map.put("cover", "114.123751,22.609474");
		map.put("stationId", "1");
		map.put("inStation", "0");
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = mapper.writeValueAsString(map);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GlobalConsts.R_ID_CAMERA).
				header("Authorization", "Bearer " + accessToken).content(jsonStr).contentType(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = this.mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.city").value("深圳")).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		System.err.println(content);
    }
	
	@Test
    public void testList() throws Exception {
		cameraService.save(buildTemplateCamera());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GlobalConsts.R_ID_CAMERA).
				header("Authorization", "Bearer " + accessToken);
		
		this.mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].city").value("shenzhen"));
		
    }
	
	private CameraInfo buildTemplateCamera() {
		CameraInfo camera = new CameraInfo();
		camera.setCity("shenzhen");
		camera.setCounty("longgang");
		camera.setAddr("buji");
		camera.setRtspuri("rtmp://192.168.2.150:5119/live/OfficeDome");
		camera.setUri("192.168.2.204");
		camera.setStatus(1);
		camera.setPort(8000);
		camera.setShortName("CF");
		camera.setName("Camera Fox");
		camera.setUsername("Fusrodoh");
		camera.setPassword("pw");
		camera.setCapability(5);
		camera.setType(2);
		camera.setCover("114.123751,22.609474");
		camera.setStationId(1L);
		camera.setInStation(0);
		return camera;
	}
	
}
