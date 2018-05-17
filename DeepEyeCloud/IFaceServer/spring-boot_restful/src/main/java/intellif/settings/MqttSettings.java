package intellif.settings;

import intellif.enums.MqttTopicNames;
import intellif.enums.TaskTypes;
import intellif.database.entity.TaskInfo;

public class MqttSettings {
    //
    private static String uri;

    public static String getUri() {
        return uri;
    }

    public static void setUri(String uri) {
        MqttSettings.uri = uri;
    }

    public static String getTopicName(TaskInfo tInfo) {
        String topicName = "*";//Wildcard for all topic names.
        if (tInfo.getType() == TaskTypes.NORMAL.getValue()) {
            topicName = String.valueOf(tInfo.getSourceType()) + "/" + String.valueOf(tInfo.getSourceId());
        } else {
            topicName = MqttTopicNames.EngineReport.getValue();
        }
        return topicName;
    }
}
