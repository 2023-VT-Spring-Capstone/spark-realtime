package com.capstone.realtimebackend;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.capstone.realtimebackend.mapper", annotationClass = Mapper.class)
public class CapstoneRealtimeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapstoneRealtimeBackendApplication.class, args);
    }

}
