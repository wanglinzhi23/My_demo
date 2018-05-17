package intellif.database.entity;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 */
public class ResumableInfoStorage {

    private static ResumableInfoStorage sInstance;
    
    private HashMap<String, UploadedStatus> mMap = new HashMap<String, UploadedStatus>();
    
    private BlockingQueue<UploadedStatus> statusQueue=new LinkedBlockingQueue<>();
    
    private BlockingQueue<UploadedFile> fileQueue=new LinkedBlockingQueue<>();

    public static synchronized ResumableInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResumableInfoStorage();
        }
        return sInstance;
    }

	public HashMap<String, UploadedStatus> getmMap() {
		return mMap;
	}

	public void setmMap(HashMap<String, UploadedStatus> mMap) {
		this.mMap = mMap;
	}

    public BlockingQueue<UploadedStatus> getStatusQueue() {
        return statusQueue;
    }

    public void setStatusQueue(BlockingQueue<UploadedStatus> statusQueue) {
        this.statusQueue = statusQueue;
    }

    public BlockingQueue<UploadedFile> getFileQueue() {
        return fileQueue;
    }

    public void setFileQueue(BlockingQueue<UploadedFile> fileQueue) {
        this.fileQueue = fileQueue;
    }


}
