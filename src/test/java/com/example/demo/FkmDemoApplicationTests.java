package com.example.demo;

import com.example.demo.entity.APMessage;
import com.example.demo.entity.PersonInfo;
import com.example.demo.service.DBOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@SpringBootTest
class FkmDemoApplicationTests {

	@Autowired
	private DBOperation dboperation;
	@Test
	void contextLoads() {
		// 获取当前时间作为超时时间
		Date begin_time = new Date();
		dboperation.updateAlarmInfoByGongHao("50687",begin_time);
		List<APMessage>apMessages= dboperation.getAllAPInfos();

		List<PersonInfo>personInfos= dboperation.getAllPersonInfo();

		int count1=personInfos.size();
		int count2=apMessages.size();
		System.out.println(count1);
		System.out.println(count2);

		String dateString="2025-02-21 15:53:02.703";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		// 解析字符串为 LocalDateTime 对象
		LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
		// 将 LocalDateTime 转换为 Date 对象
		Date beginTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		int r1=dboperation.insertAlarmInfo("TO","24-1A-E6-F9-A2-29",beginTime,1,1,0,0);


		dateString="2025-02-21 17:21:06.000";
		 localDateTime = LocalDateTime.parse(dateString, formatter);
		Date iNTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		int r2=dboperation.insertAttendanceInfo("06048",iNTime,"APX00003");

		dateString="2025-02-21 18:40:01.000";
		localDateTime = LocalDateTime.parse(dateString, formatter);
		Date GETDATE = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		int r3=dboperation.insertPersonPositionData("06304",GETDATE,"APX45502");

		System.out.println(r3);


		int u1,u2,u3;
		Date date=new Date();
		u1=dboperation.updateAttendanceById(1,"123",date);
		u2=dboperation.updateAttendanceByGongHao("06049","123",date);
		u3=dboperation.updateAlarmInfoById(1,date);

	}

}
