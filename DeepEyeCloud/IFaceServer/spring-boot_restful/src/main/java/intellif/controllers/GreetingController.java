/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UserInfo;
import intellif.dto.Greeting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>The Class GreetingController just for testing.</h1>
 * The GreetingController which serves request of the form /greeting and returns a JSON object representing an instance of Greeting.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc.
 * (see <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and static data storages),
 * while REST is a very-high-level API style (mostly for webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-09-31
 */
@RestController
//@RequestMapping("/intellif/greeting")
@RequestMapping(GlobalConsts.R_ID_GREETING)
public class GreetingController {

    private static final String template = "Hello, %s!";
    private static Logger LOG = LogManager.getLogger(GreetingController.class);
    private final AtomicLong counter = new AtomicLong();

    //    @RequestMapping("/intellif/greeting")
    @PreAuthorize("#oauth2.clientHasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a greeting object.")
    public Greeting greeting(@AuthenticationPrincipal UserInfo userInfo, OAuth2Authentication authentication) {
        LOG.info("OAuth2Authentication:" + authentication.toString());
        return new Greeting(counter.incrementAndGet(),
                String.format(template, getPrincipal()));
    }

    //@see: http://docs.spring.io/spring-security/site/docs/3.2.5.RELEASE/reference/htmlsingle/#el-access
//    @PreAuthorize("hasRole('USER')")
//    @PostAuthorize("hasRole('USER')")
//    @PreAuthorize("isAnonymous()")
//    @PreAuthorize("isAuthenticated()")
//    @RolesAllowed({"USER"})
//    @RolesAllowed({"ADMIN"})
    //@see: https://github.com/spring-projects/spring-security-javaconfig/blob/master/samples/oauth2-sparklr/src/main/java/org/springframework/security/oauth/examples/sparklr/mvc/PhotoController.java
//    @PreAuthorize(GlobalConsts.OAUTH_C_H_R_USER)
//    @PreAuthorize("#oauth2.hasAnyRole({'USER','ADMIN','SUPER_ADMIN'})")
    @PreAuthorize("hasAnyAuthority({'ADMIN','USER'})")
    @RequestMapping(method = RequestMethod.GET, value = "/*")
    public Greeting greeting() {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, "@PreAuthorized:" + getPrincipal()));
    }

    private String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            System.out.println("getAuthorities:" + ((UserDetails) principal).getAuthorities().toString());
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

}
