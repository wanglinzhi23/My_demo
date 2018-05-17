/**
 *
 */
package intellif.service.impl;

import intellif.enums.MqttTopicNames;
import intellif.mqtt.EventBusHelper;
import intellif.mqtt.IfMqttClient;
import intellif.mqtt.MqttAlarmEBSubscriber;
import intellif.mqtt.MqttEngRptEBSubscriber;
import intellif.service.AlarmServiceItf;
import intellif.service.MqttMessageServiceItf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class MqttMessageServiceImpl.
 *
 * @author yangboz
 */
@Service
public class MqttMessageServiceImpl implements MqttMessageServiceItf {

    //
    private static Logger LOG = LogManager.getLogger(MqttMessageServiceImpl.class);
    //
    @Autowired
    private AlarmServiceItf alarmService;

//    @Autowired
//    private TaskServiceItf taskServiceItf;
//
//    @Autowired
//    private ServerServiceItf serverServiceItf;

    /*
     * (non-Javadoc)
     *
     * @see intellif.service.MqttMessageServiceItf#setup(java.lang.String)
     */
    @Override
    public void setup(String topicName) {
        setupMqttMessaging(topicName);
    }

    //
    private EventBusHelper eventBusHelper;
    // Message testing,@see: http://www.eclipse.org/paho/clients/java/
    // private static IfMqttClient client = null;
    private IfMqttClient mqttClient;
    @Autowired
    private MqttAlarmEBSubscriber mqttAlarmEBSubscriber;

    private void setupMqttMessaging(String topicName) {
        if (topicName == MqttTopicNames.AlarmInfo.getValue()) {
            try {
                //
                mqttClient = new IfMqttClient();
                eventBusHelper = new EventBusHelper();
                mqttAlarmEBSubscriber.setAlarmServiceItf(alarmService);
                eventBusHelper.registerSubscriber(mqttAlarmEBSubscriber);
                //
                mqttClient.setEventBusHelper(eventBusHelper);
                mqttClient.connectAndSubscribe(topicName);
            } catch (Exception e) {
                // e.printStackTrace();
                LOG.error(e.toString());
            }
        }
//        else if (topicName == MqttTopicNames.EngineReport.getValue()) {
//            try {
//                //
//                mqttClient = new IfMqttClient();
//                eventBusHelper = new EventBusHelper();
//                mqttEngRptEBSubscriber = new MqttEngRptEBSubscriber(); //
////                mqttEngRptEBSubscriber.setTaskServiceItf(this.taskServiceItf);
////                mqttEngRptEBSubscriber.setServerServiceItf(this.serverServiceItf);
//                eventBusHelper.registerSubscriber(mqttEngRptEBSubscriber);
//                //
//                mqttClient.setEventBusHelper(eventBusHelper);
//                mqttClient.connectAndSubscribe(topicName);
//            } catch (Exception e) {
//                // e.printStackTrace();
//                LOG.error(e.toString());
//            }
//        }
    }

}
