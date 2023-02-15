package com.nithin.gradlejwttokens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class GradleJwtTokensApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradleJwtTokensApplication.class, args);
    }

}
