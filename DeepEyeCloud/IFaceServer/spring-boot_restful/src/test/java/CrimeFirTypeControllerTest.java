import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.io.ParseException;
import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.CrimeFriTypeDao;
import intellif.database.entity.CrimeFriType;
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

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

/**
 * Created by yangboz on 12/29/15.
 */
//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class CrimeFirTypeControllerTest {
    // 6
    @Value("${local.server.port}")
    int port;

    //    @Autowired
//    PoliceStationDao policeStationDao;
    private CrimeFriType minnie;
    private CrimeFriType pluto;
    private CrimeFriType mickey;
    @Autowired
    private CrimeFriTypeDao repository;

    @Before
    public void setUp() throws ParseException {
        // 7
        mickey = new CrimeFriType();
        mickey.setShortName("mickey");
        minnie = new CrimeFriType();
        minnie.setShortName("minnie");
        pluto = new CrimeFriType();
        pluto.setShortName("pluto");

        // 8
        repository.deleteAll();
        repository.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;

    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_CRIME_FRI_TYPE;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }

    // 10
    @Test
    public void testGet() {
        long mickeyId = mickey.getId();
        String apiPath = getApiPath();
        ValidatableResponse resp = when().
                get(apiPath + "/{id}", mickeyId).
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
                get(getApiPath()).
                then().
                statusCode(HttpStatus.SC_OK);
//                body("name", Matchers.hasItems("Mickey Mouse", "Minnie Mouse", "Pluto"));
    }

    @Test
    public void testDelete() {
        long plutoId = pluto.getId();

        when()
                .delete(getApiPath() + "/{id}", plutoId).
                then().
//                statusCode(HttpStatus.SC_NO_CONTENT);
        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testQueryByName() {
        ValidatableResponse resp = when().
                get(getApiPath() + "/query/{name}", mickey.getShortName()).
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        mickey.setShortName("updated mickey");
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
        CrimeFriType anewMikey = new CrimeFriType();
        mickey.setShortName("anewMickey");
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
