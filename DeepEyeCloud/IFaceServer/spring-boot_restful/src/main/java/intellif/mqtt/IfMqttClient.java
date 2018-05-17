/**
 *
 */
package intellif.mqtt;

import intellif.events.MqttMessageEvent;
import intellif.settings.MqttSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author yangboz
 * @see <a href="http://docs.spring.io/spring-integration/reference/html/mqtt.html">http://docs.spring.io/spring-integration/reference/html/mqtt.html</a>
 * @see <a href="https://github.com/chinesejie/paho-for-android/blob/master/src/ChinesejieClient.java">https://github.com/chinesejie/paho-for-android/blob/master/src/ChinesejieClient.java</a>
 */
//@Component
//@Scope(value = "singleton")
public class IfMqttClient {

    // Message testing,@see: http://www.eclipse.org/paho/clients/java/
    public static MqttClient client = null;

    //	private static IfMqttClient instance = null;
    private static Logger LOG = LogManager.getLogger(IfMqttClient.class);
    private EventBusHelper eventBusHelper;

    public IfMqttClient() throws MqttException {
        client = new MqttClient(MqttSettings.getUri(),
                MqttClient.generateClientId(), new MemoryPersistence());
        //
//		LOG.info("alarmServiceItf:"+alarmServiceItf.toString());
//		LOG.info("eventBusService:"+eventBusService.toString());
    }
    
    public static MqttClient getClient() {
  		return client;
  	}

    public EventBusHelper getEventBusHelper() {
        return eventBusHelper;
    }

//	public static IfMqttClient getInstance() {
//		if (instance == null) {
//			try {
//				instance = new IfMqttClient();
//			} catch (MqttException e) {
//				// e.printStackTrace();
//				LOG.error(e.toString());
//			}
//		}
//		return instance;
//	}

    public void setEventBusHelper(EventBusHelper eventBusHelper) {
        this.eventBusHelper = eventBusHelper;
    }

    // public IfMqttClient(String serverURI, String clientId,
    // MqttClientPersistence persistence) throws MqttException {
    // }

    public void publish(String topic, MqttMessage message) throws MqttException {
        client.publish(topic, message);
    }
    
    public void connect() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(0);
        try {
            client.setCallback(new MqttCallback() {
                //
                @Override
                public void connectionLost(Throwable cause) {
                    LOG.info("connectionLost-----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LOG.info("deliveryComplete-----------" + token.isComplete());
                }

                @Override
                public void messageArrived(String topic, MqttMessage arg1)
                        throws Exception {
                    LOG.info("messageArrived-----------Topic: " + topic
                            + "  Message: " + arg1.toString());
                    getEventBusHelper().postEvent(new MqttMessageEvent(arg1.toString()));
                }
            });

            client.connect(options);
        } catch (Exception e) {
            // e.printStackTrace();
            LOG.error(e.toString());
        }

    }
    
    public void connectAndSubscribe(String topic) {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        // options.setUserName(userName);
        // options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(0);
        try {
            client.setCallback(new MqttCallback() {
                //
                @Override
                public void connectionLost(Throwable cause) {
                    LOG.info("connectionLost-----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LOG.info("deliveryComplete-----------" + token.isComplete());
                }

                @Override
                public void messageArrived(String topic, MqttMessage arg1)
                        throws Exception {
                    LOG.info("messageArrived-----------Topic: " + topic
                            + "  Message: " + arg1.toString());
                    //
                    getEventBusHelper().postEvent(new MqttMessageEvent(arg1.toString()));
//					if (GlobalConsts.MQTT_TOPIC_ALARM.equals(topic)) {
//						//Post the message by event bus
////						EventBusService.getInstance().postEvent(new MqttMessageEvent(arg1.toString()));
//						getEventBusHelper().postEvent(new MqttMessageEvent(arg1.toString()));
//					}
                }
            });

            // topic = client.getTopic(myTopic);

			/*
             * message = new MqttMessage(); message.setQos(1);
			 * message.setRetained(false);
			 * System.out.println(message.isRetained() + "------ratained状态");
			 * message.setPayload(" 33".getBytes("UTF-8"));
			 */
            //
            client.connect(options);
            //
            client.subscribe(topic);
        } catch (Exception e) {
            // e.printStackTrace();
            LOG.error(e.toString());
        }

    }
}