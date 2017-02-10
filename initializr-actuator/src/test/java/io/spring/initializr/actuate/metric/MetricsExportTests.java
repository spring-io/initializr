/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.actuate.metric;

import io.spring.initializr.actuate.metric.MetricsExportTests.Config;
import io.spring.initializr.actuate.test.RedisRunning;
import io.spring.initializr.generator.ProjectGeneratedEvent;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class, properties = {
		"spring.metrics.export.delayMillis:500", "spring.metrics.export.enabled:true",
		"initializr.metrics.prefix:test.prefix", "initializr.metrics.key:key.test" })
public class MetricsExportTests {

	@Rule
	public RedisRunning running = new RedisRunning();

	@Autowired
	private ProjectGenerationMetricsListener listener;

	@Autowired
	@Qualifier("writer")
	private MetricWriter writer;

	private RedisMetricRepository repository;

	@Before
	public void init() throws Exception {
		repository = (RedisMetricRepository) writer;
		repository.findAll().forEach(it -> {
			repository.reset(it.getName());
		});
		assertTrue("Metrics not empty", repository.count() == 0);
	}

	@Test
	public void exportAndCheckMetricsExist() throws Exception {
		listener.onGeneratedProject(new ProjectGeneratedEvent(new ProjectRequest()));
		Thread.sleep(1000L);
		assertTrue("No metrics exported", repository.count() > 0);
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableConfigurationProperties(InitializrProperties.class)
	protected static class Config {

		@Bean
		public InitializrMetadataProvider initializrMetadataProvider(
				InitializrProperties properties) {
			InitializrMetadata metadata = InitializrMetadataBuilder
					.fromInitializrProperties(properties).build();
			return new SimpleInitializrMetadataProvider(metadata);
		}
	}
}
