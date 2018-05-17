package intellif.jobs;

import intellif.service.FaceStreamServiceItf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Zheng Xiaodong
 */
@Component
public class FaceStreamJob {
    @Autowired
    private FaceStreamServiceItf faceStreamService;

    @Scheduled(fixedRate = 1000)
    public void calcFaceStream() {
        faceStreamService.calcFaceStream();
    }
}
