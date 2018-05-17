package intellif.service;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import intellif.dto.JsonObject;
import intellif.dto.UploadedStatusDto;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;

public interface ResumableJsServiceItf {

	public boolean checkIfUploadFinished(UploadedStatus info);

	public UploadedFile uploadFinished(UploadedStatus info,HttpServletResponse response);

	public JsonObject resumableUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	public JsonObject checkifUploaded(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

	public JsonObject uploadingFiles();

	public UploadedStatus getResumableUploadedStatus(UploadedStatusDto statusDto);

	public File createFile(String pathname, String suffix);

	public JsonObject findFileById(Long id);

	public UploadedFile uploading(UploadedStatus info);

    public JsonObject uploadCancle(String uploadIdentifier);

    /**
     * ��������
     * @param statusQueue
     */
    public void updateTableBatch();

}
