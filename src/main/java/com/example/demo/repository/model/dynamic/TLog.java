package com.example.demo.repository.model.dynamic;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pub_log")
public class TLog {
    @TableId
    private Integer LOG_ID;
    private String TYPE_ID;
    private String LOG_SOURCE;
    private String LOG_DESCRIPT;
    private Date LOG_TIME;
}