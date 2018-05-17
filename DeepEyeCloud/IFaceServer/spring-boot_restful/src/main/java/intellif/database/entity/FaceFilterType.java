package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;

/**
 * 非人脸图片过滤类型
 * @author Zheng Xiaodong
 */
@Entity
@Table(name= GlobalConsts.T_NAME_FACE_FILTER_TYPE, schema=GlobalConsts.INTELLIF_FACE)
public class FaceFilterType extends InfoBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FilterType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
