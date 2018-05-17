package intellif.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.core.SwaggerCache;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

import intellif.consts.GlobalConsts;
import intellif.oauth.UserSwaggerCache;

@Configuration
@EnableSwagger
public class SwaggerConfig {

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean //Don't forget the @Bean annotation
    public SwaggerSpringMvcPlugin customImplementation() {
//		  AbsoluteSwaggerPathProvider pathProvider = new AbsoluteSwaggerPathProvider();
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
				.apiInfo(apiInfo())
				// .pathProvider(pathProvider)
				// .includePatterns(".*");
				.includePatterns(GlobalConsts.RESOURCE_ID_BASE + ".*")
				.build();
    }
    
    

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "IntelliFusion Restful API",
                "API for IntelliFusion",
                "IntelliFusion API terms of service",
                "contact@intellif.com",
                "IntelliFusion API Licence Type",
                "IntelliFusion API License URL"
        );
        return apiInfo;
    }

    /**
     * override SwaggerCache bean defined in {@link SpringSwaggerConfig}
     * @return
     */
    @Bean
    @Primary
    public SwaggerCache swaggerCache() {
      return new UserSwaggerCache();
    }
}
