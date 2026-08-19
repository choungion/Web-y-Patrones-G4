package com.taru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaruApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaruApplication.class, args);
	}

}
