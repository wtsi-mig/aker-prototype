package uk.ac.sanger.mig.aker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author pi1
 * @since February 2015
 */
@SpringBootApplication
@ComponentScan
@EnableTransactionManagement
@PropertySource({
		"classpath:aker.properties"
})
public class Application {
	private static Class<Application> applicationClass = Application.class;

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}
}
