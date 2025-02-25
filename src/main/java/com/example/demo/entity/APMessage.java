package com.example.demo.entity; // 根据实际项目情况修改包名

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class APMessage {
    private String APID;
    private String APIP;
    private String APName;
    private String APMAC;
    private String PhoneMAC;
    private String GongHao;
    private String Region;
    private String IsExit;
    private Date GetTime;

    public APMessage(String APID, String APIP, String APName, String APMAC, String Region, String isExit) {
        this.APID = APID;
        this.APIP = APIP;
        this.APName = APName;
        this.APMAC = APMAC;
        this.Region = Region;
        this.IsExit = isExit;
    }

    public APMessage(String APID, String APIP, String APName, String APMAC, String Region, String PhoneMAC, Date GetTime,String GongHao) {
        this.APID = APID;
        this.APIP = APIP;
        this.APName = APName;
        this.APMAC = APMAC;
        this.Region = Region;
        this.PhoneMAC = PhoneMAC;
        this.GetTime = GetTime;
        this.GongHao = GongHao;
    }
    public APMessage(){

    }
}