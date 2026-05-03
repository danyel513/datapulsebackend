package com.nexus.datapulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DataPulseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataPulseBackendApplication.class, args);
    }

}
