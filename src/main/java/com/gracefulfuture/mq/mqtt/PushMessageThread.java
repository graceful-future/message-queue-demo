package com.gracefulfuture.mq.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description 推送消息线程
 * @author chenkun
 * @version 1.0
 * @create 2021/6/29 18:40
 */
public class PushMessageThread implements Runnable{
    private MQTTServer mqttServer;

    private String topic;

    public PushMessageThread(MQTTServer mqttServer,String topic) {
        this.mqttServer = mqttServer;
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.now();
            String currentTime = formatter.format(localDateTime);
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);
            mqttMessage.setPayload(("hello,topic:" + topic + " 当前时间" + currentTime).getBytes());
            mqttServer.publish(mqttServer.getTopic(),mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
