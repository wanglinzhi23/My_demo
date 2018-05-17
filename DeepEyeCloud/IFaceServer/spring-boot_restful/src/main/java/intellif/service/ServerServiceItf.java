package intellif.service;

import intellif.dao.ServerInfoDao;
import intellif.database.entity.ServerInfo;
import javassist.NotFoundException;

public interface ServerServiceItf {

    //    public ServerInfo updateStatus(long id, int status);
    public ServerInfo updateStatus(String ipAddr, int status) throws NotFoundException;

    public ServerInfoDao getDao();

}
