package intellif.thrift;

import intellif.ifaas.IFaaService;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.ifaas.T_IF_FACERECT;
import intellif.ifaas.T_IF_TASK_INFO;
import intellif.utils.DateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class StreamMediaThriftClient {

    private Logger LOG = LogManager.getLogger(StreamMediaThriftClient.class);

    private static Map<String, StreamMediaThriftClient> instanceMap = new HashMap<String, StreamMediaThriftClient>();
    private TTransport transport;
    private TProtocol protocol;
    private StreamMediaThrift.Client rawClient;
    private StreamMediaThrift.Iface recClient;

    protected StreamMediaThriftClient(String ip, int port) throws TException {
        //		trans_ep = new TSocket("192.168.2.3", 9090);
    	
    	
    	
        transport = new TFramedTransport(new TSocket(ip, port));
    	//使用高密度二进制协议
    	 protocol = new TCompactProtocol(transport);
        rawClient = new StreamMediaThrift.Client(protocol);
        recClient = ReconnectingThriftClient.wrap(rawClient, StreamMediaThrift.Iface.class);
        recClient = rawClient;
        LOG.info("TSocket isOpen?" + transport.isOpen());

    }

    //    private static TServiceClient rawClient;
    //@see: http://crunchify.com/thread-safe-and-a-fast-singleton-implementation-in-java/
    public synchronized static StreamMediaThriftClient getInstance(String ip, int port) throws TException {
    	if (!instanceMap.containsKey(ip+":"+port)) {
    		// Thread Safe. Might be costly operation in some case
    		StreamMediaThriftClient instance = new StreamMediaThriftClient(ip, port);
    		instanceMap.put(ip+":"+port, instance);
    	}
        return instanceMap.get(ip+":"+port);
    }

  

    @Override
    public String toString() {
        return "StreamMediaThriftClient{}";
    }

   

    public synchronized String processgetPlaybackLive(String cameraId, long start, long end) {
        LOG.info("process get playbacklive  begin!");
      String result = "";
        try {
            //first off,open the transport.
            if (!transport.isOpen()) {
            	transport.open();
            }
            LOG.info("trans_ep.isOpen!");
            String startTime = DateUtil.getformatDate(start);
            String stopTime = DateUtil.getformatDate(end);
            result = recClient.StartPlaybackLive(cameraId, startTime, stopTime);
            LOG.info("processTaskSurveillanceCreate oneway!");
        } catch (TException e) {
            LOG.error(e.toString());
        }
        LOG.info("processTaskSurveillanceCreate end!");
        //Close the transport
        transport.close();
        return result;
    }
    
    public synchronized String processGetLive(String cameraId){
    	LOG.info("process get live begin!");
    	String result = "";
    	try {
			if(!transport.isOpen()){
				transport.open();
			}
			LOG.info("trans_ep is open!");
			result = recClient.StartLive(cameraId);
			LOG.info("processTaskSurveillanceCreate oneway!");
		} catch (Exception e) {
			LOG.error(e.toString());
		}
    	LOG.info("processTaskSurveillanceCreate end!");
    	transport.close();
    	return result;
    }

}
