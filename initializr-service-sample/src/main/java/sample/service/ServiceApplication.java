/*
 * Copyright 2012 - present the original author or authors.
 */

package sample.service;

import io.spring.initializr.web.support.SpringIoInitializrMetadataUpdateStrategy;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Sample service application.
 *
 * @author Stephane Nicoll
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class ServiceApplication {

	/**
	 * Start the application.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	// This bean opt-in for fetching available Spring Boot versions from https://spring.io
	@Bean
	SpringIoInitializrMetadataUpdateStrategy springIoInitializrMetadataUpdateStrategy(
			RestTemplateBuilder restTemplateBuilder, JsonMapper jsonMapper) {
		return new SpringIoInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), jsonMapper);
	}

}
