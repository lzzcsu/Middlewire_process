package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.repository.model.dynamic.TAlarmInfo;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface AlarmInfoMapper extends BaseMapper<TAlarmInfo> {

    //插入超时记录（按id插入）利用mapper类实现插入

    //更新超时记录（按id更新）
    @Update("UPDATE t_alarm_info SET END_TIME = #{end_time} WHERE ID = #{id} AND END_TIME IS NULL")
    public int updateAlarmInfoById(int id, Date end_time);

    @Insert("INSERT INTO t_alarm_info  ( TYPE_ID, ALARM_SOURCE, ALARM_VALUE, BEGIN_TIME,  IS_CONFIM, IS_VANISH, ALARM_COLOR )  VALUES (  #{TYPE_ID}, #{ALARM_SOURCE}, #{ALARM_VALUE}, #{BEGIN_TIME},  #{IS_CONFIM}, #{IS_VANISH}, #{ALARM_COLOR}  )")
    int insertAlarmInfo(TAlarmInfo alarmInfo);

    @Update("UPDATE t_alarm_info SET END_TIME = #{endTime} WHERE ALARM_SOURCE = #{gonghao} AND END_TIME IS NULL")
    int updateAlarmInfoByGongHao(String gonghao, Date endTime);
}
