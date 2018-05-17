import com.jayway.restassured.RestAssured;
import intellif.Application;
import intellif.service.IoContrlServiceItf;
import org.apache.thrift.TException;
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

/**
 * Created by yangboz on 1/1/16.
 */
//@see: http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class IoCtrlServiceTest {

    @Value("${local.server.port}")   // 6
            int port;

//    ENGIN_IOCTRL_TRACE(1953653091),
//    ENGIN_IOCTRL_DUMP(1691706480),
//    ENGIN_IOCTRL_IOCTRL(1668575852),
//    ENGIN_IOCTRL_SURVEIL(1937076854);

    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;

    @Before
    public void setUp() {
        // 7

        // 9
        RestAssured.port = port;
//        RestAssured.port = 8082;
    }

    @Test
    @Ignore
    public void testIoCtrlTRACE() throws TException {
//        Assert.assertNotNull(ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), 1, 0, 0));
    }

    @Test
    @Ignore
    public void testIoCtrlDUMP() throws TException {
//        Assert.assertNotNull(ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), 1, 0, 0));
    }

    @Test
    @Ignore
    public void testIoCtrlIOCTRL() throws TException {
//        Assert.assertNotNull(ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), 1, 0, 0));
    }

    @Test
    @Ignore
    public void testIoCtrlSURVEIL() throws TException {
//        Assert.assertNotNull(ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), 1, 0, 0));
    }
}
