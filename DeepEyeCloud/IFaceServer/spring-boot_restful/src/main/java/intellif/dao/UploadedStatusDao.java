package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UploadedStatus;

public interface UploadedStatusDao extends CrudRepository<UploadedStatus, Long> {

	@Query(value="SELECT * FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where user_id = :userId and is_deleted=0 ",nativeQuery=true)
	List<UploadedStatus> findByUserId(@Param("userId")Long userId);

	@Query(value="SELECT * FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and resumable_identifier = :resumableIdentifier  limit 0,1",nativeQuery=true)
    UploadedStatus findByIdentifier(@Param("resumableIdentifier")String resumableIdentifier);

    @Query(value="SELECT * FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and user_id = :userId and is_finished=:isFinished ",nativeQuery=true)
    List<UploadedStatus> findFileByUserAndStatus(@Param("userId")Long userId,@Param("isFinished")Integer isFinished);

    @Query(value="select count(*) from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and file_id=:fileId and progress=100 ",nativeQuery=true)
	int findUploadedImgCountByUploadFileId(@Param("fileId")Long fileId);

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and file_id=:fileId",nativeQuery=true)
	List<UploadedStatus> findByFileId(@Param("fileId")Long fileId);

	@Query(value="SELECT * FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and resumable_identifier = :resumableIdentifier and upload_identifier=:uploadIdentifier  limit 0,1",nativeQuery=true)
	UploadedStatus findByIdentifierAndUploadIdentifier(@Param("resumableIdentifier")String resumableIdentifier, @Param("uploadIdentifier")String uploadIdentifier);

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and upload_identifier=:uploadIdentifier and user_id=:userId ",nativeQuery=true)
    List<UploadedStatus> findByUploadIdentifierAndUser(@Param("uploadIdentifier")String uploadIdentifier,@Param("userId") long userId);
    
    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and upload_identifier=:uploadIdentifier limit 0,1 ",nativeQuery=true)
    UploadedStatus findByUploadIdentifierLimitOne(@Param("uploadIdentifier")String uploadIdentifier);

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=0 and file_id=:fileId limit 0,1",nativeQuery=true)
    UploadedStatus findByFileIdLimitOne(@Param("fileId") long fileId);

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_STATUS+" where is_deleted=1 ",nativeQuery=true)
    List<UploadedStatus> findAllDeletedUploadedStatus();
}
