package intellif.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Created by yangboz on 2/18/16.
 *
 * @see: http://docs.spring.io/spring-security/site/docs/3.2.9.RELEASE/reference/htmlsingle/
 * @see: https://github.com/spring-projects/spring-security-javaconfig/blob/master/samples/oauth2-sparklr/src/main/java/org/springframework/security/oauth/examples/sparklr/config/MethodSecurityConfig.java
 */
@Configuration
//@EnableGlobalMethodSecurity(securedEnabled = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

//    @Autowired
//    private WebSecurityConfiguration securityConfig;

    @Bean
    public OAuth2MethodSecurityExpressionHandler oauthExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }

//    @Override
//    protected AuthenticationManager authenticationManager() throws Exception {
//        return securityConfig.authenticationManagerBean();
//    }
}
