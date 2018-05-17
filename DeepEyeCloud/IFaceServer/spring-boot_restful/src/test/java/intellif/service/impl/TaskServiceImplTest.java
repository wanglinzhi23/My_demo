package intellif.service.impl;

import com.jayway.restassured.RestAssured;
import intellif.Application;
import intellif.consts.GlobalConsts;
import intellif.dao.TaskInfoDao;
import intellif.service.TaskServiceItf;
import intellif.database.entity.TaskInfo;
import org.junit.After;
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

/**
 * Created by yangboz on 1/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class TaskServiceImplTest {


    ///
    TaskInfo mickey;
    TaskInfo minnie;
    TaskInfo pluto;
    @Autowired   // 5
    private TaskInfoDao repository;
    @Value("${local.server.port}")   // 6
    private int port;

    @Autowired
    private TaskServiceItf serviceItf;

    private String getApiPath() {
        String apiPath = "/api" + GlobalConsts.R_ID_ALARM;
        System.out.println("apiPath:" + apiPath);
        return apiPath;
    }

    @Before
    public void setUp() {
        // 7
        mickey = new TaskInfo();
        mickey.setTaskName("mickey");
        mickey.setServerId(1);//valid id;
        mickey.setSourceId(1);
        mickey.setSourceType(0);
        mickey.setRuleId(1);
        minnie = new TaskInfo();
        minnie.setTaskName("minnie");
        minnie.setServerId(2);//invalid id;
        minnie.setSourceId(1);
        minnie.setSourceType(0);
        minnie.setRuleId(1);
        pluto = new TaskInfo();
        pluto.setTaskName("pluto");
        pluto.setServerId(1);//valid id;
        pluto.setSourceId(1);
        pluto.setSourceType(0);
        pluto.setRuleId(1);
        // 8
        repository.deleteAll();
        repository.save(Arrays.asList(mickey, minnie, pluto));

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
        repository = null;
    }

    @Test
    @Ignore
    public void testFindBySourceTypeAndUri() throws Exception {

    }

    @Test
    @Ignore
    public void testFindByCombinedConditions() throws Exception {

    }

    @Test
    @Ignore
    public void testUpdateStatus() throws Exception {

    }

    @Test
    @Ignore
    public void testFindByStatus() throws Exception {

    }

    @Test
    @Ignore
    public void testRestartTimerTask() throws Exception {

    }

    @Test
    @Ignore
    public void testStopTimerTask() throws Exception {

    }

    @Test
    @Ignore
    public void testFindOne() throws Exception {

    }

    @Test
    @Ignore
    public void testSave() throws Exception {

    }

    @Test
    @Ignore
    public void testGetDao() throws Exception {

    }

    @Test
    @Ignore
    public void testResumeRelevance() throws Exception {

    }

    @Test
    public void testSetup() throws Exception {
//        Assert.assertEquals(-1, serviceItf.setup(mickey));
        org.junit.Assert.assertNotEquals(0, serviceItf.setup(minnie));
    }

    @Test
    @Ignore
    public void testTeardown() throws Exception {

    }

    @Test
    @Ignore
    public void testSetup_snaper() throws Exception {

    }

    @Test
    @Ignore
    public void testTeardown_snaper() throws Exception {

    }
}