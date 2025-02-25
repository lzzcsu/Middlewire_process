package com.example.demo.tool;
import com.example.demo.entity.APMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class RedisService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将 ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> 保存到 Redis
     * @param key Redis 中的键
     * @param phoneMessageDictionary 要保存的对象
     */
    public void savePhoneMessageDictionary(String key, ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> phoneMessageDictionary) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        for (String mapKey : phoneMessageDictionary.keySet()) {
            ConcurrentLinkedQueue<APMessage> queue = phoneMessageDictionary.get(mapKey);
            ArrayList<APMessage> list = new ArrayList<APMessage>(queue);
            try {
                String json = objectMapper.writeValueAsString(list);
                rMap.put(mapKey, json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从 Redis 中获取 ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>>
     * @param key Redis 中的键
     * @return 从 Redis 中获取的对象
     */
    public ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> getPhoneMessageDictionary(String key) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> result = new ConcurrentHashMap<>();
        for (String mapKey : rMap.keySet()) {
            String json = rMap.get(mapKey);
            try {
                ArrayList<APMessage> list = objectMapper.readValue(json, ArrayList.class);
                ConcurrentLinkedQueue<APMessage> queue = new ConcurrentLinkedQueue<>(list);

                result.put(mapKey, queue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 当本地 phoneMessageDictionary 某个 key 对应的队列发生变化时，更新 Redis 中的 RMap
     * @param key Redis 中的键
     * @param mapKey 本地 ConcurrentHashMap 中的键
     * @param queue 更新后的队列
     */
    public void updateQueueInRedis(String key, String mapKey, ConcurrentLinkedQueue<APMessage> queue) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        try {
            String json = objectMapper.writeValueAsString(queue);
            rMap.put(mapKey, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}