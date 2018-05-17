/**
 * Created by yangboz on 11/17/15.
 */

import intellif.Application;
import intellif.dto.Greeting;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class OAuthRestTemplateTest {

    @Value("${local.server.port}")
    protected int port;

    @Test
    public void getAccessToken() {
        OAuth2RestTemplate restTemplate = getoAuth2RestTemplate();
        Assert.assertNotNull(restTemplate.getAccessToken());
        System.out.println(restTemplate.getAccessToken());
    }

    @Ignore
    public void refreshAccessToken() {
        OAuth2RestTemplate restTemplate = getoAuth2RestTemplate();
        Assert.assertNotNull(restTemplate.getResource());
    }

    @Test
    public void getResource() {
        OAuth2RestTemplate restTemplate = getoAuth2RestTemplate();
        Assert.assertNotNull(restTemplate.getResource());
        System.out.println(restTemplate.getResource());
    }

    @Test
    public void clientAccess() {
        OAuth2RestTemplate restTemplate = getoAuth2RestTemplate();

        final Greeting greeting = restTemplate.getForObject(format("http://localhost:%d/api/intellif/greeting", port), Greeting.class);
        Assert.assertNotNull(greeting);
        System.out.println(greeting);
    }

    @Test
    public void getAdditionalInformation() {
        OAuth2RestTemplate restTemplate = getoAuth2RestTemplate();
        Map<String, Object> additionalInformation = restTemplate.getAccessToken().getAdditionalInformation();
//        UserInfo userInfo = (UserInfo) additionalInformation.get(GlobalConsts.OAUTH_A_I_K_USER_INFO);
        Assert.assertNotNull(additionalInformation);
        System.out.println(additionalInformation);
    }

    private OAuth2RestTemplate getoAuth2RestTemplate() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername("a");
        resourceDetails.setPassword("aaaaaa");
        resourceDetails.setAccessTokenUri(format("http://localhost:%d/api/oauth/token", port));
        resourceDetails.setClientId("clientapp");
        resourceDetails.setClientSecret("123456");
        resourceDetails.setGrantType("password");
        resourceDetails.setId("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18");
        resourceDetails.setScope(asList("read", "write"));

        DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(new MappingJackson2HttpMessageConverter()));
        return restTemplate;
    }
}
