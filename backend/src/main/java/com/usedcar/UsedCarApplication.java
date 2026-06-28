package com.usedcar;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@MapperScan("com.usedcar.mapper")
public class UsedCarApplication {

    /**
     * Pin the JVM to UTC so app-generated timestamps (e.g. dedup/recency windows built
     * from LocalDateTime.now()) align with DB-side NOW() regardless of the host timezone.
     * Without this, an app server in a non-UTC zone silently breaks time-window queries.
     */
    @PostConstruct
    void forceUtc() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(UsedCarApplication.class, args);
    }
}
