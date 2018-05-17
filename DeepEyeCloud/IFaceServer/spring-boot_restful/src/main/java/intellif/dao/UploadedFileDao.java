package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UploadedFile;

public interface UploadedFileDao extends CrudRepository<UploadedFile, Long> {

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" where is_deleted=1 ",nativeQuery=true)
    List<UploadedFile> findAllDeletedUploadedFile();

    @Query(value="select * from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_UPLOADED_FILE+" where is_deleted=0 and status="+GlobalConsts.FILE_FINISHED,nativeQuery=true)
    List<UploadedFile> findUnCreatedTaskFile();

}