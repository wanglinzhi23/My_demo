package intellif.ifaas;

import intellif.thrift.IFaceSdkTarget;
import intellif.thrift.ReconnectingThriftClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangboz
 * @see http://mvnrepository.com/artifact/org.apache.thrift/libthrift/0.9.2
 * @see http://people.apache.org/~jfarrell/thrift/0.6.1/javadoc/org/apache/thrift/TSerializer.html
 * @see http://www.eff-yeah.com/thrift/
 */
public class IFaaServiceThriftClient implements IFaceSdkTarget {

    private Logger LOG = LogManager.getLogger(IFaaServiceThriftClient.class);

    private static Map<String, IFaaServiceThriftClient> instanceMap = new HashMap<String, IFaaServiceThriftClient>();
    private TSocket trans_ep;
    private TBinaryProtocol protocol;
    private IFaaService.Client rawClient;
    private IFaaService.Iface recClient;

    protected IFaaServiceThriftClient(String ip, int port) throws TException {
        //		trans_ep = new TSocket("192.168.2.3", 9090);
        trans_ep = new TSocket(ip, port);
        trans_ep.setTimeout(6000);
        protocol = new TBinaryProtocol(trans_ep);
        // Message.Client recClient = new Message.Client(protocol);
        rawClient = new IFaaService.Client(protocol);
        //@see:http://liveramp.com/engineering/reconnecting-thrift-client/
        recClient = ReconnectingThriftClient.wrap(rawClient, IFaaService.Iface.class);
        recClient = rawClient;
        LOG.info("TSocket isOpen?" + trans_ep.isOpen());
//        if (!trans_ep.isOpen()) {
//            trans_ep.open();
//        }
    }

    //    private static TServiceClient rawClient;
    //@see: http://crunchify.com/thread-safe-and-a-fast-singleton-implementation-in-java/
    public synchronized static IFaaServiceThriftClient getInstance(String ip, int port) throws TException {
    	if (!instanceMap.containsKey(ip+":"+port)) {
    		// Thread Safe. Might be costly operation in some case
    		IFaaServiceThriftClient instance = new IFaaServiceThriftClient(ip, port);
    		instanceMap.put(ip+":"+port, instance);
    	}
        return instanceMap.get(ip+":"+port);
    }

