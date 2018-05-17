package intellif;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@EnableAutoConfiguration
@EnableAdminServer
@PropertySources({@PropertySource(value = "classpath:application-${spring.profiles.active}.properties")})
public class SpringMvcSwaggerOctoNinjaAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMvcSwaggerOctoNinjaAdminApplication.class, args);
    }
}
