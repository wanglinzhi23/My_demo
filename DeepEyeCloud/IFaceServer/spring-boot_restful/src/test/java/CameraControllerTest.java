import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.io.ParseException;

import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.service.CameraServiceItf;
import intellif.database.entity.CameraInfo;

/**
 * Created by yangboz on 12/29/15.
 */
//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class CameraControllerTest {
    // 6
    @Value("${local.server.port}")
    int port;
    @Autowired
    CameraServiceItf cameraServiceItf;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    //    @Autowired
//    PoliceStationDao policeStationDao;
    private CameraInfo minnie;
    private CameraInfo pluto;
    private CameraInfo mickey;
    @Autowired
    private CameraServiceItf repository;

    @Before
    public void setUp() throws ParseException {
        // 7
        mickey = new CameraInfo();
//        mickey.setId(1);
        mickey.setName("mickey");
        mickey.setAddr("Disney#1");
        mickey.setGeoString("POINT(10 10)");

        minnie = new CameraInfo();
//        minnie.setId(2);
        minnie.setName("minnie");
        minnie.setAddr("Disney#2");
        minnie.setGeoString("POINT(20 20)");

        pluto = new CameraInfo();
//        pluto.setId(3);
        pluto.setName("pluto");
        pluto.setAddr("Disney#3");
        pluto.setGeoString("POINT(30 30)");

        // 8
        //repository.deleteAll();
        //repository.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
        //@see: http://blog.czeczotka.com/2015/01/20/spring-mvc-integration-test-with-rest-assured-and-mockmvc/
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        RestAssuredMockMvc.mockMvc = mockMvc;

    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_CAMERA;
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
        given().pathParam("id", this.mickey.getId()).
                when()
                .delete(getApiPath() + "/{id}").
                then().
//                statusCode(HttpStatus.SC_NO_CONTENT);
        statusCode(HttpStatus.SC_OK);
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetCameraByPersonId() {
        //
        long mickeyId = mickey.getId();

        ArrayList<CameraInfo> cameraInfos = new ArrayList();
        // add elements to the array list
        cameraInfos.add(new CameraInfo());
        //
        Mockito.when(cameraServiceItf.getCameraByPersonId(mickeyId)).thenReturn(cameraInfos);

        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/person/{id}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testAddPersonToStation() {
        long mickeyId = mickey.getId();
        String apiPath = getApiPath();
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        pathParams.put("stationId", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        post(apiPath + "/person/{id}/add/{stationId}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testDelPersonFromStation() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        pathParams.put("stationId", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        delete(getApiPath() + "/person/{id}/del/{stationId}").
                        then().
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
    public void testCreateBadRequest() throws JsonProcessingException, ParseException {
        CameraInfo anewMikey = new CameraInfo();
        mickey.setName("anewMickey");
        mickey.setGeoString("POINT(10 10)");
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
                        statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
