import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.ServerInfoDao;
import intellif.database.entity.ServerInfo;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

/**
 * Created by yangboz on 11/27/15.
 */
//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class ServerControllerTest {

    @Autowired   // 5
            ServerInfoDao repository;

    ServerInfo mickey;
    ServerInfo minnie;
    ServerInfo pluto;

    @Value("${local.server.port}")   // 6
            int port;

    @Before
    public void setUp() {
        // 7
        mickey = new ServerInfo();
        mickey.setIp("192.168.2.7");
        mickey.setPort(5787);
        minnie = new ServerInfo();
        minnie.setIp("192.168.2.8");
        minnie.setPort(5788);
        pluto = new ServerInfo();
        pluto.setIp("192.168.2.9");
        pluto.setPort(5789);
        // 8
        repository.deleteAll();
        repository.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_SERVER;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }

    // 10
    @Test
    public void testGet() {
        long mickeyId = mickey.getId();

        ValidatableResponse resp =
                when().
                        get("/api/intellif/server/{id}", mickeyId).
                        then().
                        statusCode(HttpStatus.SC_OK)
//                .body("data",Matchers.notNullValue());
                        .body("ip", Matchers.is("192.168.2.7"));
//                body("id", Matchers.is(mickeyId));
//                System.out.println(resp.toString());
    }

    @Test
    public void testList() {
        when().
                get("/api/intellif/server").
                then().
                statusCode(HttpStatus.SC_OK);
//                body("name", Matchers.hasItems("Mickey Mouse", "Minnie Mouse", "Pluto"));
    }

    @Test
    public void testDelete() {
        long plutoId = pluto.getId();

        when()
                .delete("/api/intellif/server/{id}", plutoId).
                then().
//                statusCode(HttpStatus.SC_NO_CONTENT);
        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        mickey.setAddress("updated mickey");
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String updateJsonStr = mapper.writeValueAsString(mickey);
        System.out.println("updated jsonStr:" + updateJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParam("id", mickey.getId()).
                        body(updateJsonStr).
                        when().
                        put(getApiPath() + "/{id}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        ServerInfo anewMikey = new ServerInfo();
        mickey.setAddress("anewMickey");
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String updateJsonStr = mapper.writeValueAsString(anewMikey);
        System.out.println("updated jsonStr:" + updateJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(updateJsonStr).
                        when().
                        post(getApiPath()).
                        then().
                        statusCode(HttpStatus.SC_OK);
    }
}
