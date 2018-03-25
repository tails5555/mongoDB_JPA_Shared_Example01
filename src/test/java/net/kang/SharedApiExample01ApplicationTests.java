package net.kang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class})
@RunWith(SpringRunner.class)
@SpringBootTest
public class SharedApiExample01ApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	@ConfigurationProperties(prefix = "spring.data.db-main")
	public void mainDataSource() {
		DataSourceBuilder.create().build();
	}

	@Test
	@ConfigurationProperties(prefix = "spring.data.db-log")
	public void contractDataSource() {
		DataSourceBuilder.create().build();
	}
}
