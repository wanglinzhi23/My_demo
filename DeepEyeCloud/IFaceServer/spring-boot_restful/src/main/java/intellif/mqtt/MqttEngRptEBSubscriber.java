package intellif.mqtt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import intellif.dto.mqtt.MqttEngineReport;
import intellif.dto.mqtt.StatusRpt;
import intellif.enums.ServerStatus;
import intellif.events.MqttMessageEvent;
import intellif.service.PersonDetailServiceItf;
import intellif.service.ServerServiceItf;
import intellif.service.TaskServiceItf;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configurable
//@EnableSpringConfigured
public class MqttEngRptEBSubscriber {

    private static Logger LOG = LogManager
            .getLogger(MqttEngRptEBSubscriber.class);
    private Injector injector;//abstracted Inject service stub.
    private TaskServiceItf taskServiceItf;
    private ServerServiceItf serverServiceItf;
    private PersonDetailServiceItf personDetailServiceItf;

    public MqttEngRptEBSubscriber() {
    }

    public MqttEngRptEBSubscriber(Injector injector) {
        this.injector = injector;
        //
        this.taskServiceItf = injector.getInstance(TaskServiceItf.class);
        LOG.info("Guice injector for taskSeviceItf:" + this.taskServiceItf.toString());
        this.serverServiceItf = injector.getInstance(ServerServiceItf.class);
        LOG.info("Guice injector for serverServiceItf:" + this.serverServiceItf.toString());
    }

    @Subscribe
    public void onEvent(MqttMessageEvent event) throws NotFoundException {
        // Handle the string passed on by the Event Bus
//        LOG.info("onEvent:" + event);
//        LOG.info("taskServiceItf(onEvent):" + taskServiceItf.toString());
//        LOG.info("serverServiceItf(onEvent):" + serverServiceItf.toString());
        //
        String jsonString = event.getMessage();
        // String jsonString =
        //{"msgType":1,"ipaddr":192.168.2.8,"port":1883,"msgBody":{"statusRpt":[{"taskid":77,"status":0}]}}
        ObjectMapper objectMapper = new ObjectMapper();
        //
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 单引号处理
        objectMapper
                .configure(
                        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                        false);
        //JSON Object mapper
        JsonNode node = null;
        MqttEngineReport mqttEngineReport = new MqttEngineReport();
        try {
            node = objectMapper.readTree(jsonString);
//            LOG.info("JsonNode:" + node);
            mqttEngineReport = objectMapper.readValue(node.toString(), MqttEngineReport.class);
            LOG.info("mqttEngineReport:" + mqttEngineReport.toString());
        } catch (Exception e) {
            LOG.error(e.toString());
        }
        if (mqttEngineReport.getMsgType() == 1) {//Regular status report
            //Update task table;
            List<StatusRpt> statusRptList = mqttEngineReport.getMsgBody().getStatusRpt();
            LOG.debug("statusRptList:size:" + statusRptList.size() + ",toString:" + statusRptList.toString());
            for (int i = 0; i < statusRptList.size(); i++) {
                LOG.debug("this.taskServiceItf.updateStatus:task_id:" + statusRptList.get(i).getTaskid() + ",status:" + statusRptList.get(i).getStatus());
                this.taskServiceItf.updateStatus(statusRptList.get(i).getTaskid(), statusRptList.get(i).getStatus());
            }
            //Update server table;
            LOG.debug("this.serverServiceItf.updateStatus: with ip:" + mqttEngineReport.getIpaddr() + ",with status:" + ServerStatus.ONLINE_VALID.getValue());
//            this.serverServiceItf.updateStatus(mqttEngineReport.getIpaddr(), ServerStatus.ONLINE_VALID.getValue());
            this.serverServiceItf.updateStatus(mqttEngineReport.getIpaddr(), ServerStatus.ONLINE_VALID.getRandValue());
        } else if (mqttEngineReport.getMsgType() == 0) {//Startup event
            LOG.info("IFaceEngine startup,this.taskServiceItf.resumeRelevance() call!");
            this.taskServiceItf.resumeRelevance();
            //
        } else {
            //Empty handler.
        }
    }
//
//    public TaskServiceItf getTaskServiceItf() {
//        return taskServiceItf;
//    }
//
//    public void setTaskServiceItf(TaskServiceItf taskServiceItf) {
//        this.taskServiceItf = taskServiceItf;
//    }
//
//    public ServerServiceItf getServerServiceItf() {
//        return serverServiceItf;
//    }
//
//    public void setServerServiceItf(ServerServiceItf serverServiceItf) {
//        this.serverServiceItf = serverServiceItf;
//    }
}
