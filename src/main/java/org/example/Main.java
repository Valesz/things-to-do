package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Import(value = {MyConfiguration.class})
@PropertySource("classpath:application.properties")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}