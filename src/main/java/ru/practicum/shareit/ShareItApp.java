package ru.practicum.shareit; // 14

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@ComponentScan(basePackages = "ru.practicum.shareit.*")
//@EntityScan("ru.practicum.shareit.*")
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}

