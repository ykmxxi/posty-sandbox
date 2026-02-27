package com.posty.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.posty")
@EntityScan(basePackages = "com.posty")
public class IndexApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexApplication.class, args);
    }
}
