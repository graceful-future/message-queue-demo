package com.gracefulfuture.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenkun
 * @version 1.0
 * @description 消息队列主启动类
 * @create 2021/6/29 17:52
 */
@SpringBootApplication
public class MessageQueueApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageQueueApplication.class,args);
    }
}
