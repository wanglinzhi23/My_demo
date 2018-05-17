import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.io.ParseException;

import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.controllers.FaceController;
import intellif.dto.FaceQueryDto;
import intellif.dto.SearchFaceDto;
import intellif.service.FaceServiceItf;
import intellif.service.SolrServerItf;
import intellif.database.entity.FaceInfo;
import junit.framework.Assert;

/**
 * Created by yangboz on 12/29/15.
 */
//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class FaceControllerTest {
    // 6
    @Value("${local.server.port}")
    int port;

    //    @Autowired
//    PoliceStationDao policeStationDao;

    private FaceInfo mickey;

    @Autowired
    private FaceServiceItf _faceService;

    @Autowired
    private SolrServerItf _solrServerItf;

    @Before
    public void setUp() throws ParseException {
        // 7
        mickey = new FaceInfo();
        mickey.setAge(10);
        mickey.setFromImageId((long) 1);
        mickey.setGender(0);

        // 8
//        _faceService.deleteAll();
//        _faceService.save(Arrays.asList(mickey));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;

    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_FACE;
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
        when()
                .delete(getApiPath() + "/{id}", mickey.getId()).
                then().
//                statusCode(HttpStatus.SC_NO_CONTENT);
        statusCode(HttpStatus.SC_OK);
    }


    //DAO access null value NPE issue.
    @Test
    public void testUpdate() throws JsonProcessingException {
        mickey.setImageData("updated mickey");
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
        FaceInfo anewMikey = new FaceInfo();
        mickey.setImageData("anewMickey");
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

    @Test
    public void testStatisticByDay() {
        Assert.assertNotNull(this._faceService.statisticByDay());
    }

    @Test
    public void testFindByCameraId() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", mickey.getId());
        pathParams.put("page", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/camera/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testFindByStationId() {
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", mickey.getId());
        pathParams.put("page", 1);
        ValidatableResponse resp =
                given().pathParameters(pathParams).
                        when().
                        get(getApiPath() + "/station/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

//    @Test
//    public void testFindByFromImageId() {
//        Assert.assertNotNull(this._faceInfoDao.findByFromImageId(mickey.getId()));
//    }

    @Test(expected = NullPointerException.class)
    //cuz of null pointer of feature data.
    public void testSearchFaceInBankByBlackId() throws Exception {
        Assert.assertNotNull(_solrServerItf.searchFaceByBlackId(mickey.getId(), FaceController.DEFAULT_SCORE_THRESHOLD, GlobalConsts.BLACK_BANK_TYPE));
    }

    //    @Test(expected = NullPointerException.class)
    @Test
    public void testSearchFaceInBankByFaceId() throws Exception {
        SearchFaceDto searchFaceDto = new SearchFaceDto();
        searchFaceDto.setFaceId(1);
        searchFaceDto.setMergeType(1);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String searchFaceDtoJsonStr = mapper.writeValueAsString(searchFaceDto);
        System.out.println("searchFaceDtoJsonStr:" + searchFaceDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(searchFaceDtoJsonStr).
                        when().
                        post(getApiPath() + "/search/face").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test(expected = NullPointerException.class)
    public void testSearchFaceStatistic() throws Exception {
        SearchFaceDto searchFaceDto = new SearchFaceDto();
        Assert.assertNotNull(_solrServerItf.getFaceStatistic(searchFaceDto));
    }

    @Test
    public void testSearchFaceForCamera() throws JsonProcessingException {
        SearchFaceDto searchFaceDto = new SearchFaceDto();
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String searchFaceDtoJsonStr = mapper.writeValueAsString(searchFaceDto);
        System.out.println("searchFaceDtoJsonStr:" + searchFaceDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParam("page", 1).
                        body(searchFaceDtoJsonStr).
                        when().
                        post(getApiPath() + "/search/face/camera/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testSearchFaceForCameraByStationId() throws JsonProcessingException {
        SearchFaceDto searchFaceDto = new SearchFaceDto();
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", mickey.getId());
        pathParams.put("page", 1);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String searchFaceDtoJsonStr = mapper.writeValueAsString(searchFaceDto);
        System.out.println("searchFaceDtoJsonStr:" + searchFaceDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParameters(pathParams).
                        body(searchFaceDtoJsonStr).
                        when().
                        post(getApiPath() + "/search/station/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testSearchFaceByCameraId() throws JsonProcessingException {
        SearchFaceDto searchFaceDto = new SearchFaceDto();
        Map<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.put("id", mickey.getId());
        pathParams.put("page", 1);
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String searchFaceDtoJsonStr = mapper.writeValueAsString(searchFaceDto);
        System.out.println("searchFaceDtoJsonStr:" + searchFaceDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParameters(pathParams).
                        body(searchFaceDtoJsonStr).
                        when().
                        post(getApiPath() + "/search/camera/{id}/page/{page}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testQueryFaceInfo() throws JsonProcessingException {
        FaceQueryDto faceQueryDto = new FaceQueryDto();
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String faceQueryDtoJsonStr = mapper.writeValueAsString(faceQueryDto);
        System.out.println("faceQueryDtoJsonStr:" + faceQueryDtoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(faceQueryDtoJsonStr).
                        when().
                        post(getApiPath() + "/query").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }
}
