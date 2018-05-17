package intellif.service.impl;

import intellif.dao.ServerInfoDao;
import intellif.dao.TaskInfoDao;
import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.IFaceSdkServiceItf;
import intellif.settings.ServerSetting;
import intellif.thrift.IFaceSdkTarget;
import intellif.database.entity.ServerInfo;
import intellif.database.entity.TaskInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yangboz on 12/1/15.
 */
@Service
public class IFaceSdkServiceImpl implements IFaceSdkServiceItf {
    private static Logger LOG = LogManager.getLogger(IFaceSdkServiceImpl.class);
    @Autowired
    private ServerInfoDao serverDao;
    @Autowired
    private TaskInfoDao taskInfoDao;
    @Autowired
    private ServerInfoDao serverInfoDao;

    @Override
    public IFaaServiceThriftClient getTarget(IFaceSdkTypes type) throws TException {
        if (type.equals(IFaceSdkTypes.JNI)) {
            throw new TException("IFaaServiceThriftClient_JNI, target class not found!");
        } else if (type.equals(IFaceSdkTypes.THRIFT)) {
            //Should load-balance to assign the server resources.
            //In this stage,only loop for PEAK value one by one server instance.
            Iterable<ServerInfo> serverInfoList = serverDao.findAll();
            IFaaServiceThriftClient iFaaServiceThriftClient = null;

            for (Iterator<ServerInfo> it = serverInfoList.iterator(); it.hasNext(); ) {
                ServerInfo serverInfo = it.next();
                if((serverInfo.getStatus()==0 && ServerSetting.isEngineStatusOn()) || !(serverInfo.getType() == 0 || serverInfo.getType() == 2)){
               LOG.warn("engine not normal or not exist,severId:"+serverInfo.getId()+",serverPort:"+serverInfo.getPort());
               continue;
               }
                //Find task related server information.
                //TODO:dynamic load-balance-ed IFaaServiceThriftClient implementation.
//                int serverPeak = serverInfo.getPeak();
//                if (0 < serverPeak) {
////                    iFaaServiceThriftClient = new IFaaServiceThriftClient(serverInfo.getIp(), serverInfo.getPort());
                iFaaServiceThriftClient = IFaaServiceThriftClient.getInstance(serverInfo.getIp(), serverInfo.getPort());
                LOG.info("Fixture only-one(load-balance-ed) IFaaServiceThriftClient with ip:" + serverInfo.getIp() + ",with port:" + serverInfo.getPort());
                //Decrease the server peak value.
//                    serverInfo.setPeak(serverPeak--);
//                    serverRepository.save(serverInfo);
                return iFaaServiceThriftClient;
//                }
            }
//            throw new TException("IFaaServiceThriftClient_THRIFT, more server resource required!");
            LOG.warn("IFaaServiceThriftClient_THRIFT, more server resource required!");
        } else {
            throw new UnknownError("Un-know IFaceSdkTarget Error!");
        }
        return null;
    }

    @Override
    public IFaaServiceThriftClient getTarget(IFaceSdkTypes type, long taskId) throws Exception {
        if (type.equals(IFaceSdkTypes.JNI)) {
            throw new TException("IFaaServiceThriftClient_JNI, target class not found!");
        } else if (type.equals(IFaceSdkTypes.THRIFT)) {
            TaskInfo taskInfo = taskInfoDao.findOne(taskId);
            long serverId = taskInfo.getServerId();
            ServerInfo serverInfo = serverDao.findOne(serverId);
            if(null == serverInfo || (serverInfo.getStatus()==0&&ServerSetting.isEngineStatusOn())){
            	 throw new Exception("engine not normal or not exist,severId:"+serverId);
            }
            IFaaServiceThriftClient iFaaServiceThriftClient = IFaaServiceThriftClient.getInstance(serverInfo.getIp(), serverInfo.getPort());
            LOG.warn("IFaaServiceThriftClient_THRIFT, more server resource required!");
            return iFaaServiceThriftClient;
        } else {
            throw new UnknownError("Un-know IFaceSdkTarget Error!");
        }
    }
    //

	@Override
	public List<IFaaServiceThriftClient> getAllTarget() throws TException {
		 Iterable<ServerInfo> serverInfoList = serverDao.findAll();
         IFaaServiceThriftClient iFaaServiceThriftClient = null;
         List<IFaaServiceThriftClient> iFaaServiceThriftClientList = new ArrayList<IFaaServiceThriftClient>();
         
         for (Iterator<ServerInfo> it = serverInfoList.iterator(); it.hasNext(); ) {
             ServerInfo serverInfo = it.next();
             if((serverInfo.getStatus()==1||!ServerSetting.isEngineStatusOn())&&(serverInfo.getType() == 0 || serverInfo.getType() == 2)){
            	 iFaaServiceThriftClient = IFaaServiceThriftClient.getInstance(serverInfo.getIp(), serverInfo.getPort());
            	 iFaaServiceThriftClientList.add(iFaaServiceThriftClient);
             }
         }
         
		return iFaaServiceThriftClientList;
	}

    @Override
    public IFaaServiceThriftClient getCenterServer() throws TException {
        Iterable<ServerInfo> serverInfoList = serverDao.findByType(1);
        IFaaServiceThriftClient iFaaServiceThriftClient = null;

        for (Iterator<ServerInfo> it = serverInfoList.iterator(); it.hasNext(); ) {
            ServerInfo serverInfo = it.next();
            if(serverInfo.getStatus() == 1 || !ServerSetting.isEngineStatusOn()){
                iFaaServiceThriftClient = IFaaServiceThriftClient.getInstance(serverInfo.getIp(), serverInfo.getPort());
                LOG.info("get center engine,ip:"+serverInfo.getIp()+",port:"+serverInfo.getPort());
                break;
            }
        }

        return iFaaServiceThriftClient;
    }
}
