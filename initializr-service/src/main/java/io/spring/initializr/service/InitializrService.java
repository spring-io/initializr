/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service;

import java.util.concurrent.Executor;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.LegacyStsController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * Initializr service application. Enables legacy STS support for older clients.
 *
 * @author Stephane Nicoll
 */
@SpringBootApplication
@EnableCaching
public class InitializrService {

	public static void main(String[] args) {
		SpringApplication.run(InitializrService.class, args);
	}

	@Bean
	public LegacyStsController legacyStsController(
			InitializrMetadataProvider metadataProvider,
			ResourceUrlProvider resourceUrlProvider) {
		return new LegacyStsController(metadataProvider, resourceUrlProvider);
	}

	/**
	 * {@link Executor} configuration for event processing.
	 */
	@Configuration
	@EnableAsync
	static class AsyncConfiguration extends AsyncConfigurerSupport {

		@Override
		public Executor getAsyncExecutor() {
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(1);
			executor.setMaxPoolSize(5);
			executor.setThreadNamePrefix("initializr-");
			executor.initialize();
			return executor;
		}

	}

}
