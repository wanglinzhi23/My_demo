import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dto.AlarmInfoDto;
import intellif.dto.PersonQueryDto;
import intellif.enums.AlarmStatus;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.ImageInfo;

//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
//@ContextConfiguration(locations = {"/application-default.properties"})
public class AlarmControllerTest {

    @Autowired   // 5
            AlarmInfoDao alarmInfoDao;
    AlarmInfo mickey;
    AlarmInfo minnie;
    AlarmInfo pluto;
    @Value("${local.server.port}")   // 6
            int port;

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_ALARM;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }

    @Before
    public void setUp() {
        // 7
        mickey = new AlarmInfo();
        mickey.setBlackId(1);
        mickey.setConfidence(100);
        mickey.setFaceId(1);
        mickey.setLevel(1);
        mickey.setStatus(1);
        mickey.setTaskId(1);
        mickey.setTime(new Date());
        minnie = new AlarmInfo();
        minnie.setBlackId(1);
        minnie.setConfidence(100);
        minnie.setFaceId(1);
        minnie.setLevel(1);
        minnie.setStatus(1);
        minnie.setTaskId(1);
        minnie.setTime(new Date());
        pluto = new AlarmInfo();
        pluto.setBlackId(1);
        pluto.setConfidence(100);
        pluto.setFaceId(1);
        pluto.setLevel(1);
        pluto.setStatus(1);
        pluto.setTaskId(1);
        pluto.setTime(new Date());

        // 8
        alarmInfoDao.deleteAll();
        alarmInfoDao.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
    }

    // 10
    @Test
    public void testGet() {
        long mickeyId = mickey.getId();

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/{id}", mickeyId).
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
                get(getApiPath() + "").
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
    public void testUpdate() throws JsonProcessingException {
        mickey.setFaceId(11);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String alarmInfoJsonStr = mapper.writeValueAsString(mickey);
        System.out.println("alarmInfoJsonStr:" + alarmInfoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParam("id", mickey.getId()).
                        body(alarmInfoJsonStr).
                        when().
                        put(getApiPath() + "/{id}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        ImageInfo anewMikey = new ImageInfo();
        mickey.setFaceId(22);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String alarmInfoJsonStr = mapper.writeValueAsString(anewMikey);
        System.out.println("alarmInfoJsonStr:" + alarmInfoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(alarmInfoJsonStr).
                        when().
                        post(getApiPath()).
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    //DAO access null value NPE issue.
    @Ignore
    @Test(expected = NullPointerException.class)
    public void testGetByBlackIdNPE() {

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/black/{id}", 999999999).
                        then().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
//                .body("data",Matchers.notNullValue());
//                body("name", Matchers.is("Mickey Mouse")).
//                body("id", Matchers.is(mickeyId));
//                System.out.println(resp.toString());
    }

    @Ignore
    @Test
    public void testGetByBlackIdOK() {

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/black/{id}", 1).
                        then().
                        statusCode(HttpStatus.SC_OK);
//                .body("data",Matchers.notNullValue());
//                body("name", Matchers.is("Mickey Mouse")).
//                body("id", Matchers.is(mickeyId));
//                System.out.println(resp.toString());
    }

    //Statistics/Events related
    @Test
    public void testGetByBlackDetailIdOK() {

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/blackdetail/{id}", 1).
                        then().
                        statusCode(HttpStatus.SC_OK);
//                .body("data",Matchers.notNullValue());
//                body("name", Matchers.is("Mickey Mouse")).
//                body("id", Matchers.is(mickeyId));
//                System.out.println(resp.toString());
    }

    @Test
    public void testSearchOK() throws JsonProcessingException {
        AlarmInfoDto alarmInfoDto = new AlarmInfoDto();
        alarmInfoDto.setAlarmStatus(AlarmStatus.VALID.getValue());
        alarmInfoDto.setConfidence(50);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String alarmInfoDtoJsonStr = mapper.writeValueAsString(alarmInfoDto);
        System.out.println("alarmInfoDtoJsonStr:" + alarmInfoDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(alarmInfoDtoJsonStr).
                        when().
                        post(getApiPath() + "/search").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testSearch415() {
        AlarmInfoDto alarmInfoDto = new AlarmInfoDto();
        alarmInfoDto.setAlarmStatus(AlarmStatus.VALID.getValue());
        alarmInfoDto.setConfidence(50);
        //
        ValidatableResponse resp =
                given().
                        body(alarmInfoDto).
                        when().
                        post(getApiPath() + "/search").
                        then().
                        statusCode(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testStatistic() {

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/statistic/").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testStatisticStation() {

        ValidatableResponse resp =
                when().
                        get(getApiPath() + "/statistic/station").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Ignore
    @Test
    public void testByStatus() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        pathParams.put("value", AlarmStatus.VALID.getValue());
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        put(getApiPath() + "/{id}/status/{value}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testByStation() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        pathParams.put("page", 10);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/station/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testByCamera() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("ids", "1");
        pathParams.put("page", 10);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/camera/{ids}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test(expected = NullPointerException.class)
    @Ignore
    public void testFindEventsByPersonId() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        pathParams.put("page", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/person/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testByBlack() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/black/{id}").
                        then().
                        statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testByQuery() throws JsonProcessingException {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("page", 1);
        PersonQueryDto personQueryDto = new PersonQueryDto();
        personQueryDto.setCid("1");
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String personQueryDtoJsonStr = mapper.writeValueAsString(personQueryDto);
        System.out.println("personQueryDtoJsonStr:" + personQueryDtoJsonStr);

        ValidatableResponse resp =
                given().pathParameters(pathParams).accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(personQueryDtoJsonStr).
                        when().
                        post(getApiPath() + "/query/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }
}
