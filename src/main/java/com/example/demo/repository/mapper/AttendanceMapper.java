package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.repository.model.dynamic.TAttendance;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface AttendanceMapper extends BaseMapper<TAttendance> {

    @Update("UPDATE t_ap_attendance_info SET OUT_TIME = #{out_time} ,OUT_PLACE = #{out_place} WHERE ID = #{id} ")
    public int updateAttendanceInfoById(int id, String out_place, Date out_time);


    @Update("UPDATE t_ap_attendance_info SET OUT_TIME = #{out_time} ,OUT_PLACE = #{out_place} WHERE GONG_HAO = #{gonghao}  ")
    public int updateAttendanceInfoByGongHao(String gonghao, String out_place, Date out_time);

    @Insert("INSERT INTO t_ap_attendance_info  ( GONG_HAO, IN_TIME, IN_PLACE)  VALUES (  #{GONG_HAO}, #{IN_TIME}, #{IN_PLACE} )")
    @Options(useGeneratedKeys = true, keyProperty = "ID")
    public int insertAttendanceInfo(TAttendance attendance);
}

