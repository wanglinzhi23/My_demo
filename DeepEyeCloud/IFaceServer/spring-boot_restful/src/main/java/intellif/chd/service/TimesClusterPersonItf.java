package intellif.chd.service;

import java.util.List;

import intellif.chd.dto.FaceQuery;
import intellif.chd.vo.TimesPerson;

public interface TimesClusterPersonItf {

	List<TimesPerson> parseClusterPerson(FaceQuery faceQuery);

	void start(FaceQuery faceQuery);

	void personContrast(List<TimesPerson> timesPersons, FaceQuery faceQuery);

}
