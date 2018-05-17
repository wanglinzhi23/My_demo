package intellif.fk.dto;


public class FkPersonIcCardDto{

    private static final long serialVersionUID = -1588902803798110245L;
    //反恐人员id
    private long personId;   
    //0-icCard  1-macAddress
    private int operateObject;
    //编号
    private String code;
    //操作类别 0-添加 1-删除
    private int operateType;
    public long getPersonId() {
        return personId;
    }
    public void setPersonId(long personId) {
        this.personId = personId;
    }
    public int getOperateObject() {
        return operateObject;
    }
    public void setOperateObject(int operateObject) {
        this.operateObject = operateObject;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public int getOperateType() {
        return operateType;
    }
    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }
    
   
    
    
    


    
}
