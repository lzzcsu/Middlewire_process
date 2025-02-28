package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.APLog;
import com.example.demo.entity.APMessage;
import com.example.demo.entity.PersonInfo;
import com.example.demo.tool.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.redisson.api.*;
import org.redisson.client.codec.Codec;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class APMessageProcessor {

    //<editor-fold desc="全局变量">
    //Redis
    // 保存每个手机井下定位数据
    private  final   ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> phoneMessageDictionary = new ConcurrentHashMap<>();
    // 保存人员出入井状态
    private    ConcurrentHashMap<String, Boolean> statusDictionary = new ConcurrentHashMap<>();
    // 用于更新超时报警
    private    ConcurrentHashMap<String, Integer> overtimeDictionary = new ConcurrentHashMap<>();
    // 如果某人的定位数据超过1个小时，则自动删除定位数据
    private   final ConcurrentHashMap<String, ScheduledFuture<?>> OverTimeTimers = new ConcurrentHashMap<>();
    // 保存每个手机最后一次经过的AP
    private   final ConcurrentHashMap<String, PersonInfo> phoneLastMessage = new ConcurrentHashMap<>();


    //Local
    // 保存所有的AP信息，键值为AP的名称（因为漫游数据通过名称来区别AP）
    private   final ConcurrentHashMap<String, APMessage> AllAPInformation = new ConcurrentHashMap<>();
    // 保存所有的用户信息，键值为MAC地址
    private   final ConcurrentHashMap<String, PersonInfo> AllPersonInformation = new ConcurrentHashMap<>();

    private    Socket server;
    private   boolean isRun = false;

    @Autowired
    private DBOperation dbo;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;
    //</editor-fold>

    @PostConstruct
    public void init() {
        server = new Socket();

        loadDataFromDatabase();


        ExecutorService handleDelayTask = Executors.newSingleThreadExecutor();//处理超时任务
        handleDelayTask.submit(() ->delayTask());
        handleDelayTask.shutdown();

//        ScheduledExecutorService syncCacheToRedis = Executors.newScheduledThreadPool(1);//定期转存Redis
//        syncCacheToRedis.scheduleAtFixedRate(() ->migrateToRedis(), 0, 10, TimeUnit.SECONDS);

        System.out.println("初始化完成");

    }

    //<editor-fold desc="数据迁移">
    private  void loadDataFromDatabase() {
        // 读取所有的 AP 信息
        AllAPInformation.clear();
        List<APMessage> apInfos = dbo.getAllAPInfos();
        for (APMessage item : apInfos) {
            AllAPInformation.put(item.getAPName(), item);
        }

        // 读取所有的人员信息
        AllPersonInformation.clear();
        List<PersonInfo> personInfos = dbo.getAllPersonInfo();
        for (PersonInfo item : personInfos) {
            AllPersonInformation.put(item.getMAC(), item);
        }

        /**************************************************************************************************************/
        //从redis读取缓存
        boolean flag=retrieveFromRedis();
        if(!flag) {
            for (PersonInfo item : personInfos) {
                //第一次运行，redis没有对应缓存
                statusDictionary.put(item.getMAC(), false); // 初始所有人员判定出井
                overtimeDictionary.put(item.getMAC(), 0);

                migrateToRedis();
            }
        }
        /**************************************************************************************************************/

    }
    // 将数据迁移到 Redis
    public void migrateToRedis() {
        // 迁移 phoneMessageDictionary
        redisService.savePhoneMessageDictionary("phoneMessageDictionary", phoneMessageDictionary);

//        redisService.saveObjectToRedis("statusDictionary", statusDictionary);
//        redisService.saveObjectToRedis("attendanceDictionary", attendanceDictionary);
//        redisService.saveObjectToRedis("overtimeDictionary", overtimeDictionary);


         //迁移 statusDictionary
        RMap<String, Boolean> statusRedisMap = redissonClient.getMap("statusDictionary");
        statusRedisMap.putAll(statusDictionary);



        // 迁移 overtimeDictionary
        RMap<String, Integer> overtimeRedisMap = redissonClient.getMap("overtimeDictionary");
        overtimeRedisMap.putAll(overtimeDictionary);

        RBlockingQueue<String> queue = redissonClient.getBlockingQueue("delayQueue");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(queue);// 获取一个延时队列，将
    }

    // 从 Redis 中获取数据
    public boolean retrieveFromRedis() {
        // 获取 phoneMessageDictionary
        boolean status = false;

        boolean s1=checkIfRMapExists("phoneMessageDictionary");

        status = s1;
        if (status) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<APMessage>> tmp_phoneMessageDictionary  =redisService.getPhoneMessageDictionary("phoneMessageDictionary");
            phoneMessageDictionary.clear();
            tmp_phoneMessageDictionary.forEach((key, value) -> {
                phoneMessageDictionary.put(key, value);
            });
            // 获取 statusDictionary
            RMap<String, Boolean> statusRedisMap = redissonClient.getMap("statusDictionary");
            statusDictionary.putAll(statusRedisMap);

            // 获取 attendanceDictionary
//            RMap<String, Integer> attendanceRedisMap = redissonClient.getMap("attendanceDictionary");
//            attendanceDictionary.putAll(attendanceRedisMap);

            // 获取 overtimeDictionary
            RMap<String, Integer> overtimeRedisMap = redissonClient.getMap("overtimeDictionary");
            overtimeDictionary.putAll(overtimeRedisMap);
        }


        return status;
    }
    //</editor-fold>

    //<editor-fold desc="消费人员定位消息">
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {

        APMessage apMessage= null;

        apMessage = JSONObject.parseObject(message,APMessage.class);
        System.out.println(apMessage.getGetTime().toString());

        if (apMessage != null) {
            cacheMessage(apMessage);
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 10);


//        System.out.println("Received message: " + apMessage);


    }

    // 将 AP 定位数据保存到内存中
    private  void cacheMessage(APMessage m) {

        phoneMessageDictionary.compute(m.getPhoneMAC(), (key, oldValue) -> {
            if (oldValue == null) {
                //System.out.println(m.getPhoneMAC()+"第一次被检测");
                // 不存在键值，此方法会被调用
                oldValue = new ConcurrentLinkedQueue<>();

                if (IsExitAPByTagID(m.getAPName())==1) {
                    oldValue.add(m);
                }
                //redis更新
                /********************************************************************************************************/
                // 将更新后的队列同步到 Redis
                redisService.updateQueueInRedis("phoneMessageDictionary", key, oldValue);
                /********************************************************************************************************/
                return oldValue;
            }

            // 存在此键值，此方法会被调用
            // 如果为空，则为出井状态
            boolean flag_empty = oldValue.isEmpty();
            boolean flag_IsIn = statusDictionary.getOrDefault(m.getPhoneMAC(), false);

            if (IsExitAPByTagID(m.getAPName())==1) {
                //System.out.println(IsExitAPByTagID(m.getAPName()));
                if (!flag_IsIn) {
                    // 仅在地面出口活动
                    //System.out.println(m.getPhoneMAC()+"在井口活动"+": "+m.getGetTime());
                    oldValue.clear();
                    oldValue.add(m);
                    /**************************************************************************************************/
                    redisService.updateQueueInRedis("phoneMessageDictionary", key, oldValue);
                    /**************************************************************************************************/
                }
                else  { // 判定出井
                    // 去数据库找该员工最新下井记录
                    // 更新出井时间和地点
                    statusDictionary.put(m.getPhoneMAC(), false);
                    /**************************************************************************************************/
                    //redis更新
                    RMap<String, Boolean> statusRedisMap = redissonClient.getMap("statusDictionary");
                    statusRedisMap.put(m.getPhoneMAC(), false);
                    /**************************************************************************************************/
                    // 更新考勤记录，补充出井信息
                    dbo.updateAttendanceByGongHao(m.getGongHao(), m.getAPID(), m.getGetTime());

                    // 假如有超时记录，也要更新
                    int overtime_id = overtimeDictionary.getOrDefault(m.getPhoneMAC(), 0);
                    if (overtime_id != 0) {
                        dbo.updateAlarmInfoByGongHao(m.getGongHao(), m.getGetTime());
                        overtimeDictionary.put(m.getPhoneMAC(), 0);
                        ///*********************************开始
                        RMap<String, Integer> overtimeRedisMap = redissonClient.getMap("overtimeDictionary");
                        overtimeRedisMap.put(m.getPhoneMAC(), 0);
                        ///************************************结束

                    }

                    oldValue = new ConcurrentLinkedQueue<>();
                    ///*********************************开始
                    redisService.updateQueueInRedis("phoneMessageDictionary", key, oldValue);
                    ///************************************结束
                    // 同时记得将该人员的定时器清除
//                    ScheduledFuture<?> future = OverTimeTimers.remove(m.getPhoneMAC());
//                    if (future != null) {
//                        boolean isCancelled = future.cancel(false);
//                    }
                    ///*********************************开始
                    RBlockingQueue<String> queue = redissonClient.getBlockingQueue("delayQueue");
                    RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(queue);// 获取一个延时队列，将普通队列包装成延时队列
                    boolean isCancelled = delayedQueue.remove(m.getPhoneMAC());

                    ///************************************结束
                }
            }
            else if (IsExitAPByTagID(m.getAPName())==0 && !flag_empty) { // 井下活动
                if (!flag_IsIn) { // 判定入井并插入考勤记录
                    statusDictionary.put(m.getPhoneMAC(), true);
                    /**************************************************************************************************/
                    //redis更新
                    RMap<String, Boolean> statusRedisMap = redissonClient.getMap("statusDictionary");
                    statusRedisMap.put(m.getPhoneMAC(), true);
                    /**************************************************************************************************/
                    // 记录考勤

                    //bug********************************************************************************************************
                    String jsonObject= JSON.toJSONString(oldValue.peek());
                    //将json转成需要的对象
                    //APMessage inAPMessage = (APMessage) oldValue.peek();
                    APMessage inAPMessage= JSONObject.parseObject(jsonObject,APMessage.class);
                    //************************************************************************************************************

                    int id = dbo.insertAttendanceInfo(m.getGongHao(),inAPMessage.getGetTime(), inAPMessage.getAPID());

                    oldValue.add(m);
                    ///*********************************开始
                    redisService.updateQueueInRedis("phoneMessageDictionary", key, oldValue);
                    ///************************************结束
//                    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//                    ScheduledFuture<?> future=executor.schedule(() -> recordOverTimeData(m.getPhoneMAC()), 8 * 60 * 60, TimeUnit.SECONDS);
//                    executor.shutdown();
//                    OverTimeTimers.put(m.getPhoneMAC(), future);
                    ///*********************************开始
                    RBlockingQueue<String> queue = redissonClient.getBlockingQueue("delayQueue");
                    RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(queue);// 获取一个延时队列，将
                    String task = m.getPhoneMAC();
                    int randomNumber = ThreadLocalRandom.current().nextInt(20, 100);

                    delayedQueue.offer(task, randomNumber, TimeUnit.SECONDS);
                    ///************************************结束

                }
                else { // 井下活动
                    //System.out.println(m.getPhoneMAC()+"在井下活动"+": "+m.getGetTime());
                    oldValue.add(m);
                    ///*********************************开始
                    redisService.updateQueueInRedis("phoneMessageDictionary", key, oldValue);
                    ///************************************结束
                }
            }

            return oldValue;
        });



    }
    //</editor-fold>

    //<editor-fold desc="处理超时任务">
    private  void delayTask()  {

        System.out.println("开始执行超时任务：");
        long flag= redissonClient.getKeys().countExists("delayQueue");
        while (true) {
            RBlockingQueue<String> queue = redissonClient.getBlockingQueue("delayQueue");

            // 从普通队列中获取任务，如果队列为空则阻塞
            String queueTask = "";
            try {
                System.out.println("人员超时："+queueTask);
                queueTask = queue.take();

                recordOverTimeData(queueTask);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    // 记录超时数据的方法
    private  boolean recordOverTimeData(String phoneMAC) {
        boolean flag = false;

        // 获取当前时间作为超时时间
        Date begin_time = new Date();


        String gonghao = "";

        // 尝试从 AllPersonInformation 中获取对应 phonemac 的 PersonInfo 对象
        PersonInfo personInfo = AllPersonInformation.get(phoneMAC);
        if (personInfo != null) {
            gonghao = personInfo.getGongHao();
        }

        String type_id = "TO";
        int is_confirm = 1;
        int is_vanish = 0;
        int alarm_color = 1;
        double alarm_value = 0;

        int overtime_id = -1;
        // 插入报警信息到数据库
        System.out.println(phoneMAC+"  "+gonghao);
        overtime_id = dbo.insertAlarmInfo(type_id, gonghao, begin_time, is_confirm, alarm_color, is_vanish, alarm_value);
        // 将超时记录的 ID 存入 overtimeDictionary
        overtimeDictionary.put(phoneMAC, overtime_id);
        ///*********************************开始
        RMap<String, Integer> overtimeRedisMap = redissonClient.getMap("overtimeDictionary");
        overtimeRedisMap.put(phoneMAC, overtime_id);
        ///************************************结束
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="工具方法">
    public boolean checkIfRMapExists(String mapName) {

        return redissonClient.getKeys().countExists(mapName) > 0;
    }
    private int IsExitAPByTagID(String apname) {
        APMessage result = AllAPInformation.get(apname);
        if (result != null) {
            if(result.getIsExit().equals("1"))return 1;
            else return 0;

        } else {
            return -1;
        }
    }
    //</editor-fold>


}
