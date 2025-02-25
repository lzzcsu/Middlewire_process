package com.example.demo.repository.model.dynamic;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_ap_attendance_info")
public class TAttendance {
    @TableId(type = IdType.AUTO)
    private Integer ID;


    private String GONG_HAO;
    private Date IN_TIME;
    private Date OUT_TIME;
    private String IN_PLACE;
    private String OUT_PLACE;

    public TAttendance(String GONG_HAO, Date IN_TIME, String IN_PLACE) {
        this.GONG_HAO = GONG_HAO;
        this.IN_TIME = IN_TIME;
        this.IN_PLACE = IN_PLACE;
    }

}