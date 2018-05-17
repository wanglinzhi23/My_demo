//package intellif.oauth;
//
//import static org.junit.Assert.assertNotNull;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.FormHttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.http.converter.xml.SourceHttpMessageConverter;
//import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.client.token.AccessTokenRequest;
//import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
//import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
///**
// * need to start server
// * @author simon_zhang
// *
// */
//public class OAuth2TokenGrantTests {
//
//	private OAuth2AccessToken existingToken;
//
//	private ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
//
//	OAuth2RestTemplate template;
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Before
//	public void setup() {
//		resourceDetails.setUsername("xinyitech");
//		resourceDetails.setPassword("xinyi666888");
//		// http://192.168.2.220:8082/api/intellif/
//		resourceDetails.setAccessTokenUri("http://127.0.0.1:8082/api/oauth/token");
//		// resourceDetails.setAccessTokenUri("http://192.168.2.7:8083/api/oauth/token");
//		resourceDetails.setClientId("clientapp");
//		resourceDetails.setClientSecret("123456");
//		resourceDetails.setGrantType("password");
//		resourceDetails.setScope(Arrays.asList("read", "write"));
//		DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
//		template = new OAuth2RestTemplate(resourceDetails, clientContext);
//
//		List messageConverters = new ArrayList();
//		messageConverters.add(new SourceHttpMessageConverter());
//		messageConverters.add(new FormHttpMessageConverter());
//		messageConverters.add(new MappingJackson2HttpMessageConverter());
//		template.setMessageConverters(messageConverters);
//		existingToken = template.getAccessToken();
//		// ((DefaultOAuth2AccessToken) existingToken).setExpiration(new
//		// Date(0L));
//
//		// SecurityContextImpl securityContext = new SecurityContextImpl();
//		// securityContext.setAuthentication(new
//		// TestingAuthenticationToken("intellif", "renlianxitong", "USER"));
//		// SecurityContextHolder.setContext(securityContext);
//
//	}
//
//	@Test
//	public void testConnectDirectlyToResourceServer() throws Exception {
//
//		AccessTokenRequest request = new DefaultAccessTokenRequest();
//		request.setExistingToken(existingToken);
//
////		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(request));
//		//unauthorized resource
//		//Object result = template.getForObject("http://127.0.0.1:8082/api/intellif/task", Object.class);
//		//authorized 113
//		Object result = null;
//		String jsonObject = "{\"id\": 0,\"faceId\": 0,\"scoreThreshold\": 0, \"type\": 0,\"ids\": \"string\",\"mergeType\": 0,\"dataType\": 0,\"starttime\": \"string\",\"endtime\": \"string\"}";
//		SearchFaceResultDto searchFaceDto = new SearchFaceResultDto();
//		for (int index = 0; index < 10; index++) {
//			result = template.postForObject("http://localhost:8082/api/intellif/face/search/statistic",jsonObject, Object.class);
//		}
//		File img = new File("/Users/simon_zhang/intellif_code/IFaceServer/spring-boot_restful/src/test/java/intellif/oauth/20160401T103732_349367.jpg");
//        
////		Map<String, Object> map = new HashMap<String, Object>();
////		map.put("face", Boolean.TRUE);
////		map.put("file", img);
////		MultipartFile  multipartFile = null;
////		multipart/form-data
//		
//		Resource resource = new FileSystemResource(
//	            "/Users/simon_zhang/intellif_code/IFaceServer/spring-boot_restful/src/test/java/intellif/oauth/20160401T103732_349367.jpg");
//    	MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
//    	parts.add("Content-Type",  MediaType.MULTIPART_FORM_DATA_VALUE);
//    	parts.add("file", resource);
//	
//		HttpEntity<MultiValueMap<String, Object>> entity =  new HttpEntity<MultiValueMap<String, Object>>(parts);
//
//		result = template.exchange("http://localhost:8082/api/intellif/image/upload/true", HttpMethod.POST, entity, Object.class);
//		
//		assertNotNull(result);
//		
//		System.out.println(result.toString());
//	}
//
//}
