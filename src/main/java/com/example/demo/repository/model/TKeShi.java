package com.example.demo.repository.model;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_pub_keshi")
public class TKeShi {
    @TableId
    private String KESHI_ID;
    private String KESHI_NAME;
    private String PHONE;
    private String REMARK;
    private String FATHER_DEPT_ID;
}