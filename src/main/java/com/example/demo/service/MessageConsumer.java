package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.APMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

//@Service
public class MessageConsumer {

    //@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        APMessage apMessage= JSONObject.parseObject(message,APMessage.class);
        System.out.println("Received message: " + apMessage);
        try {
            int randomNumber = ThreadLocalRandom.current().nextInt(1, 301);
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}