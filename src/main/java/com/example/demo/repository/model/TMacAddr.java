package com.example.demo.repository.model;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_mac_addr")
public class TMacAddr {
    @TableId
    private String GONG_HAO;
    private String MAC;
    private String REMARK;
}