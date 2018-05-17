package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraAndBlackDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

//@Transactional(readOnly = true)
public interface CameraAndBlackDetailDao extends CrudRepository<CameraAndBlackDetail, Long> {
    List<CameraAndBlackDetail> findByCameraId(long cId);

    List<CameraAndBlackDetail> findByBlackdetailId(long bdId);

//    @Modifying
//    @Transactional
//    @Query("delete from User u where u.firstName = ?1")
//	void deleteByCameraIdAndBlackdetailId(long cameraId, long blackdetailId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_BLACKDETAIL+" WHERE camera_id = :cameraId and blackdetail_id=:blackId", nativeQuery = true)
	void deleteByCameraIdAndBlackdetailId(@Param("cameraId") long cameraId, @Param("blackId") long blackId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_BLACKDETAIL+" WHERE blackdetail_id=:blackId", nativeQuery = true)
	void deleteByBlackdetailId(@Param("blackId") long blackId);
    
    @Query(value = "select c.camera_id from "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_BLACKDETAIL+" c LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" d on d.id = c.blackdetail_id LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" p on d.from_person_id = p.id WHERE p.id = :pId GROUP BY c.camera_id", nativeQuery = true)
    List<BigInteger> findCameraIdsByPersonId(@Param("pId") long pId);

    
}
