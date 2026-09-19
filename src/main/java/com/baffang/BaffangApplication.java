package com.baffang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BaffangApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaffangApplication.class, args);
    }

}
