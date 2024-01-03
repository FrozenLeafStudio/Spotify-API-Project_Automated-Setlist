package com.frozenleafstudio.dev.AutomatedSetlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutomatedSetlistApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomatedSetlistApplication.class, args);
	}
}
