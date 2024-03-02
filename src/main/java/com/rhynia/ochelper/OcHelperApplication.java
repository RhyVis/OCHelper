package com.rhynia.ochelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.rhynia.ochelper.mapper")
public class OcHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcHelperApplication.class, args);
    }

}
