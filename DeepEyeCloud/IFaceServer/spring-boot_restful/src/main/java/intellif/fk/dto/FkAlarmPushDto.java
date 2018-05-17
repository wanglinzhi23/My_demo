package intellif.fk.dto;

import java.io.Serializable;


public class FkAlarmPushDto implements Serializable{

    private static final long serialVersionUID = -1588902803798110245L;
    
  
    private long alarmId;
    
    private long personId;
 
    private long faceId;
    
    private String notes;

    public long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(long alarmId) {
        this.alarmId = alarmId;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getFaceId() {
        return faceId;
    }

    public void setFaceId(long faceId) {
        this.faceId = faceId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    
    
}