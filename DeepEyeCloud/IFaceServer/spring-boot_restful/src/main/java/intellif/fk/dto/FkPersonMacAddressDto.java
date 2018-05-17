package intellif.fk.dto;

public class FkPersonMacAddressDto {

    private static final long serialVersionUID = -1588902803798110245L;
   
    //反恐人员id
    private long personId;
    //要添加的mac地址
    private String macAddress;
    public long getPersonId() {
        return personId;
    }
    public void setPersonId(long personId) {
        this.personId = personId;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
}
