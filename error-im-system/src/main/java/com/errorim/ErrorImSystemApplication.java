package com.errorim;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.errorim.mapper")
public class ErrorImSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrorImSystemApplication.class, args);
    }

}
