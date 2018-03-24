package net.kang;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class})
@EnableMongoRepositories(basePackages = "net.kang.repository")
@EntityScan(basePackages = "net.kang.domain")
@ComponentScan(basePackages = "net.kang")
@SpringBootApplication
public class SharedApiExample01Application {

	public static void main(String[] args) {
		SpringApplication.run(SharedApiExample01Application.class, args);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.data.db-main")
	public DataSource mainDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.data.db-log")
	public DataSource contractDataSource() {
		return DataSourceBuilder.create().build();
	}

}
