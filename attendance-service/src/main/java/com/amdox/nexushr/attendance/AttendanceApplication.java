package com.amdox.nexushr.attendance;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(
        scanBasePackages = "com.amdox.nexushr"
)
public class AttendanceApplication {


    public static void main(String[] args) {


        SpringApplication.run(
                AttendanceApplication.class,
                args
        );

    }

}