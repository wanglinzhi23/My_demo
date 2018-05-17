package intellif.service.impl;

import intellif.dao.ServerInfoDao;
import intellif.service.ServerServiceItf;
import intellif.database.entity.ServerInfo;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerServiceImpl implements ServerServiceItf {

    private static Logger LOG = LogManager
            .getLogger(ServerServiceImpl.class);

    @Autowired
    ServerInfoDao serverDao;

    @Autowired
    ServerInfoDao serverInfoDao;

    @Override
//    public ServerInfo updateStatus(long id, int status)
    public ServerInfo updateStatus(String ipAddr, int status) throws NotFoundException {
//        ServerInfo find = serverRepository.findOne(id);
        List<ServerInfo> serverInfoList = serverDao.findByIp(ipAddr);
        if (serverInfoList.size() > 0) {
            ServerInfo find = serverInfoList.get(0);///XXX:server list load-balance issue.
            LOG.info("find one:" + find.toString());
            find.setStatus(status);
            return serverDao.save(find);
        } else {
            throw new NotFoundException("ServerRepository.findByIp:" + ipAddr + ",not found!");
        }
    }

    @Override
    public ServerInfoDao getDao() {
        return serverInfoDao;
    }

}
