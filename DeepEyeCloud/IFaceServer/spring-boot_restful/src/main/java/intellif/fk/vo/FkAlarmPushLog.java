package intellif.fk.vo;

import intellif.consts.GlobalConsts;
import intellif.fk.dto.FkPersonDto;
import intellif.database.entity.InfoBase;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = GlobalConsts.T_NAME_FK_ALARM_PUSH_LOG,schema=GlobalConsts.INTELLIF_BASE)
public class FkAlarmPushLog implements Serializable {

    private static final long serialVersionUID = -409284155257335672L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String image;
    
    private String notes;
    
    private String user;
 
    private Date time;
    
    private String result;

    public FkAlarmPushLog() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    

  
}
