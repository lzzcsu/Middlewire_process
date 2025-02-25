package com.example.demo.entity;

import lombok.Data;

// 使用 Lombok 的 @Data 注解，自动生成 getter、setter、toString、equals 和 hashCode 方法
@Data
public class APHistory {
    private String GONG_HAO;
    private String TAG_ID;
    private String APName;
    private String APID;
    private String APMAC;
    private String PhoneMAC;
    private String GetTime;
}