package intellif.service;


import intellif.dto.FaceStreamRequest;

import java.util.Set;


/**
 * @author Zheng Xiaodong
 */
public interface FaceStreamServiceItf {
    long getRealTimeCount(FaceStreamRequest request);

    void updateRealTimeCount(Long venueId, String startTime, Long count);

    Long queryFaceStreamCount(Long venueId, String startTime);

    void calcFaceStream();
    
    long getRealTimeCountByStartTimeAndEndTime(FaceStreamRequest request);

    long getFaceStreamCount(FaceStreamRequest request);
}
