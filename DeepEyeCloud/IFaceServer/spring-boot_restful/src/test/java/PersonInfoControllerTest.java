import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.PersonInfoDao;
import intellif.database.entity.PersonInfo;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

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
public class PersonInfoControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired   // 5
            PersonInfoDao repository;
    PersonInfo mickey;
    PersonInfo minnie;
    PersonInfo pluto;
    @Value("${local.server.port}")   // 6
            int port;
    //
    private RestTemplate restTemplate = new RestTemplate();

    @Before
    public void setUp() {
        // 7
        mickey = new PersonInfo();
        mickey.setGender(0);
        mickey.setName("Mickey");
        minnie = new PersonInfo();
        minnie.setGender(1);
        minnie.setName("Minnie");
        pluto = new PersonInfo();
        pluto.setGender(0);
        pluto.setName("Pluto");
        // 8
        repository.deleteAll();
        repository.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_PERSON_INFO;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }

    // 10
    @Test
    public void testGet() {
        long mickeyId = mickey.getId();

        ValidatableResponse resp =
                when().
                        get("/api/intellif/person/info/{id}", mickeyId).
                        then().
                        statusCode(HttpStatus.SC_OK);
//                .body("data",Matchers.notNullValue());
//                body("name", Matchers.is("Mickey Mouse")).
//                body("id", Matchers.is(mickeyId));
//                System.out.println(resp.toString());
    }

    @Test
    public void testList() {
        when().
                get("/api/intellif/person/info").
                then().
                statusCode(HttpStatus.SC_OK);
//                body("name", Matchers.hasItems("Mickey Mouse", "Minnie Mouse", "Pluto"));
    }

    @Test
    public void testDelete() {
        long plutoId = pluto.getId();

        when()
                .delete("/api/intellif/person/info/{id}", plutoId).
                then().
//                statusCode(HttpStatus.SC_NO_CONTENT);
        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        mickey.setName("updated mickey");
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
        PersonInfo anewMikey = new PersonInfo();
        mickey.setName("anewMickey");
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