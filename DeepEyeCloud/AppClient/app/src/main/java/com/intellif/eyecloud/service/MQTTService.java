package com.intellif.eyecloud.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.MqttBean;
import com.intellif.eyecloud.bean.post.ListenerBean;
import com.intellif.eyecloud.utils.IListener;
import com.intellif.eyecloud.utils.ListenerManager;
import com.intellif.eyecloud.utils.SPContent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017-04-22.
 */
public class MQTTService extends Service implements IListener{
    //private String host ="tcp://183.3.223.120:5883";
    private boolean isruning=false;
    private int i = 1;
    private MqttClient client;
    private String myTopic = "";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private List<ManageBean> stringList = new ArrayList<>();
    private String jsonString = "";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init();
        startReconnect();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
            } else if (msg.what == 2) {

                System.out.println("连接成功");
                Log.e("sss","连接成功");
                Log.e("sss",jsonString);
                try {
                    if(!jsonString.isEmpty()){
                    Gson gson = new Gson();
                    stringList  = gson.fromJson(jsonString,new TypeToken<List<ManageBean>>(){}.getType());
                   new Thread(){
                       @Override
                       public void run() {
                           for (int j = 0; j <stringList.size() ; j++) {
                               try {
                                   client.subscribe("0/"+stringList.get(j).id, 1);
                               } catch (MqttException e) {
                                   e.printStackTrace();
                               }
                               Log.e("MTQQService","0/"+stringList.get(j).id);
                           }
                       }
                   }.start();
                    }else{
                        return;
                    }
                    isruning=false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == 3) {
//                Toast.makeText(MainActivity.this, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
                System.out.println("连接失败，系统正在重连");
            }
        }
    };
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
    private void init() {
        jsonString = SPContent.getTopic(getApplicationContext());
        Log.e("mqtt---topic",myTopic);
        ListenerManager.getInstance().registerListtener(this);
        try {
            java.util.Random random=new java.util.Random();// 定义随机类
            final int result=random.nextInt(100000);
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(UrlConfig.MQTT_HOST, "appCloud"+result,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
//            options.setUserName(userName);
//            //设置连接的密码
//            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    Log.e("mqtt_messaage",message.toString());
                    Log.e("mqtt_messaage",topicName.toString());
                    Gson gson = new Gson();
                    MqttBean bean = gson.fromJson(message.toString(),MqttBean.class);
                    boolean  ring = SPContent.getRing(getApplicationContext());
                    if(ring){
                        ListenerBean listenerBean = new ListenerBean();
                        listenerBean.code=3;
                    ListenerManager.getInstance().sendBroadCast(listenerBean);
                    }else {
                        return;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void notifyAllActivity(ListenerBean bean) {
        if(bean.code==1){
            String topic = SPContent.getTopic(getApplicationContext());
            if(topic.equals("null")||topic.equals("null")||topic.isEmpty()){
                SPContent.saveTopic(getApplicationContext(),"");
                startReconnect();
            }else {
                if(isruning==false){
                    isruning=true;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            init();
                            startReconnect();
                        }
                    }.start();
                }else{
                    return;
                }

            }
        }

    }

}