    // IFaceSdkTarget interfaces;
    public synchronized List<T_IF_FACERECT> processFaceDetectExtract(String input, long bdId, int extType) {
        List<T_IF_FACERECT> faceRectFeats = null;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            faceRectFeats = recClient.face_detect_extract(input, bdId, extType);
        } catch (Exception e) {
//			e.printStackTrace();
            LOG.error("face detect error:",e);
            return null;                           ///解决下面的faceRectFeats空指针的隐患
        }
        LOG.info("testingFaceDetectExtract begin!");
        for (int i = 0; i < faceRectFeats.size(); i++) {
            LOG.info("testingFaceDetectExtract result(Confidence):" + String.valueOf(faceRectFeats.get(i).Rect));
            LOG.info("testingFaceDetectExtract result(Pose):" + faceRectFeats.get(i).Pose.toString());
            LOG.info("testingFaceDetectExtract result(Rect):" + faceRectFeats.get(i).Rect.toString());
//			LOG.info("testingFaceDetectExtract result(Feature):"+faceRectFeats.get(i).FaceFeature);
        }
        LOG.info("testingFaceDetectExtract end!");
//Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return faceRectFeats;
    }

    @Override
    public String toString() {
        return "IFaaServiceThriftClient{}";
    }

    public synchronized double processFaceFeatureVerify(String img1, String img2) {
        LOG.info("processFaceFeatureVerify begin!");
        double result = 0;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            result = recClient.face_feature_verify(img1, img2);
            LOG.info("processFaceFeatureVerify result:" + result);
        } catch (Exception e) {
            LOG.error("face feature verify error:",e);
        }
        LOG.info("processFaceFeatureVerify end!");
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;
    }

    public synchronized int processTaskSurveillanceCreate(T_IF_TASK_INFO info) {
        LOG.info("processTaskSurveillanceCreate begin!");
        int result = -1;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            LOG.info("trans_ep.isOpen!");
            result = recClient.task_surveillance_create(info);
            LOG.info("processTaskSurveillanceCreate oneway!");
        } catch (Exception e) {
            LOG.error("Task surveillance create error:",e);
        }
        LOG.info("processTaskSurveillanceCreate end!");
        //Close the transport
        trans_ep.close();
        return result;
    }

    //	public boolean processTaskSurveillanceTerminate(T_IF_TASK_INFO info)
    public synchronized int processTaskSurveillanceTerminate(long tId) {
        LOG.info("processTaskSurveillanceTerminate begin!");
        int result = -1;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            result = recClient.task_surveillance_terminate(tId);
            LOG.info("processTaskSurveillanceTerminate result:" + result);
        } catch (Exception e) {
            LOG.error("Task surveillance terminate error:",e);
        }
        LOG.info("processTaskSurveillanceTerminate end!");
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;

    }

    public synchronized int iface_engine_ioctrl(int type, long para0, long para1, long para2, long para3) {
        LOG.info("iface_engine_ioctrl begin!");
        int result = -1;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            result = recClient.iface_engin_ioctrl(type, para0, para1, para2, para3);
            LOG.info("iface_engine_ioctrl result:" + result +"socket:"+trans_ep.getSocket());
        } catch (Exception e) {
            LOG.error("connect to c++ server error socket:"+trans_ep.getSocket()+" e:",e);
            
        }
        LOG.info("iface_engine_ioctrl end!");
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;
    }

    public synchronized List<T_IF_FACERECT> image_detect_extract(String inputImgName, long fromImgId) {
        LOG.info("image_detect_extract begin! fromImgId:" + fromImgId);
        List<T_IF_FACERECT> result = null;
        for(int i=0;i<3;i++){
        	try {
        		//first off,open the transport.
        		if (!trans_ep.isOpen()) {
        			trans_ep.open();
        		}
        		result = recClient.image_detect_extract(inputImgName, fromImgId);
        		LOG.info("image_detect_extract result:" + result);
        		break;
        	} catch (Exception e) {
                LOG.error("image detect extract try times: "+i+" error:",e);
        		continue;
        	}
        }
        LOG.info("image_detect_extract end! fromImgId:" + fromImgId);
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;
    }

    public synchronized int task_snaper_create(int sourceType, long sourceId) {
        LOG.info("task_snaper_create begin!");
        int result = -1;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            result = recClient.task_snaper_create(sourceType, sourceId);
            LOG.info("task_snaper_create result:" + result);
        } catch (Exception e) {
            LOG.error("task snaper create error:",e);
        }
        LOG.info("task_snaper_create end!");
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;
    }

    public synchronized int task_snaper_terminate(int sourceType, long sourceId) {
        LOG.info("task_snaper_terminate begin!");
        int result = -1;
        try {
            //first off,open the transport.
            if (!trans_ep.isOpen()) {
                trans_ep.open();
            }
            result = recClient.task_snaper_terminate(sourceType, sourceId);
            LOG.info("task_snaper_terminate result:" + result);
        } catch (Exception e) {
            LOG.error("task snaper terminate error:",e);
        }
        LOG.info("task_snaper_terminate end!");
        //Close the transport
        if (trans_ep.isOpen()) {
            trans_ep.close();
        }
        return result;
    }

	@Override
	public synchronized String face_detect_rect(String inputImgName, long feceId) {
		LOG.info("face_detect_rect begin!");
		String result = "";
		try {
			if (!trans_ep.isOpen()) {
				trans_ep.open();
			}
			result = recClient.face_detect_rect(inputImgName, feceId);
			LOG.info("face_detect_rect result : " + result);
		} catch (Exception e) {
            LOG.error("face detect rect error:",e);
		}
		LOG.info("face_detect_rect end !");
		//close the transport
		if (trans_ep.isOpen()) {
			trans_ep.close();
		}
		return result;
	}

	@Override
	public synchronized T_ProgressQueryRst getFeatureUpdateState(int type,int userId) {
		LOG.info("get feature update schedule begin!");
		T_ProgressQueryRst result = null;
		try {
			if (!trans_ep.isOpen()) {
				trans_ep.open();
			}
			result = recClient.query_prgress_rate(type, userId);
			LOG.info("feature update schedule : " + result);
		} catch (Exception e) {
            LOG.error("get feature update state error:",e);
		}
		LOG.info("get feature update schedule end!");
		//close the transport
		if (trans_ep.isOpen()) {
			trans_ep.close();
		}
		return result;
	}

	@Override
	public synchronized T_MulAlgFeatureExtRsp queryMultipFeature(T_MulAlgFeatureExtReq tMulFeatureExtPara) {
		LOG.info("query multip feature begin!");
		T_MulAlgFeatureExtRsp result = null;
		try {
			if (!trans_ep.isOpen()) {
				trans_ep.open();
			}
			result = recClient.multip_feature_query(tMulFeatureExtPara);
			LOG.info("query multip feature count : " + result.getFeatureCnt());
		} catch (Exception e) {
            LOG.error("query multip feature error:",e);
		}
		LOG.info("query multip feature end!");
		//close the transport
		if (trans_ep.isOpen()) {
			trans_ep.close();
		}
		return result;
	}
}