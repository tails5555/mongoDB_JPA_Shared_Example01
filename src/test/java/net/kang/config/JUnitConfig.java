package net.kang.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

@Configuration
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@ComponentScan({"net.kang.domain", "net.kang.repository", "net.kang.service", "net.kang.controller"})
public class JUnitConfig {
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
	    propertyPlaceholderConfigurer.setLocations(new ClassPathResource("application.properties"));
	    propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
	    return propertyPlaceholderConfigurer;
	}
}
