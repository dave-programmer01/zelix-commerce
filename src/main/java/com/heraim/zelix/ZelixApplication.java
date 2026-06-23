package com.heraim.zelix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZelixApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZelixApplication.class, args);
    }

}
