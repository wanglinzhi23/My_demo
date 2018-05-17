package intellif.oauth;

import intellif.dao.ApiResourceDao;
import intellif.dao.UserDao;
import intellif.database.entity.ApiResourceInfo;
import intellif.database.entity.UserInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mangofactory.swagger.core.SwaggerCache;
import com.mangofactory.swagger.models.dto.ApiDescription;
import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.Operation;
import com.mangofactory.swagger.models.dto.builder.ApiListingBuilder;

@Component
public class UserSwaggerCache extends SwaggerCache{


    @Autowired
    private UserDao userRepository;
    
    @Autowired
    private ApiResourceDao resourceRepository;
    
    @Override
    public Map<String, Map<String, ApiListing>> getSwaggerApiListingMap() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo userInfo = userRepository.findByLogin(authentication.getName());
        Long roleId = userInfo.getRoleId();
        if (roleId <= 0) {
            noResourceFound(authentication.getName());
            return null;
        }
        
        List<ApiResourceInfo> apis = resourceRepository.findResources(roleId);
        if (apis == null || apis.isEmpty()) {
            noResourceFound(authentication.getName());
            return null;
        }
        
        Map<String, Map<String, ApiListing>> allMaps = super.getSwaggerApiListingMap();
        Map<String, Map<String, ApiListing>> userMaps = new HashMap<>();
        ApiListingBuilder builder = new ApiListingBuilder();
        
        for (Map.Entry<String, Map<String, ApiListing>> map : allMaps.entrySet()) {
            Map<String, ApiListing> apiListingMap = map.getValue();
            String swaggerName = map.getKey();
            Map<String, ApiListing> userApiMap = new HashMap<>(); 
            
            for (Map.Entry<String, ApiListing> entry: apiListingMap.entrySet()) {
               
                String groupName = entry.getKey();
                ApiListing apiListing = entry.getValue();
                List<ApiDescription> apiDeses = apiListing.getApis();
                
                for (ApiDescription description: apiDeses) {
                    String path = description.getPath();
                    ApiDescription newDescription = null;

                    for (Operation operation : description.getOperations()) {
                       String method = operation.getMethod();
                       for (ApiResourceInfo apiInfo : apis) {
                           if (apiInfo.getUri().equals(path) && apiInfo.getHttpMethod().equals(method)) {
                               
                               if (newDescription == null) {
                                   newDescription = initialApiDescription(userApiMap, groupName, builder, apiListing, description, false);
                               }
                               
                               newDescription.getOperations().add(operation);
                           }
                       }
                    }
                }
            }
            
            if (!userApiMap.isEmpty()) {
                userMaps.put(swaggerName, userApiMap);
            }
        }
        
        
        return userMaps;
    }
    
    private void noResourceFound(String userName)  {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getResponse();
        try {
            response.sendError(404, "No any api for user ["+userName+"] in swagger");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private ApiDescription initialApiDescription(Map<String, ApiListing> userApiMap, String groupName, 
            ApiListingBuilder builder, ApiListing apiListing, 
            ApiDescription description, boolean addWholeApis) {
        
        ApiListing newApiList =  userApiMap.get(groupName);
        
        if (newApiList == null) {
            newApiList = builder.apiVersion(apiListing.getApiVersion())
                    .basePath(apiListing.getBasePath())
                    .description(apiListing.getDescription())
                    .position(apiListing.getPosition())
                    .protocol(apiListing.getProtocol())
                    .resourcePath(apiListing.getResourcePath())
                    .authorizations(apiListing.getAuthorizations())
                    .consumes(apiListing.getConsumes())
                    .models(apiListing.getModels())
                    .produces(apiListing.getProduces())
                    .swaggerVersion(apiListing.getSwaggerVersion())
                    .apis(new ArrayList<ApiDescription>())
                    .build();
            userApiMap.put(groupName, newApiList);
        }
        
        if (addWholeApis) {
            newApiList.getApis().add(description);
            return null;
        } else {
            ApiDescription   newDescription = new ApiDescription(description.getPath(), description.getDescription(), 
                    new ArrayList<Operation>(), description.isHidden());
            newApiList.getApis().add(newDescription);
            return newDescription;
        }
        
       
    }
}
