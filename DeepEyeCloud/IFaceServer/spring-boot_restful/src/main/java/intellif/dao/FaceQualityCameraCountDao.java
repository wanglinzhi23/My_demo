package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dto.FaceStatisticDto;
import intellif.database.entity.FaceCameraCount;
import intellif.database.entity.FaceQualityCameraCount;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FaceQualityCameraCountDao extends CrudRepository<FaceQualityCameraCount, Long> {

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_QUALITY_CAMERA_COUNT+"  WHERE time = :date", nativeQuery = true)
	List<FaceQualityCameraCount> findByTime(@Param("date") String date);


}
