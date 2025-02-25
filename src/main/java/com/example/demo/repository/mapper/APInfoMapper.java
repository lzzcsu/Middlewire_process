package com.example.demo.repository.mapper;

import com.example.demo.entity.APMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface APInfoMapper {

    //获取AP信息
    @Select("SELECT " +
            "d.DEVICE_ID AS APID, " +
            "d.IP_ADDRESS AS APIP, " +
            "d.INSTALL_PLACE AS APName, " +
            "d.MAC_ADDRESS AS APMAC, " +
            "d.INSTALL_PLACE AS Region, " +
            "e.IS_EXIT AS IsExit " +
            "FROM `t_device_info` d " +
            "JOIN `t_ap_exit` e ON d.DEVICE_ID = e.TAG_ID "+
            "WHERE d.TYPE_ID = 'AP'")
    List<APMessage> getAllAPInfos();



}
