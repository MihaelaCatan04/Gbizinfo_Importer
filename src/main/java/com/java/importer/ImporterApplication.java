package com.java.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ImporterApplication {

	static void main(String[] args) {
		SpringApplication.run(ImporterApplication.class, args);
	}

}
