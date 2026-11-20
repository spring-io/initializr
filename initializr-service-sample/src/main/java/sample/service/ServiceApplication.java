/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package sample.service;

import io.spring.initializr.web.support.SpringIoInitializrMetadataUpdateStrategy;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;

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
			RestClient.Builder restClientBuilder, JsonMapper jsonMapper) {
		return new SpringIoInitializrMetadataUpdateStrategy(restClientBuilder.build(), jsonMapper);
	}

}
