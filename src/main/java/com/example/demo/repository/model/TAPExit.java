package com.example.demo.repository.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_ap_exit")
public class TAPExit {
    @TableId
    private String TAG_ID;
    private String IS_EXIT;
    private String REMARK;
}