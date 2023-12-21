package com.fiap.postech.fase4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
public class StreamingApp {

	public static void main(String[] args) {
		SpringApplication.run(StreamingApp.class, args);
	}

}
