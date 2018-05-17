package intellif.dto;

import java.util.List;
/**
 * 该Dto用于库单位批量授权
 * @author zhanghang
 *
 */
public class AuthorityListDto {
    
    //库Id
    private long bankId;
    //授权的区域集合
    private List<Long> pStationList;
    //授权类型
    private int type;
    
    public long getBankId() {
        return bankId;
    }
    public void setBankId(long bankId) {
        this.bankId = bankId;
    }
    public List<Long> getpStationList() {
        return pStationList;
    }
    public void setpStationList(List<Long> pStationList) {
        this.pStationList = pStationList;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
