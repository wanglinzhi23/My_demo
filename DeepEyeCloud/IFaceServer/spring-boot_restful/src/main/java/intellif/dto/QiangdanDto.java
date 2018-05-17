package intellif.dto;


public class QiangdanDto {
    private String policeNo;
    private String cameraLens;
    private String littlePhotoPath;
    private String bigPhotoPath;
    private String validateTime;
    
    public QiangdanDto(String policeNo,String cameraId,String faceUrl,String imageUrl,String date){
        this.policeNo = policeNo;
        this.cameraLens = cameraId;
        this.littlePhotoPath = faceUrl;
        this.bigPhotoPath = imageUrl;
        this.validateTime = date;
    }
    public String getPoliceNo() {
        return policeNo;
    }
    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }
  
    public String getCameraLens() {
        return cameraLens;
    }
    public void setCameraLens(String cameraLens) {
        this.cameraLens = cameraLens;
    }
    public String getLittlePhotoPath() {
        return littlePhotoPath;
    }
    public void setLittlePhotoPath(String littlePhotoPath) {
        this.littlePhotoPath = littlePhotoPath;
    }
    public String getBigPhotoPath() {
        return bigPhotoPath;
    }
    public void setBigPhotoPath(String bigPhotoPath) {
        this.bigPhotoPath = bigPhotoPath;
    }
    public String getValidateTime() {
        return validateTime;
    }
    public void setValidateTime(String validateTime) {
        this.validateTime = validateTime;
    }
  

}
