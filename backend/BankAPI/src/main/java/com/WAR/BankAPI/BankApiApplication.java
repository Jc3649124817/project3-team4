package com.WAR.BankAPI;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@ComponentScan("com.WAR")
@EntityScan("com.WAR.BankAPI.models")
@EnableJpaRepositories("com.WAR.BankAPI.repos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"}, allowedHeaders = "*")
public class BankApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApiApplication.class, args);
	}

}
