package com.jim.console;

import com.jim.console.config.AppConfigBean;
import com.jim.console.config.SSEServerConfig;
import com.jim.console.sse.SSEServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class})
public class Green26WebConsoleApplication {
	public static void main(String[] args) {
		SpringApplication.run(Green26WebConsoleApplication.class, args);
	}
}