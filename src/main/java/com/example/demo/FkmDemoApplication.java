package com.example.demo;

import com.example.demo.entity.APMessage;
import com.example.demo.repository.mapper.APInfoMapper;
import com.example.demo.service.DBOperation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.TimeZone;

@MapperScan("com.example.demo.repository.mapper")
@SpringBootApplication
public class FkmDemoApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		SpringApplication.run(FkmDemoApplication.class, args);


	}


}
