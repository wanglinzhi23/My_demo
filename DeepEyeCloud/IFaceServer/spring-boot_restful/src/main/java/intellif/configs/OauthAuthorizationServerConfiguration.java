package intellif.configs;

import intellif.dao.OauthClientDetailsDao;
import intellif.oauth.OauthTokenEnhancer;
import intellif.service.impl.UserDetailsServiceImpl;
import intellif.database.entity.OauthClientDetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

import java.util.Iterator;

/**
 * Created by yangboz on 11/18/15.
 */
@Configuration
@EnableAuthorizationServer
public class OauthAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static final Logger LOG = LogManager.getLogger(OauthAuthorizationServerConfiguration.class);
    private static int token_expire_in ;   //24 * 60 * 60 * 1000 ; //默认24小时;
    private static int token_invalidate_time ; // 24 * 60 * 60 * 1000 ; //默认24小时;

    @Autowired
    private DataSource dataSource;
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    //        private TokenStore tokenStore = new InMemoryTokenStore();
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private OauthClientDetailsDao oauthClientDetailsRepository;
//    private ClientDetailsService clientDetailsService;

    
    public static int getToken_expire_in() {
		return token_expire_in;
	}

	public static void setToken_expire_in(int token_expire_in) {
		OauthAuthorizationServerConfiguration.token_expire_in = token_expire_in;
	}

	public static int getToken_invalidate_time() {
		return token_invalidate_time;
	}

	public static void setToken_invalidate_time(int token_invalidate_time) {
		OauthAuthorizationServerConfiguration.token_invalidate_time = token_invalidate_time;
	}
	
    
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        // @formatter:off
        endpoints
                .tokenStore(this.tokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(userDetailsService);
        // @formatter:on
        endpoints.tokenEnhancer(tokenEnhancer());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off
        clients.jdbc(dataSource);
        // @formatter:on
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
    	DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(this.tokenStore());
        ///@see: http://stackoverflow.com/questions/28492116/can-i-include-user-information-while-issuing-an-access-token
        tokenServices.setTokenEnhancer(tokenEnhancer());
        
      //  tokenServices.setAccessTokenValiditySeconds(60 * 60 * 24 * 2);      // 修改access失效时间 为2天  之前默认是12小时
        tokenServices.setAccessTokenValiditySeconds(token_invalidate_time); 
        
        return tokenServices;
    }

//    @Bean
//    @Primary
//    public AuthorizationServerTokenServices tokenServices() {
////        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        DefaultTokenServices tokenServices = defaultTokenServices();
//        // ...
//        tokenServices.setTokenEnhancer(tokenEnhancer());
//        return tokenServices;
//    }

    // Some @Bean here like tokenStore
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new OauthTokenEnhancer();
    }

}
