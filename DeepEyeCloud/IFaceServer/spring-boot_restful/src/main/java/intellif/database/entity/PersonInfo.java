package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Entity
@Table(name = GlobalConsts.T_NAME_PERSON_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class PersonInfo extends InfoBase implements Serializable {

    private static final long serialVersionUID = -409284155257335672L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 名称
    private String name;
    // 性别
    private int gender;
    //犯罪类型
    private int type;
    //犯罪地址
    private String address;
    //说明
    private String description;
    // 民族
    private String nation;
    //布控ID
    private long taskDeployId;
    private String[] imageIds;

    public PersonInfo() {
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", nation='" + nation + '\'' +
                ", taskDeployId=" + taskDeployId +
                ", imageIds=" + Arrays.toString(imageIds) +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public long getTaskDeployId() {
        return taskDeployId;
    }

    public void setTaskDeployId(long taskDeployId) {
        this.taskDeployId = taskDeployId;
    }

    public String[] getImageIds() {
        return imageIds;
    }

    public void setImageIds(String[] imageIds) {
        this.imageIds = imageIds;
    }
}
