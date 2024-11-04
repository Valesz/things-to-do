package org.example;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.example.configs.SecurityConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({MyConfiguration.class, SecurityConfiguration.class})
@TestPropertySource(value = "classpath:test.application.properties")
public abstract class AbstractTest
{

	@Autowired
	private Environment environment;

	@PostConstruct
	public void inMemoryBdSetup()
	{
		try
		{
			Connection conn = DriverManager.getConnection(
				environment.getProperty("datasource.url"),
				environment.getProperty("datasource.username"),
				environment.getProperty("datasource.password"));

			ScriptRunner sr = new ScriptRunner(conn);

			File file = ResourceUtils.getFile("classpath:init.sql");
			BufferedReader br = new BufferedReader(new FileReader(file));
			sr.setLogWriter(null);
			sr.runScript(br);

			conn.close();
		}
		catch (SQLException e)
		{
			System.out.println("SQLException: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("File not found: " + e.getMessage());
		}
	}
}
