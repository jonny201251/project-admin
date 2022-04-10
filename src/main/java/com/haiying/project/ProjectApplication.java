package com.haiying.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class},
        scanBasePackages = {"org.jeecg.modules.jmreport", "com.haiying.project"}
)
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

}
