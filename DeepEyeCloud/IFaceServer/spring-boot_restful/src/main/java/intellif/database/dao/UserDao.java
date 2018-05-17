package intellif.database.dao;
import intellif.database.entity.UserInfo;
import intellif.dto.UserDto;

import java.util.List;

public interface UserDao<T> extends CommonDao<T>{
    public UserInfo findOneUserWithAdditionalInfo(long id);
    public  List<UserDto> findAllBySuperAdmin(String userName);
    public  List<UserDto> findBySuperAdmin(long stationid,String username);
    public  List<UserDto> findByAdmin(long stationid,String username);
}
