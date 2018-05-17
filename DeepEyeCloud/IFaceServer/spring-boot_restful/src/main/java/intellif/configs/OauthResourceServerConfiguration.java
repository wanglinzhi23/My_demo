package intellif.configs;

import intellif.dao.AllowIpRangeDao;
import intellif.dao.ApiResourceDao;
import intellif.dao.AuditLogDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserApiLimitDao;
import intellif.dao.UserDao;
import intellif.database.dao.OauthResourceDao;
import intellif.oauth.APIAccessControlAuthenticator;
import intellif.service.RoleServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by yangboz on 11/18/15.
 */
@Configuration
@EnableResourceServer
public class OauthResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final Logger LOG = LogManager.getLogger(OauthResourceServerConfiguration.class);

    public static String RESOURCE_IDs_ALL = "";

    @Autowired
//    @Qualifier("roleServiceItf")
    private RoleServiceItf roleServiceItf;

    private APIAccessControlAuthenticator apiAccessControlAuthenticator = new APIAccessControlAuthenticator();
    
    @Autowired
    private OauthResourceDao oauthResourceRepository;

	@Autowired
	private UserDao userRepository;
	
	@Autowired
	private ApiResourceDao resourceRepository;
	
	@Autowired
	private PoliceStationDao policestationDao;
	@Autowired
   	private  AuditLogDao auditLogRepository;
	
	@Autowired
	private RequestMappingHandlerMapping apiMapping;
	
	@Autowired
	private UserApiLimitDao limitiRepository;
	
	@Autowired
	private AllowIpRangeDao ipRangeDao;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // @formatter:off
    	// @formatter:on
        //register authenticator into OAuth2AuthenticationProcessingFilter
    	apiAccessControlAuthenticator.setPoliceStationDao(policestationDao);
    	apiAccessControlAuthenticator.setAuditLogRepository(auditLogRepository);
        apiAccessControlAuthenticator.setResourceRepository(resourceRepository);
        apiAccessControlAuthenticator.setUserRepository(userRepository);
        apiAccessControlAuthenticator.setApiMapping(apiMapping);
        apiAccessControlAuthenticator.setLimitiRepository(limitiRepository);
        apiAccessControlAuthenticator.setIpRangeDao(ipRangeDao);
        resources.authenticationManager(apiAccessControlAuthenticator);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
		http.authorizeRequests()  
		.antMatchers("/intellif/red/detail/check/result/**").permitAll()
		.antMatchers("/intellif/dataExport/**").permitAll()// 这个白名单的url需要规划
        .and()
        .authorizeRequests()			
		.antMatchers("/intellif/**").authenticated().and()
		.addFilterAfter(apiAccessControlAuthenticator, AbstractPreAuthenticatedProcessingFilter.class);

        // @formatter:on
    }

   
}
