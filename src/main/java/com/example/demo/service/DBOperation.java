package com.example.demo.service;


import com.example.demo.entity.APLog;
import com.example.demo.entity.APMessage;
import com.example.demo.entity.PersonInfo;

import com.example.demo.repository.mapper.*;
import com.example.demo.repository.model.dynamic.TAlarmInfo;
import com.example.demo.repository.model.dynamic.TAttendance;
import com.example.demo.repository.model.dynamic.TPersonPositionData;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class DBOperation {

    @Resource
    private APInfoMapper APInfoMapper;
    @Resource
    private AlarmInfoMapper AlarmInfoMapper;

    @Resource
    private AttendanceMapper AttendanceMapper;
    @Resource
    private PersonInfoMapper PersonInfoMapper;
    @Resource
    private PersonPosMapper PersonPosMapper;

    @Resource
    private LogMapper logMapper;


    // 查询所有用户
    public List<PersonInfo> getAllPersonInfo() {
        if (PersonInfoMapper == null) {
            throw new IllegalStateException("personInfoMapper is not initialized***");
        }
        return PersonInfoMapper.getAllPersonInfo();
    }
    //查询所有AP信息
    public List<APMessage> getAllAPInfos() {
        return APInfoMapper.getAllAPInfos();
    }
    //插入超时信息
    public int insertAlarmInfo(String type_id, String phonemac, Date begin_time, int is_confirm, int alarm_color, int is_vanish, double alarm_value) {
        TAlarmInfo TAlarmInfo = new TAlarmInfo( type_id,  phonemac,alarm_value,  begin_time, is_confirm, is_vanish,alarm_color);

        return AlarmInfoMapper.insertAlarmInfo(TAlarmInfo);
    }
    //更新超时信息
    public int updateAlarmInfoById(int id, Date end_time) {
        return AlarmInfoMapper.updateAlarmInfoById(  id,   end_time);
    }
    //更新超时信息
    public int updateAlarmInfoByGongHao(String gonghao, Date end_time) {
        return AlarmInfoMapper.updateAlarmInfoByGongHao(  gonghao,   end_time);
    }
    //插入考勤信息
    public int insertAttendanceInfo(String GONG_HAO, Date IN_TIME, String IN_PLACE) {
        TAttendance TAttendance = new TAttendance( GONG_HAO,  IN_TIME,  IN_PLACE);

        return AttendanceMapper.insertAttendanceInfo(TAttendance);
    }
    //更新考勤信息
    public int updateAttendanceById(int id, String out_place, Date out_time) {
        return AttendanceMapper.updateAttendanceInfoById(id,out_place,out_time);
    }
    public int updateAttendanceByGongHao(String gonghao, String out_place, Date out_time) {
        return AttendanceMapper.updateAttendanceInfoByGongHao(gonghao,out_place,out_time);
    }
    //插入人员定位信息
    public int insertPersonPositionData(String CARD_ID, Date GET_DATE, String TAG_ID) {

        TPersonPositionData TPersonPositionData = new TPersonPositionData( CARD_ID,  GET_DATE,  TAG_ID,  "1",  0,  Date.from( Instant.now()));
        return PersonPosMapper.insertPersonPositionData(TPersonPositionData);
    }

    public void Log(String e, String type, String message) {

    }

    public Collection<APLog> SelectAPLog(int id) {

        return  logMapper.getLogInfo(id);
    }
}