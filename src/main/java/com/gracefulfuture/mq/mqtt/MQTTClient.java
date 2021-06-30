package com.gracefulfuture.mq.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description MQTT客户端程序
 * @author chenkun
 * @version 1.0
 * @create 2021/6/29 17:11
 */
@Configuration
public class MQTTClient {
    @Value("${spring.mqtt.hostUrl}")
    private String hostUrl;

    @Value("${spring.mqtt.topic}")
    private String topicStr;

    @Value("${spring.mqtt.clientId2}")
    private String clientId;

    private MqttClient client;

    private MqttConnectOptions options;

    @Value("${spring.mqtt.username}")
    private String userName;

    @Value("${spring.mqtt.password}")
    private String passWord;

    private void connect(){
        try {
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            client.setCallback(new PushCallback());
            MqttTopic topic = client.getTopic(topicStr);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "close".getBytes(), 2, true);

            client.connect(options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start() {
        try {
//            while (true) {
                //订阅消息
                int[] Qos = {1};
                String[] topic1 = {topicStr};
                client.subscribe(topic1, Qos);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initiateProperties() throws MqttException {
        // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
        connect();
        start();
    }
}
