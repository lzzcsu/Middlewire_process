package com.example.demo.repository.model;



import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pub_employee")
public class TEmployee {
    private String KESHI_ID;
    private String GONG_HAO;
    private Integer HTIRD_UNIT;
    private String EMPLOYEE_NAME;
    private String IDENTITY_CARD;
    private String SEX;
    private String RACE;
    private Date BIRTH_DATE;
    private String BIRTH_PLACE;
    private String DUTY;
    private String DUTY_LEVEL;
    private String WORK_TYPE;
    private String STATE;
    private String REMARK;
}