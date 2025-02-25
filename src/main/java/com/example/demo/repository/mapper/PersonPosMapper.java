package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.repository.model.dynamic.TAttendance;
import com.example.demo.repository.model.dynamic.TPersonPositionData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface PersonPosMapper extends BaseMapper<TPersonPositionData> {

    @Insert("INSERT INTO t_person_position_data  ( CARD_ID, GET_DATE, TAG_ID,QUALITY,HAS_VISITED,SAVE_TIME)  VALUES (  #{CARD_ID}, #{GET_DATE}, #{TAG_ID},#{QUALITY} ,#{HAS_VISITED} ,#{SAVE_TIME}    )")
    public int insertPersonPositionData(TPersonPositionData tPersonPositionData);
}
