/*package intellif.lire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.enums.MqttTopicNames;
import intellif.service.MqttMessageServiceItf;

@Component
public class MqttAlarmInfoThread extends Thread {
    @Autowired
    private MqttMessageServiceItf mqttMessageServiceItf;

    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void run() {
        mqttMessageServiceItf.setup(MqttTopicNames.AlarmInfo.getValue());
    }

}
*/