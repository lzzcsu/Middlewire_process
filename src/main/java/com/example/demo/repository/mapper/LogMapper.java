package com.example.demo.repository.mapper;

import com.example.demo.entity.APLog;
import com.example.demo.entity.PersonInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface LogMapper {

    @Select("SELECT  " +
            "l.LOG_DESCRIPT AS LOG, " +
            "l.LOG_ID AS ID, " +
            "l.LOG_TIME AS TIME " +


            "FROM `t_pub_log` l " +

            "WHERE l.LOG_ID > #{id} "+
            "order by LOG_ID asc "+
            " limit 10")
    List<APLog> getLogInfo(int id);
}
