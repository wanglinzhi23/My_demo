package intellif.service.impl;

import java.util.List;

import intellif.dao.TaskInfoDao;
import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.thrift.IFaceSdkTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yangboz on 12/9/15.
 */
@Service
public class IoContrlServiceImpl implements IoContrlServiceItf {

    private static Logger LOG = LogManager.getLogger(IoContrlServiceImpl.class);

    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private TaskInfoDao taskInfoDao;

    @Override
    public int ioContrlWith(int type, long para0, long para1, long para2, long para3) throws TException {
        IFaceSdkTarget target = null;
        target = iFaceSdkServiceItf.getCenterServer();
        LOG.info("IFaceSdkTarget:" + target.toString());
        int result = target.iface_engine_ioctrl(type, para0, para1, para2, para3);
        return result;

        // target.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(),
        // ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), para1,
        // para2, para3);
    }

	@Override
	public int ioContrlWithBatch(int type, long para0, long para1, long para2,
			long para3) throws TException {

        LOG.info("ioContrlWith,type:" + type + ",para0:" + para0 + ",para1:" + para1 + ",para2:" + para2 + ",para3:" + para3);
        long taskId = 0;
        int result = 0;
        if(para0 == ESurveilIoctrlType.SURVEIL_IOCTRL_ADD_PERSON.getValue() || para0 == ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue()) {
        	try {
        		taskId = taskInfoDao.findBySourceId(para3).get(0).getId();
        	} catch (Exception e) {
				LOG.error(para3+"号摄像头的任务不存在！");
				return -1;
			}
        } else if(para0 == ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue()) {
        	taskId = 0;
        }
        
        if(taskId!=0) {
        	IFaceSdkTarget target = null;
        	try{
        		 target = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT, taskId);
        	}catch(Exception e){
        		LOG.error(e.getMessage());
        		return -1;
        	}
            LOG.info("IFaceSdkTarget:" + target.toString());
             result = target.iface_engine_ioctrl(type, para0, para1, para2, para3);
        } else {
        	List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf.getAllTarget();
        	for(IFaceSdkTarget target : targetList) {
                LOG.info("IFaceSdkTarget:" + target.toString());
               result = target.iface_engine_ioctrl(type, para0, para1, para2, para3);
        	}
        }
        return result;
        
//        target.iface_engine_ioctrl(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), para1, para2, para3);
    
	}
}
