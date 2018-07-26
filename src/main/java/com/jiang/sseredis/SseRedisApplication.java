package com.jiang.sseredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SseRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SseRedisApplication.class, args);
    }
}
