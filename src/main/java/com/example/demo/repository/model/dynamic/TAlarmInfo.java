package com.example.demo.repository.model.dynamic;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_alarm_info")
public class TAlarmInfo {
    @TableId
    private Integer ID;
    private String TYPE_ID;
    private String ALARM_SOURCE;
    private double ALARM_VALUE;
    private Date BEGIN_TIME;
    private Date END_TIME;
    private int IS_CONFIM;
    private int IS_VANISH;
    private Integer ALARM_COLOR;
    private BigDecimal ALARM_VALUE_MAX;
    private Date ALARM_VALUE_MAX_TIME;

    public TAlarmInfo(String TYPE_ID, String ALARM_SOURCE, double ALARM_VALUE, Date BEGIN_TIME, int IS_CONFIM, int IS_VANISH, Integer ALARM_COLOR) {
        this.TYPE_ID = TYPE_ID;
        this.ALARM_SOURCE = ALARM_SOURCE;
        this.ALARM_VALUE = ALARM_VALUE;
        this.BEGIN_TIME = BEGIN_TIME;
        this.IS_CONFIM = IS_CONFIM;
        this.IS_VANISH = IS_VANISH;
        this.ALARM_COLOR = ALARM_COLOR;
    }
}