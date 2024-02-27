package com.rhynia.ochelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.rhynia.ochelper.servlet")
public class OcHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcHelperApplication.class, args);
    }

}
