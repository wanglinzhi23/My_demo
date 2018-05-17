package intellif.facecollision.dao;

import intellif.consts.GlobalConsts;
import intellif.facecollision.vo.FaceExtractTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Zheng Xiaodong
 */
public interface FaceExtractTaskDao extends JpaRepository<FaceExtractTask, Long> {

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_FACE_EXTRACT_TASK+" where file_id=:fileId limit 0,1 ",nativeQuery=true)
    public FaceExtractTask findByFileId(@Param("fileId") long fileId);

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_FACE_EXTRACT_TASK+" where file_id=:fileId and deleted=:deleted limit 0,1 ",nativeQuery=true)
    public FaceExtractTask findByFileIdAndDeleted(@Param("fileId") long fileId, @Param("deleted") boolean deleted);
}
