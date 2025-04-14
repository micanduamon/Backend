package com.example.authsystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements CommandLineRunner {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Redis host: " + redisHost);
    }
}