package com.smartplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.smartplanner"})
@EntityScan("com.smartplanner.model")
@EnableJpaRepositories("com.smartplanner.repository")
public class SmartPlannerApi {

    public static void main(String[] args) {
        SpringApplication.run(SmartPlannerApi.class, args);
    }
}
