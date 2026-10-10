package com.sgb.mylibrum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MylibrumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MylibrumApplication.class, args);
	}

}
