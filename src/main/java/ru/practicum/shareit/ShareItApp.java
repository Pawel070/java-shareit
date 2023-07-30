package ru.practicum.shareit; // 14

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
//@Configuration
//@PropertySource("classpath:application.properties")
public class ShareItApp {


    public static void main(String[] args) {

        SpringApplication.run(ShareItApp.class, args);
    }

}

