package br.com.microservices.transactionsapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Configuration
@EnableMongoRepositories(basePackages={"br.com.microservices.transactionsapi.repositories"})
@ComponentScan(basePackages={"br.com.microservices.transactionsapi"})
public class TransactionsApiApplication  {
	
	public static void main(String[] args) {
        SpringApplication.run(TransactionsApiApplication.class, args);
    }
}