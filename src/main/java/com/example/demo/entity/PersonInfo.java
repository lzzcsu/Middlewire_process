package com.example.demo.entity; // 请根据实际项目情况修改包名

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PersonInfo implements Serializable {
    private String MAC;
    private String GongHao;
    private String Name;
    private String KeShi;
    private String Region;
    private Date InTime;
    private double WorkTime;
}