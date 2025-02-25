package com.example.demo.repository.model;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_device_info")
public class TDeviceInfo {
    @TableId
    private String DEVICE_ID;
    private String TYPE_ID;
    private String DEVICE_NAME;
    private String IP_ADDRESS;
    private String MAC_ADDRESS;
    private String INSTALL_PLACE;
    private String TUNNEL_AREA;
    private Date USE_DATE;
    private String LIFE_SPAN;
    private String DEVICE_BROKEN;
    private String REMARK;
}