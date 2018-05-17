/**
 * Created by yangboz on 11/27/15.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.service.ImageServiceItf;
import intellif.database.entity.ImageInfo;
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

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ImageControllerTest {
    private static final String CHECKED_FIELD = "checked";
    private static final String DESCRIPTION_FIELD = "data";
    private static final String ITEMS_RESOURCE = "/api/intellif/image";
    private static final String ITEM_RESOURCE = "/api/intellif/image/{id}";
    private static final int NON_EXISTING_ID = 999;
    private static final String FIRST_ITEM_DESCRIPTION = "First item";
    private static final String SECOND_ITEM_DESCRIPTION = "Second item";
    private static final String THIRD_ITEM_DESCRIPTION = "Third item";
    private static final ImageInfo FIRST_ITEM = new ImageInfo();

    private static final ImageInfo SECOND_ITEM = new ImageInfo();

    private static final ImageInfo THIRD_ITEM = new ImageInfo();

    @Autowired
    private ImageServiceItf imageServiceItf;
    @Value("${local.server.port}")
    private int serverPort;
    private ImageInfo firstItem;
    private ImageInfo secondItem;


    private ImageInfo minnie;
    private ImageInfo pluto;
    private ImageInfo mickey;

    @Before
    public void setUp() {
     //   imageServiceItf.deleteAll();
     //   firstItem = imageServiceItf.save(FIRST_ITEM);
     //   secondItem = imageServiceItf.save(SECOND_ITEM);
        RestAssured.port = serverPort;
        //
        mickey = new ImageInfo();
        mickey.setUri("mickey");
        minnie = new ImageInfo();
        minnie.setUri("minnie");
        pluto = new ImageInfo();
        pluto.setUri("pluto");
       // imageServiceItf.save(Arrays.asList(mickey, minnie, pluto));
    }

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_IMAGE;
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


    //DAO access null value NPE issue.
    @Test
    public void testUpdate() throws JsonProcessingException {
        mickey.setUri("updated mickey");
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String imageInfoJsonStr = mapper.writeValueAsString(mickey);
        System.out.println("imageInfoJsonStr:" + imageInfoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        pathParam("id", mickey.getId()).
                        body(imageInfoJsonStr).
                        when().
                        put(getApiPath() + "/{id}").
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        ImageInfo anewMikey = new ImageInfo();
        mickey.setUri("anewMickey");
        //Object to JSON in String
        ObjectMapper mapper = new ObjectMapper();
        String imageInfoJsonStr = mapper.writeValueAsString(anewMikey);
        System.out.println("imageInfoJsonStr:" + imageInfoJsonStr);
        //
        ValidatableResponse resp =
                given().accept(ContentType.JSON).
                        contentType(ContentType.JSON).
                        body(imageInfoJsonStr).
                        when().
                        post(getApiPath()).
                        then().
                        statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Ignore
    public void getItemsShouldReturnBothItems() {
        when()
                .get(ITEMS_RESOURCE)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(DESCRIPTION_FIELD, hasItems(FIRST_ITEM_DESCRIPTION, SECOND_ITEM_DESCRIPTION))
                .body(CHECKED_FIELD, hasItems(true, false));
    }

    @Test
    @Ignore
    public void addItemShouldReturnSavedItem() {
        given()
                .body(THIRD_ITEM)
                .contentType(ContentType.JSON)
                .when()
                .post(ITEMS_RESOURCE)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(DESCRIPTION_FIELD, is(THIRD_ITEM_DESCRIPTION))
                .body(CHECKED_FIELD, is(false));
    }

    @Test
    @Ignore
    public void addItemShouldReturnBadRequestWithoutBody() {
        when()
                .post(ITEMS_RESOURCE)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Ignore
    public void addItemShouldReturnNotSupportedMediaTypeIfNonJSON() {
        given()
                .body(THIRD_ITEM)
                .when()
                .post(ITEMS_RESOURCE)
                .then()
                .statusCode(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    @Ignore
    public void updateItemShouldReturnUpdatedItem() {
        // Given an unchecked first item
        ImageInfo item = new ImageInfo();

        given()
                .body(item)
                .contentType(ContentType.JSON)
                .when()
                .put(ITEM_RESOURCE, firstItem.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(DESCRIPTION_FIELD, is(FIRST_ITEM_DESCRIPTION))
                .body(CHECKED_FIELD, is(false));
    }

    @Test
    @Ignore
    public void updateItemShouldReturnBadRequestWithoutBody() {
        when()
                .put(ITEM_RESOURCE, firstItem.getId())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Ignore
    public void updateItemShouldReturnNotSupportedMediaTypeIfNonJSON() {
        given()
                .body(FIRST_ITEM)
                .when()
                .put(ITEM_RESOURCE, firstItem.getId())
                .then()
                .statusCode(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    @Ignore
    public void updateItemShouldBeBadRequestIfNonExistingID() {
        given()
                .body(FIRST_ITEM)
                .contentType(ContentType.JSON)
                .when()
                .put(ITEM_RESOURCE, NON_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Ignore
    public void deleteItemShouldReturnNoContent() {
        when()
                .delete(ITEM_RESOURCE, secondItem.getId())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Ignore
    public void deleteItemShouldBeBadRequestIfNonExistingID() {
        when()
                .delete(ITEM_RESOURCE, NON_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

}