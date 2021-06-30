package com.gracefulfuture.mq.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description MQTT服务端程序
 * @author chenkun
 * @version 1.0
 * @create 2021/6/29 17:06
 */
@Configuration
public class MQTTServer {
    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    @Value("${spring.mqtt.hostUrl}")
    private String hostUrl;

    //定义一个主题
    @Value("${spring.mqtt.topic}")
    private   String topicStr;

    //定义MQTT的ID，可以在MQTT服务配置中指定
    @Value("${spring.mqtt.clientId1}")
    private String clientId;

    private MqttClient client;

    private MqttTopic topic;

    @Value("${spring.mqtt.username}")
    private String userName;

    @Value("${spring.mqtt.password}")
    private String passWord;

    private MqttMessage message;

    private ScheduledExecutorService scheduler;

    /**
     * 构造函数
     *
     * @throws MqttException
     */
    public MQTTServer()  {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
    }

    @PostConstruct
    public void initiateProperties() throws MqttException{
        client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
        connect();
        scheduler = Executors.newScheduledThreadPool(10);
        scheduler.scheduleAtFixedRate(new PushMessageThread(this,topicStr),1,60, TimeUnit.SECONDS);
    }

    /**
     * 用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic = client.getTopic(topicStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic, MqttMessage message) throws Exception{
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

    public MqttTopic getTopic() {
        return topic;
    }

    /**
     * 启动入口
     *
     * @param args
     * @throws MqttException
     */
    public static void main(String[] args) throws Exception {
        MQTTServer server = new MQTTServer();
        server.message.setQos(1);
        server.message.setRetained(true);
        server.message.setPayload("hello,topic11".getBytes());
        server.publish(server.topic, server.message);
        System.out.println(server.message.isRetained() + "------ratained状态");
    }
}
