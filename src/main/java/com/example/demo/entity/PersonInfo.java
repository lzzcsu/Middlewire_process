package com.example.demo.entity; // 请根据实际项目情况修改包名

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PersonInfo {
    private String MAC;
    private String GongHao;
    private String Name;
    private String KeShi;
    private String Region;
    private Date InTime;

    private double WorkTime;

    public PersonInfo() {
    }

    public PersonInfo(String MAC, String gongHao, String name, String keShi, String region, Date inTime, double workTime) {
        this.MAC = MAC;
        GongHao = gongHao;
        Name = name;
        KeShi = keShi;
        Region = region;
        InTime = inTime;
        WorkTime = workTime;
    }

}