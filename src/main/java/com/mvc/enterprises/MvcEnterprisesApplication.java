package com.mvc.enterprises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableJpaRepositories
@EnableCaching
public class MvcEnterprisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcEnterprisesApplication.class, args);
	}

}
