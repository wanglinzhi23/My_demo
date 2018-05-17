package intellif.enums;

/**
 * Created by Zheng Xiaodong on 2017/5/2.
 * @author Zheng Xiaodong
 */
public enum RoleTypes {
    SUPER_ADMIN("SUPER_ADMIN"),
    MIDDLE_ADMIN("MIDDLE_ADMIN"),
    ADMIN("ADMIN"),
    USER("USER"),
    GUEST("GUEST");

    private String name;

    RoleTypes(String name) {
        this.name = name;
    }

    public static RoleTypes fromName(String name) {
        for (RoleTypes t : RoleTypes.values()) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public String getCnName() {
        String cnName = "";
        switch (this) {
            case SUPER_ADMIN:
                cnName = "超级管理员";
                break;
            case MIDDLE_ADMIN:
                cnName = "中级管理员";
                break;
            case ADMIN:
                cnName = "初级管理员";
                break;
            case USER:
                cnName = "操作账号";
                break;
            case GUEST:
                cnName = "查询账号";
                break;
        }
        return cnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoleTypes{" +
                "name='" + name + '\'' +
                '}';
    }
}
