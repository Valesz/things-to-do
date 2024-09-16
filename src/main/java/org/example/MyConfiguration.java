package org.example;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@EnableJdbcRepositories
@PropertySource("classpath:application.properties")
public class MyConfiguration extends AbstractJdbcConfiguration {

    @Autowired
    Environment environment;

    @Autowired
    DataSource dataSource;

    @Bean
    @Primary
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

    @Bean
    DataSource createDataSource() {
        PoolDataSource dataSource;

        try {
            dataSource = PoolDataSourceFactory.getPoolDataSource();

            dataSource.setConnectionFactoryClassName(environment.getProperty("datasource.driver"));
            dataSource.setURL(environment.getProperty("datasource.url"));
            dataSource.setUser(environment.getProperty("datasource.username"));
            dataSource.setPassword(environment.getProperty("datasource.password"));
            dataSource.setMinPoolSize(0);
            dataSource.setMaxPoolSize(100);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return dataSource;
    }

    @Bean
    NamedParameterJdbcTemplate createNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
