package intellif.database.entity;

import intellif.annotation.MultiTablePrefix;
import intellif.consts.GlobalConsts;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = GlobalConsts.T_NAME_POLICE_CASE,schema=GlobalConsts.INTELLIF_STATIC)
public class PoliceCase implements Serializable,Cloneable{

    /**
     * 
     */
    private static final long serialVersionUID = -6509929760916063129L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    private long id;
    //案件类型
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
