package com.example.demo.repository.mapper;

import com.example.demo.entity.PersonInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;



@Mapper
public interface PersonInfoMapper {
    @Select("SELECT " +
            "m.MAC AS MAC, " +
            "e.Gong_Hao AS GongHao, " +
            "e.EMPLOYEE_NAME AS Name, " +
            "k.KESHI_NAME AS KeShi " +

            "FROM `t_pub_employee` e " +
            "JOIN `t_pub_keshi` k ON e.KESHI_ID = k.KESHI_ID " +
            "JOIN `t_mac_addr` m ON e.GONG_HAO = m.GONG_HAO")
    List<PersonInfo> getAllPersonInfo();


}

