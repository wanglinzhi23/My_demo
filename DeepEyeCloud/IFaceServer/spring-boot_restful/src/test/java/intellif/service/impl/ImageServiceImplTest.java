package intellif.service.impl;

import intellif.Application;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by yangboz on 1/24/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class ImageServiceImplTest {
    @Autowired
    private ImageServiceItf imageServiceItf;
    @Autowired
    private FaceServiceItf faceServiceItf;

    private long face_id;

    @Before
    public void setUp() {
        face_id = 1;
    }

    @After
    public void tearDown() {
        face_id = 0;
    }

    @Test(expected = RuntimeException.class)
    public void testFindOneByFaceId() throws Exception {
       // ImageInfo imageInfo = imageServiceItf.findOneByFaceId(this.face_id);
    	FaceInfo faceInfo = faceServiceItf.findOne(this.face_id);
    	ImageInfo imageInfo = imageServiceItf.findById(faceInfo.getFromImageId());
        Assert.assertNotNull(imageInfo);
    }
}