package br.com.microservices.accountsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Configuration
public class AccountsApiApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(AccountsApiApplication.class, args);
    }
}
