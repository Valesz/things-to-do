package org.example;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableJdbcRepositories
@PropertySource("classpath:application.properties")
public class MyConfiguration extends AbstractJdbcConfiguration
{

	@Autowired
	Environment environment;

	@Autowired
	DataSource dataSource;

	@Bean
	DataSource createDataSource()
	{
		PoolDataSource dataSource;

		try
		{
			dataSource = PoolDataSourceFactory.getPoolDataSource();

			dataSource.setConnectionFactoryClassName(environment.getProperty("datasource.driver"));
			dataSource.setURL(environment.getProperty("datasource.url"));
			dataSource.setUser(environment.getProperty("datasource.username"));
			dataSource.setPassword(environment.getProperty("datasource.password"));
			dataSource.setMinPoolSize(0);
			dataSource.setMaxPoolSize(100);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return dataSource;
	}

	@Bean
	NamedParameterJdbcTemplate createNamedParameterJdbcTemplate()
	{
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
}
