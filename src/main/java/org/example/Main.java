package org.example;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

@SpringBootApplication
@Import(value = {MyConfiguration.class})
@PropertySource("classpath:application.properties")
public class Main implements CommandLineRunner {

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args)
    {
        inMemoryBdSetup();

        System.out.println("In memory db init done ------------------------------");

    }

    public void inMemoryBdSetup() {
        try {
            Connection conn = DriverManager.getConnection(
                    environment.getProperty("datasource.url"),
                    environment.getProperty("datasource.username"),
                    environment.getProperty("datasource.password"));

            ScriptRunner sr = new ScriptRunner(conn);

            File file = ResourceUtils.getFile("classpath:init.sql");
            BufferedReader br = new BufferedReader(new FileReader(file));

            sr.runScript(br);

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}