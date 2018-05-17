package intellif.utils;

import intellif.dto.MessageDto;
import intellif.enums.MqttTopicNames;
import intellif.lire.OfflineThread;
import intellif.mqtt.IfMqttClient;
import intellif.database.entity.PersonDetail;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttUtil {
	 private static Logger LOG = LogManager.getLogger(MqttUtil.class);
	//后台手动给前台推送消息
	@SuppressWarnings("static-access")
	public static boolean setMqtt(Object object,String topic) {
		try {
			IfMqttClient ifMqttClient = new IfMqttClient();
			ifMqttClient.connect();
			MqttMessage mqttMessage = new MqttMessage();
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JtsModule());
			String applyTaskDtoJsonStr = mapper.writeValueAsString(object);
			mqttMessage.setPayload(applyTaskDtoJsonStr.getBytes("UTF-8"));
			ifMqttClient.publish(topic, mqttMessage);
			ifMqttClient.getClient().disconnect();
			return true;
		} catch (Exception e) {
			LOG.error("send mqtt message error:",e);
			return false;
		}
	}

}
